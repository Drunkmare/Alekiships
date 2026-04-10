package com.alekiponi.alekiships.util;

import com.alekiponi.alekiships.common.block.AlekiShipsBlocks;
import com.alekiponi.alekiships.common.entity.AlekiShipsEntities;
import com.alekiponi.alekiships.common.entity.vehicle.AbstractVehicle;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Locale;
import java.util.Optional;

/**
 * The vanilla wood {@link BoatMaterial} implementation.
 */
public enum VanillaWood implements BoatMaterial {
    OAK(Blocks.OAK_PLANKS, Items.OAK_FENCE, Items.STRIPPED_OAK_LOG),
    SPRUCE(Blocks.SPRUCE_PLANKS, Items.SPRUCE_FENCE, Items.STRIPPED_SPRUCE_LOG),
    BIRCH(Blocks.BIRCH_PLANKS, Items.BIRCH_FENCE, Items.STRIPPED_BIRCH_LOG),
    ACACIA(Blocks.ACACIA_PLANKS, Items.ACACIA_FENCE, Items.STRIPPED_ACACIA_LOG),
    CHERRY(Blocks.CHERRY_PLANKS, Items.CHERRY_FENCE, Items.STRIPPED_CHERRY_LOG),
    JUNGLE(Blocks.JUNGLE_PLANKS, Items.JUNGLE_FENCE, Items.STRIPPED_JUNGLE_LOG),
    DARK_OAK(Blocks.DARK_OAK_PLANKS, Items.DARK_OAK_FENCE, Items.STRIPPED_DARK_OAK_LOG),
    CRIMSON(Blocks.CRIMSON_PLANKS, Items.CRIMSON_FENCE, Items.STRIPPED_CRIMSON_STEM),
    WARPED(Blocks.WARPED_PLANKS, Items.WARPED_FENCE, Items.STRIPPED_WARPED_STEM),
    MANGROVE(Blocks.MANGROVE_PLANKS, Items.MANGROVE_FENCE, Items.STRIPPED_MANGROVE_LOG),
    BAMBOO(Blocks.BAMBOO_PLANKS, Items.BAMBOO_FENCE, Items.STRIPPED_BAMBOO_BLOCK);

    private final Block plankBlock;
    private final Item railingItem;
    private final Item strippedLogItem;

    VanillaWood(final Block plankBlock, final Item railingItem, final Item strippedLogItem) {
        this.plankBlock = plankBlock;
        this.railingItem = railingItem;
        this.strippedLogItem = strippedLogItem;
    }

    public static void registerFrames() {
        for (final VanillaWood wood : values()) {
            AlekiShipsBlocks.BOAT_FRAME_FLAT.get()
                    .registerFrame(wood.plankBlock.asItem(), AlekiShipsBlocks.WOODEN_BOAT_FRAME_FLAT.get(wood).get());
            AlekiShipsBlocks.BOAT_FRAME_ANGLED.get()
                    .registerFrame(wood.plankBlock.asItem(), AlekiShipsBlocks.WOODEN_BOAT_FRAME_ANGLED.get(wood).get());
            AlekiShipsBlocks.WATERWHEEL_FRAME.get()
                    .registerFrame(wood.plankBlock.asItem(), AlekiShipsBlocks.WOODEN_WATERWHEEL_FRAME.get(wood).get());
            AlekiShipsBlocks.MILLSTONE_FRAME.get()
                    .registerFrame(wood.plankBlock.asItem(), AlekiShipsBlocks.PROCESSED_MILLSTONE_FRAME.get(wood).get());
        }
    }

    @Override
    public String getSerializedName() {
        return this.name().toLowerCase(Locale.ROOT);
    }

    @Override
    public Item getDeckItem() {
        return this.plankBlock.asItem();
    }

    @Override
    public Item getRailing() {
        return this.railingItem;
    }

    @Override
    public Item getStrippedLog() {
        return this.strippedLogItem;
    }

    @Override
    public boolean withstandsLava() {
        return this.equals(WARPED) || this.equals(CRIMSON);
    }

    @Override
    public BlockState getDeckBlock() {
        return this.plankBlock.defaultBlockState();
    }

    @Override
    public Optional<EntityType<? extends AbstractVehicle>> getEntityType(final BoatType boatType) {
        return switch (boatType) {
            case ROWBOAT -> Optional.of(AlekiShipsEntities.ROWBOATS.get(this).get());
            case SLOOP -> Optional.of(AlekiShipsEntities.SLOOPS.get(this).get());
            case CONSTRUCTION_SLOOP -> Optional.of(AlekiShipsEntities.SLOOPS_UNDER_CONSTRUCTION.get(this).get());
        };
    }
}