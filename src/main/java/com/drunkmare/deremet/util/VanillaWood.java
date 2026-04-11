package com.drunkmare.deremet.util;

import com.alekiponi.alekiships.common.entity.vehicle.AbstractVehicle;
import com.alekiponi.alekiships.util.BoatMaterial;
import com.drunkmare.deremet.common.block.DeReMetallicaBlocks;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Locale;
import java.util.Optional;

public enum VanillaWood implements BoatMaterial {
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

    public static void registerFrames() {
        for (final VanillaWood wood : values()) {
            DeReMetallicaBlocks.MILLSTONE_FRAME.get()
                    .registerFrame(wood.plankBlock.asItem(), DeReMetallicaBlocks.PROCESSED_MILLSTONE_FRAME.get(wood).get());
        }
    }

    @Override
    public String getSerializedName() {
        return this.name().toLowerCase(Locale.ROOT);
    }

    @Override
    public Item getRailing() {
        return null;
    }

    @Override
    public Item getStrippedLog() {
        return null;
    }

    @Override
    public boolean withstandsLava() {
        return false;
    }

    @Override
    public BlockState getDeckBlock() {
        return this.plankBlock.defaultBlockState();
    }

    @Override
    public Optional<EntityType<? extends AbstractVehicle>> getEntityType(BoatType boatType) {
        return Optional.empty();
    }
}
