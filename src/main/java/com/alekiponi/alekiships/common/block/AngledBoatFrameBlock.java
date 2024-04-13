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

    public static Direction[] getSolid(BlockState state) {
        ConstantShape shape = ConstantShape.getConstantShape(state);
        ConstantDirection directions = ConstantDirection.getConstantDirection(state);
        if (shape == ConstantShape.STRAIGHT) {
            return new Direction[]{state.getValue(FACING)};
        }
        if (shape == ConstantShape.INNER) {
            if (directions == ConstantDirection.NORTH_AND_EAST) {
                return new Direction[]{Direction.WEST, Direction.SOUTH};
            }
            if (directions == ConstantDirection.SOUTH_AND_EAST) {
                return new Direction[]{Direction.WEST, Direction.NORTH};
            }
            if (directions == ConstantDirection.NORTH_AND_WEST) {
                return new Direction[]{Direction.SOUTH, Direction.EAST};
            }
            if (directions == ConstantDirection.SOUTH_AND_WEST) {
                return new Direction[]{Direction.EAST, Direction.NORTH};
            }
        }
        return new Direction[]{};
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
        public static ConstantDirection getConstantDirection(final BlockState blockState) {
            if (!(blockState.getBlock() instanceof AngledBoatFrameBlock)) return null;

            return switch (blockState.getValue(FACING)) {
                case SOUTH -> switch (blockState.getValue(SHAPE)) {
                    case STRAIGHT -> EAST_AND_WEST;
                    case INNER_RIGHT -> NORTH_AND_EAST;
                    case INNER_LEFT -> NORTH_AND_WEST;
                    case OUTER_RIGHT -> SOUTH_AND_WEST;
                    case OUTER_LEFT -> SOUTH_AND_EAST;
                };
                case NORTH -> switch (blockState.getValue(SHAPE)) {
                    case STRAIGHT -> EAST_AND_WEST;
                    case INNER_RIGHT -> SOUTH_AND_WEST;
                    case INNER_LEFT -> SOUTH_AND_EAST;
                    case OUTER_RIGHT -> NORTH_AND_EAST;
                    case OUTER_LEFT -> NORTH_AND_WEST;
                };
                case EAST -> switch (blockState.getValue(SHAPE)) {
                    case STRAIGHT -> NORTH_AND_SOUTH;
                    case INNER_RIGHT -> NORTH_AND_WEST;
                    case INNER_LEFT -> SOUTH_AND_WEST;
                    case OUTER_RIGHT -> SOUTH_AND_EAST;
                    case OUTER_LEFT -> NORTH_AND_EAST;
                };
                case WEST -> switch (blockState.getValue(SHAPE)) {
                    case STRAIGHT -> NORTH_AND_SOUTH;
                    case INNER_RIGHT -> SOUTH_AND_EAST;
                    case INNER_LEFT -> NORTH_AND_EAST;
                    case OUTER_RIGHT -> NORTH_AND_WEST;
                    case OUTER_LEFT -> SOUTH_AND_WEST;
                };
                default -> null;
            };
        }

        @Nullable
        public static ConstantDirection rotateConstantDirection(final ConstantDirection constantDirection,
                final Direction direction) {
            return switch (direction) {
                case NORTH -> constantDirection;
                case SOUTH ->
                    // clockwise twice / flip
                        switch (constantDirection) {
                            case NORTH_AND_SOUTH, EAST_AND_WEST -> constantDirection;
                            case NORTH_AND_EAST -> SOUTH_AND_WEST;
                            case NORTH_AND_WEST -> SOUTH_AND_EAST;
                            case SOUTH_AND_EAST -> NORTH_AND_WEST;
                            case SOUTH_AND_WEST -> NORTH_AND_EAST;
                        };
                case EAST ->
                    // clockwise once
                        switch (constantDirection) {
                            case NORTH_AND_SOUTH -> EAST_AND_WEST;
                            case EAST_AND_WEST -> NORTH_AND_SOUTH;
                            case NORTH_AND_EAST -> SOUTH_AND_EAST;
                            case NORTH_AND_WEST -> NORTH_AND_EAST;
                            case SOUTH_AND_EAST -> SOUTH_AND_WEST;
                            case SOUTH_AND_WEST -> NORTH_AND_WEST;
                        };
                case WEST ->
                    // counterclockwise once
                        switch (constantDirection) {
                            case NORTH_AND_SOUTH -> EAST_AND_WEST;
                            case EAST_AND_WEST -> NORTH_AND_SOUTH;
                            case NORTH_AND_EAST -> NORTH_AND_WEST;
                            case NORTH_AND_WEST -> SOUTH_AND_WEST;
                            case SOUTH_AND_EAST -> NORTH_AND_EAST;
                            case SOUTH_AND_WEST -> SOUTH_AND_EAST;
                        };
                default -> null;
            };
        }
    }
}