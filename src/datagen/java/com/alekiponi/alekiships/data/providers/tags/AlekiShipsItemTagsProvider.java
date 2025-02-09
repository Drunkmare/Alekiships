package com.alekiponi.alekiships.data.providers.tags;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.common.item.AlekiShipsItems;
import com.alekiponi.alekiships.util.AlekiShipsTags;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;
import org.jetbrains.annotations.Nullable;

public class AlekiShipsItemTagsProvider extends ItemTagsProvider {

    public AlekiShipsItemTagsProvider(final PackOutput packOutput,
            final CompletableFuture<HolderLookup.Provider> lookupProvider,
            final CompletableFuture<TagLookup<Block>> blockTags,
            @Nullable final ExistingFileHelper existingFileHelper) {
        super(packOutput, lookupProvider, blockTags, AlekiShips.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(final HolderLookup.Provider provider) {
        this.tag(Tags.Items.MUSIC_DISCS).add(AlekiShipsItems.MUSIC_DISC_PIRATE_CRAFTING.get());

        this.tag(AlekiShipsTags.Items.ICEBREAKER_UPGRADES).add(Items.IRON_BLOCK);
    }
}