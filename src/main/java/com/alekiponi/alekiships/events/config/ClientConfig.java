package com.alekiponi.alekiships.events.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class ClientConfig {

    public static final ClientConfig CONFIG;
    public static final ModConfigSpec CONFIG_SPEC;

    static {
        final var pair = new ModConfigSpec.Builder().configure(ClientConfig::new);

        //Store the resulting values
        CONFIG = pair.getLeft();
        CONFIG_SPEC = pair.getRight();
    }

    public final ModConfigSpec.EnumValue<RudderSchemes> rudderControlScheme;

    ClientConfig(final ModConfigSpec.Builder builder) {
        this.rudderControlScheme = builder.translation("alekiships.config.client.tillerControlScheme")
                .comment("Change how the rudder behaves on boats that have them")
                .defineEnum("tillerControlScheme", RudderSchemes.RETURN_TO_CENTER);
    }

    public enum RudderSchemes {
        RETURN_TO_CENTER,
        STAY_IN_PLACE
    }
}