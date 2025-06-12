package com.alekiponi.alekiships.wind;

import com.mojang.serialization.Codec;

import com.alekiponi.alekiships.common.AlekiShipsBuiltInRegistries;
import com.alekiponi.alekiships.common.AlekiShipsDataMaps;
import com.alekiponi.alekiships.common.AlekiShipsRegistries;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public interface WindModel {

    Codec<WindModel> CODEC = AlekiShipsBuiltInRegistries.WIND_MODEL_SERIALIZERS.byNameCodec()
            .dispatch(WindModel::getSerializer, WindModelSerializer::codec);
    StreamCodec<RegistryFriendlyByteBuf, WindModel> STREAM_CODEC = ByteBufCodecs.registry(
                    AlekiShipsRegistries.WIND_MODEL_SERIALIZERS)
            .dispatch(WindModel::getSerializer, WindModelSerializer::streamCodec);

    /**
     * Grab the appropriate wind model for the level
     *
     * @param level The level
     *
     * @return The attached wind model
     */
    static WindModel get(final Level level) {
        final var data = level.dimensionTypeRegistration().getData(AlekiShipsDataMaps.WIND_MODEL);
        if (data == null) return SimpleWindModel.INSTANCE;
        return data;
    }

    /**
     * Gets the {@link Wind} for the given level at the block position.
     *
     * @param level    The level
     * @param blockPos The block pos at which the wind is being queried
     *
     * @return The wind at the given block position
     */
    @SuppressWarnings("unused")
    default Wind getWind(final Level level, final BlockPos blockPos) {
        return this.getWind(level, blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    /**
     * Gets the {@link Wind} for the given level at the position.
     *
     * @param level The level
     * @param pos   The position to query the wind
     *
     * @return The wind at the given position
     */
    default Wind getWind(final Level level, final Vec3 pos) {
        return this.getWind(level, pos.x, pos.y, pos.z);
    }

    /**
     * Gets the {@link Wind} for the given level at the position.
     *
     * @param level The level
     * @param x     The x position to query the wind
     * @param y     The y position to query the wind
     * @param z     The z position to query the wind
     *
     * @return The wind at the given position
     */
    Wind getWind(final Level level, double x, double y, double z);

    WindModelSerializer<?> getSerializer();
}