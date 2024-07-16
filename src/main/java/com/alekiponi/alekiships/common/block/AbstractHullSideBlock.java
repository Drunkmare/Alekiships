package com.alekiponi.alekiships.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

public abstract class AbstractHullSideBlock extends HorizontalDirectionalBlock implements SimpleWaterloggedBlock {

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    protected AbstractHullSideBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, false));
    }

    public static boolean isSupportedByWatercraftFrame(LevelReader pLevel, BlockPos thispos) {
        if (!(pLevel.getBlockState(
                thispos.below()).getBlock() instanceof AngledWoodenBoatFrameBlock || pLevel.getBlockState(
                thispos.below()).getBlock() instanceof AngledBoatFrameBlock)) {
            return false;
        }
        return AngledBoatFrameBlock.ConstantShape.getConstantShape(pLevel.getBlockState(
                thispos.below())) == AngledBoatFrameBlock.ConstantShape.INNER || AngledBoatFrameBlock.ConstantShape.getConstantShape(pLevel.getBlockState(
                thispos.below())) == AngledBoatFrameBlock.ConstantShape.STRAIGHT;
    }

    @Override
    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        return isSupportedByWatercraftFrame(pLevel, pPos);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        Direction direction = pContext.getClickedFace();
        BlockPos blockpos = pContext.getClickedPos();
        final FluidState fluidstate = pContext.getLevel().getFluidState(blockpos);
        LevelAccessor level = pContext.getLevel();
        BlockState blockstate = this.defaultBlockState()
                .setValue(FACING, pContext.getHorizontalDirection().getOpposite()).setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);

        if (level.getBlockState(blockpos.below()).getBlock() instanceof AngledWoodenBoatFrameBlock || level.getBlockState(blockpos.below()).getBlock() instanceof AngledBoatFrameBlock) {

            Direction[] directions = AngledBoatFrameBlock.getSolid(level.getBlockState(blockpos.below()));

            if (directions.length == 0) {
                return blockstate;
            }
            for (Direction dir : directions) {
                if (dir == pContext.getHorizontalDirection().getOpposite()) {
                    return blockstate;
                }
                if (dir == pContext.getHorizontalDirection()) {
                    return blockstate.setValue(FACING, pContext.getHorizontalDirection());
                }
            }

            blockstate = blockstate.setValue(FACING, directions[0]);
        }
        return blockstate;
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING, WATERLOGGED);
    }

    @Override
    @SuppressWarnings("deprecation")
    public FluidState getFluidState(final BlockState blockState) {
        return blockState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(blockState);
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState pState) {
        return true;
    }

    @Override
    public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
        return !pState.canSurvive(pLevel, pCurrentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
    }
}
