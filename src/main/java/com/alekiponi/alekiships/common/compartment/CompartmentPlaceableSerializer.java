package com.alekiponi.alekiships.common.compartment;

import com.mojang.serialization.MapCodec;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public interface CompartmentPlaceableSerializer<T extends CompartmentPlaceable<?>> {

    MapCodec<T> codec();

    StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec();
}