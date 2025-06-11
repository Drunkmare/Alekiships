package com.alekiponi.alekiships.common.compartment;

import com.alekiponi.alekiships.common.AlekiShipsBuiltInRegistries;
import com.alekiponi.alekiships.common.entity.compartment.vanilla.ChestCompartmentData;

import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.alekiponi.alekiships.AlekiShips.MOD_ID;

public final class AlekiShipsChestCompartmentTypes {

    public static final DeferredRegister<ChestCompartmentData.ChestType> CHEST_TYPES = DeferredRegister.create(
            AlekiShipsBuiltInRegistries.CHEST_COMPARTMENT_TYPES, MOD_ID);

    public static final DeferredHolder<ChestCompartmentData.ChestType, ChestCompartmentData.ChestType> VANILLA_CHEST = CHEST_TYPES.register(
            "vanilla", () -> new ChestCompartmentData.ChestType() {});
}