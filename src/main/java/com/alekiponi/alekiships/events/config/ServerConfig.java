package com.alekiponi.alekiships.events.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class ServerConfig {
    public final ModConfigSpec.BooleanValue windAffectsBoatsWithNoAnchor;

    ServerConfig(final ModConfigSpec.Builder builder) {
        this.windAffectsBoatsWithNoAnchor = builder.translation("alekiships.config.server.windAffectsBoatsWithNoAnchor")
                .comment("When true, boats with no anchor will drift in the wind")
                .define("windAffectsBoatsWithNoAnchor", false);
    }
}