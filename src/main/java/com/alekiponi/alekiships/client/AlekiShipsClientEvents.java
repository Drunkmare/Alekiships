package com.alekiponi.alekiships.client;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class AlekiShipsClientEvents {

    public static void init() {
        final IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        bus.addListener(AlekiShipsClientEvents::clientSetup);
        bus.addListener(IngameOverlays::registerOverlays);
    }

    private static void clientSetup(final FMLClientSetupEvent event) {

    }
}
