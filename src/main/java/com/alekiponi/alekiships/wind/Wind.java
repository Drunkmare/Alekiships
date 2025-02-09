package com.alekiponi.alekiships.wind;

import io.netty.buffer.ByteBuf;

import com.alekiponi.alekiships.network.AlekiShipsEntityDataSerializers;
import com.alekiponi.alekiships.util.CommonHelper;

import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;

import java.text.MessageFormat;
import org.jetbrains.annotations.Contract;

/**
 * A simple 2D representation of wind.
 * <p>
 * {@link AlekiShipsEntityDataSerializers#WIND} is available for easy syncing over the network
 *
 * @param speed The wind speed, always a positive value
 * @param angle The wind angle [-180;180)
 *
 * @see WindModel
 */
public record Wind(float speed, float angle) {

    public static final StreamCodec<ByteBuf, Wind> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.FLOAT,
            Wind::speed, ByteBufCodecs.FLOAT, Wind::angle, Wind::new);

    public static final Wind ZERO = new Wind(0, 0);

    public Wind {
        if (0 > speed) throw new IllegalArgumentException(MessageFormat.format("Speed:{0} must be positive", speed));
        if (180 <= angle || -180 > angle) {
            throw new IllegalArgumentException(MessageFormat.format("Angle:{0} must be [-180;180)", angle));
        }
    }

    /**
     * Safely creates a wind object with untrusted values
     *
     * @param speed The speed to be clamped
     * @param angle The angle to be wrapped
     */
    @Contract("_, _ -> new")
    public static Wind of(final float speed, final float angle) {
        return new Wind(Math.clamp(speed, 0, Float.MAX_VALUE), Mth.wrapDegrees(angle));
    }

    /**
     * @param x The x component of the wind
     * @param z The z component of the wind
     */
    public static Wind fromComponents(final float x, final float z) {
        final var speed = Math.abs(Mth.sqrt(x * x + z * z));
        final var angle = Mth.wrapDegrees((float) Math.round(CommonHelper.vec2ToWrappedDegrees(x / speed, z / speed)));
        return new Wind(speed, angle);
    }

    /**
     * @param vec2 A {@link Vec2} representing the wind
     *
     * @apiNote Delegates to {@link #fromComponents(float, float)}
     */
    @SuppressWarnings("unused")
    public static Wind fromVec(final Vec2 vec2) {
        return fromComponents(vec2.x, vec2.y);
    }

    /**
     * Converts this wind object into a {@link Vec2}
     *
     * @return A {@link Vec2} representing the wind with its components
     */
    @SuppressWarnings("unused")
    public Vec2 asVec() {
        final float radians = Mth.DEG_TO_RAD * this.angle;
        return new Vec2(Mth.cos(radians) * this.speed, Mth.sin(radians) * this.speed);
    }
}