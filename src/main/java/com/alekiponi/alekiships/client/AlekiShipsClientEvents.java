package com.alekiponi.alekiships.client;

import net.neoforged.bus.api.IEventBus;

public final class AlekiShipsClientEvents {

    public static void init(final IEventBus modBus) {
        modBus.addListener(IngameOverlays::registerOverlays);
        modBus.register(RenderEventHandler.class);
    }
}