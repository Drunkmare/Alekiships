package com.alekiponi.alekiships.client;

import com.alekiponi.alekiships.client.render.icon.IconRenderDispatcher;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;

public final class AlekiShipsClientEvents {

    public static void init(final IEventBus modBus) {
        modBus.addListener(IngameOverlays::registerOverlays);
        modBus.addListener(AlekiShipsClientEvents::onRegisterReloadListeners);
        modBus.register(RenderEventHandler.class);
    }

    private static void onRegisterReloadListeners(final RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(IconRenderDispatcher.INSTANCE);
    }
}