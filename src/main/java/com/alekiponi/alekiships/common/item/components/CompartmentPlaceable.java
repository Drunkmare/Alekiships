package com.alekiponi.alekiships.common.item.components;

import com.alekiponi.alekiships.common.entity.compartment.CompartmentType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record CompartmentPlaceable(CompartmentType<?> compartmentType) {

    public static final Codec<CompartmentPlaceable> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            CompartmentType.REGISTRY.byNameCodec().fieldOf("compartment_type")
                    .forGetter(CompartmentPlaceable::compartmentType)).apply(instance, CompartmentPlaceable::new));

    public static final StreamCodec<? super RegistryFriendlyByteBuf, CompartmentPlaceable> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.registry(CompartmentType.REGISTRY_KEY), CompartmentPlaceable::compartmentType,
            CompartmentPlaceable::new);
}