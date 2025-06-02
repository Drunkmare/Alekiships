package com.alekiponi.alekiships.common.item.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import com.alekiponi.alekiships.common.AlekiShipsBuiltInRegistries;
import com.alekiponi.alekiships.common.AlekiShipsRegistries;
import com.alekiponi.alekiships.common.entity.compartment.AbstractCompartmentEntity;
import com.alekiponi.alekiships.common.entity.compartment.CompartmentType;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Optional;

public record CompartmentPlaceable(CompartmentType<?> compartmentType) {

    public static final Codec<CompartmentPlaceable> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            AlekiShipsBuiltInRegistries.COMPARTMENT_TYPES.byNameCodec()
                    .fieldOf("compartment_type")
                    .forGetter(CompartmentPlaceable::compartmentType)).apply(instance, CompartmentPlaceable::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, CompartmentPlaceable> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.registry(AlekiShipsRegistries.COMPARTMENT_TYPE), CompartmentPlaceable::compartmentType,
            CompartmentPlaceable::new);

    /**
     * Gets an applicable {@link CompartmentPlaceable} for an {@link ItemStack}
     *
     * @param itemStack The {@link ItemStack}
     */
    public static Optional<CompartmentPlaceable> fromStack(final ItemStack itemStack) {
        final var compartmentPlaceable = itemStack.get(AlekiShipsComponents.COMPARTMENT_PLACEABLE);
        return compartmentPlaceable == null ? Optional.empty() : Optional.of(compartmentPlaceable);
    }

    public Optional<? extends AbstractCompartmentEntity> createCompartment(final Level level,
            final ItemStack itemStack) {
        return this.compartmentType.create(level, itemStack);
    }
}