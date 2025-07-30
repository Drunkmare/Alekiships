package com.alekiponi.alekiships.data.providers;

import com.google.common.collect.Iterators;
import snownee.jade.api.IJadeProvider;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.client.IngameOverlays;
import com.alekiponi.alekiships.common.block.AlekiShipsBlocks;
import com.alekiponi.alekiships.common.entity.AlekiShipsEntities;
import com.alekiponi.alekiships.common.entity.vehicle.ConstructionSloopVariant;
import com.alekiponi.alekiships.common.item.AlekiShipsItems;
import com.alekiponi.alekiships.common.sounds.AlekiShipsJukeboxSongs;
import com.alekiponi.alekiships.compat.jei.JeiIntegration;
import com.alekiponi.alekiships.compat.waila.compartment.*;
import com.alekiponi.alekiships.compat.waila.compartment.vehicle.ConstructionEntityProvider;
import com.alekiponi.alekiships.data.DataGenHelper;
import com.alekiponi.alekiships.data.SmartLanguageProvider;
import com.alekiponi.alekiships.util.*;

import net.minecraft.Util;
import net.minecraft.data.PackOutput;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Locale;

public class AlekiShipsLanguageProvider extends SmartLanguageProvider {

    public AlekiShipsLanguageProvider(final PackOutput output) {
        super(output, AlekiShips.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations() {
        this.addTranslationsForBlocks();
        this.addTranslationsForItems();
        this.addTranslationsForEntities();
        this.addTranslationsForConfig();

        this.add("creativetab.alekiships_tab", "aleki's Nifty Ships");
        this.add("alekiships.failed_multiblock_detection", "No Valid Hull Structure Found");

        this.add(IngameOverlays.EJECT_PASSENGERS_KEY, "Press %s + %s to eject");

        // Our wood types must be named so our entities can reflect their name
        Iterators.<Wood>concat(Iterators.forArray(OverworldWood.values()), Iterators.forArray(NetherWood.values()))
                .forEachRemaining(wood -> {
                    final var id = AlekiShips.location(wood.getSerializedName());
                    final var name = DataGenHelper.langify(wood.getSerializedName());
                    this.add(DynamicBoatMaterial.getDescriptionId(id), name);
                    this.add(ConstructionSloopVariant.getDescriptionId(id), name);
                    this.add(FrameMaterial.getDescriptionId(id), name);
                });

        this.addTranslationsForJade();
        this.addTranslationsForJei();

        AlekiShipsAdvancementsProvider.addTranslations(this::add);
    }

    private void addTranslationsForBlocks() {
        AlekiShipsBlocks.WOODEN_BOAT_FRAME_FLAT.forEach((wood, registryObject) -> this.addBlock(registryObject,
                String.format(Locale.ROOT, "%s Flat Shipwright's Scaffolding",
                        DataGenHelper.langify(wood.getSerializedName()))));

        AlekiShipsBlocks.WOODEN_BOAT_FRAME_ANGLED.forEach((wood, registryObject) -> this.addBlock(registryObject,
                String.format(Locale.ROOT, "%s Sloped Shipwright's Scaffolding",
                        DataGenHelper.langify(wood.getSerializedName()))));

        this.addBlock(AlekiShipsBlocks.BOAT_FRAME_ANGLED, "Sloped Shipwright's Scaffolding");
        this.addBlock(AlekiShipsBlocks.BOAT_FRAME_FLAT, "Flat Shipwright's Scaffolding");
        this.addBlock(AlekiShipsBlocks.OARLOCK, "Oarlock");
        this.addBlock(AlekiShipsBlocks.CLEAT, "Cleat");
    }

    private void addTranslationsForItems() {
        this.addItem(AlekiShipsItems.CANNON, "Cannon");
        this.addItem(AlekiShipsItems.CANNONBALL, "Cannonball");
        this.addItem(AlekiShipsItems.ANCHOR, "Anchor");
        this.addItem(AlekiShipsItems.SLOOP_ICON_ONLY, "Sloop (ICON ONLY)");
        this.addItem(AlekiShipsItems.ROWBOAT_ICON_ONLY, "Rowboat (ICON ONLY)");
        this.addItem(AlekiShipsItems.OAR, "Oar");

        this.add(Util.makeDescriptionId("jukebox_song", AlekiShipsJukeboxSongs.PIRATE_CRAFTING.location()),
                "Captain Thrack - Pirate Crafting");
        this.addItem(AlekiShipsItems.MUSIC_DISC_PIRATE_CRAFTING, "Music Disc");
    }

    private void addTranslationsForEntities() {
        // Vehicles
        this.addEntityType(AlekiShipsEntities.ROWBOAT, "%s Rowboat");
        this.addEntityType(AlekiShipsEntities.SLOOP, "%s Sloop");
        this.addEntityType(AlekiShipsEntities.CONSTRUCTION_SLOOP, "%s Construction Sloop");

        // Misc
        this.addEntityType(AlekiShipsEntities.VEHICLE_PART, "Vehicle Part");
        this.addEntityType(AlekiShipsEntities.VEHICLE_CLEAT_ENTITY, "Cleat");
        this.addEntityType(AlekiShipsEntities.WINDLASS_SWITCH_ENTITY, "Windlass");
        this.addEntityType(AlekiShipsEntities.SAIL_SWITCH_ENTITY, "Sail");
        this.addEntityType(AlekiShipsEntities.VEHICLE_COLLIDER_ENTITY, "Vehicle");
        this.addEntityType(AlekiShipsEntities.CONSTRUCTION_ENTITY, "Construction Entity");
        this.addEntityType(AlekiShipsEntities.MAST_ENTITY, "Mast");
        this.addEntityType(AlekiShipsEntities.ANCHOR_ENTITY, "Anchor");

        this.addEntityType(AlekiShipsEntities.CANNONBALL_ENTITY, "Cannonball");
        this.addEntityType(AlekiShipsEntities.CANNON_ENTITY, "Cannon");

        // Compartments
        this.addEntityType(AlekiShipsEntities.EMPTY_COMPARTMENT_ENTITY, "Empty Compartment");
        this.addEntityType(AlekiShipsEntities.BLOCK_COMPARTMENT_ENTITY, "Block Compartment");
        this.addEntityType(AlekiShipsEntities.BARREL_COMPARTMENT_ENTITY, "Barrel Compartment");
        this.addEntityType(AlekiShipsEntities.CHEST_COMPARTMENT_ENTITY, "Chest Compartment");
        this.addEntityType(AlekiShipsEntities.ENDER_CHEST_COMPARTMENT_ENTITY, "Ender Chest Compartment");
        this.addEntityType(AlekiShipsEntities.SHULKER_BOX_COMPARTMENT_ENTITY, "Shulker Box Compartment");
        this.addEntityType(AlekiShipsEntities.FURNACE_COMPARTMENT_ENTITY, "Furnace Compartment");
        this.addEntityType(AlekiShipsEntities.BLAST_FURNACE_COMPARTMENT_ENTITY, "Blast Furnace Compartment");
        this.addEntityType(AlekiShipsEntities.SMOKER_COMPARTMENT_ENTITY, "Smoker Compartment");
        this.addEntityType(AlekiShipsEntities.BREWING_STAND_COMPARTMENT_ENTITY, "Brewing Stand Compartment");
        this.addEntityType(AlekiShipsEntities.WORKBENCH_COMPARTMENT_ENTITY, "Workbench Compartment");
        this.addEntityType(AlekiShipsEntities.STONECUTTER_COMPARTMENT_ENTITY, "Stonecutter Compartment");
        this.addEntityType(AlekiShipsEntities.CARTOGRAPHY_TABLE_COMPARTMENT_ENTITY, "Cartography Table Compartment");
        this.addEntityType(AlekiShipsEntities.SMITHING_TABLE_COMPARTMENT_ENTITY, "Smithing Table Compartment");
        this.addEntityType(AlekiShipsEntities.GRINDSTONE_COMPARTMENT_ENTITY, "Grindstone Compartment");
        this.addEntityType(AlekiShipsEntities.LOOM_COMPARTMENT_ENTITY, "Loom Compartment");
        this.addEntityType(AlekiShipsEntities.NOTE_BLOCK_COMPARTMENT_ENTITY, "Note Block Compartment");
        this.addEntityType(AlekiShipsEntities.JUKEBOX_COMPARTMENT_ENTITY, "Jukebox Compartment");
    }

    private void addTranslationsForConfig() {
        // It feels like there should be a better way to do this so that we stay in sync with changes to the config :|
        this.add("alekiships.config.server.windAffectsBoatsWithNoAnchor", "Wind Affects Boats With No Anchor");
        this.add("alekiships.config.client.tillerControlScheme", "Tiller Control Scheme");
    }

    private void addTranslationsForJade() {
        this.jade(JukeboxCompartmentProvider.INSTANCE, "Jukebox Compartment");
        this.jade(NoteBlockCompartmentProvider.INSTANCE, "Note Block Compartment");
        this.jade(FurnaceCompartmentProvider.INSTANCE, "Furnace Compartment");
        this.jade(BrewingStandCompartmentProvider.INSTANCE, "Brewing Stand Compartment");
        this.jade(BlockCompartmentProvider.INSTANCE, "Block Compartment");
        this.jade(ConstructionEntityProvider.INSTANCE, "Construction Entity");

        this.add(BlockCompartmentProvider.COMPARTMENT_BLOCK_KEY, "%s Compartment");
        this.add(ConstructionEntityProvider.INPUTS_REMAINING_KEY, "Inputs remaining: %s");
        this.add(ConstructionEntityProvider.CURRENT_STAGE_KEY, "Current Stage: %s");
        this.add(ConstructionEntityProvider.NEXT_STAGE_KEY, "Next Stage: %s");
    }

    private void addTranslationsForJei() {
        this.add(JeiIntegration.CAN_PLACE_INTO_COMPARTMENTS_KEY, "Can be placed into compartments");
        this.add(JeiIntegration.CAN_BE_USED_TO_DYE_SHIPS_SAILS_KEY, "Can be used to dye ships & sails");
    }

    private void jade(final IJadeProvider provider, final String value) {
        this.add("config.jade.plugin_" + provider.getUid().toLanguageKey(), value);
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return AlekiShipsBlocks.BLOCKS.getEntries()
                .stream()
                .<Block>map(DeferredHolder::get)::iterator;
    }

    @Override
    protected Iterable<Item> getKnownItems() {
        return AlekiShipsItems.ITEMS.getEntries()
                .stream()
                .<Item>map(DeferredHolder::get)::iterator;
    }

    @Override
    protected Iterable<EntityType<?>> getKnownEntityTypes() {
        return AlekiShipsEntities.ENTITY_TYPES.getEntries()
                .stream()
                .<EntityType<?>>map(DeferredHolder::get)::iterator;
    }

    /**
     * Used to allow translations used in advancements to be defined where the advancements are.
     */
    public interface TranslationWriter {
        void add(String key, String value);
    }
}