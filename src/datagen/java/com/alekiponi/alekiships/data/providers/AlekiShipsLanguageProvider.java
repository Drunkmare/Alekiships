package com.alekiponi.alekiships.data.providers;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.common.block.AlekiShipsBlocks;
import com.alekiponi.alekiships.common.entity.AlekiShipsEntities;
import com.alekiponi.alekiships.common.item.AlekiShipsItems;
import com.alekiponi.alekiships.data.DataGenHelper;
import com.alekiponi.alekiships.data.SmartLanguageProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Locale;
import java.util.stream.Stream;

public class AlekiShipsLanguageProvider extends SmartLanguageProvider {

    public AlekiShipsLanguageProvider(final PackOutput output) {
        super(output, AlekiShips.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations() {
        this.addTranslationsForBlocks();
        this.addTranslationsForItems();
        this.addTranslationsForEntities();

        this.add("creativetab.alekiships_tab", "aleki's Nifty Ships");
        this.add("alekiships.failed_multiblock_detection", "No Valid Hull Structure Found");
        this.add("alekiships.config.server.windAffectsBoatsWithNoAnchor", "Wind Affects Boats With No Anchor");

        this.add("press_button", "Press");
        this.add("eject_passengers", "to eject");


        this.addTranslationsForJade();

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

        this.add("item.alekiships.music_disc_pirate_crafting.desc", "Captain Thrack - Pirate Crafting");
        this.addItem(AlekiShipsItems.MUSIC_DISC_PIRATE_CRAFTING, "Music Disc");
    }

    private void addTranslationsForEntities() {
        // Vehicles
        AlekiShipsEntities.ROWBOATS.forEach((wood, registryObject) -> this.addEntityType(registryObject,
                String.format(Locale.ROOT, "%s Rowboat", DataGenHelper.langify(wood.getSerializedName()))));
        AlekiShipsEntities.SLOOPS.forEach((wood, registryObject) -> this.addEntityType(registryObject,
                String.format(Locale.ROOT, "%s Sloop", DataGenHelper.langify(wood.getSerializedName()))));
        AlekiShipsEntities.SLOOPS_UNDER_CONSTRUCTION.forEach(
                (wood, registryObject) -> this.addEntityType(registryObject,
                        String.format(Locale.ROOT, "%s Sloop", DataGenHelper.langify(wood.getSerializedName()))));

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

    private void addTranslationsForJade() {
        this.add("config.jade.plugin_alekiships.jukebox", "Jukebox Compartment");
        this.add("config.jade.plugin_alekiships.note_block", "Note Block Compartment");
        this.add("config.jade.plugin_alekiships.furnace", "Furnace Compartment");
        this.add("config.jade.plugin_alekiships.brewing_stand", "Brewing Stand Compartment");
        this.add("config.jade.plugin_alekiships.block", "Block Compartment");

        this.add("alekiships.jade.compartment_block", "%s Compartment");
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        final Stream<Block> objectStream = AlekiShipsBlocks.BLOCKS.getEntries().stream().map(DeferredHolder::get);
        return objectStream::iterator;
    }

    @Override
    protected Iterable<Item> getKnownItems() {
        final Stream<Item> stream = AlekiShipsItems.ITEMS.getEntries().stream().map(DeferredHolder::get);
        return stream::iterator;
    }

    @Override
    protected Iterable<EntityType<?>> getKnownEntityTypes() {
        return AlekiShipsEntities.ENTITY_TYPES.getEntries().stream().<EntityType<?>>map(DeferredHolder::get)::iterator;
    }

    /**
     * Used to allow translations used in advancements to be defined where the advancements are.
     */
    public interface TranslationWriter {
        void add(String key, String value);
    }
}