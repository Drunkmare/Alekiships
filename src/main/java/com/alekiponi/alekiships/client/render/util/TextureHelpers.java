package com.alekiponi.alekiships.client.render.util;

import com.mojang.blaze3d.platform.NativeImage;

import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.util.FastColor;

import java.util.ArrayList;
import java.util.List;

public final class TextureHelpers {

    private TextureHelpers() {
    }

    public static boolean checkAspectRatio(final int bgWidth, final int bgHeight, final int fgWidth,
            final int fgHeight) {
        int widthFactor = Math.max(bgWidth, fgWidth) / Math.min(bgWidth, fgWidth);
        int heightFactor = Math.max(bgHeight, fgHeight) / Math.min(bgHeight, fgHeight);
        return widthFactor == heightFactor;
    }

    public static void copyRect(final NativeImage sourceImage, final NativeImage destinationImage, final int sourceX,
            final int sourceY, final int destinationX, final int destinationY, final int width, final int height) {
        for (int y = 0; y < width; y++) {
            for (int x = 0; x < height; x++) {
                int rgba = sourceImage.getPixelRGBA(sourceX + x, sourceY + y);
                int alpha = FastColor.ARGB32.alpha(rgba);
                if (alpha == 255) {
                    destinationImage.setPixelRGBA(destinationX + x, destinationY + y, rgba);
                } else if (alpha > 0) {
                    destinationImage.blendPixel(destinationX + x, destinationY + y, rgba);
                }
            }
        }
    }

    public static List<FrameInfo> collectFrames(final NativeImage image, final FrameSize size) {
        final List<FrameInfo> frames = new ArrayList<>();
        final int rowCount = image.getWidth() / size.width();
        final int frameCount = rowCount * (image.getHeight() / size.height());
        for (int idx = 0; idx < frameCount; idx++) {
            final int frameX = (idx % rowCount) * size.width();
            final int frameY = (idx / rowCount) * size.height();
            frames.add(new FrameInfo(idx, frameX, frameY));
        }
        return frames;
    }

    public static NativeImage scaleImage(final NativeImage source, final int scale) {
        if (scale <= 1) {
            return source;
        }

        final NativeImage scaled = new NativeImage(source.format(), source.getWidth() * scale,
                source.getHeight() * scale, false);
        scaled.resizeSubRectTo(0, 0, source.getWidth(), source.getHeight(), source);
        source.close();
        return scaled;
    }
}