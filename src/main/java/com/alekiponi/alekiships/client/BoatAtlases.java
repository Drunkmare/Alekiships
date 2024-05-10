package com.alekiponi.alekiships.client;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.client.render.ShipSheets;
import com.alekiponi.alekiships.client.resources.BoatAtlasHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public final class BoatAtlases {

    @Nullable
    private static BoatAtlasHolder ROWBOAT_ATLAS;
    @Nullable
    private static BoatAtlasHolder SLOOP_ATLAS;

    public static BoatAtlasHolder getRowboatAtlas() {
        if (ROWBOAT_ATLAS == null) {
            final Minecraft minecraft = Minecraft.getInstance();
            final TextureManager textureManager = minecraft.getTextureManager();
            ROWBOAT_ATLAS = new BoatAtlasHolder(textureManager, ShipSheets.ROWBOAT_SHEET,
                    new ResourceLocation(AlekiShips.MOD_ID, "rowboats"));
        }
        return ROWBOAT_ATLAS;
    }

    public static BoatAtlasHolder getSloopAtlas() {
        if (SLOOP_ATLAS == null) {
            final Minecraft minecraft = Minecraft.getInstance();
            final TextureManager textureManager = minecraft.getTextureManager();
            SLOOP_ATLAS = new BoatAtlasHolder(textureManager, ShipSheets.SLOOP_SHEET,
                    new ResourceLocation(AlekiShips.MOD_ID, "sloops"));
        }
        return SLOOP_ATLAS;
    }
}