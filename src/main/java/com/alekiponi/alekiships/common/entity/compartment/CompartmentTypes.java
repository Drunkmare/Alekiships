package com.alekiponi.alekiships.common.entity.compartment;

import com.alekiponi.alekiships.common.entity.AlekiShipsEntities;
import com.alekiponi.alekiships.common.entity.compartment.vanilla.*;
import com.alekiponi.alekiships.common.entity.compartment.vanilla.crafting.*;
import com.alekiponi.alekiships.util.AlekiShipsTags;
import net.minecraft.world.item.Items;

@SuppressWarnings("unused")
public final class CompartmentTypes {

    /**
     * Special fallback {@link CompartmentType} used when no registered {@link CompartmentType}s accept the stack.
     * This cannot really be registered as it'll take priority over anything after it.
     */
    public static final CompartmentType<BlockCompartmentEntity> BLOCK_COMPARTMENT = CompartmentType.of(
            AlekiShipsEntities.BLOCK_COMPARTMENT_ENTITY, BlockCompartment.create(BlockCompartmentEntity::new));

    public static final CompartmentType<BarrelCompartmentEntity> BARREL_COMPARTMENT = CompartmentType.register(
            CompartmentType.postInit(AlekiShipsEntities.BARREL_COMPARTMENT_ENTITY, BlockCompartment::initialize,
                    CompartmentCloneable::initialize), itemStack -> itemStack.is(Items.BARREL));

    public static final CompartmentType<ChestCompartmentEntity> CHEST_COMPARTMENT = CompartmentType.register(
            CompartmentType.postInit(AlekiShipsEntities.CHEST_COMPARTMENT_ENTITY, CompartmentCloneable::initialize),
            itemStack -> itemStack.is(Items.CHEST));

    public static final CompartmentType<EnderChestCompartmentEntity> ENDER_CHEST_COMPARTMENT = CompartmentType.register(
            CompartmentType.simple(AlekiShipsEntities.ENDER_CHEST_COMPARTMENT_ENTITY),
            itemStack -> itemStack.is(Items.ENDER_CHEST));

    public static final CompartmentType<ShulkerBoxCompartmentEntity> SHULKER_BOX_COMPARTMENT = CompartmentType.register(
            CompartmentType.of(AlekiShipsEntities.SHULKER_BOX_COMPARTMENT_ENTITY, ShulkerBoxCompartmentEntity::create),
            itemStack -> itemStack.is(AlekiShipsTags.Items.SHULKER_BOXES));

    public static final CompartmentType<FurnaceCompartmentEntity> FURNACE_COMPARTMENT = CompartmentType.register(
            CompartmentType.postInit(AlekiShipsEntities.FURNACE_COMPARTMENT_ENTITY, BlockCompartment::initialize,
                    CompartmentCloneable::initialize), itemStack -> itemStack.is(Items.FURNACE));

    public static final CompartmentType<BlastFurnaceCompartmentEntity> BLAST_FURNACE_COMPARTMENT = CompartmentType.register(
            CompartmentType.postInit(AlekiShipsEntities.BLAST_FURNACE_COMPARTMENT_ENTITY, BlockCompartment::initialize,
                    CompartmentCloneable::initialize), itemStack -> itemStack.is(Items.BLAST_FURNACE));

    public static final CompartmentType<SmokerCompartmentEntity> SMOKER_COMPARTMENT = CompartmentType.register(
            CompartmentType.postInit(AlekiShipsEntities.SMOKER_COMPARTMENT_ENTITY, BlockCompartment::initialize,
                    CompartmentCloneable::initialize), itemStack -> itemStack.is(Items.SMOKER));

    public static final CompartmentType<BrewingStandCompartmentEntity> BREWING_STAND_COMPARTMENT = CompartmentType.register(
            CompartmentType.postInit(AlekiShipsEntities.BREWING_STAND_COMPARTMENT_ENTITY, BlockCompartment::initialize,
                    CompartmentCloneable::initialize), itemStack -> itemStack.is(Items.BREWING_STAND));

    public static final CompartmentType<CraftingTableCompartment> CRAFTING_TABLE_COMPARTMENT = CompartmentType.register(
            CompartmentType.of(AlekiShipsEntities.WORKBENCH_COMPARTMENT_ENTITY,
                    BlockCompartment.create(CraftingTableCompartment::new)),
            itemStack -> itemStack.is(AlekiShipsTags.Items.CRAFTING_TABLES));

    public static final CompartmentType<StonecutterCompartmentEntity> STONECUTTER_COMPARTMENT = CompartmentType.register(
            CompartmentType.of(AlekiShipsEntities.STONECUTTER_COMPARTMENT_ENTITY,
                    BlockCompartment.create(StonecutterCompartmentEntity::new)),
            itemStack -> itemStack.is(Items.STONECUTTER));

    public static final CompartmentType<CartographyTableCompartmentEntity> CARTOGRAPHY_TABLE_COMPARTMENT = CompartmentType.register(
            CompartmentType.of(AlekiShipsEntities.CARTOGRAPHY_TABLE_COMPARTMENT_ENTITY,
                    BlockCompartment.create(CartographyTableCompartmentEntity::new)),
            itemStack -> itemStack.is(Items.CARTOGRAPHY_TABLE));

    public static final CompartmentType<SmithingTableCompartmentEntity> SMITHING_TABLE_COMPARTMENT = CompartmentType.register(
            CompartmentType.of(AlekiShipsEntities.SMITHING_TABLE_COMPARTMENT_ENTITY,
                    BlockCompartment.create(SmithingTableCompartmentEntity::new)),
            itemStack -> itemStack.is(Items.SMITHING_TABLE));

    public static final CompartmentType<GrindstoneCompartmentEntity> GRINDSTONE_COMPARTMENT = CompartmentType.register(
            CompartmentType.of(AlekiShipsEntities.GRINDSTONE_COMPARTMENT_ENTITY,
                    BlockCompartment.create(GrindstoneCompartmentEntity::new)),
            itemStack -> itemStack.is(Items.GRINDSTONE));

    public static final CompartmentType<LoomCompartmentEntity> LOOM_COMPARTMENT = CompartmentType.register(
            CompartmentType.of(AlekiShipsEntities.LOOM_COMPARTMENT_ENTITY,
                    BlockCompartment.create(LoomCompartmentEntity::new)), itemStack -> itemStack.is(Items.LOOM));

    public static final CompartmentType<NoteBlockCompartmentEntity> NOTE_BLOCK_COMPARTMENT = CompartmentType.register(
            CompartmentType.of(AlekiShipsEntities.NOTE_BLOCK_COMPARTMENT_ENTITY,
                    BlockCompartment.create(NoteBlockCompartmentEntity::new)),
            itemStack -> itemStack.is(Items.NOTE_BLOCK));

    public static final CompartmentType<JukeboxCompartmentEntity> JUKEBOX_COMPARTMENT = CompartmentType.register(
            CompartmentType.postInit(AlekiShipsEntities.JUKEBOX_COMPARTMENT_ENTITY, BlockCompartment::initialize,
                    CompartmentCloneable::initialize), itemStack -> itemStack.is(Items.JUKEBOX));

    public static void init() {
    }
}