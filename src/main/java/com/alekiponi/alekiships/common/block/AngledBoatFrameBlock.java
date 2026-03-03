package com.alekiponi.alekiships.common.block;

import com.alekiponi.alekiships.common.AlekiShipsDataMaps;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.stream.IntStream;

public class AngledBoatFrameBlock extends Block implements SimpleWaterloggedBlock, FrameBlock {

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final EnumProperty<StairsShape> SHAPE = BlockStateProperties.STAIRS_SHAPE;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    protected static final VoxelShape[] SHAPES;
    private static final int[] SHAPE_BY_STATE = new int[]{12, 5, 3, 10, 14, 13, 7, 11, 13, 7, 11, 14, 8, 4, 1, 2, 4, 1, 2, 8};

    static {
        final VoxelShape bottom = Block.box(0, 0, 0, 16, 8, 16);
        final VoxelShape northWest = Block.box(0, 8, 0, 8, 16, 8);
        final VoxelShape northEast = Block.box(8, 8, 0, 16, 16, 8);
        final VoxelShape southWest = Block.box(0, 8, 8, 8, 16, 16);
        final VoxelShape southEast = Block.box(8, 8, 8, 16, 16, 16);
        SHAPES = makeShapes(bottom, northWest, northEast, southWest, southEast);
    }

    public AngledBoatFrameBlock(final Properties properties) {
        super(properties);
        this.registerDefaultState(
                this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(SHAPE, StairsShape.STRAIGHT)
                        .setValue(WATERLOGGED, false));
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

    @SuppressWarnings("SameParameterValue")
    private static VoxelShape[] makeShapes(final VoxelShape slabShape, final VoxelShape northWestCorner,
            final VoxelShape northEastCorner, final VoxelShape southWestCorner, final VoxelShape southEastCorner) {
        return IntStream.range(0, 16).mapToObj(
                (bitfield) -> makeStairShape(bitfield, slabShape, northWestCorner, northEastCorner, southWestCorner,
                        southEastCorner)).toArray(VoxelShape[]::new);
    }

    /**
     * Combines the shapes according to the mode set in the bitfield
     */
    private static VoxelShape makeStairShape(final int bitfield, final VoxelShape slabShape,
            final VoxelShape northWestCorner, final VoxelShape northEastCorner, final VoxelShape southWestCorner,
            final VoxelShape southEastCorner) {
        final VoxelShape northWestCornerOpposite;
        final VoxelShape northEastCornerOpposite;
        final VoxelShape southWestCornerOpposite;
        final VoxelShape southEastCornerOpposite;
        {
            northWestCornerOpposite = northWestCorner.move(0, -0.5, 0);
            northEastCornerOpposite = northEastCorner.move(0, -0.5, 0);
            southWestCornerOpposite = southWestCorner.move(0, -0.5, 0);
            southEastCornerOpposite = southEastCorner.move(0, -0.5, 0);
        }

        VoxelShape voxelshape = slabShape;
        if ((bitfield & 1) != 0) {
            voxelshape = Shapes.or(slabShape, northWestCorner);
            voxelshape = Shapes.join(voxelshape, northWestCornerOpposite, BooleanOp.ONLY_FIRST);
        }

        if ((bitfield & 2) != 0) {
            voxelshape = Shapes.or(voxelshape, northEastCorner);
            voxelshape = Shapes.join(voxelshape, northEastCornerOpposite, BooleanOp.ONLY_FIRST);
        }

        if ((bitfield & 4) != 0) {
            voxelshape = Shapes.or(voxelshape, southWestCorner);
            voxelshape = Shapes.join(voxelshape, southWestCornerOpposite, BooleanOp.ONLY_FIRST);
        }

        if ((bitfield & 8) != 0) {
            voxelshape = Shapes.or(voxelshape, southEastCorner);
            voxelshape = Shapes.join(voxelshape, southEastCornerOpposite, BooleanOp.ONLY_FIRST);
        }

        return voxelshape;
    }

    /**
     * Returns a stair shape property based on the surrounding stairs from the given blockstate and position
     */
    protected static StairsShape getStairsShape(final BlockState blockState, final BlockGetter blockGetter,
            final BlockPos blockPos) {
        final Direction direction = blockState.getValue(FACING);
        {
            final BlockState neighborBlockState = blockGetter.getBlockState(blockPos.relative(direction));
            if (isAngledBoatFrame(neighborBlockState)) {
                final Direction neighborFacing = neighborBlockState.getValue(FACING);
                if (neighborFacing.getAxis() != blockState.getValue(FACING).getAxis() && canTakeShape(blockState,
                        blockGetter, blockPos, neighborFacing.getOpposite())) {
                    if (neighborFacing == direction.getCounterClockWise()) {
                        return StairsShape.OUTER_LEFT;
                    }

                    return StairsShape.OUTER_RIGHT;
                }
            }
        }

        final BlockState neighborBlockState = blockGetter.getBlockState(blockPos.relative(direction.getOpposite()));
        if (isAngledBoatFrame(neighborBlockState)) {
            final Direction neighborFacing = neighborBlockState.getValue(FACING);
            if (neighborFacing.getAxis() != blockState.getValue(FACING).getAxis() && canTakeShape(blockState,
                    blockGetter, blockPos, neighborFacing)) {
                if (neighborFacing == direction.getCounterClockWise()) {
                    return StairsShape.INNER_LEFT;
                }

                return StairsShape.INNER_RIGHT;
            }
        }

        return StairsShape.STRAIGHT;
    }

    private static boolean canTakeShape(final BlockState blockState, final BlockGetter blockGetter,
            final BlockPos blockPos, final Direction direction) {
        final BlockState blockstate = blockGetter.getBlockState(blockPos.relative(direction));
        return !isAngledBoatFrame(blockstate) || blockstate.getValue(FACING) != blockState.getValue(FACING);
    }

    public static boolean isAngledBoatFrame(final BlockState blockState) {
        return blockState.getBlock() instanceof AngledBoatFrameBlock;
    }

    @Override
    protected ItemInteractionResult useItemOn(final ItemStack stack, final BlockState blockState, final Level level,
            final BlockPos blockPos, final Player player, final InteractionHand hand, final BlockHitResult hitResult) {
        return FrameBlock.tryPlaceFilledFrame(stack, blockState, level, blockPos, player,
                AlekiShipsDataMaps.ANGLED_BOAT_FRAME);
    }

    @Override
    public boolean useShapeForLightOcclusion(final BlockState blockState) {
        return true;
    }

    @Override
    public VoxelShape getShape(final BlockState blockState, final BlockGetter blockGetter, final BlockPos blockPos,
            final CollisionContext context) {
        return SHAPES[SHAPE_BY_STATE[this.getShapeIndex(blockState)]];
    }

    private int getShapeIndex(final BlockState blockState) {
        return blockState.getValue(SHAPE).ordinal() * 4 + blockState.getValue(FACING).get2DDataValue();
    }

    @Override
    public BlockState getStateForPlacement(final BlockPlaceContext placeContext) {
        final BlockPos blockpos = placeContext.getClickedPos();
        final FluidState fluidstate = placeContext.getLevel().getFluidState(blockpos);
        final BlockState blockstate = this.defaultBlockState().setValue(FACING, placeContext.getHorizontalDirection())
                .setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);

        return blockstate.setValue(SHAPE, getStairsShape(blockstate, placeContext.getLevel(), blockpos));
    }

