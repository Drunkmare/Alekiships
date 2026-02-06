package com.alekiponi.alekiships.data.loot;

import com.alekiponi.alekiships.common.block.AlekiShipsBlocks;
import com.alekiponi.alekiships.common.block.AngledWoodenBoatFrameBlock;
import com.alekiponi.alekiships.common.block.FlatWoodenBoatFrameBlock;
import com.alekiponi.alekiships.data.util.FrameBlockHelper;
import com.alekiponi.alekiships.util.Wood;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;

import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class AlekiShipsBlockLootTables extends BlockLootSubProvider {

    public AlekiShipsBlockLootTables(final HolderLookup.Provider provider) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), provider);
    }

    @Override
    protected void generate() {
        this.dropSelf(AlekiShipsBlocks.BOAT_FRAME_ANGLED.get());
        this.dropSelf(AlekiShipsBlocks.WOODEN_BOAT_FRAME_ANGLED.get());
        this.dropSelf(AlekiShipsBlocks.WOODEN_BOAT_FRAME_FLAT.get());
        this.dropSelf(AlekiShipsBlocks.BOAT_FRAME_FLAT.get());

        this.dropSelf(AlekiShipsBlocks.OARLOCK.get());
        this.dropSelf(AlekiShipsBlocks.CLEAT.get());
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return AlekiShipsBlocks.BLOCKS.getEntries()
                .stream()
                .<Block>map(DeferredHolder::get)::iterator;
    }
}