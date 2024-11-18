package com.alekiponi.alekiships.events.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class AlekishipsConfig {

    public static final ServerConfig SERVER;
    public static final ModConfigSpec SERVER_SPEC;
    public static final ClientConfig CLIENT;
    public static final ModConfigSpec CLIENT_SPEC;

    static {
        {
            final var pair = new ModConfigSpec.Builder().configure(ClientConfig::new);

            //Store the resulting values
            CLIENT = pair.getLeft();
            CLIENT_SPEC = pair.getRight();
        }

        {
            final var pair = new ModConfigSpec.Builder().configure(ServerConfig::new);

            //Store the resulting values
            SERVER = pair.getLeft();
            SERVER_SPEC = pair.getRight();
        }
    }
}