package com.alekiponi.alekiships.client.render;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.util.AlekiShipsHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.EnumMap;

@OnlyIn(Dist.CLIENT)
public final class ShipSheets {
    public static final ResourceLocation ROWBOAT_SHEET = new ResourceLocation(AlekiShips.MOD_ID,
            "textures/atlas/rowboats.png");
    public static final ResourceLocation SLOOP_SHEET = new ResourceLocation(AlekiShips.MOD_ID,
            "textures/atlas/sloops.png");

    public static final EnumMap<DyeColor, ResourceLocation> ROWBOAT_TEXTURE_LOCATION = AlekiShipsHelper.mapOfKeys(
            DyeColor.class,
            dyeColor -> new ResourceLocation(AlekiShips.MOD_ID, "textures/entity/watercraft/rowboat/oak/" + dyeColor));
}