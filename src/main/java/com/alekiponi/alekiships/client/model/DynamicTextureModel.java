package com.alekiponi.alekiships.client.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;

import com.alekiponi.alekiships.AlekiShips;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.state.BlockState;

import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.ElementsModel;
import net.neoforged.neoforge.client.model.IDynamicBakedModel;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import org.jetbrains.annotations.Nullable;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * A model which is dynamically textured using {@link DynamicTextureModelData}.
 *
 * @implNote Textures use {@link InventoryMenu#BLOCK_ATLAS}
 */
@Slf4j
@AllArgsConstructor
public class DynamicTextureModel implements IUnbakedGeometry<DynamicTextureModel> {

    public static final ResourceLocation LOADER_ID = AlekiShips.location("dynamic_texture");

    private static final Codec<Set<String>> RETEXTURED_TEXTURES_CODEC = Codec.either(Codec.STRING,
            Codec.STRING.listOf().comapFlatMap(strings -> {
                if (strings.isEmpty()) {
                    return DataResult.error(() -> "Must have at least one texture");
                }

                return DataResult.success(Set.copyOf(strings));
            }, List::copyOf)).xmap(stringSetEither -> Either.unwrap(stringSetEither.mapLeft(Set::of)), strings -> {
        if (strings.size() == 1) {
            return Either.left(strings.iterator().next());
        }

        return Either.right(strings);
    });

    public static final IGeometryLoader<DynamicTextureModel> LOADER = DynamicTextureModel::deserialize;

    private final ElementsModel baseModel;
    private final Set<String> dynamicTextures;

    public static DynamicTextureModel deserialize(final JsonObject jsonObject,
            final JsonDeserializationContext context) throws JsonParseException {
        final var baseModel = ElementsModel.Loader.INSTANCE.read(jsonObject, context);

        final var retextured = RETEXTURED_TEXTURES_CODEC.parse(JsonOps.INSTANCE, jsonObject.get("dynamic_textures"));
        retextured.ifError(error -> {
            throw new JsonParseException(error.message());
        });

        return new DynamicTextureModel(baseModel, retextured.getOrThrow());
    }

    @Override
    public BakedModel bake(final IGeometryBakingContext context, final ModelBaker baker,
            final Function<Material, TextureAtlasSprite> spriteGetter, final ModelState modelState,
            final ItemOverrides overrides) {
        final var bake = this.baseModel.bake(context, baker, spriteGetter, modelState, overrides);
        return new BakedDynamicTextureModel(bake, context, this.baseModel, modelState, this.dynamicTextures);
    }

    /**
     * The primary model that handles the dynamic dispatch
     */
    private static final class BakedDynamicTextureModel extends BakedModelWrapper<BakedModel> implements IDynamicBakedModel {
        /**
         * Our model cache, our actual model `this` doesn't have any geometry. As baking models is expensive they are
         * cached so they can be retrieved again with little overhead. This cache is safe with resource reload thanks to
         * all models being rebaked giving us a fresh cache to populate.
         */
        private final Map<ResourceLocation, BakedModel> cache = new ConcurrentHashMap<>();

        private final IGeometryBakingContext context;
        private final ElementsModel baseModel;
        private final ModelState modelState;
        private final Set<String> dynamicTextures;

        public BakedDynamicTextureModel(final BakedModel bake, final IGeometryBakingContext context,
                final ElementsModel baseModel, final ModelState modelState, final Set<String> dynamicTextures) {
            super(bake);
            this.context = context;
            this.baseModel = baseModel;
            this.modelState = modelState;
            this.dynamicTextures = dynamicTextures;
        }

        /**
         * Gets the actual model for the provided texture
         *
         * @param texture The dynamic texture
         *
         * @return The actual model for the provided texture
         */
        private BakedModel getModel(final ResourceLocation texture) {
            return this.cache.computeIfAbsent(texture, this::bake);
        }

        /**
         * Bakes the actual model for the provided texture
         *
         * @param texture The dynamic texture
         *
         * @return The final baked model
         */
        private BakedModel bake(final ResourceLocation texture) {
            log.debug("Baking dynamic texture model {} for {}", this.context.getModelName(), texture);
            return ModelUtils.bakeDynamic(new DynamicTexturesContext(this.context, this.dynamicTextures, texture),
                    this.baseModel, this.modelState);
        }

        @Override
        public List<BakedQuad> getQuads(final @Nullable BlockState state, final @Nullable Direction side,
                final RandomSource random, final ModelData data, final @Nullable RenderType renderType) {
            final var property = data.get(DynamicTextureModelData.PROPERTY);
            if (property != null) {
                return this.getModel(property.texture()).getQuads(state, side, random, data, renderType);
            }
            return super.getQuads(state, side, random, data, renderType);
        }

        @Override
        public TextureAtlasSprite getParticleIcon(final ModelData data) {
            final var property = data.get(DynamicTextureModelData.PROPERTY);
            if (property != null) {
                return this.getModel(property.texture()).getParticleIcon(data);
            }
            return super.getParticleIcon();
        }

        /**
         * A Baking context that handles our dynamic texture dispatch
         */
        private static final class DynamicTexturesContext extends WrappedGeometryBakingContext {

            private final Set<String> dynamicTextures;
            private final Material texture;

            public DynamicTexturesContext(final IGeometryBakingContext delegate, final Set<String> dynamicTextures,
                    final ResourceLocation texture) {
                super(delegate);
                this.dynamicTextures = dynamicTextures;
                this.texture = new Material(InventoryMenu.BLOCK_ATLAS, texture);
            }

            @Override
            public boolean hasMaterial(final String name) {
                if (this.dynamicTextures.contains(name)) {
                    return !MissingTextureAtlasSprite.getLocation().equals(this.texture.texture());
                }
                return super.hasMaterial(name);
            }

            @Override
            public Material getMaterial(final String name) {
                if (this.dynamicTextures.contains(name)) {
                    return this.texture;
                }
                return super.getMaterial(name);
            }
        }
    }
}