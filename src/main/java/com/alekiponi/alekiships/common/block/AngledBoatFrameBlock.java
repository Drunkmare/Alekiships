package com.alekiponi.alekiships.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;
import java.util.IdentityHashMap;

public class AngledBoatFrameBlock extends SquaredAngleBlock {

    private static final IdentityHashMap<Item, AngledBoatFrameBlock> ANGLED_FRAMES = new IdentityHashMap<>();

    public AngledBoatFrameBlock(final Properties properties) {
        super(properties);
    }

    /**
     * Registers a mapping of the passed in {@link Item} instance and the passed in {@link AngledBoatFrameBlock}
     * A given {@link Item} instance may only map to one {@link AngledBoatFrameBlock} instance but multiple
     * {@link Item}s can map to the same {@link AngledBoatFrameBlock}.
     */
    public static void registerFrame(final Item item, final AngledBoatFrameBlock frameBlock) {
        ANGLED_FRAMES.put(item, frameBlock);
    }

    @Nullable
    public static AngledBoatFrameBlock getFrame(final Item item) {
        return ANGLED_FRAMES.get(item);
    }

    /**
     * @return If the {@link #SHAPE} property is {@link StairsShape#INNER_LEFT} or {@link StairsShape#INNER_RIGHT}
     */
    public static boolean isInner(final BlockState blockState) {
        return blockState.getValue(SHAPE) == StairsShape.INNER_LEFT || blockState.getValue(
                SHAPE) == StairsShape.INNER_RIGHT;
    }

    /**
     * @return If the {@link #SHAPE} property is {@link StairsShape#OUTER_LEFT} or {@link StairsShape#OUTER_RIGHT}
     */
    public static boolean isOuter(final BlockState blockState) {
        return blockState.getValue(SHAPE) == StairsShape.OUTER_LEFT || blockState.getValue(
                SHAPE) == StairsShape.OUTER_RIGHT;
    }

