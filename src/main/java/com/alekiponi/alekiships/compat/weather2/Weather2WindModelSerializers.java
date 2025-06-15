package com.alekiponi.alekiships.compat.weather2;

import com.mojang.serialization.MapCodec;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.common.AlekiShipsRegistries;
import com.alekiponi.alekiships.wind.WindModel;
import com.alekiponi.alekiships.wind.WindModelSerializer;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class Weather2WindModelSerializers {
    public static final DeferredRegister<WindModelSerializer<?>> WIND_MODEL_SERIALIZERS = DeferredRegister.create(
            AlekiShipsRegistries.WIND_MODEL_SERIALIZERS, AlekiShips.MOD_ID);

    public static final DeferredHolder<WindModelSerializer<?>, WindModelSerializer<Weather2WindModel>> WEATHER_2_MODEL = register(
            "weather_2_model", Weather2WindModel.CODEC, Weather2WindModel.STREAM_CODEC);

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