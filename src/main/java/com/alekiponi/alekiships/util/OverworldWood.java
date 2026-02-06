package com.alekiponi.alekiships.util;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.Locale;
import lombok.AllArgsConstructor;

/**
 * An enum for all the vanilla Overworld woods to assist in registration and data generation
 */
@AllArgsConstructor
public enum OverworldWood implements Wood {
    OAK(Items.OAK_PLANKS, FrameMaterial.OAK),
    SPRUCE(Items.SPRUCE_PLANKS, FrameMaterial.SPRUCE),
    BIRCH(Items.BIRCH_PLANKS, FrameMaterial.BIRCH),
    ACACIA(Items.ACACIA_PLANKS, FrameMaterial.ACACIA),
    CHERRY(Items.CHERRY_PLANKS, FrameMaterial.CHERRY),
    JUNGLE(Items.JUNGLE_PLANKS, FrameMaterial.JUNGLE),
    DARK_OAK(Items.DARK_OAK_PLANKS, FrameMaterial.DARK_OAK),
    MANGROVE(Items.MANGROVE_PLANKS, FrameMaterial.MANGROVE),
    BAMBOO(Items.BAMBOO_PLANKS, FrameMaterial.BAMBOO);

    private final Item plankItem;
    private final ResourceKey<FrameMaterial> frameMaterial;

    @Override
    public String getSerializedName() {
        return this.name().toLowerCase(Locale.ROOT);
    }

    @Override
    public Item getPlankItem() {
        return this.plankItem;
    }

    @Override
    public ResourceKey<FrameMaterial> frameMaterialKey() {
        return this.frameMaterial;
    }
}