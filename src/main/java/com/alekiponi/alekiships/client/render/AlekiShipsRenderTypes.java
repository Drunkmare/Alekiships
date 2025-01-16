package com.alekiponi.alekiships.client.render;

import com.alekiponi.alekiships.client.render.texture.OverlayTexture;
import com.alekiponi.alekiships.util.CommonHelper;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.function.TriFunction;

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
        @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
        private final Optional<ResourceLocation> texture;
        protected boolean blur;
        @SuppressWarnings("SpellCheckingInspection")
        protected boolean mipmap;

        public PaintedTextureStateShard(final ResourceLocation baseTexturePath,
                final ResourceLocation paintedTexturePath, final boolean blur, final boolean mipmap) {
            super(() -> {
                final var texture = OverlayTexture.getTexture(baseTexturePath, paintedTexturePath);
                texture.setFilter(blur, mipmap);
                RenderSystem.setShaderTexture(0, texture.getId());
            }, () -> {
            });
            this.texture = Optional.of(baseTexturePath);
            this.blur = blur;
            this.mipmap = mipmap;
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