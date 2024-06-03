package com.alekiponi.alekiships.common.entity;

import com.alekiponi.alekiships.common.entity.vehicle.RowboatEntity;
import com.alekiponi.alekiships.common.entity.vehicle.SloopEntity;
import com.alekiponi.alekiships.common.entity.vehicle.SloopUnderConstructionEntity;
import com.alekiponi.alekiships.common.entity.vehiclehelper.*;
import com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.AbstractCompartmentEntity;
import com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.BlockCompartmentEntity;
import com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.EmptyCompartmentEntity;
import com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.vanilla.*;
import com.alekiponi.alekiships.util.CommonHelper;
import com.alekiponi.alekiships.util.AlekiShipsTags;
import com.alekiponi.alekiships.util.VanillaWood;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.EnumMap;
import java.util.Locale;

import static com.alekiponi.alekiships.AlekiShips.MOD_ID;

public final class AlekiShipsEntities {

    private static final int LARGE_VEHICLE_TRACKING = 20;
    private static final int VEHICLE_HELPER_TRACKING = LARGE_VEHICLE_TRACKING + 1;
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(
            ForgeRegistries.ENTITY_TYPES, MOD_ID);
    public static final EnumMap<VanillaWood, RegistryObject<EntityType<RowboatEntity>>> ROWBOATS = CommonHelper.mapOfKeys(
            VanillaWood.class, vanillaWood -> registerRowboat(vanillaWood,
                    EntityType.Builder.of((entityType, level) -> new RowboatEntity(entityType, level, vanillaWood),
                            MobCategory.MISC)));

    public static final EnumMap<VanillaWood, RegistryObject<EntityType<SloopEntity>>> SLOOPS = CommonHelper.mapOfKeys(
            VanillaWood.class, vanillaWood -> registerSloop(vanillaWood,
                    EntityType.Builder.of((entityType, level) -> new SloopEntity(entityType, level, vanillaWood),
                            MobCategory.MISC)));

    public static final EnumMap<VanillaWood, RegistryObject<EntityType<SloopUnderConstructionEntity>>> SLOOPS_UNDER_CONSTRUCTION = CommonHelper.mapOfKeys(
            VanillaWood.class, vanillaWood -> registerSloopConstruction(vanillaWood, EntityType.Builder.of(
                    (entityType, level) -> new SloopUnderConstructionEntity(entityType, level, vanillaWood),
                    MobCategory.MISC)));


    public static final RegistryObject<CompartmentType<EmptyCompartmentEntity>> EMPTY_COMPARTMENT_ENTITY = registerCompartment(
            "compartment_empty", CompartmentType.Builder.createBasic(EmptyCompartmentEntity::new));

    public static final RegistryObject<CompartmentType<BlockCompartmentEntity>> BLOCK_COMPARTMENT_ENTITY = registerCompartment(
            "compartment_block", CompartmentType.Builder.of(BlockCompartmentEntity::new, BlockCompartmentEntity::new));

    public static final RegistryObject<CompartmentType<BarrelCompartmentEntity>> BARREL_COMPARTMENT_ENTITY = CompartmentType.register(
            registerCompartment("compartment_barrel",
                    CompartmentType.Builder.of(BarrelCompartmentEntity::new, BarrelCompartmentEntity::new)),
            itemStack -> itemStack.is(Items.BARREL));

    public static final RegistryObject<CompartmentType<ChestCompartmentEntity>> CHEST_COMPARTMENT_ENTITY = CompartmentType.register(
            registerCompartment("compartment_chest",
                    CompartmentType.Builder.of(ChestCompartmentEntity::new, ChestCompartmentEntity::new)),
            itemStack -> itemStack.is(Items.CHEST));

    public static final RegistryObject<CompartmentType<EnderChestCompartmentEntity>> ENDER_CHEST_COMPARTMENT_ENTITY = CompartmentType.register(
            registerCompartment("compartment_ender_chest",
                    CompartmentType.Builder.createBasic(EnderChestCompartmentEntity::new)),
            itemStack -> itemStack.is(Items.ENDER_CHEST));

    public static final RegistryObject<CompartmentType<ShulkerBoxCompartmentEntity>> SHULKER_BOX_COMPARTMENT_ENTITY = CompartmentType.register(
            registerCompartment("compartment_shulker_box",
                    CompartmentType.Builder.of(ShulkerBoxCompartmentEntity::new, ShulkerBoxCompartmentEntity::new)),
            itemStack -> itemStack.is(AlekiShipsTags.Items.SHULKER_BOXES));

    public static final RegistryObject<CompartmentType<FurnaceCompartmentEntity>> FURNACE_COMPARTMENT_ENTITY = CompartmentType.register(
            registerCompartment("compartment_furnace",
                    CompartmentType.Builder.of(FurnaceCompartmentEntity::new, FurnaceCompartmentEntity::new)),
            itemStack -> itemStack.is(Items.FURNACE));

