package com.alekiponi.alekiships.common.compartment;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.common.AlekiShipsRegistries;
import com.alekiponi.alekiships.common.entity.AlekiShipsEntities;
import com.alekiponi.alekiships.common.entity.compartment.AbstractCompartmentEntity;
import com.alekiponi.alekiships.common.entity.compartment.BlockCompartment;
import com.alekiponi.alekiships.common.entity.compartment.CompartmentCloneable;
import com.alekiponi.alekiships.common.entity.compartment.vanilla.*;
import com.alekiponi.alekiships.common.entity.compartment.vanilla.crafting.*;

import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Locale;
import java.util.function.Supplier;

public final class AlekiShipsDirectCompartmentTypes {

    public static final DeferredRegister<DirectCompartmentType<?>> DIRECT_COMPARTMENTS = DeferredRegister.create(
            AlekiShipsRegistries.DIRECT_COMPARTMENT_TYPE, AlekiShips.MOD_ID);

    public static final DeferredHolder<DirectCompartmentType<?>, DirectCompartmentType<EnderChestCompartmentEntity>> ENDER_CHEST_COMPARTMENT = register(
            "ender_chest", () -> DirectCompartmentType.simple(AlekiShipsEntities.ENDER_CHEST_COMPARTMENT_ENTITY));

    public static final DeferredHolder<DirectCompartmentType<?>, DirectCompartmentType<FurnaceCompartmentEntity>> FURNACE_COMPARTMENT = register(
            "furnace", () -> DirectCompartmentType.of(AlekiShipsEntities.FURNACE_COMPARTMENT_ENTITY,
                    AbstractFurnaceCompartmentEntity.create(FurnaceCompartmentEntity::new)));

    public static final DeferredHolder<DirectCompartmentType<?>, DirectCompartmentType<BlastFurnaceCompartmentEntity>> BLAST_FURNACE_COMPARTMENT = register(
            "blast_furnace", () -> DirectCompartmentType.of(AlekiShipsEntities.BLAST_FURNACE_COMPARTMENT_ENTITY,
                    AbstractFurnaceCompartmentEntity.create(BlastFurnaceCompartmentEntity::new)));

    public static final DeferredHolder<DirectCompartmentType<?>, DirectCompartmentType<SmokerCompartmentEntity>> SMOKER_COMPARTMENT = register(
            "smoker", () -> DirectCompartmentType.of(AlekiShipsEntities.SMOKER_COMPARTMENT_ENTITY,
                    AbstractFurnaceCompartmentEntity.create(SmokerCompartmentEntity::new)));

    public static final DeferredHolder<DirectCompartmentType<?>, DirectCompartmentType<BrewingStandCompartmentEntity>> BREWING_STAND_COMPARTMENT = register(
            "brewing_stand", () -> DirectCompartmentType.of(AlekiShipsEntities.BREWING_STAND_COMPARTMENT_ENTITY,
                    BlockCompartment.<BrewingStandCompartmentEntity>create(BrewingStandCompartmentEntity::new)
                            .postInit(CompartmentCloneable::initialize)));

    public static final DeferredHolder<DirectCompartmentType<?>, DirectCompartmentType<CraftingTableCompartment>> CRAFTING_TABLE_COMPARTMENT = register(
            "crafting_table", () -> DirectCompartmentType.of(AlekiShipsEntities.WORKBENCH_COMPARTMENT_ENTITY,
                    BlockCompartment.create(CraftingTableCompartment::new)));

    public static final DeferredHolder<DirectCompartmentType<?>, DirectCompartmentType<StonecutterCompartmentEntity>> STONECUTTER_COMPARTMENT = register(
            "stonecutter", () -> DirectCompartmentType.of(AlekiShipsEntities.STONECUTTER_COMPARTMENT_ENTITY,
                    BlockCompartment.create(StonecutterCompartmentEntity::new)));

    public static final DeferredHolder<DirectCompartmentType<?>, DirectCompartmentType<CartographyTableCompartmentEntity>> CARTOGRAPHY_TABLE_COMPARTMENT = register(
            "cartography_table", () -> DirectCompartmentType.of(AlekiShipsEntities.CARTOGRAPHY_TABLE_COMPARTMENT_ENTITY,
                    BlockCompartment.create(CartographyTableCompartmentEntity::new)));

    public static final DeferredHolder<DirectCompartmentType<?>, DirectCompartmentType<SmithingTableCompartmentEntity>> SMITHING_TABLE_COMPARTMENT = register(
            "smithing_table", () -> DirectCompartmentType.of(AlekiShipsEntities.SMITHING_TABLE_COMPARTMENT_ENTITY,
                    BlockCompartment.create(SmithingTableCompartmentEntity::new)));

    public static final DeferredHolder<DirectCompartmentType<?>, DirectCompartmentType<GrindstoneCompartmentEntity>> GRINDSTONE_COMPARTMENT = register(
            "grindstone", () -> DirectCompartmentType.of(AlekiShipsEntities.GRINDSTONE_COMPARTMENT_ENTITY,
                    BlockCompartment.create(GrindstoneCompartmentEntity::new)));

    public static final DeferredHolder<DirectCompartmentType<?>, DirectCompartmentType<LoomCompartmentEntity>> LOOM_COMPARTMENT = register(
            "loom", () -> DirectCompartmentType.of(AlekiShipsEntities.LOOM_COMPARTMENT_ENTITY,
                    BlockCompartment.create(LoomCompartmentEntity::new)));

    public static final DeferredHolder<DirectCompartmentType<?>, DirectCompartmentType<NoteBlockCompartmentEntity>> NOTE_BLOCK_COMPARTMENT = register(
            "note_block", () -> DirectCompartmentType.of(AlekiShipsEntities.NOTE_BLOCK_COMPARTMENT_ENTITY,
                    BlockCompartment.create(NoteBlockCompartmentEntity::new)));

    public static final DeferredHolder<DirectCompartmentType<?>, DirectCompartmentType<JukeboxCompartmentEntity>> JUKEBOX_COMPARTMENT = register(
            "jukebox", () -> DirectCompartmentType.of(AlekiShipsEntities.JUKEBOX_COMPARTMENT_ENTITY,
                    BlockCompartment.<JukeboxCompartmentEntity>create(JukeboxCompartmentEntity::new)
                            .postInit(CompartmentCloneable::initialize)));

    @SuppressWarnings("SameParameterValue")
    private static <E extends AbstractCompartmentEntity> DeferredHolder<DirectCompartmentType<?>, DirectCompartmentType<E>> register(
            final String name, final Supplier<DirectCompartmentType<E>> compartmentType) {
        final String id = name.toLowerCase(Locale.ROOT);
        return DIRECT_COMPARTMENTS.register(id, compartmentType);
    }
}