package com.alekiponi.alekiships.common.block;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.stream.Stream;

public class CleatBlock extends AbstractHullSideBlock {

    public static final MapCodec<CleatBlock> CODEC = simpleCodec(CleatBlock::new);
    private static final VoxelShape SHAPE_NORTH = Stream.of(
                    Block.box(5, 0, 2, 11, 2, 4))
            .reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    private static final VoxelShape SHAPE_SOUTH = Stream.of(
                    Block.box(5, 0, 12, 11, 2, 14))
            .reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    private static final VoxelShape SHAPE_WEST = Stream.of(
                    Block.box(2, 0, 5, 4, 2, 11))
            .reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    private static final VoxelShape SHAPE_EAST = Stream.of(
                    Block.box(12, 0, 5, 14, 2, 11))
            .reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    protected CleatBlock(Properties pProperties) {
        super(pProperties);
    }

    public MapCodec<CleatBlock> codec() {
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