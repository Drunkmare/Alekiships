package com.alekiponi.alekiships.client.render.flywheel;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.common.entity.AlekiShipsEntities;
import com.alekiponi.alekiships.util.AlekiShipsHelper;
import com.alekiponi.alekiships.util.VanillaWood;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;

import static com.jozufozu.flywheel.lib.visual.SimpleEntityVisualizer.builder;

public class Flywheel {


    public static void init() {
        for (final VanillaWood vanillaWood : VanillaWood.values()) {
            builder(AlekiShipsEntities.ROWBOATS.get(vanillaWood).get()).factory(
                    RowboatVisual.create(getRowboatBaseTexture(vanillaWood), AlekiShipsHelper.mapOfKeys(DyeColor.class,
                            dyeColor -> getRowboatPaintTexture(vanillaWood, dyeColor)))).apply();
            builder(AlekiShipsEntities.SLOOPS.get(vanillaWood).get()).factory(
                    SloopVisual.create(getSloopBaseTexture(vanillaWood), AlekiShipsHelper.mapOfKeys(DyeColor.class,
                            dyeColor -> getSloopPaintTexture(vanillaWood, dyeColor)))).apply();
        }
    }

    private static ResourceLocation getRowboatPaintTexture(final VanillaWood vanillaWood, final DyeColor dyeColor) {
        return new ResourceLocation(AlekiShips.MOD_ID,
                "textures/entity/watercraft/rowboat/" + vanillaWood.getSerializedName() + "/" + dyeColor.getSerializedName());
    }

    private static ResourceLocation getRowboatBaseTexture(final VanillaWood vanillaWood) {
        return new ResourceLocation(AlekiShips.MOD_ID,
                "textures/entity/watercraft/rowboat/" + vanillaWood.getSerializedName());
    }

    private static ResourceLocation getSloopPaintTexture(final VanillaWood vanillaWood, final DyeColor dyeColor) {
        return new ResourceLocation(AlekiShips.MOD_ID,
                "textures/entity/watercraft/sloop/" + vanillaWood.getSerializedName() + "/" + dyeColor.getSerializedName());
    }

    private static ResourceLocation getSloopBaseTexture(final VanillaWood vanillaWood) {
        return new ResourceLocation(AlekiShips.MOD_ID,
                "textures/entity/watercraft/sloop/" + vanillaWood.getSerializedName());
    }
}