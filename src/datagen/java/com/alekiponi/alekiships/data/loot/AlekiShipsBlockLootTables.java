package com.alekiponi.alekiships.data.loot;

import com.alekiponi.alekiships.common.block.AlekiShipsBlocks;
import com.alekiponi.alekiships.common.block.AngledWoodenBoatFrameBlock;
import com.alekiponi.alekiships.common.block.FlatWoodenBoatFrameBlock;
import com.alekiponi.alekiships.data.util.FrameBlockHelper;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Set;
import java.util.function.Supplier;

public class AlekiShipsBlockLootTables extends BlockLootSubProvider {

    public AlekiShipsBlockLootTables(final HolderLookup.Provider provider) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), provider);
    }

    @Override
    protected void generate() {
        this.dropSelf(AlekiShipsBlocks.BOAT_FRAME_ANGLED.get());

        AlekiShipsBlocks.WOODEN_BOAT_FRAME_ANGLED.forEach(
                (vanillaWood, registryObject) -> dropAngledWoodenFrame(registryObject, vanillaWood.getPlankItem()));
        AlekiShipsBlocks.WOODEN_BOAT_FRAME_FLAT.forEach(
                (vanillaWood, registryObject) -> dropFlatWoodenFrame(registryObject, vanillaWood.getPlankItem()));

        this.dropSelf(AlekiShipsBlocks.BOAT_FRAME_FLAT.get());

        this.dropSelf(AlekiShipsBlocks.OARLOCK.get());
        this.dropSelf(AlekiShipsBlocks.CLEAT.get());
    }

    private void dropAngledWoodenFrame(final Supplier<AngledWoodenBoatFrameBlock> angledBoatFrame,
            final Item plankItem) {
        final var frameBlock = angledBoatFrame.get();
        this.add(frameBlock, this.createSingleItemTable(AlekiShipsBlocks.BOAT_FRAME_ANGLED.get()).withPool(
                this.applyExplosionCondition(plankItem,
                        FrameBlockHelper.createProcessedFrameTable(frameBlock, plankItem, 0,
                                AngledWoodenBoatFrameBlock.FULLY_PROCESSED,
                                AngledWoodenBoatFrameBlock.FRAME_PROCESSED))));
    }

    private void dropFlatWoodenFrame(final Supplier<FlatWoodenBoatFrameBlock> flatBoatFrame, final Item plankItem) {
        final var frameBlock = flatBoatFrame.get();
        this.add(frameBlock, this.createSingleItemTable(AlekiShipsBlocks.BOAT_FRAME_FLAT.get()).withPool(
                this.applyExplosionCondition(plankItem,
                        FrameBlockHelper.createProcessedFrameTable(frameBlock, plankItem, 0,
                                FlatWoodenBoatFrameBlock.FULLY_PROCESSED, FlatWoodenBoatFrameBlock.FRAME_PROCESSED))));
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return AlekiShipsBlocks.BLOCKS.getEntries().stream().<Block>map(DeferredHolder::get)::iterator;
    }
}