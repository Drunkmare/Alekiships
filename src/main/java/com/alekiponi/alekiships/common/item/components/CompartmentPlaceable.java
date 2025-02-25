package com.alekiponi.alekiships.common.item.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import com.alekiponi.alekiships.common.AlekiShipsBuiltInRegistries;
import com.alekiponi.alekiships.common.AlekiShipsRegistries;
import com.alekiponi.alekiships.common.entity.compartment.CompartmentType;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record CompartmentPlaceable(CompartmentType<?> compartmentType) {

    public static final Codec<CompartmentPlaceable> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            AlekiShipsBuiltInRegistries.COMPARTMENT_TYPES.byNameCodec().fieldOf("compartment_type")
                    .forGetter(CompartmentPlaceable::compartmentType)).apply(instance, CompartmentPlaceable::new));

    public static final StreamCodec<? super RegistryFriendlyByteBuf, CompartmentPlaceable> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.registry(AlekiShipsRegistries.COMPARTMENT_TYPE), CompartmentPlaceable::compartmentType,
            CompartmentPlaceable::new);
}