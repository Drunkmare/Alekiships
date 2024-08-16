package com.alekiponi.alekiships.events.config;

import java.util.function.Function;
import net.minecraftforge.common.ForgeConfigSpec;

// TODO remove if this continues to be empty
public final class ClientConfig
{
    public ForgeConfigSpec.EnumValue<RudderSchemes> rudderControlScheme;

    ClientConfig(final ForgeConfigSpec.Builder innerBuilder)
    {
        //noinspection unused
        final Function<String, ForgeConfigSpec.Builder> builder = (name) -> innerBuilder.translation(
            "alekiships.config.client." + name);

        this.rudderControlScheme = builder.apply("tillerControlScheme")
            .comment(
                "Change how the rudder behaves on boats that have them")
            .defineEnum("tillerControlScheme", RudderSchemes.RETURN_TO_CENTER, RudderSchemes.values());

    }

    public enum RudderSchemes
    {
        RETURN_TO_CENTER,
        STAY_IN_PLACE

    }
}