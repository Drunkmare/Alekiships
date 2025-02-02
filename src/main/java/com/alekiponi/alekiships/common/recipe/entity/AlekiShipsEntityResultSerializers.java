package com.alekiponi.alekiships.common.recipe.entity;

import com.mojang.serialization.MapCodec;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.common.AlekiShipsRegistries;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AlekiShipsEntityResultSerializers {
    public static final DeferredRegister<EntityResultSerializer<?>> ENTITY_RESULT_SERIALIZERS = DeferredRegister.create(
            AlekiShipsRegistries.ENTITY_RESULT_SERIALIZER, AlekiShips.MOD_ID);

    public static final DeferredHolder<EntityResultSerializer<?>, EntityResultSerializer<SimpleResult>> SIMPLE = register(
            "simple", SimpleResult.CODEC, SimpleResult.STREAM_CODEC);

    public static final DeferredHolder<EntityResultSerializer<?>, EntityResultSerializer<RowboatResult>> ROWBOAT = register(
            "rowboat", RowboatResult.CODEC, RowboatResult.STREAM_CODEC);

    public static final DeferredHolder<EntityResultSerializer<?>, EntityResultSerializer<SloopResult>> SLOOP = register(
            "sloop", SloopResult.CODEC, SloopResult.STREAM_CODEC);

    private static <R extends EntityResult> DeferredHolder<EntityResultSerializer<?>, EntityResultSerializer<R>> register(
            final String name, final MapCodec<R> codec,
            final StreamCodec<? super RegistryFriendlyByteBuf, R> streamCodec) {
        return ENTITY_RESULT_SERIALIZERS.register(name, () -> new EntityResultSerializer<>() {
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