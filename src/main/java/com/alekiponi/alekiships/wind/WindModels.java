package com.alekiponi.alekiships.wind;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class WindModels {

    /**
     * A wind model that returns {@link Wind#ZERO}
     */
    public static final WindModel NOOP = blockPos -> Wind.ZERO;

    private static final Map<ResourceKey<Level>, WindModel> WIND_MODELS = new HashMap<>();

    /**
     * Register a dimension -> {@link WindModel} mapping
     *
     * @param dimension The dimension the {@link WindModel} should be associated with
     * @param windModel The {@link WindModel} instance for the dimension
     */
    public static void register(final ResourceKey<Level> dimension, final WindModel windModel) {
        WIND_MODELS.put(Objects.requireNonNull(dimension), Objects.requireNonNull(windModel));
    }

    /**
     * Get a {@link WindModel} for the passed dimension
     *
     * @param dimension The dimension
     * @return The registered wind model or {@link #NOOP}
     */
    public static WindModel get(final ResourceKey<Level> dimension) {
        final WindModel windModel = WIND_MODELS.get(dimension);
        if (windModel == null) {
            return NOOP;
        }
        return windModel;
    }
}