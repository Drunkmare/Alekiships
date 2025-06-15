package com.alekiponi.alekiships.compat.weather2;

import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import weather2.util.WindReader;

import com.alekiponi.alekiships.wind.Wind;
import com.alekiponi.alekiships.wind.WindModel;
import com.alekiponi.alekiships.wind.WindModelSerializer;

import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Our wind model for Weather2. Crazy how descriptive class names can be, eh?
 */
public enum Weather2WindModel implements WindModel {
    INSTANCE;

    public static final MapCodec<Weather2WindModel> CODEC = MapCodec.unit(() -> INSTANCE);
    public static final StreamCodec<ByteBuf, Weather2WindModel> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    private static final int ANGLE_OFFSET = -180;

    @Override
    public Wind getWind(final Level level, final BlockPos blockPos) {
        final var windManager = WindReader.getWeatherManagerFor(level).getWindManager();
        return new Wind(windManager.getWindSpeed(blockPos),
                windManager.getWindAngle(blockPos.getCenter()) + ANGLE_OFFSET);
    }

    @Override
    public Wind getWind(final Level level, final double x, final double y, final double z) {
        final var pos = new Vec3(x, y, z);
        final var windManager = WindReader.getWeatherManagerFor(level).getWindManager();
        return Wind.of(windManager.getWindSpeed(BlockPos.containing(pos)),
                windManager.getWindAngle(pos) + ANGLE_OFFSET);
    }

    @Override
    public Wind getWind(final Level level, final Vec3 pos) {
        final var windManager = WindReader.getWeatherManagerFor(level).getWindManager();
        return Wind.of(windManager.getWindSpeed(BlockPos.containing(pos)),
                windManager.getWindAngle(pos) + ANGLE_OFFSET);
    }

    @Override
    public WindModelSerializer<?> getSerializer() {
        return Weather2WindModelSerializers.WEATHER_2_MODEL.get();
    }
}