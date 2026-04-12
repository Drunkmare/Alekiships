package com.drunkmare.deremet.data.loot;

import com.alekiponi.alekiships.common.block.ProcessedBoatFrame;
import com.alekiponi.alekiships.data.DataGenHelper;
import com.drunkmare.deremet.common.block.DeReMetallicaBlocks;
import com.drunkmare.deremet.common.block.MillstoneProcessedFrameBlock;
import com.drunkmare.deremet.util.VanillaWood;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraftforge.registries.RegistryObject;

import java.util.Set;

// Generates loot tables for all DE RE METALLICA blocks.
public class DeReMetallicaBlockLootTables extends BlockLootSubProvider {

    public DeReMetallicaBlockLootTables() {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags());
    }

    /**
     * Builds a loot pool for a processed frame block that drops a variable number of
     * plank items depending on how far through the build sequence the frame is.
     *
     * Each processing stage (0 through plankStates-1) gets its own loot condition so
     * the player recovers more planks the further along the frame is built.
     */
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
        // Unprocessed frame just drops itself.
        this.dropSelf(DeReMetallicaBlocks.MILLSTONE_FRAME.get());

        // Each wood variant of the processed frame drops the unprocessed frame item
        // plus a number of planks equal to the current build stage.
        DeReMetallicaBlocks.PROCESSED_MILLSTONE_FRAME.forEach(this::dropFullProcessedMillstoneFrame);
    }

    private void dropFullProcessedMillstoneFrame(final VanillaWood material,
            final RegistryObject<MillstoneProcessedFrameBlock> registryObject) {
        this.add(registryObject.get(),
                this.createSingleItemTable(DeReMetallicaBlocks.MILLSTONE_FRAME.get()).withPool(
                        this.applyExplosionCondition(material.getDeckItem(),
                                createProcessedFrameTable(registryObject.get(), material.getDeckItem(),
                                        MillstoneProcessedFrameBlock.FULLY_PROCESSED + 1))));
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return DeReMetallicaBlocks.BLOCKS.getEntries().stream().map(RegistryObject::get)::iterator;
    }
}
