package com.alekiponi.alekiships.common;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.common.entity.EntityInput;
import com.alekiponi.alekiships.common.entity.SloopConstructionState;
import com.alekiponi.alekiships.common.entity.compartment.CompartmentType;
import com.alekiponi.alekiships.common.entity.vehicle.ConstructionInput;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public class AlekiShipsRegistries {
    public static final ResourceKey<Registry<CompartmentType<?>>> COMPARTMENT_TYPE = createRegistryKey(
            "compartment_type");
    public static final ResourceKey<Registry<EntityInput>> ENTITY_INPUT = createRegistryKey("entity_input");
    public static final ResourceKey<Registry<ConstructionInput<SloopConstructionState.SloopConstructionStage>>> CONSTRUCTION_SLOOP_INPUT = createRegistryKey(
            "construction/sloop");

    private static <T> ResourceKey<Registry<T>> createRegistryKey(final String name) {
        return ResourceKey.createRegistryKey(AlekiShips.location(name));
    }
}
