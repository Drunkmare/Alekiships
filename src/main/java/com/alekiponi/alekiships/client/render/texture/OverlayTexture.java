package com.alekiponi.alekiships.client.render.texture;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import com.alekiponi.alekiships.client.render.util.TextureHelpers;

import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class OverlayTexture extends AbstractTexture {

    private static final Logger LOGGER = LogUtils.getLogger();

    private static final Map<Pair<ResourceLocation, ResourceLocation>, OverlayTexture> CACHE = new ConcurrentHashMap<>();


    private final ResourceLocation baseTexture;
    private final ResourceLocation overlayTexture;

    protected OverlayTexture(final ResourceLocation baseTexture, final ResourceLocation overlayTexture) {
        this.baseTexture = baseTexture;
        this.overlayTexture = overlayTexture;
    }

    public static AbstractTexture getTexture(final ResourceLocation baseTexture,
            final ResourceLocation overlayTexture) {
        try {
            final var key = Pair.of(baseTexture, overlayTexture);
            var texture = CACHE.get(key);
            if (texture == null) {
                texture = new OverlayTexture(baseTexture, overlayTexture);
                CACHE.put(key, texture);
                texture.load(Minecraft.getInstance().getResourceManager());
            }
            return texture;
        } catch (final IOException e) {
            if (baseTexture != TextureManager.INTENTIONAL_MISSING_TEXTURE) {
                LOGGER.warn("Failed to load base texture: {}", baseTexture, e);
            }

            if (overlayTexture != TextureManager.INTENTIONAL_MISSING_TEXTURE) {
                LOGGER.warn("Failed to load overlay texture: {}", overlayTexture, e);
            }

            return MissingTextureAtlasSprite.getTexture();
        } catch (final Throwable throwable) {
            final CrashReport crashReport = CrashReport.forThrowable(throwable, "Registering texture");
            final CrashReportCategory reportCategory = crashReport.addCategory("Resource locations being registered");
            reportCategory.setDetail("Base Texture location", baseTexture);
            reportCategory.setDetail("Painted Texture location", overlayTexture);
            throw new ReportedException(crashReport);
        }
    }

    /**
     * Builds a {@link NativeImage} combining the two inputs
     *
     * @param resultSize The size of the output image
     * @param foreground The foreground image
     * @param background The background image
     *
     * @return A new image combining the foreground and background
     */
    private static NativeImage buildCombinedTexture(final FrameSize resultSize, final NativeImage foreground,
            final NativeImage background) {
        final NativeImage resultImage = new NativeImage(foreground.format(), foreground.getWidth(),
                foreground.getHeight(), false);
        TextureHelpers.collectFrames(foreground, resultSize).forEach(frame -> {
            final int x = frame.x();
            final int y = frame.y();

            background.copyRect(resultImage, 0, 0, x, y, resultSize.width(), resultSize.height(), false, false);

            TextureHelpers.copyRect(foreground, resultImage, x, y, x, y, resultSize.width(), resultSize.height());
        });

        background.close();
        foreground.close();

        return resultImage;
    }

    /**
     * Overlays the base resource texture with the overlay resource texture
     *
     * @param baseResource    The resource for the base texture
     * @param overlayResource The resource for the overlay texture
     *
     * @return A painted {@link NativeImage}
     */
    private static NativeImage overlayTexture(final Resource baseResource,
            final Resource overlayResource) throws IOException {
        final NativeImage baseTexture;
        try (final InputStream inputstream = baseResource.open()) {
            baseTexture = NativeImage.read(inputstream);
        }

        final NativeImage overlayTexture;
        try (final InputStream inputStream = overlayResource.open()) {
            overlayTexture = NativeImage.read(inputStream);
        }

        final int overlayWidth = overlayTexture.getWidth();
        final int overlayHeight = overlayTexture.getHeight();
        final int baseWidth = baseTexture.getWidth();
        final int baseHeight = baseTexture.getHeight();

        if (!TextureHelpers.checkAspectRatio(overlayWidth, overlayHeight, baseWidth, baseHeight)) {
            throw new IllegalArgumentException(
                    MessageFormat.format("Aspect ratios don''t match. {0}x{1} and {2}x{3}", overlayWidth, overlayHeight,
                            baseWidth, baseHeight));
        }

        final FrameSize resultSize = new FrameSize(Math.max(overlayWidth, baseWidth),
                Math.max(overlayHeight, baseHeight));

        final int baseScale = baseWidth > overlayWidth ? baseWidth / overlayWidth : 1;
        final int overlayScale = overlayWidth > baseWidth ? overlayWidth / baseWidth : 1;

        return buildCombinedTexture(resultSize, TextureHelpers.scaleImage(overlayTexture, baseScale),
                TextureHelpers.scaleImage(baseTexture, overlayScale));
    }

    @Override
    public void load(final ResourceManager resourceManager) throws IOException {
        final Optional<Resource> baseResource = resourceManager.getResource(this.baseTexture);

        if (baseResource.isEmpty()) {
            throw new IOException(MessageFormat.format("Missing base texture: {0}", this.baseTexture));
        }

        final Optional<Resource> overlayResource = resourceManager.getResource(this.overlayTexture);

        if (overlayResource.isEmpty()) {
            throw new IOException(MessageFormat.format("Missing overlay texture: {0}", this.overlayTexture));
        }

        final NativeImage overlaidImage;
        try {
            //noinspection resource
            overlaidImage = overlayTexture(baseResource.get(), overlayResource.get());
        } catch (final IllegalArgumentException e) {
            throw new IOException(MessageFormat.format("Ensure {0} and {1} have the same resolution", this.baseTexture,
                    this.overlayTexture), e);
        }

        LOGGER.debug("Generated overlay for Base: {}, Overlay: {}", this.baseTexture, this.overlayTexture);

        TextureUtil.prepareImage(this.getId(), 0, overlaidImage.getWidth(), overlaidImage.getHeight());
        overlaidImage.upload(0, 0, 0, true);
    }
}