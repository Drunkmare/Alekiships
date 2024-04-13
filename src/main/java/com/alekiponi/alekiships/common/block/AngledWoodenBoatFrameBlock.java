package com.alekiponi.alekiships.common.block;

import com.alekiponi.alekiships.util.AlekiShipsHelper;
import com.alekiponi.alekiships.util.BoatMaterial;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;

public class AngledWoodenBoatFrameBlock extends AngledBoatFrameBlock implements ProcessedBoatFrame {
    public static final IntegerProperty FRAME_PROCESSED = AlekiShipsBlockStateProperties.FRAME_PROCESSED;
    public static final int FULLY_PROCESSED = 3;

    public final BoatMaterial boatMaterial;

    public AngledWoodenBoatFrameBlock(final BoatMaterial boatMaterial, final Properties properties) {
        super(properties);
        this.registerDefaultState(
                this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(SHAPE, StairsShape.STRAIGHT)
                        .setValue(WATERLOGGED, false).setValue(FRAME_PROCESSED, 0));
        this.boatMaterial = boatMaterial;
    }

    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(FRAME_PROCESSED));
    }

    public enum ConstantDirection {
        NORTH_AND_EAST,
        NORTH_AND_WEST,
        SOUTH_AND_EAST,
        SOUTH_AND_WEST,
        NORTH_AND_SOUTH,
        EAST_AND_WEST,
    }

    public enum ConstantShape {
        STRAIGHT,
        INNER,
        OUTER
    }

    public static ConstantShape getConstantShape(BlockState state){
        if(isInner(state)){
            return ConstantShape.INNER;
        }
        if(isOuter(state)){
            return ConstantShape.OUTER;
        }
        return ConstantShape.STRAIGHT;
    }

    public static Direction[] getSolid(BlockState state){
        ConstantShape shape = getConstantShape(state);
        ConstantDirection directions = getConstantDirection(state);
        if(shape == ConstantShape.STRAIGHT){
            return new Direction[]{state.getValue(FACING)};
        }
        if(shape == ConstantShape.INNER){
            if(directions == ConstantDirection.NORTH_AND_EAST){
                return new Direction[]{Direction.WEST, Direction.SOUTH};
            }
            if(directions == ConstantDirection.SOUTH_AND_EAST){
                return new Direction[]{Direction.WEST, Direction.NORTH};
            }
            if(directions == ConstantDirection.NORTH_AND_WEST){
                return new Direction[]{Direction.SOUTH, Direction.EAST};
            }
            if(directions == ConstantDirection.SOUTH_AND_WEST){
                return new Direction[]{Direction.EAST, Direction.NORTH};
            }
        }
        return new Direction[]{};
    }

    @Nullable
    public static ConstantDirection getConstantDirection(BlockState state) {
        if (!(state.getBlock() instanceof AngledWoodenBoatFrameBlock)) {
            return null;
        }
        if (state.getValue(FACING) == Direction.SOUTH) {
            if (state.getValue(SHAPE) == StairsShape.STRAIGHT) {
                return ConstantDirection.EAST_AND_WEST;
            }
            if (state.getValue(SHAPE) == StairsShape.INNER_RIGHT) {
                return ConstantDirection.NORTH_AND_EAST;
            }
            if (state.getValue(SHAPE) == StairsShape.INNER_LEFT) {
                return ConstantDirection.NORTH_AND_WEST;
            }
            if (state.getValue(SHAPE) == StairsShape.OUTER_RIGHT) {
                return ConstantDirection.SOUTH_AND_WEST;
            }
            if (state.getValue(SHAPE) == StairsShape.OUTER_LEFT) {
                return ConstantDirection.SOUTH_AND_EAST;
            }
        }
        if (state.getValue(FACING) == Direction.NORTH) {
            if (state.getValue(SHAPE) == StairsShape.STRAIGHT) {
                return ConstantDirection.EAST_AND_WEST;
            }
            if (state.getValue(SHAPE) == StairsShape.INNER_RIGHT) {
                return ConstantDirection.SOUTH_AND_WEST;
            }
            if (state.getValue(SHAPE) == StairsShape.INNER_LEFT) {
                return ConstantDirection.SOUTH_AND_EAST;
            }
            if (state.getValue(SHAPE) == StairsShape.OUTER_RIGHT) {
                return ConstantDirection.NORTH_AND_EAST;
            }
            if (state.getValue(SHAPE) == StairsShape.OUTER_LEFT) {
                return ConstantDirection.NORTH_AND_WEST;
            }
        }
        if (state.getValue(FACING) == Direction.EAST) {
            if (state.getValue(SHAPE) == StairsShape.STRAIGHT) {
                return ConstantDirection.NORTH_AND_SOUTH;
            }
            if (state.getValue(SHAPE) == StairsShape.INNER_RIGHT) {
                return ConstantDirection.NORTH_AND_WEST;
            }
            if (state.getValue(SHAPE) == StairsShape.INNER_LEFT) {
                return ConstantDirection.SOUTH_AND_WEST;
            }
            if (state.getValue(SHAPE) == StairsShape.OUTER_RIGHT) {
                return ConstantDirection.SOUTH_AND_EAST;
            }
            if (state.getValue(SHAPE) == StairsShape.OUTER_LEFT) {
                return ConstantDirection.NORTH_AND_EAST;
            }
        }
        if (state.getValue(FACING) == Direction.WEST) {
            if (state.getValue(SHAPE) == StairsShape.STRAIGHT) {
                return ConstantDirection.NORTH_AND_SOUTH;
            }
            if (state.getValue(SHAPE) == StairsShape.INNER_RIGHT) {
                return ConstantDirection.SOUTH_AND_EAST;
            }
            if (state.getValue(SHAPE) == StairsShape.INNER_LEFT) {
                return ConstantDirection.NORTH_AND_EAST;
            }
            if (state.getValue(SHAPE) == StairsShape.OUTER_RIGHT) {
                return ConstantDirection.NORTH_AND_WEST;
            }
            if (state.getValue(SHAPE) == StairsShape.OUTER_LEFT) {
                return ConstantDirection.SOUTH_AND_WEST;
            }
        }
        return null;
    }

    public static boolean isInner(BlockState state){
        return state.getValue(SHAPE) == StairsShape.INNER_LEFT || state.getValue(SHAPE) == StairsShape.INNER_RIGHT;
    }

    public static boolean isOuter(BlockState state){
        return state.getValue(SHAPE) == StairsShape.OUTER_LEFT || state.getValue(SHAPE) == StairsShape.OUTER_RIGHT;
    }

    public static boolean isStraight(BlockState state){
        return state.getValue(SHAPE) == StairsShape.STRAIGHT;
    }

    @Nullable
    public static ConstantDirection rotateConstantDirection(ConstantDirection constantDirection, Direction direction){
        if(direction == Direction.NORTH){
            return constantDirection;
        }
        if(direction == Direction.SOUTH){
            // clockwise twice / flip
            if(constantDirection == ConstantDirection.NORTH_AND_SOUTH){
                return constantDirection;
            }
            if(constantDirection == ConstantDirection.EAST_AND_WEST){
                return constantDirection;
            }

            if(constantDirection == ConstantDirection.NORTH_AND_EAST){
                return ConstantDirection.SOUTH_AND_WEST;
            }
            if(constantDirection == ConstantDirection.NORTH_AND_WEST){
                return ConstantDirection.SOUTH_AND_EAST;
            }
            if(constantDirection == ConstantDirection.SOUTH_AND_EAST){
                return ConstantDirection.NORTH_AND_WEST;
            }
            if(constantDirection == ConstantDirection.SOUTH_AND_WEST){
                return ConstantDirection.NORTH_AND_EAST;
            }
        }
        if(direction == Direction.EAST){
            // clockwise once
            if(constantDirection == ConstantDirection.NORTH_AND_SOUTH){
                return ConstantDirection.EAST_AND_WEST;
            }
            if(constantDirection == ConstantDirection.EAST_AND_WEST){
                return ConstantDirection.NORTH_AND_SOUTH;
            }

            if(constantDirection == ConstantDirection.NORTH_AND_EAST){
                return ConstantDirection.SOUTH_AND_EAST;
            }
            if(constantDirection == ConstantDirection.NORTH_AND_WEST){
                return ConstantDirection.NORTH_AND_EAST;
            }
            if(constantDirection == ConstantDirection.SOUTH_AND_EAST){
                return ConstantDirection.SOUTH_AND_WEST;
            }
            if(constantDirection == ConstantDirection.SOUTH_AND_WEST){
                return ConstantDirection.NORTH_AND_WEST;
            }
        }
        if(direction == Direction.WEST){
            // counterclockwise once
            if(constantDirection == ConstantDirection.NORTH_AND_SOUTH){
                return ConstantDirection.EAST_AND_WEST;
            }
            if(constantDirection == ConstantDirection.EAST_AND_WEST){
                return ConstantDirection.NORTH_AND_SOUTH;
            }

            if(constantDirection == ConstantDirection.NORTH_AND_EAST){
                return ConstantDirection.NORTH_AND_WEST;
            }
            if(constantDirection == ConstantDirection.NORTH_AND_WEST){
                return ConstantDirection.SOUTH_AND_WEST;
            }
            if(constantDirection == ConstantDirection.SOUTH_AND_EAST){
                return ConstantDirection.NORTH_AND_EAST;
            }
            if(constantDirection == ConstantDirection.SOUTH_AND_WEST){
                return ConstantDirection.SOUTH_AND_EAST;
            }
        }
        return null;
    }

    @Override
    public InteractionResult use(final BlockState blockState, final Level level, final BlockPos blockPos,
                                 final Player player, final InteractionHand hand, final BlockHitResult hitResult) {

        if (hand != InteractionHand.MAIN_HAND) return InteractionResult.PASS;

        final ItemStack heldStack = player.getItemInHand(hand);

        final int processState = blockState.getValue(FRAME_PROCESSED);

        // Try extract
        if (heldStack.isEmpty() && !level.isClientSide) {
            // Extract an item
            if (processState <= FULLY_PROCESSED) {
                AlekiShipsHelper.giveItemToPlayer(player, new ItemStack(this.boatMaterial.getDeckItem()));
            }

            // Set ourselves back to our base
            if (processState == 0) {
                final BlockState newState = AlekiShipsBlocks.BOAT_FRAME_ANGLED.get().defaultBlockState()
                        .setValue(SHAPE, blockState.getValue(SHAPE)).setValue(FACING, blockState.getValue(FACING));

                level.setBlockAndUpdate(blockPos, newState);
                return InteractionResult.SUCCESS;
            }

            level.setBlockAndUpdate(blockPos, blockState.setValue(FRAME_PROCESSED, processState - 1));

            return InteractionResult.SUCCESS;
        }

        // Should we do plank stuff
        if (heldStack.is(this.boatMaterial.getDeckItem())) {
            // Must be [0,3)
            if (processState < FULLY_PROCESSED) {
                if(!player.getAbilities().instabuild){
                    heldStack.shrink(1);
                }
                level.setBlockAndUpdate(blockPos, blockState.cycle(FRAME_PROCESSED));
                level.playSound(null, blockPos, SoundEvents.WOOD_PLACE, SoundSource.BLOCKS, 1.5F,
                        level.getRandom().nextFloat() * 0.1F + 0.9F);
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.CONSUME;
        }

        return InteractionResult.PASS;
    }

    @Override
    @SuppressWarnings("deprecation")
    public ItemStack getCloneItemStack(final BlockGetter blockGetter, final BlockPos blockPos,
                                       final BlockState blockState) {
        // We don't exist as an item so pass it the base version instead
        return AlekiShipsBlocks.BOAT_FRAME_ANGLED.get().getCloneItemStack(blockGetter, blockPos, blockState);
    }

    @Override
    public IntegerProperty getProcessingProperty() {
        return FRAME_PROCESSED;
    }

    @Override
    public int getProcessingLimit() {
        return FULLY_PROCESSED;
    }

    @Override
    public BoatMaterial getBoatMaterial() {
        return this.boatMaterial;
    }
}