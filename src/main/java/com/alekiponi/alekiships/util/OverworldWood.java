package com.alekiponi.alekiships.util;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.Locale;
import lombok.AllArgsConstructor;

/**
 * An enum for all the vanilla Overworld woods to assist in registration and data generation
 */
@AllArgsConstructor
public enum OverworldWood implements Wood {
    OAK(Items.OAK_PLANKS, Blocks.OAK_PLANKS),
    SPRUCE(Items.SPRUCE_PLANKS, Blocks.SPRUCE_PLANKS),
    BIRCH(Items.BIRCH_PLANKS, Blocks.BIRCH_PLANKS),
    ACACIA(Items.ACACIA_PLANKS, Blocks.ACACIA_PLANKS),
    CHERRY(Items.CHERRY_PLANKS, Blocks.CHERRY_PLANKS),
    JUNGLE(Items.JUNGLE_PLANKS, Blocks.JUNGLE_PLANKS),
    DARK_OAK(Items.DARK_OAK_PLANKS, Blocks.DARK_OAK_PLANKS),
    MANGROVE(Items.MANGROVE_PLANKS, Blocks.MANGROVE_PLANKS),
    BAMBOO(Items.BAMBOO_PLANKS, Blocks.BAMBOO_PLANKS);

    private final Item plankItem;
    private final Block plankBlock;

    @Override
    public String getSerializedName() {
        return this.name().toLowerCase(Locale.ROOT);
    }

    @Override
    public Item getPlankItem() {
        return this.plankItem;
    }

    @Override
    public Block getPlankBlock() {
        return this.plankBlock;
    }
}