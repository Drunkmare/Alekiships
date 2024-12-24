package com.alekiponi.alekiships.client.render;

import com.alekiponi.alekiships.client.render.texture.PaintedTexture;
import com.alekiponi.alekiships.util.CommonHelper;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.logging.LogUtils;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.util.Lazy;
import org.apache.commons.lang3.function.TriFunction;
import org.slf4j.Logger;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Optional;

public final class AlekiShipsRenderTypes {

    public static final TriFunction<ResourceLocation, ResourceLocation, Boolean, RenderType> PAINTED_ENTITY_CUTOUT_NO_CULL = CommonHelper.memoize(
            (baseTextureLocation, paintTextureLocation, outline) -> {
                final RenderType.CompositeState compositeState = RenderType.CompositeState.builder()
                        .setShaderState(RenderStateShard.RENDERTYPE_ENTITY_TRANSLUCENT_SHADER).setTextureState(
                                new PaintedTextureStateShard(baseTextureLocation, paintTextureLocation, false, false))
                        .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                        .setCullState(RenderStateShard.NO_CULL).setLightmapState(RenderStateShard.LIGHTMAP)
                        .setOverlayState(RenderStateShard.OVERLAY).createCompositeState(outline);
                return RenderType.create("painted_entity_cutout_no_cull", DefaultVertexFormat.NEW_ENTITY,
                        VertexFormat.Mode.QUADS, RenderType.TRANSIENT_BUFFER_SIZE, true, false, compositeState);
            });

    public static RenderType paintedEntityCutoutNoCull(final ResourceLocation baseTexture,
            final ResourceLocation paintedTexturePath, final boolean outline) {
        return PAINTED_ENTITY_CUTOUT_NO_CULL.apply(baseTexture, paintedTexturePath, outline);
    }

    public static RenderType paintedEntityCutoutNoCull(final ResourceLocation baseTexture,
            final ResourceLocation paintedTexturePath) {
        return paintedEntityCutoutNoCull(baseTexture, paintedTexturePath, true);
    }

    public static class PaintedTextureStateShard extends RenderStateShard.EmptyTextureStateShard {
        private static final Logger LOGGER = LogUtils.getLogger();
        @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
        private final Optional<ResourceLocation> texture;
        protected boolean blur;
        @SuppressWarnings("SpellCheckingInspection")
        protected boolean mipmap;

        public PaintedTextureStateShard(final ResourceLocation baseTexturePath,
                final ResourceLocation paintedTexturePath, final boolean blur, final boolean mipmap) {
            super(setupState(baseTexturePath, paintedTexturePath, blur, mipmap), () -> {
            });
            this.texture = Optional.of(baseTexturePath);
            this.blur = blur;
            this.mipmap = mipmap;
        }

        private static Runnable setupState(final ResourceLocation baseTexturePath,
                final ResourceLocation paintedTexturePath, final boolean blur, final boolean mipmap) {
            final var lazyTexture = Lazy.of(() -> {
                final PaintedTexture paintedTexture = new PaintedTexture(baseTexturePath, paintedTexturePath);
                try {
                    paintedTexture.load(Minecraft.getInstance().getResourceManager());
                    return paintedTexture;
                } catch (final IOException e) {
                    if (baseTexturePath != TextureManager.INTENTIONAL_MISSING_TEXTURE) {
                        LOGGER.warn("Failed to load texture: {}", baseTexturePath, e);
                    }
                    if (paintedTexturePath != TextureManager.INTENTIONAL_MISSING_TEXTURE) {
                        LOGGER.warn("Failed to load texture: {}", paintedTexturePath, e);
                    }

                    return MissingTextureAtlasSprite.getTexture();
                } catch (final Throwable throwable) {
                    final CrashReport crashReport = CrashReport.forThrowable(throwable, "Registering texture");
                    final CrashReportCategory reportCategory = crashReport.addCategory(
                            "Resource location being registered");
                    reportCategory.setDetail("Base Texture location", baseTexturePath);
                    reportCategory.setDetail("Painted Texture location", paintedTexturePath);
                    throw new ReportedException(crashReport);
                }
            });

            return () -> {
                final var texture = lazyTexture.get();
                texture.setFilter(blur, mipmap);
                RenderSystem.setShaderTexture(0, texture.getId());
            };
        }

        @Override
        public String toString() {
            return MessageFormat.format("{0}[{1}(blur={2}, mipmap={3})]", this.name, this.texture, this.blur,
                    this.mipmap);
        }

        @Override
        protected Optional<ResourceLocation> cutoutTexture() {
            return this.texture;
        }
    }
}