package com.alekiponi.alekiships.client;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.client.resources.PaintedTextureGenerator;
import com.alekiponi.alekiships.util.VanillaWood;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;

public final class AlekiShipsClientEvents {

    public static void init(final IEventBus modBus) {
        modBus.addListener(AlekiShipsClientEvents::onRegisterReloadListeners);
        modBus.addListener(IngameOverlays::registerOverlays);
        modBus.register(RenderEventHandler.class);
    }

    private static void onRegisterReloadListeners(final RegisterClientReloadListenersEvent event) {
        {
            final ResourceLocation sloopPaint = ResourceLocation.fromNamespaceAndPath(AlekiShips.MOD_ID,
                    "entity/watercraft/sloop/paint");

            for (final VanillaWood wood : VanillaWood.values()) {
                event.registerReloadListener(new PaintedTextureGenerator(
                        ResourceLocation.fromNamespaceAndPath(AlekiShips.MOD_ID,
                                "entity/watercraft/sloop/" + wood.getSerializedName()), sloopPaint));
            }
        }
    }
}