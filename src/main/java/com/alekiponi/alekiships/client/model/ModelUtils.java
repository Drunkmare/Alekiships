package com.alekiponi.alekiships.client.model;

import com.mojang.math.Transformation;

import com.alekiponi.alekiships.mixins.client.accessors.ElementsModelAccessor;

import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

import net.neoforged.neoforge.client.RenderTypeGroup;
import net.neoforged.neoforge.client.model.ElementsModel;
import net.neoforged.neoforge.client.model.IQuadTransformer;
import net.neoforged.neoforge.client.model.QuadTransformers;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.UnbakedGeometryHelper;

import java.util.List;
import java.util.function.Function;

/**
 * General model utils. Primarily for dynamic baking of models such as via
 * {@link #bakeDynamic(IGeometryBakingContext, ElementsModel, ModelState)}
 */
public final class ModelUtils {

    /**
     * Dynamically bakes an elements model
     *
     * @param context    The bake context
     * @param model      The model
     * @param modelState The model state
     */
    public static BakedModel bakeDynamic(final IGeometryBakingContext context, final ElementsModel model,
            final ModelState modelState) {
        return bakeWithElements(context, ((ElementsModelAccessor) model).getElements(), modelState);
    }

    /**
     * Dynamically bake a model using the provided model elements
     *
     * @param context    The bake context
     * @param elements   The model elements
     * @param modelState The model state
     */
    public static BakedModel bakeWithElements(final IGeometryBakingContext context, final List<BlockElement> elements,
            final ModelState modelState) {
        return bakeModel(context, elements, Material::sprite, modelState, ItemOverrides.EMPTY);
    }

    /**
     * @param context      The bake context
     * @param elements     The model elements
     * @param spriteGetter The sprite getter, typically just {@code Material::sprite}
     * @param modelState   The model state
     * @param overrides    The item overrides
     */
    public static BakedModel bakeModel(final IGeometryBakingContext context, final List<BlockElement> elements,
            final Function<Material, TextureAtlasSprite> spriteGetter, final ModelState modelState,
            final ItemOverrides overrides) {
        final var particle = spriteGetter.apply(context.getMaterial("particle"));

        final var builder = getBuilder(context, overrides).particle(particle);
        final var quadTransformer = getTransformer(modelState, context.getRootTransform());

        for (final var part : elements) {
            bakePart(builder, context, part, spriteGetter, modelState, quadTransformer);
        }

        final ResourceLocation renderTypeHint = context.getRenderTypeHint();

        return builder.build(renderTypeHint != null ? context.getRenderType(renderTypeHint) : RenderTypeGroup.EMPTY);
    }

    /**
     * Bake a model part dynamically
     *
     * @param builder         The builder to bake the part to
     * @param context         The bake context
     * @param part            The part to bake
     * @param spriteGetter    The sprite getter, typically just {@code Material::sprite}
     * @param modelState      The model state
     * @param quadTransformer The quad transformer
     */
    public static void bakePart(final SimpleBakedModel.Builder builder, final IGeometryBakingContext context,
            final BlockElement part, final Function<Material, TextureAtlasSprite> spriteGetter,
            final ModelState modelState, final IQuadTransformer quadTransformer) {
        for (final var direction : part.faces.keySet()) {
            final var face = part.faces.get(direction);
            String texture = face.texture();
            if (texture.charAt(0) == '#') {
                texture = texture.substring(1);
            }
            final var sprite = spriteGetter.apply(context.getMaterial(texture));
            final var bakedQuad = BlockModel.bakeFace(part, face, sprite, direction, modelState);
            quadTransformer.processInPlace(bakedQuad);
            if (face.cullForDirection() == null) {
                builder.addUnculledFace(bakedQuad);
            } else {
                builder.addCulledFace(Direction.rotate(modelState.getRotation().getMatrix(), face.cullForDirection()),
                        bakedQuad);
            }
        }
    }

    /**
     * Helper to create a {@link SimpleBakedModel.Builder}
     *
     * @param context   Context to initialize the builder with
     * @param overrides The overrides
     */
    public static SimpleBakedModel.Builder getBuilder(final IGeometryBakingContext context,
            final ItemOverrides overrides) {
        return new SimpleBakedModel.Builder(context.useAmbientOcclusion(), context.useBlockLight(), context.isGui3d(),
                context.getTransforms(), overrides);
    }

    /**
     * @param modelState     The model state
     * @param transformation The transformation
     */
    public static IQuadTransformer getTransformer(final ModelState modelState, final Transformation transformation) {
        if (transformation.isIdentity()) {
            return QuadTransformers.empty();
        } else {
            return UnbakedGeometryHelper.applyRootTransform(modelState, transformation);
        }
    }
}