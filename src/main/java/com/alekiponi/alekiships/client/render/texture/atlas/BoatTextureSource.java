package com.alekiponi.alekiships.client.render.texture.atlas;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.client.render.util.TextureHelpers;
import com.google.common.base.Preconditions;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.SpriteSourceType;
import net.minecraft.client.renderer.texture.atlas.SpriteSources;
import net.minecraft.client.renderer.texture.atlas.sources.LazyLoadedImage;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.DyeColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public final class BoatTextureSource implements SpriteSource {

    private static final Logger LOGGER = LogUtils.getLogger();

    private static final Codec<BoatTextureSource> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(ResourceLocation.CODEC.fieldOf("woodTexture").forGetter(o -> o.woodTexture),
                            Codec.STRING.fieldOf("paintPrefix").forGetter(o -> o.paintPrefix))
                    .apply(instance, BoatTextureSource::create));

    private static SpriteSourceType TYPE = null;
    final ResourceLocation woodTexture;
    final String paintPrefix;

    public BoatTextureSource(final ResourceLocation woodTexture, final String paintPrefix) {
        this.woodTexture = woodTexture;
        this.paintPrefix = paintPrefix;
    }

    /**
     * Register our {@link SpriteSource} type so it can be used in atlases
     */
    public static void register() {
        TYPE = SpriteSources.register(AlekiShips.MOD_ID + ":boat_texture_generator", CODEC);
    }

    private static BoatTextureSource create(final ResourceLocation baseTexture, final String basePaintPath) {
        return new BoatTextureSource(baseTexture, basePaintPath);
    }

    private static SpriteContents buildCombinedTexture(final ResourceLocation name, final FrameSize resultSize,
            final NativeImage image, final NativeImage background) {
        final NativeImage resultImage = new NativeImage(image.format(), image.getWidth(), image.getHeight(), false);
        TextureHelpers.collectFrames(image, resultSize).forEach(frame -> {
            final int x = frame.x();
            final int y = frame.y();

            background.copyRect(resultImage, 0, 0, x, y, resultSize.width(), resultSize.height(), false, false);

            TextureHelpers.copyRect(image, resultImage, x, y, x, y, resultSize.width(), resultSize.height());
        });

        background.close();
        image.close();

        return new SpriteContents(name, resultSize, resultImage, AnimationMetadataSection.EMPTY, null);
    }

    @Override
    public void run(final ResourceManager resourceManager, final @NotNull Output output) {
        final ResourceLocation woodPath = TEXTURE_ID_CONVERTER.idToFile(this.woodTexture);
        final Optional<Resource> optionalWood = resourceManager.getResource(woodPath);
        if (optionalWood.isEmpty()) {
            LOGGER.warn("Missing wood texture: {}", woodPath);
            return;
        }

        final Resource woodResource = optionalWood.get();
        final ResourceLocation woodTexturePath = this.woodTexture.withPrefix("textures/");

        for (final DyeColor dyeColor : DyeColor.values()) {
            final ResourceLocation paintPath = TEXTURE_ID_CONVERTER.idToFile(
                    new ResourceLocation(this.paintPrefix + "/" + dyeColor.getSerializedName()));
            final Optional<Resource> optionalPaint = resourceManager.getResource(paintPath);

            if (optionalPaint.isEmpty()) {
                LOGGER.warn("Missing paint texture {}", paintPath);
                return;
            }

            final Resource paintResource = optionalPaint.get();

            final LazyLoadedImage lazyWood = new LazyLoadedImage(woodPath, woodResource, 1);
            final LazyLoadedImage lazyPaint = new LazyLoadedImage(paintPath, paintResource, 1);
            final ResourceLocation outPath = woodTexturePath.withSuffix("/" + dyeColor.getSerializedName());
            output.add(outPath, this.createSupplier(outPath, lazyWood, lazyPaint));
            LOGGER.debug("Generated overlay for Wood: {}, Paint: {} with output path of {}", woodPath, paintPath,
                    outPath);
        }
        output.add(woodTexturePath, woodResource);
        LOGGER.debug("Added {} to atlas", woodTexturePath);
    }

    BoatTextureSupplier createSupplier(final ResourceLocation outPath, final LazyLoadedImage lazyWood,
            final LazyLoadedImage lazyPaint) {
        return new BoatTextureSupplier(outPath, lazyWood, lazyPaint);
    }

    @NotNull
    @Override
    public SpriteSourceType type() {
        return Preconditions.checkNotNull(TYPE, "BoatTextureSource not registered");
    }

    static final class BoatTextureSupplier implements SpriteSupplier {
        private final ResourceLocation outPath;
        private final LazyLoadedImage lazyWood;
        private final LazyLoadedImage lazyPaint;

        BoatTextureSupplier(final ResourceLocation outPath, final LazyLoadedImage lazyWood,
                final LazyLoadedImage lazyPaint) {
            this.outPath = outPath;
            this.lazyWood = lazyWood;
            this.lazyPaint = lazyPaint;
        }

        @Override
        public SpriteContents get() {
            try {
                final NativeImage paintTexture = lazyPaint.get();
                final NativeImage woodTexture = lazyWood.get();

                final int paintWidth = paintTexture.getWidth();
                final int paintHeight = paintTexture.getHeight();
                final int woodWidth = woodTexture.getWidth();
                final int woodHeight = woodTexture.getHeight();

                if (!TextureHelpers.checkAspectRatio(paintWidth, paintHeight, woodWidth, woodHeight)) {
                    throw new IllegalArgumentException(
                            "Aspect ratio of ore and background texture does not match for texture '" + this.outPath + "'");
                }

                final FrameSize resultSize = new FrameSize(Math.max(paintWidth, woodWidth),
                        Math.max(paintHeight, woodHeight));
                final int woodScale = woodWidth > paintWidth ? woodWidth / paintWidth : 1;
                final int paintScale = paintWidth > woodWidth ? paintWidth / woodWidth : 1;
                return buildCombinedTexture(this.outPath, resultSize,
                        TextureHelpers.scaleImage(paintTexture, woodScale),
                        TextureHelpers.scaleImage(woodTexture, paintScale));

            } catch (final Exception e) {
                LOGGER.error("Failed to generate boat texture '{}'", this.outPath, e);
            } finally {
                lazyWood.release();
                lazyPaint.release();
            }
            return MissingTextureAtlasSprite.create();
        }

        @Override
        public void discard() {
            lazyWood.release();
            lazyPaint.release();
        }
    }
}