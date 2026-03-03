package com.alekiponi.alekiships.client;

import com.alekiponi.alekiships.client.model.DynamicTextureModel;
import com.alekiponi.alekiships.client.render.icon.IconRenderDispatcher;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;

public final class AlekiShipsClientEvents {

    public static void init(final IEventBus modBus) {
        modBus.addListener(IngameOverlays::registerOverlays);
        modBus.addListener(AlekiShipsClientEvents::onRegisterReloadListeners);
        modBus.addListener(AlekiShipsClientEvents::registerModelLoaders);
        modBus.register(RenderEventHandler.class);
    }

    private static void onRegisterReloadListeners(final RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(IconRenderDispatcher.INSTANCE);
    }

    private static void registerModelLoaders(final ModelEvent.RegisterGeometryLoaders event) {
        event.register(DynamicTextureModel.LOADER_ID, DynamicTextureModel.LOADER);
    }
}