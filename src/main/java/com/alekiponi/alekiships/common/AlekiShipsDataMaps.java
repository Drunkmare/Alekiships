package com.alekiponi.alekiships.common;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.common.compartment.CompartmentPlaceable;
import com.alekiponi.alekiships.wind.WindModel;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.dimension.DimensionType;

import net.neoforged.neoforge.registries.datamaps.DataMapType;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;

public final class AlekiShipsDataMaps {

    public static final DataMapType<Item, CompartmentPlaceable<?>> COMPARTMENT_PLACEABLE = DataMapType.builder(
                    AlekiShips.location("compartment_placeable"), Registries.ITEM, CompartmentPlaceable.CODEC)
            .synced(CompartmentPlaceable.CODEC, true)
            .build();

    public static final DataMapType<DimensionType, WindModel> WIND_MODEL = DataMapType.builder(
                    AlekiShips.location("wind_model"), Registries.DIMENSION_TYPE, WindModel.CODEC).synced(WindModel.CODEC, true)
            .build();

    public static void registerDataMaps(final RegisterDataMapTypesEvent event) {
        event.register(COMPARTMENT_PLACEABLE);
        event.register(WIND_MODEL);
    }
}