    /**
     * @return If the {@link #SHAPE} property is {@link StairsShape#STRAIGHT}
     */
    @SuppressWarnings("unused")
    public static boolean isStraight(final BlockState blockState) {
        return blockState.getValue(SHAPE) == StairsShape.STRAIGHT;
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(final BlockState blockState, final Level level, final BlockPos blockPos,
            final Player player, final InteractionHand hand, final BlockHitResult hitResult) {

        final ItemStack heldStack = player.getItemInHand(hand);

        final SquaredAngleBlock frameBlock = getFrame(heldStack.getItem());

        if (frameBlock != null) {
            final BlockState newBlockState = frameBlock.defaultBlockState().setValue(SHAPE, blockState.getValue(SHAPE))
                    .setValue(FACING, blockState.getValue(FACING))
                    .setValue(WATERLOGGED, blockState.getValue(WATERLOGGED));

            level.setBlockAndUpdate(blockPos, newBlockState);

            if (!player.getAbilities().instabuild) {
                heldStack.shrink(1);
            }

            final SoundType soundType = newBlockState.getSoundType(level, blockPos, player);

            level.playSound(player, blockPos, soundType.getPlaceSound(), SoundSource.BLOCKS,
                    (soundType.getVolume() + 1) / 2, soundType.getPitch() * 0.8F);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    public enum ConstantShape {
        STRAIGHT,
        INNER,
        OUTER;

        /**
         * Compresses the five states of {@link StairsShape} to just 3
         */
        public static ConstantShape getConstantShape(final BlockState blockState) {
            if (isInner(blockState)) {
                return INNER;
            }
            if (isOuter(blockState)) {
                return OUTER;
            }
            return STRAIGHT;
        }
    }

    public enum ConstantDirection {
        NORTH_AND_EAST,
        NORTH_AND_WEST,
        SOUTH_AND_EAST,
        SOUTH_AND_WEST,
        NORTH_AND_SOUTH,
        EAST_AND_WEST;

        @Nullable
        public static ConstantDirection getConstantDirection(BlockState state) {
            if (!(state.getBlock() instanceof AngledWoodenBoatFrameBlock)) {
                return null;
            }
            if (state.getValue(FACING) == Direction.SOUTH) {
                if (state.getValue(SHAPE) == StairsShape.STRAIGHT) {
                    return EAST_AND_WEST;
                }
                if (state.getValue(SHAPE) == StairsShape.INNER_RIGHT) {
                    return NORTH_AND_EAST;
                }
                if (state.getValue(SHAPE) == StairsShape.INNER_LEFT) {
                    return NORTH_AND_WEST;
                }
                if (state.getValue(SHAPE) == StairsShape.OUTER_RIGHT) {
                    return SOUTH_AND_WEST;
                }
                if (state.getValue(SHAPE) == StairsShape.OUTER_LEFT) {
                    return SOUTH_AND_EAST;
                }
            }
            if (state.getValue(FACING) == Direction.NORTH) {
                if (state.getValue(SHAPE) == StairsShape.STRAIGHT) {
                    return EAST_AND_WEST;
                }
                if (state.getValue(SHAPE) == StairsShape.INNER_RIGHT) {
                    return SOUTH_AND_WEST;
                }
                if (state.getValue(SHAPE) == StairsShape.INNER_LEFT) {
                    return SOUTH_AND_EAST;
                }
                if (state.getValue(SHAPE) == StairsShape.OUTER_RIGHT) {
                    return NORTH_AND_EAST;
                }
                if (state.getValue(SHAPE) == StairsShape.OUTER_LEFT) {
                    return NORTH_AND_WEST;
                }
            }
            if (state.getValue(FACING) == Direction.EAST) {
                if (state.getValue(SHAPE) == StairsShape.STRAIGHT) {
                    return NORTH_AND_SOUTH;
                }
                if (state.getValue(SHAPE) == StairsShape.INNER_RIGHT) {
                    return NORTH_AND_WEST;
                }
                if (state.getValue(SHAPE) == StairsShape.INNER_LEFT) {
                    return SOUTH_AND_WEST;
                }
                if (state.getValue(SHAPE) == StairsShape.OUTER_RIGHT) {
                    return SOUTH_AND_EAST;
                }
                if (state.getValue(SHAPE) == StairsShape.OUTER_LEFT) {
                    return NORTH_AND_EAST;
                }
            }
            if (state.getValue(FACING) == Direction.WEST) {
                if (state.getValue(SHAPE) == StairsShape.STRAIGHT) {
                    return NORTH_AND_SOUTH;
                }
                if (state.getValue(SHAPE) == StairsShape.INNER_RIGHT) {
                    return SOUTH_AND_EAST;
                }
                if (state.getValue(SHAPE) == StairsShape.INNER_LEFT) {
                    return NORTH_AND_EAST;
                }
                if (state.getValue(SHAPE) == StairsShape.OUTER_RIGHT) {
                    return NORTH_AND_WEST;
                }
                if (state.getValue(SHAPE) == StairsShape.OUTER_LEFT) {
                    return SOUTH_AND_WEST;
                }
            }
            return null;
        }

        @Nullable
        public static ConstantDirection rotateConstantDirection(ConstantDirection constantDirection, Direction direction){
            if(direction == Direction.NORTH){
                return constantDirection;
            }
            if(direction == Direction.SOUTH){
                // clockwise twice / flip
                if(constantDirection == NORTH_AND_SOUTH){
                    return constantDirection;
                }
                if(constantDirection == EAST_AND_WEST){
                    return constantDirection;
                }

                if(constantDirection == NORTH_AND_EAST){
                    return SOUTH_AND_WEST;
                }
                if(constantDirection == NORTH_AND_WEST){
                    return SOUTH_AND_EAST;
                }
                if(constantDirection == SOUTH_AND_EAST){
                    return NORTH_AND_WEST;
                }
                if(constantDirection == SOUTH_AND_WEST){
                    return NORTH_AND_EAST;
                }
            }
            if(direction == Direction.EAST){
                // clockwise once
                if(constantDirection == NORTH_AND_SOUTH){
                    return EAST_AND_WEST;
                }
                if(constantDirection == EAST_AND_WEST){
                    return NORTH_AND_SOUTH;
                }

                if(constantDirection == NORTH_AND_EAST){
                    return SOUTH_AND_EAST;
                }
                if(constantDirection == NORTH_AND_WEST){
                    return NORTH_AND_EAST;
                }
                if(constantDirection == SOUTH_AND_EAST){
                    return SOUTH_AND_WEST;
                }
                if(constantDirection == SOUTH_AND_WEST){
                    return NORTH_AND_WEST;
                }
            }
            if(direction == Direction.WEST){
                // counterclockwise once
                if(constantDirection == NORTH_AND_SOUTH){
                    return EAST_AND_WEST;
                }
                if(constantDirection == EAST_AND_WEST){
                    return NORTH_AND_SOUTH;
                }

                if(constantDirection == NORTH_AND_EAST){
                    return NORTH_AND_WEST;
                }
                if(constantDirection == NORTH_AND_WEST){
                    return SOUTH_AND_WEST;
                }
                if(constantDirection == SOUTH_AND_EAST){
                    return NORTH_AND_EAST;
                }
                if(constantDirection == SOUTH_AND_WEST){
                    return SOUTH_AND_EAST;
                }
            }
            return null;
        }
    }
}