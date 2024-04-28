package com.alekiponi.alekiships.client.reosurces;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.TextureAtlasHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class BoatAtlasHolder extends TextureAtlasHolder {

    public BoatAtlasHolder(final TextureManager textureManager, final ResourceLocation atlasName,
            final ResourceLocation atlasInfo) {
        super(textureManager, atlasName, atlasInfo);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to make it public
     * </p>
     */
    @Override
    public @NotNull TextureAtlasSprite getSprite(final @NotNull ResourceLocation resourceLocation) {
        return super.getSprite(resourceLocation);
    }
}