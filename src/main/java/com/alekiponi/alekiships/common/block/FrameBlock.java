package com.alekiponi.alekiships.common.block;

import com.alekiponi.alekiships.common.block.entity.AlekishipsBlockEntities;
import com.alekiponi.alekiships.common.block.entity.FrameBlockEntity;
import com.alekiponi.alekiships.util.BoatFrame;
import com.alekiponi.alekiships.util.CommonHelper;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.HitResult;

import net.neoforged.neoforge.registries.datamaps.DataMapType;

/**
 * Some sort of frame block
 */
public interface FrameBlock {

    /**
     * Tries to place a filled frame for the datamap type. Handles initializing the {@link FrameBlockEntity}
     *
     * @param filledBoatFrameDataMap The filled frame datamap
     */
    static ItemInteractionResult tryPlaceFilledFrame(final ItemStack stack, final BlockState blockState,
            final Level level, final BlockPos blockPos, final Player player,
            final DataMapType<Item, BoatFrame> filledBoatFrameDataMap) {
        final var filledBoatFrame = stack.getItemHolder().getData(filledBoatFrameDataMap);
        if (filledBoatFrame == null) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        final BlockState frameBlockstate = filledBoatFrame.frame().withPropertiesOf(blockState);

        level.setBlockAndUpdate(blockPos, frameBlockstate);
        level.getBlockEntity(blockPos, AlekishipsBlockEntities.FRAME_BLOCK.get()).ifPresent(frameBlock -> {
            frameBlock.setFrameMaterial(filledBoatFrame.frameMaterial());
            frameBlock.addStack(stack.copyWithCount(1));
        });

        if (!player.hasInfiniteMaterials()) stack.shrink(1);

        level.playSound(player, blockPos, filledBoatFrame.getFrameMaterial().getSound(), SoundSource.BLOCKS, 1, 0.8F);

        return ItemInteractionResult.SUCCESS;
    }

    /**
     * Tries to continue filling the frame
     *
     * @param filledBoatFrameDataMap The filled frame datamap
     * @param processingProperty     The processing property
     * @param fullyProcessed         The point this is considered fully processed
     */
    static ItemInteractionResult tryContinueFillFrame(final ItemStack heldStack, final BlockState blockState,
            final Level level, final BlockPos blockPos, final Player player,
            final DataMapType<Item, BoatFrame> filledBoatFrameDataMap, final IntegerProperty processingProperty,
            final int fullyProcessed) {
        if (heldStack.isEmpty()) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        return level.getBlockEntity(blockPos, AlekishipsBlockEntities.FRAME_BLOCK.get())
                .map(frameBlock -> {
                    final var filledBoatFrame = heldStack.getItemHolder().getData(filledBoatFrameDataMap);
                    if (filledBoatFrame == null) return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
                    final var material = frameBlock.getMaterial();

                    if (!filledBoatFrame.getFrameMaterial().equals(material)) {
                        return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
                    }

                    if (blockState.getValue(processingProperty) >= fullyProcessed) {
                        return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
                    }

                    if (!player.isCreative()) {
                        frameBlock.addStack(heldStack.split(1));
                    } else {
                        frameBlock.addStack(heldStack.copyWithCount(1));
                    }

                    level.setBlockAndUpdate(blockPos, blockState.cycle(processingProperty));
                    level.playSound(player, blockPos, material.sound().value(), SoundSource.BLOCKS, 1, 0.8F);

                    return ItemInteractionResult.SUCCESS;
                }).orElse(ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION);
    }

    /**
     * @param emptyFrame         The empty frame block
     * @param processingProperty The processing property
     * @param fullyProcessed     The fully processed state
     */
    static <F extends Block & FrameBlock> InteractionResult tryExtractFrame(final BlockState blockState,
            final Level level, final BlockPos blockPos, final Player player, final F emptyFrame,
            final IntegerProperty processingProperty, final int fullyProcessed) {
        final int processState = blockState.getValue(processingProperty);

        // Try extract
        if (processState <= fullyProcessed) {
            level.getBlockEntity(blockPos, AlekishipsBlockEntities.FRAME_BLOCK.get())
                    .ifPresent(frameBlock -> CommonHelper.giveItemToPlayer(player, frameBlock.removeStack()));
        }

        // Set ourselves back to our base
        if (processState == 0) {
            final BlockState newState = emptyFrame.withPropertiesOf(blockState);

            level.setBlockAndUpdate(blockPos, newState);
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        level.setBlockAndUpdate(blockPos, blockState.setValue(processingProperty, processState - 1));

        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    /**
     * Handles destroying frame blocks
     */
    static boolean onDestroy(final BlockState state, final Level level, final BlockPos pos, final Player player,
            final FluidState fluid) {
        final var canBreak = state.canHarvestBlock(level, pos, player);

        state.getBlock().playerWillDestroy(level, pos, state, player);

        if (player.isCreative() && canBreak && level.getBlockEntity(pos) instanceof final FrameBlockEntity frameBlock) {
            frameBlock.removeAllStacks(stack -> {});
        }

        return level.setBlock(pos, fluid.createLegacyBlock(),
                level.isClientSide ? Block.UPDATE_ALL_IMMEDIATE : Block.UPDATE_ALL);
    }

    /**
     * Drop the stacks the frame contains
     */
    static void dropContents(final BlockState prevState, final Level level, final BlockPos pos,
            final BlockState newState) {
        if (!newState.is(prevState.getBlock())) {
            level.getBlockEntity(pos, AlekishipsBlockEntities.FRAME_BLOCK.get())
                    .ifPresent(frameBlock -> frameBlock.removeAllStacks(
                            itemStack -> Block.popResource(level, pos, itemStack)));
        }
    }

    /**
     * @param emptyFrame The empty frame block
     */
    static <F extends Block & FrameBlock> ItemStack getCloneStack(final BlockState state, final HitResult target,
            final LevelReader level, final BlockPos pos, final Player player, final F emptyFrame) {
        return level.getBlockEntity(pos, AlekishipsBlockEntities.FRAME_BLOCK.get())
                .map(FrameBlockEntity::getPickedItemStack)
                .orElseGet(() -> emptyFrame.getCloneItemStack(state, target, level, pos, player));
    }
}