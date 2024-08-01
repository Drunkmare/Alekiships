package com.alekiponi.alekiships.events.config;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.function.Function;

// TODO remove if this continues to be empty
public final class ServerConfig {
    public ForgeConfigSpec.BooleanValue windAffectsBoatsWithNoAnchor;

    ServerConfig(final ForgeConfigSpec.Builder innerBuilder) {
        //noinspection unused
        final Function<String, ForgeConfigSpec.Builder> builder = (name) -> innerBuilder.translation(
                "alekiships.config.server." + name);

        this.windAffectsBoatsWithNoAnchor = builder.apply("windAffectsBoatsWithNoAnchor")
                .comment(
                        "When true, boats with no anchor will drift in the wind")
                .define("windAffectsBoatsWithNoAnchor", false);

    }
}