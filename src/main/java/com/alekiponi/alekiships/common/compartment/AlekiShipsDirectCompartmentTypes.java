package com.alekiponi.alekiships.common.compartment;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.common.AlekiShipsRegistries;
import com.alekiponi.alekiships.common.entity.AlekiShipsEntities;
import com.alekiponi.alekiships.common.entity.compartment.AbstractCompartmentEntity;
import com.alekiponi.alekiships.common.entity.compartment.BlockCompartment;
import com.alekiponi.alekiships.common.entity.compartment.CompartmentCloneable;
import com.alekiponi.alekiships.common.entity.compartment.SimpleBlockMenuCompartmentEntity;
import com.alekiponi.alekiships.common.entity.compartment.vanilla.*;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.GrindstoneBlock;
import net.minecraft.world.level.block.state.properties.AttachFace;

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
            "furnace", () -> DirectCompartmentType.postInit(AlekiShipsEntities.FURNACE_COMPARTMENT_ENTITY,
                    BlockCompartment.dynamicFactory(FurnaceCompartmentEntity::new), CompartmentCloneable::initialize));

    public static final DeferredHolder<DirectCompartmentType<?>, DirectCompartmentType<BlastFurnaceCompartmentEntity>> BLAST_FURNACE_COMPARTMENT = register(
            "blast_furnace", () -> DirectCompartmentType.postInit(AlekiShipsEntities.BLAST_FURNACE_COMPARTMENT_ENTITY,
                    BlockCompartment.dynamicFactory(BlastFurnaceCompartmentEntity::new),
                    CompartmentCloneable::initialize));

    public static final DeferredHolder<DirectCompartmentType<?>, DirectCompartmentType<SmokerCompartmentEntity>> SMOKER_COMPARTMENT = register(
            "smoker", () -> DirectCompartmentType.postInit(AlekiShipsEntities.SMOKER_COMPARTMENT_ENTITY,
                    BlockCompartment.dynamicFactory(SmokerCompartmentEntity::new), CompartmentCloneable::initialize));

    public static final DeferredHolder<DirectCompartmentType<?>, DirectCompartmentType<BrewingStandCompartmentEntity>> BREWING_STAND_COMPARTMENT = register(
            "brewing_stand", () -> DirectCompartmentType.postInit(AlekiShipsEntities.BREWING_STAND_COMPARTMENT_ENTITY,
                    BlockCompartment.staticFactory(BrewingStandCompartmentEntity::new, Blocks.BREWING_STAND),
                    CompartmentCloneable::initialize));

    public static final DeferredHolder<DirectCompartmentType<?>, DirectCompartmentType<SimpleBlockMenuCompartmentEntity>> CRAFTING_TABLE_COMPARTMENT = register(
            "crafting_table", () -> DirectCompartmentType.of(AlekiShipsEntities.WORKBENCH_COMPARTMENT_ENTITY,
                    BlockCompartment.dynamicFactory(SimpleBlockMenuCompartmentEntity.directCompartmentFactory(
                            SimpleBlockMenuCompartmentEntity.CRAFTING_TABLE))));

    public static final DeferredHolder<DirectCompartmentType<?>, DirectCompartmentType<SimpleBlockMenuCompartmentEntity>> STONECUTTER_COMPARTMENT = register(
            "stonecutter", () -> DirectCompartmentType.of(AlekiShipsEntities.STONECUTTER_COMPARTMENT_ENTITY,
                    BlockCompartment.staticFactory(SimpleBlockMenuCompartmentEntity.directCompartmentFactory(
                            SimpleBlockMenuCompartmentEntity.STONECUTTER), Blocks.STONECUTTER)));

    public static final DeferredHolder<DirectCompartmentType<?>, DirectCompartmentType<SimpleBlockMenuCompartmentEntity>> CARTOGRAPHY_TABLE_COMPARTMENT = register(
            "cartography_table", () -> DirectCompartmentType.of(AlekiShipsEntities.CARTOGRAPHY_TABLE_COMPARTMENT_ENTITY,
                    BlockCompartment.staticFactory(SimpleBlockMenuCompartmentEntity.directCompartmentFactory(
                            SimpleBlockMenuCompartmentEntity.CARTOGRAPHY_TABLE), Blocks.CARTOGRAPHY_TABLE)));

    public static final DeferredHolder<DirectCompartmentType<?>, DirectCompartmentType<SimpleBlockMenuCompartmentEntity>> SMITHING_TABLE_COMPARTMENT = register(
            "smithing_table", () -> DirectCompartmentType.of(AlekiShipsEntities.SMITHING_TABLE_COMPARTMENT_ENTITY,
                    BlockCompartment.staticFactory(SimpleBlockMenuCompartmentEntity.directCompartmentFactory(
                            SimpleBlockMenuCompartmentEntity.SMITHING_TABLE), Blocks.SMITHING_TABLE)));

    public static final DeferredHolder<DirectCompartmentType<?>, DirectCompartmentType<SimpleBlockMenuCompartmentEntity>> GRINDSTONE_COMPARTMENT = register(
            "grindstone", () -> DirectCompartmentType.of(AlekiShipsEntities.GRINDSTONE_COMPARTMENT_ENTITY,
                    BlockCompartment.staticFactory(SimpleBlockMenuCompartmentEntity.directCompartmentFactory(
                                    SimpleBlockMenuCompartmentEntity.GRINDSTONE),
                            Blocks.GRINDSTONE.defaultBlockState().setValue(GrindstoneBlock.FACE, AttachFace.FLOOR))));

    public static final DeferredHolder<DirectCompartmentType<?>, DirectCompartmentType<SimpleBlockMenuCompartmentEntity>> LOOM_COMPARTMENT = register(
            "loom", () -> DirectCompartmentType.of(AlekiShipsEntities.LOOM_COMPARTMENT_ENTITY,
                    BlockCompartment.staticFactory(SimpleBlockMenuCompartmentEntity.directCompartmentFactory(
                            SimpleBlockMenuCompartmentEntity.LOOM), Blocks.LOOM)));

    public static final DeferredHolder<DirectCompartmentType<?>, DirectCompartmentType<NoteBlockCompartmentEntity>> NOTE_BLOCK_COMPARTMENT = register(
            "note_block", () -> DirectCompartmentType.of(AlekiShipsEntities.NOTE_BLOCK_COMPARTMENT_ENTITY,
                    BlockCompartment.staticFactory(NoteBlockCompartmentEntity::new, Blocks.NOTE_BLOCK)));

    public static final DeferredHolder<DirectCompartmentType<?>, DirectCompartmentType<JukeboxCompartmentEntity>> JUKEBOX_COMPARTMENT = register(
            "jukebox", () -> DirectCompartmentType.postInit(AlekiShipsEntities.JUKEBOX_COMPARTMENT_ENTITY,
                    BlockCompartment.staticFactory(JukeboxCompartmentEntity::new, Blocks.JUKEBOX),
                    CompartmentCloneable::initialize));

    @SuppressWarnings("SameParameterValue")
    private static <E extends AbstractCompartmentEntity> DeferredHolder<DirectCompartmentType<?>, DirectCompartmentType<E>> register(
            final String name, final Supplier<DirectCompartmentType<E>> compartmentType) {
        final String id = name.toLowerCase(Locale.ROOT);
        return DIRECT_COMPARTMENTS.register(id, compartmentType);
    }
}