package com.alekiponi.alekiships.data.providers.tags;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.util.AlekiShipsTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

public class AlekiShipsItemTagsProvider extends ItemTagsProvider {

    public AlekiShipsItemTagsProvider(final PackOutput packOutput,
            final CompletableFuture<HolderLookup.Provider> lookupProvider,
            final CompletableFuture<TagLookup<Block>> blockTags,
            @Nullable final ExistingFileHelper existingFileHelper) {
        super(packOutput, lookupProvider, blockTags, AlekiShips.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(final HolderLookup.Provider provider) {
        // Tag that allows things to go into compartments
        this.tag(AlekiShipsTags.Items.CAN_PLACE_IN_COMPARTMENTS)
                .add(Items.BARREL, Items.CHEST, Items.ENDER_CHEST, Items.FURNACE, Items.BLAST_FURNACE, Items.SMOKER,
                        Items.STONECUTTER, Items.CARTOGRAPHY_TABLE, Items.SMITHING_TABLE, Items.GRINDSTONE, Items.LOOM,
                        Items.BREWING_STAND, Items.NOTE_BLOCK, Items.JUKEBOX)
                .addTag(AlekiShipsTags.Items.CRAFTING_TABLES).addTag(AlekiShipsTags.Items.SHULKER_BOXES);

        this.tag(AlekiShipsTags.Items.CRAFTING_TABLES).add(Items.CRAFTING_TABLE);

        {
            final var shulkerBoxes = this.tag(AlekiShipsTags.Items.SHULKER_BOXES).add(Items.SHULKER_BOX);
            Arrays.stream(DyeColor.values()).map(ShulkerBoxBlock::getBlockByColor).map(ItemLike::asItem)
                    .forEach(shulkerBoxes::add);
        }

        this.tag(AlekiShipsTags.Items.ICEBREAKER_UPGRADES).add(Items.IRON_BLOCK);
    }
}