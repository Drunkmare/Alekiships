package com.alekiponi.alekiships.events.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class ClientConfig {

    private static final String LANG_KEY = AlekishipsConfig.LANG_KEY + ".client";

    public final ModConfigSpec.EnumValue<RudderSchemes> rudderControlScheme;

    ClientConfig(final ModConfigSpec.Builder builder) {
        this.rudderControlScheme = builder.comment("Change how the rudder behaves on boats that have them")
                .translation(LANG_KEY + ".tillerControlScheme")
                .defineEnum("tillerControlScheme", RudderSchemes.RETURN_TO_CENTER);
    }

    public enum RudderSchemes {
        RETURN_TO_CENTER,
        STAY_IN_PLACE
    }
}