package com.alekiponi.alekiships.common;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.common.compartment.CompartmentPlaceableSerializer;
import com.alekiponi.alekiships.common.compartment.DirectCompartmentType;
import com.alekiponi.alekiships.common.entity.EntityInput;
import com.alekiponi.alekiships.common.entity.SloopConstructionState;
import com.alekiponi.alekiships.common.entity.compartment.vanilla.ChestCompartmentData;
import com.alekiponi.alekiships.common.recipe.entity.EntityResultSerializer;
import com.alekiponi.alekiships.util.DynamicBoatMaterial;
import com.alekiponi.alekiships.wind.WindModelSerializer;

import net.minecraft.core.Registry;

import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

public final class AlekiShipsBuiltInRegistries {
    public static final Registry<DirectCompartmentType<?>> DIRECT_COMPARTMENT_TYPES = new RegistryBuilder<>(
            AlekiShipsRegistries.DIRECT_COMPARTMENT_TYPE).sync(true).create();
    public static final Registry<CompartmentPlaceableSerializer<?>> COMPARTMENT_TYPE_SERIALIZERS = new RegistryBuilder<>(
            AlekiShipsRegistries.COMPARTMENT_PLACEABLE_SERIALIZER).sync(true).create();
    public static final Registry<ChestCompartmentData.ChestType> CHEST_COMPARTMENT_TYPES = new RegistryBuilder<>(
            AlekiShipsRegistries.CHEST_COMPARTMENT_TYPES).defaultKey(AlekiShips.location("vanilla"))
            .sync(true)
            .create();
    public static final Registry<WindModelSerializer<?>> WIND_MODEL_SERIALIZERS = new RegistryBuilder<>(
            AlekiShipsRegistries.WIND_MODEL_SERIALIZERS).sync(true).create();
    public static final Registry<EntityResultSerializer<?>> ENTITY_RESULT_SERIALIZERS = new RegistryBuilder<>(
            AlekiShipsRegistries.ENTITY_RESULT_SERIALIZER).sync(true).create();

    public static void registerRegistries(final NewRegistryEvent event) {
        event.register(DIRECT_COMPARTMENT_TYPES);
        event.register(COMPARTMENT_TYPE_SERIALIZERS);
        event.register(CHEST_COMPARTMENT_TYPES);
        event.register(WIND_MODEL_SERIALIZERS);
        event.register(ENTITY_RESULT_SERIALIZERS);
    }

    public static void registerDatapackRegistries(final DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(AlekiShipsRegistries.ENTITY_INPUT, EntityInput.CODEC, EntityInput.CODEC);
        event.dataPackRegistry(AlekiShipsRegistries.CONSTRUCTION_SLOOP_INPUT,
                SloopConstructionState.SloopConstructionStage.INPUT_CODEC,
                SloopConstructionState.SloopConstructionStage.INPUT_CODEC);
        event.dataPackRegistry(AlekiShipsRegistries.BOAT_MATERIAL, DynamicBoatMaterial.DIRECT_CODEC,
                DynamicBoatMaterial.DIRECT_CODEC);
    }
}
