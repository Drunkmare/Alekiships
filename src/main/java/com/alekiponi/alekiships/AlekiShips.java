package com.alekiponi.alekiships;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import com.alekiponi.alekiships.client.AlekiShipsClientEvents;
import com.alekiponi.alekiships.client.AlekiShipsClientForgeEvents;
import com.alekiponi.alekiships.common.AlekiShipsAttachments;
import com.alekiponi.alekiships.common.AlekiShipsBuiltInRegistries;
import com.alekiponi.alekiships.common.block.AlekiShipsBlocks;
import com.alekiponi.alekiships.common.entity.AlekiShipsEntities;
import com.alekiponi.alekiships.common.entity.compartment.AlekiShipsCompartmentTypes;
import com.alekiponi.alekiships.common.item.AlekiShipsItems;
import com.alekiponi.alekiships.common.item.AlekiShipsTabs;
import com.alekiponi.alekiships.common.item.components.AlekiShipsComponents;
import com.alekiponi.alekiships.common.item.components.BlockCompartmentData;
import com.alekiponi.alekiships.common.item.components.ChestCompartmentData;
import com.alekiponi.alekiships.common.item.components.CompartmentPlaceable;
import com.alekiponi.alekiships.common.sounds.AlekiShipsSounds;
import com.alekiponi.alekiships.events.ForgeEventHandler;
import com.alekiponi.alekiships.events.config.AlekishipsConfig;
import com.alekiponi.alekiships.network.AlekiShipsEntityDataSerializers;
import com.alekiponi.alekiships.network.PacketHandler;
import com.alekiponi.alekiships.util.VanillaWood;
import com.alekiponi.alekiships.util.advancements.AlekiShipsAdvancements;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ShulkerBoxBlock;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;

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
        AlekiShipsComponents.COMPONENTS.register(modBus);
        AlekiShipsBlocks.BLOCKS.register(modBus);
        AlekiShipsEntities.ENTITY_TYPES.register(modBus);
        AlekiShipsEntityDataSerializers.ENTITY_DATA_SERIALIZERS.register(modBus);
        AlekiShipsAttachments.ATTACHMENT_TYPES.register(modBus);
        AlekiShipsSounds.SOUNDS.register(modBus);
        AlekiShipsAdvancements.TRIGGERS.register(modBus);
        AlekiShipsCompartmentTypes.COMPARTMENT_TYPES.register(modBus);
        modBus.addListener(AlekiShipsBuiltInRegistries::registerRegistries);
        modBus.addListener(AlekiShipsBuiltInRegistries::registerDatapackRegistries);

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
    private static void setup(final FMLCommonSetupEvent event) {
        event.enqueueWork(VanillaWood::registerFrames);
    }

    @SubscribeEvent
    public static void modifyComponents(final ModifyDefaultComponentsEvent event) {
        event.modify(Items.BARREL, builder -> builder.set(AlekiShipsComponents.COMPARTMENT_PLACEABLE.get(),
                        new CompartmentPlaceable(AlekiShipsCompartmentTypes.BARREL_COMPARTMENT.get()))
                .set(AlekiShipsComponents.BLOCK_COMPARTMENT_DATA.get(), new BlockCompartmentData(
                        Blocks.BARREL.defaultBlockState().setValue(BarrelBlock.FACING, Direction.UP))));

        event.modify(Items.CHEST, builder -> builder.set(AlekiShipsComponents.COMPARTMENT_PLACEABLE.get(),
                        new CompartmentPlaceable(AlekiShipsCompartmentTypes.CHEST_COMPARTMENT.get()))
                .set(AlekiShipsComponents.CHEST_COMPARTMENT_DATA.get(), ChestCompartmentData.VANILLA_CHEST_NORMAL));

        event.modify(Items.TRAPPED_CHEST, builder -> builder.set(AlekiShipsComponents.COMPARTMENT_PLACEABLE.get(),
                        new CompartmentPlaceable(AlekiShipsCompartmentTypes.CHEST_COMPARTMENT.get()))
                .set(AlekiShipsComponents.CHEST_COMPARTMENT_DATA.get(), ChestCompartmentData.VANILLA_CHEST_TRAPPED));

        event.modify(Items.ENDER_CHEST, builder -> builder.set(AlekiShipsComponents.COMPARTMENT_PLACEABLE.get(),
                new CompartmentPlaceable(AlekiShipsCompartmentTypes.ENDER_CHEST_COMPARTMENT.get())));

        event.modify(Items.SHULKER_BOX, builder -> builder.set(AlekiShipsComponents.COMPARTMENT_PLACEABLE.get(),
                new CompartmentPlaceable(AlekiShipsCompartmentTypes.SHULKER_BOX_COMPARTMENT.get())));
        for (final DyeColor value : DyeColor.values()) {
            event.modify(ShulkerBoxBlock.getBlockByColor(value).asItem(),
                    builder -> builder.set(AlekiShipsComponents.COMPARTMENT_PLACEABLE.get(),
                            new CompartmentPlaceable(AlekiShipsCompartmentTypes.SHULKER_BOX_COMPARTMENT.get())));
        }

        event.modify(Items.FURNACE, builder -> builder.set(AlekiShipsComponents.COMPARTMENT_PLACEABLE.get(),
                        new CompartmentPlaceable(AlekiShipsCompartmentTypes.FURNACE_COMPARTMENT.get()))
                .set(AlekiShipsComponents.BLOCK_COMPARTMENT_DATA.get(), new BlockCompartmentData(Blocks.FURNACE)));
        event.modify(Items.BLAST_FURNACE, builder -> builder.set(AlekiShipsComponents.COMPARTMENT_PLACEABLE.get(),
                        new CompartmentPlaceable(AlekiShipsCompartmentTypes.BLAST_FURNACE_COMPARTMENT.get()))
                .set(AlekiShipsComponents.BLOCK_COMPARTMENT_DATA.get(),
                        new BlockCompartmentData(Blocks.BLAST_FURNACE)));
        event.modify(Items.SMOKER, builder -> builder.set(AlekiShipsComponents.COMPARTMENT_PLACEABLE.get(),
                        new CompartmentPlaceable(AlekiShipsCompartmentTypes.SMOKER_COMPARTMENT.get()))
                .set(AlekiShipsComponents.BLOCK_COMPARTMENT_DATA.get(), new BlockCompartmentData(Blocks.SMOKER)));

        event.modify(Items.BREWING_STAND, builder -> builder.set(AlekiShipsComponents.COMPARTMENT_PLACEABLE.get(),
                        new CompartmentPlaceable(AlekiShipsCompartmentTypes.BREWING_STAND_COMPARTMENT.get()))
                .set(AlekiShipsComponents.BLOCK_COMPARTMENT_DATA.get(),
                        new BlockCompartmentData(Blocks.BREWING_STAND)));

        event.modify(Items.CRAFTING_TABLE, builder -> builder.set(AlekiShipsComponents.COMPARTMENT_PLACEABLE.get(),
                        new CompartmentPlaceable(AlekiShipsCompartmentTypes.CRAFTING_TABLE_COMPARTMENT.get()))
                .set(AlekiShipsComponents.BLOCK_COMPARTMENT_DATA.get(),
                        new BlockCompartmentData(Blocks.CRAFTING_TABLE)));
        event.modify(Items.STONECUTTER, builder -> builder.set(AlekiShipsComponents.COMPARTMENT_PLACEABLE.get(),
                        new CompartmentPlaceable(AlekiShipsCompartmentTypes.STONECUTTER_COMPARTMENT.get()))
                .set(AlekiShipsComponents.BLOCK_COMPARTMENT_DATA.get(), new BlockCompartmentData(Blocks.STONECUTTER)));
        event.modify(Items.CARTOGRAPHY_TABLE, builder -> builder.set(AlekiShipsComponents.COMPARTMENT_PLACEABLE.get(),
                        new CompartmentPlaceable(AlekiShipsCompartmentTypes.CARTOGRAPHY_TABLE_COMPARTMENT.get()))
                .set(AlekiShipsComponents.BLOCK_COMPARTMENT_DATA.get(),
                        new BlockCompartmentData(Blocks.CARTOGRAPHY_TABLE)));
        event.modify(Items.SMITHING_TABLE, builder -> builder.set(AlekiShipsComponents.COMPARTMENT_PLACEABLE.get(),
                        new CompartmentPlaceable(AlekiShipsCompartmentTypes.SMITHING_TABLE_COMPARTMENT.get()))
                .set(AlekiShipsComponents.BLOCK_COMPARTMENT_DATA.get(),
                        new BlockCompartmentData(Blocks.SMITHING_TABLE)));
        event.modify(Items.GRINDSTONE, builder -> builder.set(AlekiShipsComponents.COMPARTMENT_PLACEABLE.get(),
                        new CompartmentPlaceable(AlekiShipsCompartmentTypes.GRINDSTONE_COMPARTMENT.get()))
                .set(AlekiShipsComponents.BLOCK_COMPARTMENT_DATA.get(), new BlockCompartmentData(Blocks.GRINDSTONE)));
        event.modify(Items.LOOM, builder -> builder.set(AlekiShipsComponents.COMPARTMENT_PLACEABLE.get(),
                        new CompartmentPlaceable(AlekiShipsCompartmentTypes.LOOM_COMPARTMENT.get()))
                .set(AlekiShipsComponents.BLOCK_COMPARTMENT_DATA.get(), new BlockCompartmentData(Blocks.LOOM)));

        event.modify(Items.NOTE_BLOCK, builder -> builder.set(AlekiShipsComponents.COMPARTMENT_PLACEABLE.get(),
                        new CompartmentPlaceable(AlekiShipsCompartmentTypes.NOTE_BLOCK_COMPARTMENT.get()))
                .set(AlekiShipsComponents.BLOCK_COMPARTMENT_DATA.get(), new BlockCompartmentData(Blocks.NOTE_BLOCK)));
        event.modify(Items.JUKEBOX, builder -> builder.set(AlekiShipsComponents.COMPARTMENT_PLACEABLE.get(),
                        new CompartmentPlaceable(AlekiShipsCompartmentTypes.JUKEBOX_COMPARTMENT.get()))
                .set(AlekiShipsComponents.BLOCK_COMPARTMENT_DATA.get(), new BlockCompartmentData(Blocks.JUKEBOX)));
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