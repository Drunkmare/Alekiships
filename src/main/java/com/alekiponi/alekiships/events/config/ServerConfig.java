package com.alekiponi.alekiships.events.config;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.function.Function;

// TODO remove if this continues to be empty
public final class ServerConfig {

    ServerConfig(final ForgeConfigSpec.Builder innerBuilder) {
        //noinspection unused
        final Function<String, ForgeConfigSpec.Builder> builder = (name) -> innerBuilder.translation(
                "firmaciv.config.server." + name);
    }
}