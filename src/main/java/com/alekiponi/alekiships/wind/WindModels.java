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

    private static final Map<ResourceKey<Level>, WindModel.WindModelFactory> WIND_MODELS = new HashMap<>();

    /**
     * Register a dimension -> {@link WindModel.WindModelFactory} mapping
     *
     * @param dimension        The dimension the {@link WindModel} should be associated with
     * @param windModelFactory A factory for the {@link WindModel} for the dimension
     */
    public static void register(final ResourceKey<Level> dimension, final WindModel.WindModelFactory windModelFactory) {
        WIND_MODELS.put(Objects.requireNonNull(dimension), Objects.requireNonNull(windModelFactory));
    }

    /**
     * Get a {@link WindModel} for the passed dimension
     *
     * @param level The level
     * @return The registered wind model or {@link #NOOP}
     */
    public static WindModel get(final Level level) {
        final WindModel.WindModelFactory windModel = WIND_MODELS.get(level.dimension());
        if (windModel == null) {
            return NOOP;
        }
        return windModel.create(level);
    }
}