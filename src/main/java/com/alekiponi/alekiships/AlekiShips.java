package com.alekiponi.alekiships;

import com.alekiponi.alekiships.client.AlekiShipsClientEvents;
import com.alekiponi.alekiships.client.AlekiShipsClientForgeEvents;
import com.alekiponi.alekiships.common.block.AlekiShipsBlocks;
import com.alekiponi.alekiships.common.entity.AlekiShipsEntities;
import com.alekiponi.alekiships.common.item.AlekiShipsItems;
import com.alekiponi.alekiships.common.item.AlekiShipsTabs;
import com.alekiponi.alekiships.common.sounds.AlekiShipsJukeboxSongs;
import com.alekiponi.alekiships.common.sounds.AlekiShipsSounds;
import com.alekiponi.alekiships.events.ForgeEventHandler;
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
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;
import org.slf4j.Logger;

import java.util.List;

@Mod(AlekiShips.MOD_ID)
public final class AlekiShips {
    public static final String MOD_ID = "alekiships";

    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public AlekiShips(final ModContainer modContainer, final IEventBus modBus, final Dist dist) {
        modBus.addListener(PacketHandler::init);
        modBus.register(AlekiShips.class);

        modContainer.registerConfig(ModConfig.Type.CLIENT, AlekishipsConfig.CLIENT_SPEC);
        modContainer.registerConfig(ModConfig.Type.SERVER, AlekishipsConfig.SERVER_SPEC);

        AlekiShipsTabs.CREATIVE_MODE_TABS.register(modBus);
        AlekiShipsItems.ITEMS.register(modBus);
        AlekiShipsBlocks.BLOCKS.register(modBus);
        AlekiShipsEntities.ENTITY_TYPES.register(modBus);
        AlekiShipsEntityDataSerializers.ENTITY_DATA_SERIALIZERS.register(modBus);
        AlekiShipsSounds.SOUNDS.register(modBus);
        AlekiShipsJukeboxSongs.SONGS.register(modBus);
        AlekiShipsAdvancements.TRIGGERS.register(modBus);

        NeoForge.EVENT_BUS.register(ForgeEventHandler.class);

        if (dist == Dist.CLIENT) {
            AlekiShipsClientEvents.init(modBus);
            AlekiShipsClientForgeEvents.init(NeoForge.EVENT_BUS);
        }
    }

    @SubscribeEvent
    private static void setup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            VanillaWood.registerFrames();

            WindModels.register(Level.OVERWORLD, OverworldWindModel::new);
        });
    }

    @SubscribeEvent
    private static void registerCapabilities(final RegisterCapabilitiesEvent event) {
        final var nonSidedContainerCompartments = List.of(AlekiShipsEntities.CHEST_COMPARTMENT_ENTITY,
                AlekiShipsEntities.BARREL_COMPARTMENT_ENTITY);
        for (final var supplier : nonSidedContainerCompartments) {
            event.registerEntity(Capabilities.ItemHandler.ENTITY, supplier.get(),
                    (containerCompartment, unused) -> new InvWrapper(containerCompartment));
            event.registerEntity(Capabilities.ItemHandler.ENTITY_AUTOMATION, supplier.get(),
                    (containerCompartment, unused) -> new InvWrapper(containerCompartment));
        }

        final var sidedContainerCompartments = List.of(AlekiShipsEntities.FURNACE_COMPARTMENT_ENTITY,
                AlekiShipsEntities.BLAST_FURNACE_COMPARTMENT_ENTITY, AlekiShipsEntities.SMOKER_COMPARTMENT_ENTITY,
                AlekiShipsEntities.BREWING_STAND_COMPARTMENT_ENTITY);
        for (final var sidedContainerCompartment : sidedContainerCompartments) {
            event.registerEntity(Capabilities.ItemHandler.ENTITY, sidedContainerCompartment.get(),
                    (containerCompartment, unused) -> new InvWrapper(containerCompartment));
            event.registerEntity(Capabilities.ItemHandler.ENTITY_AUTOMATION, sidedContainerCompartment.get(),
                    (containerCompartment, side) -> side == null ? new InvWrapper(
                            containerCompartment) : new SidedInvWrapper(containerCompartment, side));
        }

        event.registerEntity(Capabilities.ItemHandler.ENTITY, AlekiShipsEntities.SHULKER_BOX_COMPARTMENT_ENTITY.get(),
                (containerCompartment, unused) -> new InvWrapper(containerCompartment));
        event.registerEntity(Capabilities.ItemHandler.ENTITY_AUTOMATION,
                AlekiShipsEntities.SHULKER_BOX_COMPARTMENT_ENTITY.get(), SidedInvWrapper::new);
    }
}