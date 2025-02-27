package com.alekiponi.alekiships.util;

import com.alekiponi.alekiships.common.entity.vehicle.ConstructionSloopVariant;
import com.alekiponi.alekiships.common.entity.vehicle.ConstructionSloopVariants;
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
 * An enum for all the vanilla Nether woods to assist in registration and data generation
 */
@AllArgsConstructor
public enum NetherWood implements Wood {
    CRIMSON(Items.CRIMSON_PLANKS, FrameMaterial.CRIMSON, RowboatVariants.CRIMSON, ConstructionSloopVariants.CRIMSON),
    WARPED(Items.WARPED_PLANKS, FrameMaterial.WARPED, RowboatVariants.WARPED, ConstructionSloopVariants.WARPED);

    private final Item plankItem;
    private final ResourceKey<FrameMaterial> frameMaterial;
    @Getter
    @Accessors(fluent = true)
    private final ResourceKey<RowboatVariant> rowboatKey;
    @Getter
    @Accessors(fluent = true)
    private final ResourceKey<ConstructionSloopVariant> sloopConstructionKey;

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