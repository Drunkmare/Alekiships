package com.alekiponi.alekiships.events.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

import java.util.function.Function;

public final class AlekishipsConfig {


    public static final ServerConfig SERVER = register(ModConfig.Type.SERVER, ServerConfig::new);

    public static void init() {
    }

    private static <C> C register(@SuppressWarnings("SameParameterValue") final ModConfig.Type type,
            final Function<ForgeConfigSpec.Builder, C> factory) {
        final Pair<C, ForgeConfigSpec> specPair = (new ForgeConfigSpec.Builder()).configure(factory);

        ModLoadingContext.get().registerConfig(type, specPair.getRight());

        return specPair.getLeft();
    }
}
