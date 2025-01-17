package com.alekiponi.alekiships.util;

import com.alekiponi.alekiships.common.entity.vehicle.RowboatVariant;
import com.alekiponi.alekiships.common.entity.vehicle.RowboatVariants;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.Locale;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * An enum for all the vanilla Overworld woods to assist in registration and data generation
 */
@AllArgsConstructor
public enum OverworldWood implements Wood {
    OAK(Items.OAK_PLANKS, FrameMaterial.OAK, RowboatVariants.OAK),
    SPRUCE(Items.SPRUCE_PLANKS, FrameMaterial.SPRUCE, RowboatVariants.SPRUCE),
    BIRCH(Items.BIRCH_PLANKS, FrameMaterial.BIRCH, RowboatVariants.BIRCH),
    ACACIA(Items.ACACIA_PLANKS, FrameMaterial.ACACIA, RowboatVariants.ACACIA),
    CHERRY(Items.CHERRY_PLANKS, FrameMaterial.CHERRY, RowboatVariants.CHERRY),
    JUNGLE(Items.JUNGLE_PLANKS, FrameMaterial.JUNGLE, RowboatVariants.JUNGLE),
    DARK_OAK(Items.DARK_OAK_PLANKS, FrameMaterial.DARK_OAK, RowboatVariants.DARK_OAK),
    MANGROVE(Items.MANGROVE_PLANKS, FrameMaterial.MANGROVE, RowboatVariants.MANGROVE),
    BAMBOO(Items.BAMBOO_PLANKS, FrameMaterial.BAMBOO, RowboatVariants.BAMBOO);

    private final Item plankItem;
    private final ResourceKey<FrameMaterial> frameMaterial;
    @Getter
    @Accessors(fluent = true)
    private final ResourceKey<RowboatVariant> rowboatKey;

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