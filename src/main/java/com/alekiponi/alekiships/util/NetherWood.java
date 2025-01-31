package com.alekiponi.alekiships.util;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.Locale;
import lombok.AllArgsConstructor;

/**
 * An enum for all the vanilla Nether woods to assist in registration and data generation
 */
@AllArgsConstructor
public enum NetherWood implements Wood {
    CRIMSON(Items.CRIMSON_PLANKS, Blocks.CRIMSON_PLANKS),
    WARPED(Items.WARPED_PLANKS, Blocks.WARPED_PLANKS);

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