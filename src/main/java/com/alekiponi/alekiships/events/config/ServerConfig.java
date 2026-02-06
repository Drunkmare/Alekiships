package com.alekiponi.alekiships.events.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class ServerConfig {

    private static final String LANG_KEY = AlekishipsConfig.LANG_KEY + ".server";

    public final ModConfigSpec.BooleanValue windAffectsBoatsWithNoAnchor;
    public final ModConfigSpec.BooleanValue eagerEntityMultiblockValidation;

    ServerConfig(final ModConfigSpec.Builder builder) {
        this.windAffectsBoatsWithNoAnchor = builder.comment("When true, boats with no anchor will drift in the wind")
                .translation(LANG_KEY + ".windAffectsBoatsWithNoAnchor")
                .define("windAffectsBoatsWithNoAnchor", false);
        this.eagerEntityMultiblockValidation = builder.comment(
                        "When true entity multiblocks will be eagerly checked whenever a block is placed")
                .translation(LANG_KEY + ".eagerEntityMultiblockValidation")
                .define("eagerEntityMultiblockValidation", true);
    }
}