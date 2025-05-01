package com.alekiponi.alekiships.common.block;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.stream.Stream;

public class OarlockBlock extends AbstractHullSideBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final MapCodec<OarlockBlock> CODEC = simpleCodec(OarlockBlock::new);
    private static final VoxelShape SHAPE_NORTH = Stream.of(Block.box(3, 0, 0, 13, 3, 3))
            .reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR))
            .get();
    private static final VoxelShape SHAPE_SOUTH = Stream.of(Block.box(3, 0, 13, 13, 3, 16))
            .reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR))
            .get();
    private static final VoxelShape SHAPE_WEST = Stream.of(Block.box(0, 0, 3, 3, 3, 13))
            .reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR))
            .get();
    private static final VoxelShape SHAPE_EAST = Stream.of(Block.box(13, 0, 3, 16, 3, 13))
            .reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR))
            .get();

    protected OarlockBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(
                this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, false));
    }

    public MapCodec<OarlockBlock> codec() {
        return CODEC;
    }

    @Override
    public VoxelShape getShape(BlockState pstate, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        Direction direction = pstate.getValue(FACING);
        return switch (direction) {
            case DOWN, UP, NORTH -> SHAPE_NORTH;
            case SOUTH -> SHAPE_SOUTH;
            case WEST -> SHAPE_WEST;
            case EAST -> SHAPE_EAST;
        };
    }
}