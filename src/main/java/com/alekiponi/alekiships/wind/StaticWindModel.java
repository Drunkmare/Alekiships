package com.alekiponi.alekiships.wind;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;

import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;

import net.neoforged.neoforge.common.util.NeoForgeExtraCodecs;

/**
 * A static wind model with a constant never changing wind output
 *
 * @param wind The static wind
 */
public record StaticWindModel(Wind wind) implements WindModel {

    public static final MapCodec<StaticWindModel> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            NeoForgeExtraCodecs.withAlternative(Wind.CODEC, Wind.COMPONENETS_CODEC)
                    .fieldOf("wind")
                    .forGetter(StaticWindModel::wind)).apply(instance, StaticWindModel::new));

    public static final StreamCodec<ByteBuf, StaticWindModel> STREAM_CODEC = StreamCodec.composite(Wind.STREAM_CODEC,
            StaticWindModel::wind, StaticWindModel::new);

    @Override
    public Wind getWind(final Level level, final double x, final double y, final double z) {
        return this.wind;
    }

    @Override
    public WindModelSerializer<?> getSerializer() {
        return AlekiShipsWindModelSerializers.STATIC_WIND_MODEL.get();
    }
}