package com.alekiponi.alekiships.common.compartment;

import com.mojang.serialization.Codec;

import com.alekiponi.alekiships.common.AlekiShipsBuiltInRegistries;
import com.alekiponi.alekiships.common.AlekiShipsDataMaps;
import com.alekiponi.alekiships.common.AlekiShipsRegistries;
import com.alekiponi.alekiships.common.entity.compartment.AbstractCompartmentEntity;
import com.alekiponi.alekiships.common.item.components.AlekiShipsComponents;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Optional;

public interface CompartmentPlaceable<C extends AbstractCompartmentEntity> {
    Codec<CompartmentPlaceable<?>> CODEC = AlekiShipsBuiltInRegistries.COMPARTMENT_TYPE_SERIALIZERS.byNameCodec()
            .dispatch(CompartmentPlaceable::getSerializer, CompartmentPlaceableSerializer::codec);
    StreamCodec<RegistryFriendlyByteBuf, CompartmentPlaceable<?>> STREAM_CODEC = ByteBufCodecs.registry(
                    AlekiShipsRegistries.COMPARTMENT_PLACEABLE_SERIALIZER)
            .dispatch(CompartmentPlaceable::getSerializer, CompartmentPlaceableSerializer::streamCodec);

    /**
     * Gets an applicable {@link CompartmentPlaceable} for an {@link ItemStack}
     *
     * @param itemStack The {@link ItemStack}
     */
    static Optional<CompartmentPlaceable<?>> fromStack(final ItemStack itemStack) {
        final var compartmentPlaceable = itemStack.get(AlekiShipsComponents.COMPARTMENT_PLACEABLE);
        return compartmentPlaceable == null ? Optional.ofNullable(
                itemStack.getItemHolder().getData(AlekiShipsDataMaps.COMPARTMENT_PLACEABLE)) : Optional.of(
                compartmentPlaceable);
    }

    CompartmentPlaceableSerializer<?> getSerializer();

    Optional<C> createCompartment(Level level, ItemStack itemStack);
}