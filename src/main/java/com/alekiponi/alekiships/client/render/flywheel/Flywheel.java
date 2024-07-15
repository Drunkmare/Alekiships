package com.alekiponi.alekiships.client.render.flywheel;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.client.render.flywheel.compartment.BlockCompartmentVisual;
import com.alekiponi.alekiships.client.render.flywheel.compartment.ChestCompartmentVisual;
import com.alekiponi.alekiships.client.render.flywheel.compartment.EnderChestCompartmentVisual;
import com.alekiponi.alekiships.client.render.flywheel.compartment.LightableBlockCompartmentVisual;
import com.alekiponi.alekiships.common.entity.AlekiShipsEntities;
import com.alekiponi.alekiships.util.AlekiShipsHelper;
import com.alekiponi.alekiships.util.VanillaWood;
import com.jozufozu.flywheel.lib.visual.SimpleEntityVisualizer;
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

        SimpleEntityVisualizer.builder(AlekiShipsEntities.BLOCK_COMPARTMENT_ENTITY.get())
                .factory(BlockCompartmentVisual::new).apply();
        // Storage Compartments
        SimpleEntityVisualizer.builder(AlekiShipsEntities.BARREL_COMPARTMENT_ENTITY.get())
                .factory(BlockCompartmentVisual::new).apply();

        // Chest
        SimpleEntityVisualizer.builder(AlekiShipsEntities.CHEST_COMPARTMENT_ENTITY.get())
                .factory(ChestCompartmentVisual::new).apply();
        // Ender Chest
        SimpleEntityVisualizer.builder(AlekiShipsEntities.ENDER_CHEST_COMPARTMENT_ENTITY.get())
                .factory(EnderChestCompartmentVisual::new).apply();
        // Shulker Box

        // Processing Compartments
        SimpleEntityVisualizer.builder(AlekiShipsEntities.FURNACE_COMPARTMENT_ENTITY.get())
                .factory(LightableBlockCompartmentVisual::new).apply();
        SimpleEntityVisualizer.builder(AlekiShipsEntities.BLAST_FURNACE_COMPARTMENT_ENTITY.get())
                .factory(LightableBlockCompartmentVisual::new).apply();
        SimpleEntityVisualizer.builder(AlekiShipsEntities.SMOKER_COMPARTMENT_ENTITY.get())
                .factory(LightableBlockCompartmentVisual::new).apply();
        SimpleEntityVisualizer.builder(AlekiShipsEntities.BREWING_STAND_COMPARTMENT_ENTITY.get())
                .factory(BlockCompartmentVisual::new).apply();

        // Crafting compartments
        SimpleEntityVisualizer.builder(AlekiShipsEntities.WORKBENCH_COMPARTMENT_ENTITY.get())
                .factory(BlockCompartmentVisual::new).apply();
        SimpleEntityVisualizer.builder(AlekiShipsEntities.STONECUTTER_COMPARTMENT_ENTITY.get())
                .factory(BlockCompartmentVisual::new).apply();
        SimpleEntityVisualizer.builder(AlekiShipsEntities.CARTOGRAPHY_TABLE_COMPARTMENT_ENTITY.get())
                .factory(BlockCompartmentVisual::new).apply();
        SimpleEntityVisualizer.builder(AlekiShipsEntities.SMITHING_TABLE_COMPARTMENT_ENTITY.get())
                .factory(BlockCompartmentVisual::new).apply();
        SimpleEntityVisualizer.builder(AlekiShipsEntities.GRINDSTONE_COMPARTMENT_ENTITY.get())
                .factory(BlockCompartmentVisual::new).apply();
        SimpleEntityVisualizer.builder(AlekiShipsEntities.LOOM_COMPARTMENT_ENTITY.get())
                .factory(BlockCompartmentVisual::new).apply();
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