package com.alekiponi.alekiships.client.render.texture;

import com.alekiponi.alekiships.client.render.util.TextureHelpers;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.logging.LogUtils;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public class PaintedTexture extends AbstractTexture {

    private static final Logger LOGGER = LogUtils.getLogger();

    private final ResourceLocation baseTexturePath;
    private final ResourceLocation paintTexturePath;

    public PaintedTexture(final ResourceLocation baseTexturePath, final ResourceLocation paintTexturePath) {
        this.baseTexturePath = baseTexturePath;
        this.paintTexturePath = paintTexturePath;
    }

    /**
     * Builds a {@link NativeImage} combining the two inputs
     *
     * @param resultSize The size of the output image
     * @param foreground The foreground image
     * @param background The background image
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
     * Paints the base resource texture with the paint resource texture
     *
     * @param baseResource  The resource for the base texture
     * @param paintResource The resource for the paint texture
     * @return A painted {@link NativeImage}
     */
    private static NativeImage paintTexture(final Resource baseResource,
            final Resource paintResource) throws IOException {
        final NativeImage baseTexture;
        try (final InputStream inputstream = baseResource.open()) {
            baseTexture = NativeImage.read(inputstream);
        }

        final NativeImage paintTexture;
        try (final InputStream inputStream = paintResource.open()) {
            paintTexture = NativeImage.read(inputStream);
        }

        final int paintWidth = paintTexture.getWidth();
        final int paintHeight = paintTexture.getHeight();
        final int baseWidth = baseTexture.getWidth();
        final int baseHeight = baseTexture.getHeight();

        if (!TextureHelpers.checkAspectRatio(paintWidth, paintHeight, baseWidth, baseHeight)) {
            throw new IllegalArgumentException(
                    String.format("Aspect ratios don't match. %sx%s and %sx%s", paintWidth, paintHeight, baseWidth,
                            baseHeight));
        }

        final FrameSize resultSize = new FrameSize(Math.max(paintWidth, baseWidth), Math.max(paintHeight, baseHeight));

        final int woodScale = baseWidth > paintWidth ? baseWidth / paintWidth : 1;
        final int paintScale = paintWidth > baseWidth ? paintWidth / baseWidth : 1;

        return buildCombinedTexture(resultSize, TextureHelpers.scaleImage(paintTexture, woodScale),
                TextureHelpers.scaleImage(baseTexture, paintScale));
    }

    @Override
    public void load(final ResourceManager resourceManager) throws IOException {
        final Optional<Resource> baseResource = resourceManager.getResource(this.baseTexturePath);

        if (baseResource.isEmpty()) {
            throw new IOException(String.format("Missing base texture: %s", this.baseTexturePath));
        }

        final Optional<Resource> paintResource = resourceManager.getResource(this.paintTexturePath);

        if (paintResource.isEmpty()) {
            throw new IOException(String.format("Missing paint texture: %s", this.paintTexturePath));
        }

        final NativeImage paintedImage;
        try {
            //noinspection resource
            paintedImage = paintTexture(baseResource.get(), paintResource.get());
        } catch (final IllegalArgumentException e) {
            throw new IOException(String.format("Ensure %s and %s have the same resolution", this.baseTexturePath,
                    this.paintTexturePath), e);
        }

        LOGGER.debug("Generated overlay for Base: {}, Paint: {}", this.baseTexturePath, this.paintTexturePath);

        TextureUtil.prepareImage(this.getId(), 0, paintedImage.getWidth(), paintedImage.getHeight());
        paintedImage.upload(0, 0, 0, true);
    }
}