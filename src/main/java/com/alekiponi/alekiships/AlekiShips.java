package com.alekiponi.alekiships;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;
import weather2.Weather;

import com.alekiponi.alekiships.client.AlekiShipsClientEvents;
import com.alekiponi.alekiships.client.AlekiShipsClientForgeEvents;
import com.alekiponi.alekiships.common.AlekiShipsBuiltInRegistries;
import com.alekiponi.alekiships.common.AlekiShipsDataMaps;
import com.alekiponi.alekiships.common.block.AlekiShipsBlocks;
import com.alekiponi.alekiships.common.block.entity.AlekishipsBlockEntities;
import com.alekiponi.alekiships.common.compartment.AlekiShipsChestCompartmentTypes;
import com.alekiponi.alekiships.common.compartment.AlekiShipsCompartmentPlaceableSerializers;
import com.alekiponi.alekiships.common.compartment.AlekiShipsDirectCompartmentTypes;
import com.alekiponi.alekiships.common.entity.AlekiShipsEntities;
import com.alekiponi.alekiships.common.item.AlekiShipsItems;
import com.alekiponi.alekiships.common.item.AlekiShipsTabs;
import com.alekiponi.alekiships.common.item.components.AlekiShipsComponents;
import com.alekiponi.alekiships.common.recipe.entity.AlekiShipsEntityResultSerializers;
import com.alekiponi.alekiships.common.recipe.ingredient.block.entity.AlekiShipsBlockEntityIngredientSerializers;
import com.alekiponi.alekiships.common.sounds.AlekiShipsSounds;
import com.alekiponi.alekiships.compat.weather2.Weather2WindModelSerializers;
import com.alekiponi.alekiships.events.ForgeEventHandler;
import com.alekiponi.alekiships.events.config.AlekishipsConfig;
import com.alekiponi.alekiships.network.AlekiShipsEntityDataSerializers;
import com.alekiponi.alekiships.network.PacketHandler;
import com.alekiponi.alekiships.util.advancements.AlekiShipsAdvancements;
import com.alekiponi.alekiships.wind.AlekiShipsWindModelSerializers;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;

import java.util.List;

@Mod(AlekiShips.MOD_ID)
public final class AlekiShips {
    public static final String MOD_ID = "alekiships";
    public static final String NETHER_WOOD_PACK_KEY = MOD_ID + ".builtin.datapack.netherwood.description";

    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public AlekiShips(final ModContainer modContainer, final IEventBus modBus, final Dist dist) {
        modBus.addListener(PacketHandler::init);
        modBus.register(AlekiShips.class);

        modContainer.registerConfig(ModConfig.Type.CLIENT, AlekishipsConfig.CLIENT_SPEC);
        modContainer.registerConfig(ModConfig.Type.SERVER, AlekishipsConfig.SERVER_SPEC);

        AlekiShipsTabs.CREATIVE_MODE_TABS.register(modBus);
        AlekiShipsItems.ITEMS.register(modBus);
        AlekiShipsComponents.COMPONENTS.register(modBus);
        AlekiShipsBlocks.BLOCKS.register(modBus);
        AlekiShipsEntities.ENTITY_TYPES.register(modBus);
        AlekiShipsEntityDataSerializers.ENTITY_DATA_SERIALIZERS.register(modBus);
        AlekiShipsEntityResultSerializers.ENTITY_RESULT_SERIALIZERS.register(modBus);
        AlekiShipsSounds.SOUNDS.register(modBus);
        AlekiShipsAdvancements.TRIGGERS.register(modBus);
        AlekiShipsDirectCompartmentTypes.DIRECT_COMPARTMENTS.register(modBus);
        AlekiShipsCompartmentPlaceableSerializers.COMPARTMENT_PLACEABLE_SERIALIZERS.register(modBus);
        AlekiShipsChestCompartmentTypes.CHEST_TYPES.register(modBus);
        AlekiShipsWindModelSerializers.WIND_MODEL_SERIALIZERS.register(modBus);
        AlekiShipsBlockEntityIngredientSerializers.BLOCK_ENTITY_INGREDIENT_SERIALIZERS.register(modBus);
        AlekishipsBlockEntities.BLOCK_ENTITIES.register(modBus);

        if (ModList.get().isLoaded(Weather.MODID)) {
            Weather2WindModelSerializers.WIND_MODEL_SERIALIZERS.register(modBus);
        }

        modBus.addListener(AlekiShipsBuiltInRegistries::registerRegistries);
        modBus.addListener(AlekiShipsBuiltInRegistries::registerDatapackRegistries);
        modBus.addListener(AlekiShipsDataMaps::registerDataMaps);

        NeoForge.EVENT_BUS.register(ForgeEventHandler.class);

        if (dist == Dist.CLIENT) {
            modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
            AlekiShipsClientEvents.init(modBus);
            AlekiShipsClientForgeEvents.init(NeoForge.EVENT_BUS);
        }
    }

    /**
     * Shorthand for {@code ResourceLocation.fromNamespaceAndPath(AlekiShips.MOD_ID, path)}
     */
    public static ResourceLocation location(final String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
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

    @SubscribeEvent
    private static void addBuiltInPacks(final AddPackFindersEvent event) {
        event.addPackFinders(location("data/" + MOD_ID + "/datapacks/nether_woods"), PackType.SERVER_DATA,
                Component.translatable(NETHER_WOOD_PACK_KEY), PackSource.SERVER, false, Pack.Position.TOP);
    }
}