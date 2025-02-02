package com.alekiponi.alekiships.data.loot;

import com.alekiponi.alekiships.common.entity.vehicle.RowboatVariants;
import com.alekiponi.alekiships.common.entity.vehicle.SloopVariants;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.function.BiConsumer;

import static com.alekiponi.alekiships.common.entity.vehicle.RowboatVariants.Loot.createRowboatLootTable;
import static com.alekiponi.alekiships.common.entity.vehicle.SloopVariants.Loot.createSloopLootTable;

public record AlekiShipsBoatVariantLootTables(HolderLookup.Provider registries) implements LootTableSubProvider {

    @Override
    public void generate(final BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output) {
        output.accept(RowboatVariants.Loot.OAK, createRowboatLootTable(Items.OAK_PLANKS));
        output.accept(RowboatVariants.Loot.SPRUCE, createRowboatLootTable(Items.SPRUCE_PLANKS));
        output.accept(RowboatVariants.Loot.BIRCH, createRowboatLootTable(Items.BIRCH_PLANKS));
        output.accept(RowboatVariants.Loot.ACACIA, createRowboatLootTable(Items.ACACIA_PLANKS));
        output.accept(RowboatVariants.Loot.CHERRY, createRowboatLootTable(Items.CHERRY_PLANKS));
        output.accept(RowboatVariants.Loot.JUNGLE, createRowboatLootTable(Items.JUNGLE_PLANKS));
        output.accept(RowboatVariants.Loot.DARK_OAK, createRowboatLootTable(Items.DARK_OAK_PLANKS));
        output.accept(RowboatVariants.Loot.CRIMSON, createRowboatLootTable(Items.CRIMSON_PLANKS));
        output.accept(RowboatVariants.Loot.WARPED, createRowboatLootTable(Items.WARPED_PLANKS));
        output.accept(RowboatVariants.Loot.MANGROVE, createRowboatLootTable(Items.MANGROVE_PLANKS));
        output.accept(RowboatVariants.Loot.BAMBOO, createRowboatLootTable(Items.BAMBOO_PLANKS));

        output.accept(SloopVariants.Loot.OAK,
                createSloopLootTable(Items.STRIPPED_OAK_LOG, Items.OAK_PLANKS, Items.OAK_FENCE));
        output.accept(SloopVariants.Loot.SPRUCE,
                createSloopLootTable(Items.STRIPPED_SPRUCE_LOG, Items.SPRUCE_PLANKS, Items.SPRUCE_FENCE));
        output.accept(SloopVariants.Loot.BIRCH,
                createSloopLootTable(Items.STRIPPED_BIRCH_LOG, Items.BIRCH_PLANKS, Items.BIRCH_FENCE));
        output.accept(SloopVariants.Loot.ACACIA,
                createSloopLootTable(Items.STRIPPED_ACACIA_LOG, Items.ACACIA_PLANKS, Items.ACACIA_FENCE));
        output.accept(SloopVariants.Loot.CHERRY,
                createSloopLootTable(Items.STRIPPED_CHERRY_LOG, Items.CHERRY_PLANKS, Items.CHERRY_FENCE));
        output.accept(SloopVariants.Loot.JUNGLE,
                createSloopLootTable(Items.STRIPPED_JUNGLE_LOG, Items.JUNGLE_PLANKS, Items.JUNGLE_FENCE));
        output.accept(SloopVariants.Loot.DARK_OAK,
                createSloopLootTable(Items.STRIPPED_DARK_OAK_LOG, Items.DARK_OAK_PLANKS, Items.DARK_OAK_FENCE));
        output.accept(SloopVariants.Loot.CRIMSON,
                createSloopLootTable(Items.STRIPPED_CRIMSON_STEM, Items.CRIMSON_PLANKS, Items.CRIMSON_FENCE));
        output.accept(SloopVariants.Loot.WARPED,
                createSloopLootTable(Items.STRIPPED_WARPED_STEM, Items.WARPED_PLANKS, Items.WARPED_FENCE));
        output.accept(SloopVariants.Loot.MANGROVE,
                createSloopLootTable(Items.STRIPPED_MANGROVE_LOG, Items.MANGROVE_PLANKS, Items.MANGROVE_FENCE));
        output.accept(SloopVariants.Loot.BAMBOO,
                createSloopLootTable(Items.STRIPPED_BAMBOO_BLOCK, Items.BAMBOO_PLANKS, Items.BAMBOO_FENCE));
    }
}