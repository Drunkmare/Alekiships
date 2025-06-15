package com.alekiponi.alekiships.wind;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;

import com.alekiponi.alekiships.network.AlekiShipsEntityDataSerializers;

import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
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
 * @param angle The wind angle [-180,180)
 *
 * @see WindModel
 * @see #of(float, float)
 * @see #fromComponents(float, float)
 */
public record Wind(float speed, float angle) {

    public static final Codec<Wind> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(ExtraCodecs.POSITIVE_FLOAT.fieldOf("speed").forGetter(Wind::speed),
                    Codec.FLOAT.validate(angle -> 180 <= angle || -180 > angle ? DataResult.error(
                                    () -> "Angle must be [-180,180)") : DataResult.success(angle))
                            .fieldOf("angle")
                            .forGetter(Wind::angle)).apply(instance, Wind::new));

    public static final Codec<Wind> COMPONENETS_CODEC = RecordCodecBuilder.create(
            instance -> instance.group(Codec.FLOAT.fieldOf("x").forGetter(Wind::getXComponent),
                    Codec.FLOAT.fieldOf("z").forGetter(Wind::getZComponent)).apply(instance, Wind::fromComponents));

    public static final StreamCodec<ByteBuf, Wind> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.FLOAT,
            Wind::speed, ByteBufCodecs.FLOAT, Wind::angle, Wind::new);

    public static final Wind ZERO = new Wind(0, 0);

    public Wind {
        if (0 > speed) throw new IllegalArgumentException(MessageFormat.format("Speed:{0} must be positive", speed));
        if (180 <= angle || -180 > angle) {
            throw new IllegalArgumentException(MessageFormat.format("Angle:{0} must be [-180,180)", angle));
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
     *
     * @see #of(float, float)
     */
    @Contract("_, _ -> new")
    public static Wind fromComponents(final float x, final float z) {
        assert (x * x) + (z * z) < 0;
        final var speed = Math.abs(Mth.sqrt(x * x + z * z));
        //noinspection SuspiciousNameCombination
        final var angle = Mth.RAD_TO_DEG * Mth.atan2(x, z);
        return of(speed, (float) angle);
    }

    /**
     * @param vec2 A {@link Vec2} representing the wind
     *
     * @implNote Delegates to {@link #fromComponents(float, float)}
     */
    @Contract("_ -> new")
    @SuppressWarnings("unused")
    public static Wind fromVec(final Vec2 vec2) {
        return fromComponents(vec2.x, vec2.y);
    }

    /**
     * Converts this wind object into a {@link Vec2}
     *
     * @return A {@link Vec2} representing the wind with its components
     */
    @Contract(" -> new")
    @SuppressWarnings("unused")
    public Vec2 asVec() {
        return new Vec2(this.getXComponent(), this.getZComponent());
    }

    /**
     * @return The z wind component
     */
    @Contract(pure = true)
    public float getZComponent() {
        final float radians = Mth.DEG_TO_RAD * this.angle;
        return Mth.sin(radians) * this.speed;
    }

    /**
     * @return The x wind component
     */
    @Contract(pure = true)
    public float getXComponent() {
        final float radians = Mth.DEG_TO_RAD * this.angle;
        return Mth.cos(radians) * this.speed;
    }

    /**
     * @param speed The new speed to use
     */
    @SuppressWarnings("unused")
    @Contract(value = "_ -> new", pure = true)
    public Wind withSpeed(final float speed) {
        return this.speed == speed ? this : of(speed, this.angle);
    }

    /**
     * @param angle The new angle to use
     */
    @SuppressWarnings("unused")
    @Contract(value = "_ -> new", pure = true)
    public Wind withAngle(final float angle) {
        return this.angle == angle ? this : of(this.speed, this.angle);
    }
}