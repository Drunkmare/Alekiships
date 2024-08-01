package com.alekiponi.alekiships.client;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.client.resources.PaintedTextureGenerator;
import com.alekiponi.alekiships.util.VanillaWood;
import net.minecraft.resources.ResourceLocation;
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

    }

    private static void onRegisterReloadListeners(final RegisterClientReloadListenersEvent event) {
        {
            final ResourceLocation rowboatPaint = new ResourceLocation(AlekiShips.MOD_ID,
                    "entity/watercraft/rowboat/paint");
            final ResourceLocation sloopPaint = new ResourceLocation(AlekiShips.MOD_ID,
                    "entity/watercraft/sloop/paint");

            for (final VanillaWood wood : VanillaWood.values()) {
                event.registerReloadListener(new PaintedTextureGenerator(new ResourceLocation(AlekiShips.MOD_ID,
                        "entity/watercraft/rowboat/" + wood.getSerializedName()), rowboatPaint));
                event.registerReloadListener(new PaintedTextureGenerator(
                        new ResourceLocation(AlekiShips.MOD_ID, "entity/watercraft/sloop/" + wood.getSerializedName()),
                        sloopPaint));
            }
        }
    }
}
