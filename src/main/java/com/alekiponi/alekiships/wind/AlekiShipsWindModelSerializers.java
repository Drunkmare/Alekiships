package com.alekiponi.alekiships.wind;

import com.mojang.serialization.MapCodec;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.common.AlekiShipsRegistries;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class AlekiShipsWindModelSerializers {
    public static final DeferredRegister<WindModelSerializer<?>> WIND_MODEL_SERIALIZERS = DeferredRegister.create(
            AlekiShipsRegistries.WIND_MODEL_SERIALIZERS, AlekiShips.MOD_ID);

    public static final DeferredHolder<WindModelSerializer<?>, WindModelSerializer<SimpleWindModel>> SIMPLE_WIND_MODEL = register(
            "simple_wind_model", SimpleWindModel.CODEC, SimpleWindModel.STREAM_CODEC);

    private static <M extends WindModel> DeferredHolder<WindModelSerializer<?>, WindModelSerializer<M>> register(
            final String name, final MapCodec<M> codec,
            final StreamCodec<? super RegistryFriendlyByteBuf, M> streamCodec) {
        return WIND_MODEL_SERIALIZERS.register(name, () -> new WindModelSerializer<>() {
            @Override
            public MapCodec<M> codec() {
                return codec;
            }

            @Override
            public StreamCodec<? super RegistryFriendlyByteBuf, M> streamCodec() {
                return streamCodec;
            }
        });
    }
}