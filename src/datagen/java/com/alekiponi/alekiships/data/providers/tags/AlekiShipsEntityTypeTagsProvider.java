package com.alekiponi.alekiships.data.providers.tags;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.common.entity.AlekiShipsEntities;
import com.alekiponi.alekiships.util.AlekiShipsTags;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import org.jetbrains.annotations.Nullable;

public class AlekiShipsEntityTypeTagsProvider extends EntityTypeTagsProvider {

    public AlekiShipsEntityTypeTagsProvider(final PackOutput packOutput,
            final CompletableFuture<HolderLookup.Provider> lookupProvider,
            @Nullable final ExistingFileHelper existingFileHelper) {
        super(packOutput, lookupProvider, AlekiShips.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(final HolderLookup.Provider provider) {
        this.tag(AlekiShipsTags.Entities.VEHICLE_HELPERS)
                .add(AlekiShipsEntities.VEHICLE_PART.get(), AlekiShipsEntities.VEHICLE_CLEAT_ENTITY.get(),
                        AlekiShipsEntities.VEHICLE_COLLIDER_ENTITY.get(), AlekiShipsEntities.SAIL_SWITCH_ENTITY.get(),
                        AlekiShipsEntities.ANCHOR_ENTITY.get(), AlekiShipsEntities.CONSTRUCTION_ENTITY.get(),
                        AlekiShipsEntities.WINDLASS_SWITCH_ENTITY.get(), AlekiShipsEntities.MAST_ENTITY.get());

        // Compartments
        this.tag(AlekiShipsTags.Entities.COMPARTMENTS).add(AlekiShipsEntities.EMPTY_COMPARTMENT_ENTITY.get(),
                AlekiShipsEntities.BLOCK_COMPARTMENT_ENTITY.get(), AlekiShipsEntities.BARREL_COMPARTMENT_ENTITY.get(),
                AlekiShipsEntities.CHEST_COMPARTMENT_ENTITY.get(),
                AlekiShipsEntities.ENDER_CHEST_COMPARTMENT_ENTITY.get(),
                AlekiShipsEntities.SHULKER_BOX_COMPARTMENT_ENTITY.get(),
                AlekiShipsEntities.FURNACE_COMPARTMENT_ENTITY.get(),
                AlekiShipsEntities.BLAST_FURNACE_COMPARTMENT_ENTITY.get(),
                AlekiShipsEntities.SMOKER_COMPARTMENT_ENTITY.get(),
                AlekiShipsEntities.BREWING_STAND_COMPARTMENT_ENTITY.get(),
                AlekiShipsEntities.WORKBENCH_COMPARTMENT_ENTITY.get(),
                AlekiShipsEntities.STONECUTTER_COMPARTMENT_ENTITY.get(),
                AlekiShipsEntities.CARTOGRAPHY_TABLE_COMPARTMENT_ENTITY.get(),
                AlekiShipsEntities.SMITHING_TABLE_COMPARTMENT_ENTITY.get(),
                AlekiShipsEntities.NOTE_BLOCK_COMPARTMENT_ENTITY.get(),
                AlekiShipsEntities.JUKEBOX_COMPARTMENT_ENTITY.get());

        // Carryon blacklist tags (as of writing carryon has a bug which means these are ignored)
        this.tag(TagKey.create(Registries.ENTITY_TYPE,
                        ResourceLocation.fromNamespaceAndPath("carryon", "entity_blacklist")))
                .add(AlekiShipsEntities.CANNONBALL_ENTITY.get()).add(AlekiShipsEntities.ROWBOAT.get())
                .add(AlekiShipsEntities.SLOOP.get()).addTag(AlekiShipsTags.Entities.VEHICLE_HELPERS)
                .addTag(AlekiShipsTags.Entities.COMPARTMENTS)
                .add(AlekiShipsEntities.SLOOPS_UNDER_CONSTRUCTION.values().stream().map(Supplier::get)
                        .toArray(EntityType[]::new));
    }
}