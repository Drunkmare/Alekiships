package com.alekiponi.alekiships.util;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.Locale;

public enum VanillaWood implements StringRepresentable {
    OAK(Blocks.OAK_PLANKS),
    SPRUCE(Blocks.SPRUCE_PLANKS),
    BIRCH(Blocks.BIRCH_PLANKS),
    ACACIA(Blocks.ACACIA_PLANKS),
    CHERRY(Blocks.CHERRY_PLANKS),
    JUNGLE(Blocks.JUNGLE_PLANKS),
    DARK_OAK(Blocks.DARK_OAK_PLANKS),
    CRIMSON(Blocks.CRIMSON_PLANKS),
    WARPED(Blocks.WARPED_PLANKS),
    MANGROVE(Blocks.MANGROVE_PLANKS),
    BAMBOO(Blocks.BAMBOO_PLANKS);

    private final Block plankBlock;

    VanillaWood(final Block plankBlock) {
        this.plankBlock = plankBlock;
    }

    @Override
    public String getSerializedName() {
        return this.name().toLowerCase(Locale.ROOT);
    }

    public Item getPlankItem() {
        return this.plankBlock.asItem();
    }

    public Block getDeckBlockBlock() {
        return this.plankBlock;
    }
}