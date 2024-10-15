package com.alekiponi.alekiships;

import com.alekiponi.alekiships.client.AlekiShipsClientEvents;
import com.alekiponi.alekiships.client.AlekiShipsSounds;
import com.alekiponi.alekiships.common.block.AlekiShipsBlocks;
import com.alekiponi.alekiships.common.entity.AlekiShipsEntities;
import com.alekiponi.alekiships.common.item.AlekiShipsItems;
import com.alekiponi.alekiships.common.item.AlekiShipsTabs;
import com.alekiponi.alekiships.events.config.AlekishipsConfig;
import com.alekiponi.alekiships.network.AlekiShipsEntityDataSerializers;
import com.alekiponi.alekiships.network.PacketHandler;
import com.alekiponi.alekiships.util.VanillaWood;
import com.alekiponi.alekiships.util.advancements.AlekiShipsAdvancements;
import com.alekiponi.alekiships.wind.OverworldWindModel;
import com.alekiponi.alekiships.wind.WindModels;
import com.mojang.logging.LogUtils;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.slf4j.Logger;


@Mod(AlekiShips.MOD_ID)
public class AlekiShips {
    public static final String MOD_ID = "alekiships";

    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public AlekiShips() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        AlekiShipsTabs.CREATIVE_MODE_TABS.register(eventBus);
        AlekiShipsItems.ITEMS.register(eventBus);
        AlekiShipsBlocks.BLOCKS.register(eventBus);
        AlekiShipsEntities.ENTITY_TYPES.register(eventBus);
        AlekiShipsEntityDataSerializers.ENTITY_DATA_SERIALIZERS.register(eventBus);
        AlekiShipsSounds.SOUNDS.register(eventBus);

        eventBus.addListener(this::setup);
        AlekishipsConfig.init();
        PacketHandler.init();


        if (FMLEnvironment.dist == Dist.CLIENT) {
            AlekiShipsClientEvents.init();
        }
    }

    private void setup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            VanillaWood.registerFrames();
            AlekiShipsAdvancements.registerTriggers();

            WindModels.register(Level.OVERWORLD, OverworldWindModel::new);
        });
    }
}