package com.drunkmare.deremet;

import com.drunkmare.deremet.common.block.DeReMetallicaBlocks;
import com.drunkmare.deremet.common.item.DeReMetallicaItems;
import com.drunkmare.deremet.common.item.DeReMetallicaTabs;
import com.mojang.logging.LogUtils;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// Main mod class. Hooks into the Forge mod event bus and fires off all deferred register calls.
@Mod(DeReMetallica.MOD_ID)
public class DeReMetallica {
    public static final String MOD_ID = "de_re_metallica";
    public static final Logger LOGGER = LogUtils.getLogger();

    public DeReMetallica() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register all deferred registers so Forge picks up our blocks, items, and tabs.
        DeReMetallicaTabs.CREATIVE_MODE_TABS.register(eventBus);
        DeReMetallicaItems.ITEMS.register(eventBus);
        DeReMetallicaBlocks.BLOCKS.register(eventBus);
    }
}
