package com.alekiponi.alekiships.client;

import com.alekiponi.alekiships.client.render.flywheel.Flywheel;
import com.alekiponi.alekiships.client.render.texture.atlas.BoatTextureSource;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class AlekiShipsClientEvents {

    public static void init() {
        final IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        bus.addListener(AlekiShipsClientEvents::clientSetup);
        bus.addListener(AlekiShipsClientEvents::onRegisterReloadListeners);
        bus.addListener(IngameOverlays::registerOverlays);
    }

    private static void clientSetup(final FMLClientSetupEvent event) {
        Flywheel.init();
    }

    private static void onRegisterReloadListeners(final RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(BoatAtlases.getRowboatAtlas());
        event.registerReloadListener(BoatAtlases.getSloopAtlas());
        BoatTextureSource.register();
    }
}
