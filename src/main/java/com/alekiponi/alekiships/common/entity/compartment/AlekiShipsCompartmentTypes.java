package com.alekiponi.alekiships.common.entity.compartment;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.common.AlekiShipsRegistries;
import com.alekiponi.alekiships.common.entity.AlekiShipsEntities;
import com.alekiponi.alekiships.common.entity.compartment.vanilla.*;
import com.alekiponi.alekiships.common.entity.compartment.vanilla.crafting.*;

import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Locale;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public final class AlekiShipsCompartmentTypes {

    public static final DeferredRegister<CompartmentType<?>> COMPARTMENT_TYPES = DeferredRegister.create(
            AlekiShipsRegistries.COMPARTMENT_TYPE, AlekiShips.MOD_ID);

    public static final DeferredHolder<CompartmentType<?>, CompartmentType<BlockCompartmentEntity>> BLOCK_COMPARTMENT = register(
            "block", () -> CompartmentType.of(AlekiShipsEntities.BLOCK_COMPARTMENT_ENTITY,
                    BlockCompartment.create(BlockCompartmentEntity::new)));

    public static final DeferredHolder<CompartmentType<?>, CompartmentType<BarrelCompartmentEntity>> BARREL_COMPARTMENT = register(
            "barrel", () -> CompartmentType.of(AlekiShipsEntities.BARREL_COMPARTMENT_ENTITY,
                    BlockCompartment.<BarrelCompartmentEntity>create(BarrelCompartmentEntity::new)
                            .postInit(CompartmentCloneable::initialize)));

    public static final DeferredHolder<CompartmentType<?>, CompartmentType<ChestCompartmentEntity>> CHEST_COMPARTMENT = register(
            "chest",
            () -> CompartmentType.of(AlekiShipsEntities.CHEST_COMPARTMENT_ENTITY, ChestCompartmentEntity::create));

    public static final DeferredHolder<CompartmentType<?>, CompartmentType<EnderChestCompartmentEntity>> ENDER_CHEST_COMPARTMENT = register(
            "ender_chest", () -> CompartmentType.simple(AlekiShipsEntities.ENDER_CHEST_COMPARTMENT_ENTITY));

    public static final DeferredHolder<CompartmentType<?>, CompartmentType<ShulkerBoxCompartmentEntity>> SHULKER_BOX_COMPARTMENT = register(
            "shulker_box", () -> CompartmentType.of(AlekiShipsEntities.SHULKER_BOX_COMPARTMENT_ENTITY,
                    ShulkerBoxCompartmentEntity::create));

    public static final DeferredHolder<CompartmentType<?>, CompartmentType<FurnaceCompartmentEntity>> FURNACE_COMPARTMENT = register(
            "furnace", () -> CompartmentType.of(AlekiShipsEntities.FURNACE_COMPARTMENT_ENTITY,
                    AbstractFurnaceCompartmentEntity.create(FurnaceCompartmentEntity::new)));

    public static final DeferredHolder<CompartmentType<?>, CompartmentType<BlastFurnaceCompartmentEntity>> BLAST_FURNACE_COMPARTMENT = register(
            "blast_furnace", () -> CompartmentType.of(AlekiShipsEntities.BLAST_FURNACE_COMPARTMENT_ENTITY,
                    AbstractFurnaceCompartmentEntity.create(BlastFurnaceCompartmentEntity::new)));

    public static final DeferredHolder<CompartmentType<?>, CompartmentType<SmokerCompartmentEntity>> SMOKER_COMPARTMENT = register(
            "smoker", () -> CompartmentType.of(AlekiShipsEntities.SMOKER_COMPARTMENT_ENTITY,
                    AbstractFurnaceCompartmentEntity.create(SmokerCompartmentEntity::new)));

    public static final DeferredHolder<CompartmentType<?>, CompartmentType<BrewingStandCompartmentEntity>> BREWING_STAND_COMPARTMENT = register(
            "brewing_stand", () -> CompartmentType.of(AlekiShipsEntities.BREWING_STAND_COMPARTMENT_ENTITY,
                    BlockCompartment.<BrewingStandCompartmentEntity>create(BrewingStandCompartmentEntity::new)
                            .postInit(CompartmentCloneable::initialize)));

    public static final DeferredHolder<CompartmentType<?>, CompartmentType<CraftingTableCompartment>> CRAFTING_TABLE_COMPARTMENT = register(
            "crafting_table", () -> CompartmentType.of(AlekiShipsEntities.WORKBENCH_COMPARTMENT_ENTITY,
                    BlockCompartment.create(CraftingTableCompartment::new)));

    public static final DeferredHolder<CompartmentType<?>, CompartmentType<StonecutterCompartmentEntity>> STONECUTTER_COMPARTMENT = register(
            "stonecutter", () -> CompartmentType.of(AlekiShipsEntities.STONECUTTER_COMPARTMENT_ENTITY,
                    BlockCompartment.create(StonecutterCompartmentEntity::new)));

    public static final DeferredHolder<CompartmentType<?>, CompartmentType<CartographyTableCompartmentEntity>> CARTOGRAPHY_TABLE_COMPARTMENT = register(
            "cartography_table", () -> CompartmentType.of(AlekiShipsEntities.CARTOGRAPHY_TABLE_COMPARTMENT_ENTITY,
                    BlockCompartment.create(CartographyTableCompartmentEntity::new)));

    public static final DeferredHolder<CompartmentType<?>, CompartmentType<SmithingTableCompartmentEntity>> SMITHING_TABLE_COMPARTMENT = register(
            "smithing_table", () -> CompartmentType.of(AlekiShipsEntities.SMITHING_TABLE_COMPARTMENT_ENTITY,
                    BlockCompartment.create(SmithingTableCompartmentEntity::new)));

    public static final DeferredHolder<CompartmentType<?>, CompartmentType<GrindstoneCompartmentEntity>> GRINDSTONE_COMPARTMENT = register(
            "grindstone", () -> CompartmentType.of(AlekiShipsEntities.GRINDSTONE_COMPARTMENT_ENTITY,
                    BlockCompartment.create(GrindstoneCompartmentEntity::new)));

    public static final DeferredHolder<CompartmentType<?>, CompartmentType<LoomCompartmentEntity>> LOOM_COMPARTMENT = register(
            "loom", () -> CompartmentType.of(AlekiShipsEntities.LOOM_COMPARTMENT_ENTITY,
                    BlockCompartment.create(LoomCompartmentEntity::new)));

    public static final DeferredHolder<CompartmentType<?>, CompartmentType<NoteBlockCompartmentEntity>> NOTE_BLOCK_COMPARTMENT = register(
            "note_block", () -> CompartmentType.of(AlekiShipsEntities.NOTE_BLOCK_COMPARTMENT_ENTITY,
                    BlockCompartment.create(NoteBlockCompartmentEntity::new)));

    public static final DeferredHolder<CompartmentType<?>, CompartmentType<JukeboxCompartmentEntity>> JUKEBOX_COMPARTMENT = register(
            "jukebox", () -> CompartmentType.of(AlekiShipsEntities.JUKEBOX_COMPARTMENT_ENTITY,
                    BlockCompartment.<JukeboxCompartmentEntity>create(JukeboxCompartmentEntity::new)
                            .postInit(CompartmentCloneable::initialize)));

    @SuppressWarnings("SameParameterValue")
    private static <E extends AbstractCompartmentEntity> DeferredHolder<CompartmentType<?>, CompartmentType<E>> register(
            final String name, final Supplier<CompartmentType<E>> compartmentType) {
        final String id = name.toLowerCase(Locale.ROOT);
        return COMPARTMENT_TYPES.register(id, compartmentType);
    }
}