package com.alekiponi.alekiships.data.providers.tags;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.common.block.AlekiShipsBlocks;
import com.alekiponi.alekiships.util.AlekiShipsTags;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;

import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;
import org.jetbrains.annotations.Nullable;

public class AlekiShipsBlockTagsProvider extends BlockTagsProvider {

    public AlekiShipsBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
            @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, AlekiShips.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(final HolderLookup.Provider provider) {
        this.tag(AlekiShipsTags.Blocks.PLANTS_THAT_GET_MOWED).add(Blocks.KELP);

        // Vanilla mining tags
        this.tag(BlockTags.MINEABLE_WITH_AXE)
                .add(AlekiShipsBlocks.WOODEN_BOAT_FRAME_ANGLED.get(), AlekiShipsBlocks.WOODEN_BOAT_FRAME_FLAT.get());

        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(AlekiShipsBlocks.OARLOCK.get(), AlekiShipsBlocks.CLEAT.get());
    }
}