    public static final RegistryObject<CompartmentType<BlastFurnaceCompartmentEntity>> BLAST_FURNACE_COMPARTMENT_ENTITY = CompartmentType.register(
            registerCompartment("compartment_blast_furnace",
                    CompartmentType.Builder.of(BlastFurnaceCompartmentEntity::new, BlastFurnaceCompartmentEntity::new)),
            itemStack -> itemStack.is(Items.BLAST_FURNACE));

    public static final RegistryObject<CompartmentType<SmokerCompartmentEntity>> SMOKER_COMPARTMENT_ENTITY = CompartmentType.register(
            registerCompartment("compartment_smoker",
                    CompartmentType.Builder.of(SmokerCompartmentEntity::new, SmokerCompartmentEntity::new)),
            itemStack -> itemStack.is(Items.SMOKER));

    public static final RegistryObject<CompartmentType<BrewingStandCompartmentEntity>> BREWING_STAND_COMPARTMENT_ENTITY = CompartmentType.register(
            registerCompartment("compartment_brewing_stand",
                    CompartmentType.Builder.of(BrewingStandCompartmentEntity::new, BrewingStandCompartmentEntity::new)),
            itemStack -> itemStack.is(Items.BREWING_STAND));

    public static final RegistryObject<CompartmentType<CraftingTableCompartment>> WORKBENCH_COMPARTMENT_ENTITY = CompartmentType.register(
            registerCompartment("compartment_crafting_table",
                    CompartmentType.Builder.of(CraftingTableCompartment::new, CraftingTableCompartment::new)),
            itemStack -> itemStack.is(AlekiShipsTags.Items.CRAFTING_TABLES));

    public static final RegistryObject<CompartmentType<StonecutterCompartmentEntity>> STONECUTTER_COMPARTMENT_ENTITY = CompartmentType.register(
            registerCompartment("compartment_stonecutter",
                    CompartmentType.Builder.of(StonecutterCompartmentEntity::new, StonecutterCompartmentEntity::new)),
            itemStack -> itemStack.is(Items.STONECUTTER));

    public static final RegistryObject<CompartmentType<CartographyTableCompartmentEntity>> CARTOGRAPHY_TABLE_COMPARTMENT_ENTITY = CompartmentType.register(
            registerCompartment("compartment_cartography_table",
                    CompartmentType.Builder.of(CartographyTableCompartmentEntity::new,
                            CartographyTableCompartmentEntity::new)),
            itemStack -> itemStack.is(Items.CARTOGRAPHY_TABLE));

    public static final RegistryObject<CompartmentType<SmithingTableCompartmentEntity>> SMITHING_TABLE_COMPARTMENT_ENTITY = CompartmentType.register(
            registerCompartment("compartment_smithing_table",
                    CompartmentType.Builder.of(SmithingTableCompartmentEntity::new,
                            SmithingTableCompartmentEntity::new)), itemStack -> itemStack.is(Items.SMITHING_TABLE));

    public static final RegistryObject<CompartmentType<GrindstoneCompartmentEntity>> GRINDSTONE_COMPARTMENT_ENTITY = CompartmentType.register(
            registerCompartment("compartment_grindstone",
                    CompartmentType.Builder.of(GrindstoneCompartmentEntity::new, GrindstoneCompartmentEntity::new)),
            itemStack -> itemStack.is(Items.GRINDSTONE));

    public static final RegistryObject<CompartmentType<LoomCompartmentEntity>> LOOM_COMPARTMENT_ENTITY = CompartmentType.register(
            registerCompartment("compartment_loom",
                    CompartmentType.Builder.of(LoomCompartmentEntity::new, LoomCompartmentEntity::new)),
            itemStack -> itemStack.is(Items.LOOM));

    public static final RegistryObject<CompartmentType<NoteBlockCompartmentEntity>> NOTE_BLOCK_COMPARTMENT_ENTITY = CompartmentType.register(
            registerCompartment("compartment_note_block",
                    CompartmentType.Builder.of(NoteBlockCompartmentEntity::new, NoteBlockCompartmentEntity::new)),
            itemStack -> itemStack.is(Items.NOTE_BLOCK));

    public static final RegistryObject<CompartmentType<JukeboxCompartmentEntity>> JUKEBOX_COMPARTMENT_ENTITY = CompartmentType.register(
            registerCompartment("compartment_jukebox",
                    CompartmentType.Builder.of(JukeboxCompartmentEntity::new, JukeboxCompartmentEntity::new)),
            itemStack -> itemStack.is(Items.JUKEBOX));

    public static final RegistryObject<EntityType<VehiclePart>> VEHICLE_PART = register("vehicle_part",
            EntityType.Builder.of(VehiclePart::new, MobCategory.MISC).sized(0, 0)
                    .setTrackingRange(VEHICLE_HELPER_TRACKING).noSummon().fireImmune());

    public static final RegistryObject<EntityType<CleatEntity>> VEHICLE_CLEAT_ENTITY = register("vehicle_cleat",
            EntityType.Builder.of(CleatEntity::new, MobCategory.MISC).sized(0.4F, 0.2F)
                    .setTrackingRange(VEHICLE_HELPER_TRACKING).noSummon().fireImmune());

