package com.alekiponi.alekiships.common;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.common.compartment.CompartmentPlaceable;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;

import net.neoforged.neoforge.registries.datamaps.DataMapType;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;

public final class AlekiShipsDataMaps {

    public static final DataMapType<Item, CompartmentPlaceable<?>> COMPARTMENT_PLACEABLE = DataMapType.builder(
                    AlekiShips.location("compartment_placeable"), Registries.ITEM, CompartmentPlaceable.CODEC)
            .synced(CompartmentPlaceable.CODEC, true)
            .build();

    public static void registerDataMaps(final RegisterDataMapTypesEvent event) {
        event.register(COMPARTMENT_PLACEABLE);
    }
}