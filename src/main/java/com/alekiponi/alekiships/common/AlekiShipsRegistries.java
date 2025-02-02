package com.alekiponi.alekiships.common;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.common.compartment.CompartmentPlaceableSerializer;
import com.alekiponi.alekiships.common.compartment.DirectCompartmentType;
import com.alekiponi.alekiships.common.entity.EntityInput;
import com.alekiponi.alekiships.common.entity.SloopConstructionState;
import com.alekiponi.alekiships.common.entity.compartment.vanilla.ChestCompartmentData;
import com.alekiponi.alekiships.common.entity.vehicle.ConstructionInput;
import com.alekiponi.alekiships.common.entity.vehicle.RowboatVariant;
import com.alekiponi.alekiships.common.entity.vehicle.SloopVariant;
import com.alekiponi.alekiships.common.recipe.entity.EntityResultSerializer;
import com.alekiponi.alekiships.util.DynamicBoatMaterial;
import com.alekiponi.alekiships.wind.WindModelSerializer;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public class AlekiShipsRegistries {
    public static final ResourceKey<Registry<DirectCompartmentType<?>>> DIRECT_COMPARTMENT_TYPE = createRegistryKey(
            "direct_compartment_type");
    public static final ResourceKey<Registry<EntityInput>> ENTITY_INPUT = createRegistryKey("entity_input");
    public static final ResourceKey<Registry<ConstructionInput<SloopConstructionState.SloopConstructionStage>>> CONSTRUCTION_SLOOP_INPUT = createRegistryKey(
            "construction/sloop");
    public static final ResourceKey<Registry<CompartmentPlaceableSerializer<?>>> COMPARTMENT_PLACEABLE_SERIALIZER = createRegistryKey(
            "compartment_type_serializer");
    public static final ResourceKey<Registry<ChestCompartmentData.ChestType>> CHEST_COMPARTMENT_TYPES = createRegistryKey(
            "chest_compartment_type");
    public static final ResourceKey<Registry<WindModelSerializer<?>>> WIND_MODEL_SERIALIZERS = createRegistryKey(
            "wind_model_serializers");
    public static final ResourceKey<Registry<DynamicBoatMaterial>> BOAT_MATERIAL = createRegistryKey("boat_material");
    public static final ResourceKey<Registry<EntityResultSerializer<?>>> ENTITY_RESULT_SERIALIZER = createRegistryKey(
            "entity_result_serializer");
    public static final ResourceKey<Registry<RowboatVariant>> ROWBOAT_VARIANT = createRegistryKey(
            "boat_variant/rowboat");
    public static final ResourceKey<Registry<SloopVariant>> SLOOP_VARIANT = createRegistryKey("boat_variant/sloop");

    private static <T> ResourceKey<Registry<T>> createRegistryKey(final String name) {
        return ResourceKey.createRegistryKey(AlekiShips.location(name));
    }
}