    public static final RegistryObject<EntityType<ColliderEntity>> VEHICLE_COLLIDER_ENTITY = register(
            "vehicle_collider",
            EntityType.Builder.of(ColliderEntity::new, MobCategory.MISC).sized(1, 1).noSummon().fireImmune());

    public static final RegistryObject<EntityType<SailSwitchEntity>> SAIL_SWITCH_ENTITY = register(
            "vehicle_switch_sail",
            EntityType.Builder.of(SailSwitchEntity::new, MobCategory.MISC).sized(0.8F, 0.8F).noSummon().fireImmune());

    public static final RegistryObject<EntityType<AnchorEntity>> ANCHOR_ENTITY = register("vehicle_anchor",
            EntityType.Builder.of(AnchorEntity::new, MobCategory.MISC).sized(1, 1)
                    .clientTrackingRange(VEHICLE_HELPER_TRACKING).noSummon().fireImmune());

    public static final RegistryObject<EntityType<ConstructionEntity>> CONSTRUCTION_ENTITY = register(
            "vehicle_construction", EntityType.Builder.of(ConstructionEntity::new, MobCategory.MISC).sized(1, 1)
                    .clientTrackingRange(VEHICLE_HELPER_TRACKING).noSummon().fireImmune());

    public static final RegistryObject<EntityType<WindlassSwitchEntity>> WINDLASS_SWITCH_ENTITY = register(
            "vehicle_switch_windlass",
            EntityType.Builder.of(WindlassSwitchEntity::new, MobCategory.MISC).sized(0.8F, 0.8F)
                    .setTrackingRange(VEHICLE_HELPER_TRACKING).noSummon().fireImmune());

    public static final RegistryObject<EntityType<CannonballEntity>> CANNONBALL_ENTITY = register("cannonball",
            EntityType.Builder.<CannonballEntity>of(CannonballEntity::new, MobCategory.MISC).sized(0.5F, 0.5F)
                    .setTrackingRange(32).clientTrackingRange(32).setShouldReceiveVelocityUpdates(true).fireImmune());

    public static final RegistryObject<EntityType<CannonEntity>> CANNON_ENTITY = register("cannon",
            EntityType.Builder.of(CannonEntity::new, MobCategory.MISC).sized(0.8F, 0.8F).fireImmune());

    public static final RegistryObject<EntityType<MastEntity>> MAST_ENTITY = register("vehicle_mast",
            EntityType.Builder.of(MastEntity::new, MobCategory.MISC).sized(0.3F, 8.79375f)
                    .setTrackingRange(VEHICLE_HELPER_TRACKING).noSummon().fireImmune());

    private static <E extends RowboatEntity> RegistryObject<EntityType<E>> registerRowboat(
            final VanillaWood vanillaWood, final EntityType.Builder<E> builder) {
        return register("rowboat/" + vanillaWood.getSerializedName(), builder.sized(1.875F, 0.625F));
    }

    private static <E extends SloopEntity> RegistryObject<EntityType<E>> registerSloop(final VanillaWood vanillaWood,
            final EntityType.Builder<E> builder) {
        return register("sloop/" + vanillaWood.getSerializedName(),
                builder.sized(3F, 0.75F).setTrackingRange(LARGE_VEHICLE_TRACKING));
    }

    private static <E extends SloopUnderConstructionEntity> RegistryObject<EntityType<E>> registerSloopConstruction(
            final VanillaWood vanillaWood, final EntityType.Builder<E> builder) {
        return register("sloop_construction/" + vanillaWood.getSerializedName(),
                builder.sized(4F, 0.75F).setTrackingRange(LARGE_VEHICLE_TRACKING).fireImmune().noSummon());
    }

    /**
     * Registers a compartment entity
     */
    private static <E extends AbstractCompartmentEntity> RegistryObject<CompartmentType<E>> registerCompartment(
            final String name, final CompartmentType.Builder<E> builder) {
        return register(name, builder.sized(0.6F, 0.7F).fireImmune().noSummon(), true);
    }

    /**
     * Base method for registering a compartment entity
     */
    @SuppressWarnings("SameParameterValue")
    private static <E extends AbstractCompartmentEntity> RegistryObject<CompartmentType<E>> register(final String name,
            final CompartmentType.Builder<E> builder, final boolean serialize) {
        final String id = name.toLowerCase(Locale.ROOT);
        return ENTITY_TYPES.register(id, () -> {
            if (!serialize) builder.noSave();
            return builder.build(MOD_ID + ":" + id);
        });
    }

    private static <E extends Entity> RegistryObject<EntityType<E>> register(final String name,
            final EntityType.Builder<E> builder) {
        return register(name, builder, true);
    }

    @SuppressWarnings("SameParameterValue")
    private static <E extends Entity> RegistryObject<EntityType<E>> register(final String name,
            final EntityType.Builder<E> builder, final boolean serialize) {
        final String id = name.toLowerCase(Locale.ROOT);
        return ENTITY_TYPES.register(id, () -> {
            if (!serialize) builder.noSave();
            return builder.build(MOD_ID + ":" + id);
        });
    }
}