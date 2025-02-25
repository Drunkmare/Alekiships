package com.alekiponi.alekiships.common;

import com.alekiponi.alekiships.common.entity.EntityInput;
import com.alekiponi.alekiships.common.entity.SloopConstructionState;
import com.alekiponi.alekiships.common.entity.compartment.CompartmentType;

import net.minecraft.core.Registry;

import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

public final class AlekiShipsBuiltInRegistries {
    public static final Registry<CompartmentType<?>> COMPARTMENT_TYPES = new RegistryBuilder<>(
            AlekiShipsRegistries.COMPARTMENT_TYPE).sync(true).create();

    public static void registerRegistries(final NewRegistryEvent event) {
        event.register(COMPARTMENT_TYPES);
    }

    public static void registerDatapackRegistries(final DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(AlekiShipsRegistries.ENTITY_INPUT, EntityInput.CODEC, EntityInput.CODEC);
        event.dataPackRegistry(AlekiShipsRegistries.CONSTRUCTION_SLOOP_INPUT,
                SloopConstructionState.SloopConstructionStage.INPUT_CODEC,
                SloopConstructionState.SloopConstructionStage.INPUT_CODEC);
    }
}
