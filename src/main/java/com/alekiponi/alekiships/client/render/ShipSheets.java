package com.alekiponi.alekiships.client.render;

import com.alekiponi.alekiships.AlekiShips;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class ShipSheets {
    public static final ResourceLocation ROWBOAT_SHEET = new ResourceLocation(AlekiShips.MOD_ID,
            "textures/atlas/rowboats.png");
    public static final ResourceLocation SLOOP_SHEET = new ResourceLocation(AlekiShips.MOD_ID,
            "textures/atlas/sloops.png");
}