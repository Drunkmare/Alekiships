package com.alekiponi.alekiships;

import com.alekiponi.alekiships.client.AlekiShipsClientEvents;
import com.alekiponi.alekiships.common.block.AlekiShipsBlocks;
import com.alekiponi.alekiships.common.entity.AlekiShipsEntities;
import com.alekiponi.alekiships.common.item.AlekiShipsItems;
import com.alekiponi.alekiships.common.item.AlekiShipsTabs;
import com.alekiponi.alekiships.common.sounds.AlekiShipsJukeboxSongs;
import com.alekiponi.alekiships.common.sounds.AlekiShipsSounds;
import com.alekiponi.alekiships.events.config.AlekishipsConfig;
import com.alekiponi.alekiships.network.AlekiShipsEntityDataSerializers;
import com.alekiponi.alekiships.network.PacketHandler;
import com.alekiponi.alekiships.util.VanillaWood;
import com.alekiponi.alekiships.util.advancements.AlekiShipsAdvancements;
import com.alekiponi.alekiships.wind.OverworldWindModel;
import com.alekiponi.alekiships.wind.WindModels;
import com.mojang.logging.LogUtils;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;

@Mod(AlekiShips.MOD_ID)
public final class AlekiShips {
    public static final String MOD_ID = "alekiships";

    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public AlekiShips(final IEventBus modBus, final Dist dist) {
        AlekiShipsTabs.CREATIVE_MODE_TABS.register(modBus);
        AlekiShipsItems.ITEMS.register(modBus);
        AlekiShipsBlocks.BLOCKS.register(modBus);
        AlekiShipsEntities.ENTITY_TYPES.register(modBus);
        AlekiShipsEntityDataSerializers.ENTITY_DATA_SERIALIZERS.register(modBus);
        AlekiShipsSounds.SOUNDS.register(modBus);
        AlekiShipsJukeboxSongs.SONGS.register(modBus);
        AlekiShipsAdvancements.TRIGGERS.register(modBus);

        modBus.addListener(this::setup);
        AlekishipsConfig.init();
        PacketHandler.init();

        if (dist == Dist.CLIENT) {
            AlekiShipsClientEvents.init(modBus);
        }
    }

    private void setup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            VanillaWood.registerFrames();

            WindModels.register(Level.OVERWORLD, OverworldWindModel::new);
        });
    }
}