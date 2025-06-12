package com.alekiponi.alekiships.common.compartment;

import com.mojang.serialization.MapCodec;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.common.AlekiShipsRegistries;
import com.alekiponi.alekiships.common.entity.compartment.BlockCompartmentEntity;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class AlekiShipsCompartmentPlaceableSerializers {

    public static final DeferredRegister<CompartmentPlaceableSerializer<?>> COMPARTMENT_PLACEABLE_SERIALIZERS = DeferredRegister.create(
            AlekiShipsRegistries.COMPARTMENT_PLACEABLE_SERIALIZER, AlekiShips.MOD_ID);

    public static final DeferredHolder<CompartmentPlaceableSerializer<?>, CompartmentPlaceableSerializer<BlockCompartmentPlaceable<BlockCompartmentEntity>>> BLOCK = register(
            "block", BlockCompartmentPlaceable.codec(BlockCompartmentEntity::create),
            BlockCompartmentPlaceable.streamCodec(BlockCompartmentEntity::create));

    public static final DeferredHolder<CompartmentPlaceableSerializer<?>, CompartmentPlaceableSerializer<BarrelCompartmentPlaceable>> BARREL = register(
            "barrel", BarrelCompartmentPlaceable.CODEC, BarrelCompartmentPlaceable.STREAM_CODEC);

    public static final DeferredHolder<CompartmentPlaceableSerializer<?>, CompartmentPlaceableSerializer<ChestCompartmentPlaceable>> CHEST = register(
            "chest", ChestCompartmentPlaceable.CODEC, ChestCompartmentPlaceable.STREAM_CODEC);

    public static final DeferredHolder<CompartmentPlaceableSerializer<?>, CompartmentPlaceableSerializer<ShulkerBoxCompartmentPlaceable>> SHULKER_BOX = register(
            "shulker_box", ShulkerBoxCompartmentPlaceable.CODEC, ShulkerBoxCompartmentPlaceable.STREAM_CODEC);

    public static final DeferredHolder<CompartmentPlaceableSerializer<?>, CompartmentPlaceableSerializer<DirectCompartmentPlaceable>> DIRECT = register(
            "direct", DirectCompartmentPlaceable.CODEC, DirectCompartmentPlaceable.STREAM_CODEC);

    private static <R extends CompartmentPlaceable<?>> DeferredHolder<CompartmentPlaceableSerializer<?>, CompartmentPlaceableSerializer<R>> register(
            final String name, final MapCodec<R> codec,
            final StreamCodec<? super RegistryFriendlyByteBuf, R> streamCodec) {
        return COMPARTMENT_PLACEABLE_SERIALIZERS.register(name, () -> new CompartmentPlaceableSerializer<>() {
            @Override
            public MapCodec<R> codec() {
                return codec;
            }

            @Override
            public StreamCodec<? super RegistryFriendlyByteBuf, R> streamCodec() {
                return streamCodec;
            }
        });
    }
}