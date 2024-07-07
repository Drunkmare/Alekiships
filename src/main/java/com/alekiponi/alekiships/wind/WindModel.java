package com.alekiponi.alekiships.wind;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

/**
 * Entities aren't expected to serialize or sync model state.
 * If that's necessary for your implementation it must be manually managed externally.
 * Entities are however expected to sync return values if they need the values on both sides.
 */
public interface WindModel {

    /**
     * Gets the {@link Wind} for the given level at the block position.
     *
     * @param blockPos The block pos at which the wind is being queried
     * @return The wind at that block pos
     */
    Wind getWind(BlockPos blockPos);

    @FunctionalInterface
    interface WindModelFactory {
        WindModel create(Level level);
    }
}