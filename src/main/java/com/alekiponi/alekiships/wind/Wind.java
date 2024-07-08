package com.alekiponi.alekiships.wind;

import com.alekiponi.alekiships.network.AlekiShipsEntityDataSerializers;
import com.alekiponi.alekiships.util.CommonHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;

import javax.annotation.concurrent.Immutable;

/**
 * A simple 2D representation of wind.
 * <p>
 * {@link AlekiShipsEntityDataSerializers#WIND} is available for easy syncing over the network
 *
 * @see WindModel
 */
@Immutable
public final class Wind {
    public static final Wind ZERO = fromComponents(0, 0);
    /**
     * The wind speed, always a positive value
     */
    public final float speed;
    /**
     * The wind angle between -180 and +180
     */
    public final float angle;

    public Wind(final float speed, final float angle) {
        this.speed = speed;
        this.angle = angle;
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

    public static Wind fromVec(final Vec2 vec2) {
        return fromComponents(vec2.x, vec2.y);
    }
}