    @Override
    public BlockState updateShape(final BlockState blockState, final Direction direction,
            final BlockState neighborState, final LevelAccessor levelAccessor, final BlockPos blockPos,
            final BlockPos neighborPos) {

        if (blockState.getValue(WATERLOGGED)) {
            levelAccessor.scheduleTick(blockPos, Fluids.WATER, Fluids.WATER.getTickDelay(levelAccessor));
        }

        if (direction.getAxis().isHorizontal()) {
            return blockState.setValue(SHAPE, getStairsShape(blockState, levelAccessor, blockPos));
        }

        return super.updateShape(blockState, direction, neighborState, levelAccessor, blockPos, neighborPos);
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(final BlockState blockState, final Rotation rotation) {
        return blockState.setValue(FACING, rotation.rotate(blockState.getValue(FACING)));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(final BlockState blockState, final Mirror mirror) {
        final Direction direction = blockState.getValue(FACING);
        final StairsShape stairsshape = blockState.getValue(SHAPE);
        switch (mirror) {
            case LEFT_RIGHT:
                if (direction.getAxis() == Direction.Axis.Z) {
                    return switch (stairsshape) {
                        case INNER_LEFT ->
                                blockState.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.INNER_RIGHT);
                        case INNER_RIGHT ->
                                blockState.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.INNER_LEFT);
                        case OUTER_LEFT ->
                                blockState.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.OUTER_RIGHT);
                        case OUTER_RIGHT ->
                                blockState.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.OUTER_LEFT);
                        default -> blockState.rotate(Rotation.CLOCKWISE_180);
                    };
                }
                break;
            case FRONT_BACK:
                if (direction.getAxis() == Direction.Axis.X) {
                    return switch (stairsshape) {
                        case INNER_LEFT ->
                                blockState.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.INNER_LEFT);
                        case INNER_RIGHT ->
                                blockState.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.INNER_RIGHT);
                        case OUTER_LEFT ->
                                blockState.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.OUTER_RIGHT);
                        case OUTER_RIGHT ->
                                blockState.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.OUTER_LEFT);
                        case STRAIGHT -> blockState.rotate(Rotation.CLOCKWISE_180);
                    };
                }
        }

        return super.mirror(blockState, mirror);
    }

    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, SHAPE, WATERLOGGED);
    }

    @Override
    public FluidState getFluidState(final BlockState blockState) {
        return blockState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(blockState);
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