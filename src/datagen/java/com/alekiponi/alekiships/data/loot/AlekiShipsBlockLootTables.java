package com.alekiponi.alekiships.data.loot;

import com.alekiponi.alekiships.common.block.AlekiShipsBlocks;
import com.alekiponi.alekiships.common.block.AngledWoodenBoatFrameBlock;
import com.alekiponi.alekiships.common.block.FlatWoodenBoatFrameBlock;
import com.alekiponi.alekiships.common.block.ProcessedBoatFrame;
import com.alekiponi.alekiships.data.DataGenHelper;
import com.alekiponi.alekiships.util.BoatMaterial;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;

import java.util.Set;
import java.util.function.Supplier;

public class AlekiShipsBlockLootTables extends BlockLootSubProvider {

    public AlekiShipsBlockLootTables(final HolderLookup.Provider provider) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), provider);
    }

    public static <B extends Block & ProcessedBoatFrame> LootPool.Builder createProcessedFrameTable(final B block,
            final Item plankItem, final int plankStates) {
        final var contentsPool = LootPool.lootPool();

        for (int propertyValue = 0; propertyValue < plankStates; propertyValue++) {
            final var lootItem = DataGenHelper.setCount(LootItem.lootTableItem(plankItem), propertyValue, true);
            contentsPool.add(
                    DataGenHelper.matchProperty(lootItem, block, block.getProcessingProperty(), propertyValue));
        }

        return contentsPool;
    }

    @Override
    protected void generate() {
        this.dropSelf(AlekiShipsBlocks.BOAT_FRAME_ANGLED.get());

        AlekiShipsBlocks.WOODEN_BOAT_FRAME_ANGLED.forEach(this::dropAngledWoodenFrame);
        AlekiShipsBlocks.WOODEN_BOAT_FRAME_FLAT.forEach(this::dropFlatWoodenFrame);

        this.dropSelf(AlekiShipsBlocks.BOAT_FRAME_FLAT.get());

        this.dropSelf(AlekiShipsBlocks.OARLOCK.get());
        this.dropSelf(AlekiShipsBlocks.CLEAT.get());
    }

    private void dropAngledWoodenFrame(final BoatMaterial material,
            Supplier<AngledWoodenBoatFrameBlock> registryObject) {
        this.add(registryObject.get(), this.createSingleItemTable(AlekiShipsBlocks.BOAT_FRAME_ANGLED.get()).withPool(
                this.applyExplosionCondition(material.getDeckItem(),
                        createProcessedFrameTable(registryObject.get(), material.getDeckItem(),
                                AngledWoodenBoatFrameBlock.FULLY_PROCESSED + 1))));
    }

    private void dropFlatWoodenFrame(final BoatMaterial material, Supplier<FlatWoodenBoatFrameBlock> registryObject) {
        this.add(registryObject.get(), this.createSingleItemTable(AlekiShipsBlocks.BOAT_FRAME_FLAT.get()).withPool(
                this.applyExplosionCondition(material.getDeckItem(),
                        createProcessedFrameTable(registryObject.get(), material.getDeckItem(),
                                FlatWoodenBoatFrameBlock.FULLY_PROCESSED + 1))));
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return AlekiShipsBlocks.BLOCKS.getEntries().stream()
                .map(blockDeferredHolder -> ((Block) blockDeferredHolder.get()))::iterator;
    }
}