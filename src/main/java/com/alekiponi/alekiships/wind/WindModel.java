package com.alekiponi.alekiships.wind;

import com.google.errorprone.annotations.CanIgnoreReturnValue;

import com.alekiponi.alekiships.common.AlekiShipsAttachments;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.Nullable;

public interface WindModel {

    /**
     * Grab the attached wind model from the level
     *
     * @param level The level
     *
     * @return The attached wind model
     */
    static WindModel get(final Level level) {
        return level.getData(AlekiShipsAttachments.WIND_MODEL);
    }

    /**
     * Set the attached wind model on the provided level
     *
     * @param level     The level
     * @param windModel The wind model
     *
     * @return The old wind model. Potentially {@code null}
     */
    @Nullable
    @CanIgnoreReturnValue
    static WindModel set(final Level level, final WindModel windModel) {
        return level.setData(AlekiShipsAttachments.WIND_MODEL, windModel);
    }

    /**
     * Gets the {@link Wind} for the given level at the block position.
     *
     * @param blockPos The block pos at which the wind is being queried
     *
     * @return The wind at the given block position
     */
    default Wind getWind(final BlockPos blockPos) {
        return this.getWind(blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    /**
     * Gets the {@link Wind} for the given level at the position.
     *
     * @param pos The position to query the wind
     *
     * @return The wind at the given position
     */
    @SuppressWarnings("unused")
    default Wind getWind(final Vec3 pos) {
        return this.getWind(pos.x, pos.y, pos.z);
    }

    /**
     * Gets the {@link Wind} for the given level at the position.
     *
     * @param x The x position to query the wind
     * @param y The y position to query the wind
     * @param z The z position to query the wind
     *
     * @return The wind at the given position
     */
    Wind getWind(double x, double y, double z);
}