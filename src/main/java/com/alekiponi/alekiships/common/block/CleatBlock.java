package com.alekiponi.alekiships.common.block;

import java.util.stream.Stream;

import com.alekiponi.alekiships.common.AlekiShipsRegistries;
import com.alekiponi.alekiships.common.entity.AlekiShipsEntities;
import com.alekiponi.alekiships.common.entity.vehicle.SloopUnderConstructionEntity;
import com.alekiponi.alekiships.util.BoatMaterial;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

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

    @Override
    public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pIsMoving) {
        if (!pOldState.is(pState.getBlock())) {
            validateMultiblock(pLevel, pPos, pState);
        }
        super.onPlace(pState, pLevel, pPos, pOldState, pIsMoving);
    }

    public void validateMultiblock(Level level, BlockPos thispos, BlockState blockState) {
        Direction direction = blockState.getValue(FACING);
        Direction.Axis axis = direction.getClockWise().getAxis();
        BlockPos crosspos = thispos.relative(direction.getOpposite(), 3);
        Direction structureDirection;
        BlockPos[] cleats = new BlockPos[4];
        cleats[0] = thispos;
        if (level.getBlockState(crosspos).is(AlekiShipsBlocks.CLEAT.get()) && level.getBlockState(crosspos)
                .getValue(FACING) == direction.getOpposite()) {
            cleats[1] = crosspos;

            BlockPos forwardPos = thispos.relative(axis, 4);
            BlockPos backwardPos = thispos.relative(axis, -4);

            if (level.getBlockState(forwardPos).is(AlekiShipsBlocks.CLEAT.get()) && level.getBlockState(forwardPos)
                    .getValue(FACING) == direction) {
                crosspos = forwardPos.relative(direction.getOpposite(), 3);
                if (level.getBlockState(crosspos).is(AlekiShipsBlocks.CLEAT.get()) && level.getBlockState(crosspos)
                        .getValue(FACING) == direction.getOpposite()) {
                    cleats[2] = thispos;
                    cleats[3] = cleats[1];
                    cleats[0] = forwardPos;
                    cleats[1] = crosspos;
                }
            } else if (level.getBlockState(backwardPos).is(AlekiShipsBlocks.CLEAT.get()) && level.getBlockState(
                    backwardPos).getValue(FACING) == direction) {
                crosspos = backwardPos.relative(direction.getOpposite(), 3);
                if (level.getBlockState(crosspos).is(AlekiShipsBlocks.CLEAT.get()) && level.getBlockState(crosspos)
                        .getValue(FACING) == direction.getOpposite()) {
                    cleats[2] = backwardPos;
                    cleats[3] = crosspos;
                }
            }
        }

        for (BlockPos blockPos : cleats) {
            if (blockPos == null) {
                return;
            }
        }

        // we only know the axis at this point so let's get the direction by checking forward and back front middles
        Direction.Axis crossAxis = Direction.Axis.X;
        if (axis == Direction.Axis.X) {
            crossAxis = Direction.Axis.Z;
        }

        // flip the structure if it needs to be flipped
        if (cleats[0].get(crossAxis) > cleats[1].get(crossAxis)) {
            BlockPos[] newCleats = new BlockPos[4];
            newCleats[0] = cleats[1];
            newCleats[1] = cleats[0];
            newCleats[2] = cleats[3];
            newCleats[3] = cleats[2];
            cleats = newCleats;
        }

        if (level.getBlockState(cleats[0].below().relative(crossAxis, 1).relative(axis, 2))
                .getBlock() instanceof AngledBoatFrameBlock) {
            structureDirection = Direction.fromAxisAndDirection(axis, Direction.AxisDirection.POSITIVE);
        } else {
            structureDirection = Direction.fromAxisAndDirection(axis, Direction.AxisDirection.NEGATIVE);
            BlockPos[] newCleats = new BlockPos[4];
            newCleats[0] = cleats[2];
            newCleats[1] = cleats[3];
            newCleats[2] = cleats[0];
            newCleats[3] = cleats[1];
            cleats = newCleats;
        }

        BlockPos origin = cleats[0].relative(structureDirection, 2).below();
        if (structureDirection == Direction.WEST || structureDirection == Direction.SOUTH) {
            origin = cleats[1].relative(structureDirection, 2).below();
        }

        final BlockState frameState = level.getBlockState(thispos.below());
        final BoatMaterial boatMaterial = BoatFrame.fromBlockstate(frameState);

        if (boatMaterial == null) return;

        if (ShipbuildingMultiblocks.validateShipHull(level, origin, structureDirection,
                ShipbuildingMultiblocks.Multiblock.SLOOP,
                boatMaterial) && frameState.getBlock() instanceof AngledBoatFrameBlock && frameState.getBlock() instanceof ProcessedBoatFrame boatFrameBlock) {
            // spawn sloop construction entity
            BlockPos pos1 = origin.relative(structureDirection.getOpposite(), 3)
                    .relative(structureDirection.getClockWise(), 1);
            BlockPos pos2 = origin.relative(structureDirection.getOpposite(), 5)
                    .relative(structureDirection.getClockWise(), 3);
            Vec3 spawnPosition = new Vec3(pos1.getX() + pos2.getX(), pos1.getY() + pos2.getY(),
                    pos1.getZ() + pos2.getZ()).multiply(0.5, 0.5, 0.5);
            //TODO fix this with better math instead please :)
            if (structureDirection == Direction.EAST || structureDirection == Direction.SOUTH) {
                spawnPosition = spawnPosition.add(1, 0, 0);
            }
            if (structureDirection == Direction.WEST || structureDirection == Direction.SOUTH) {
                spawnPosition = spawnPosition.add(0, 0, 1);
            }


            // TODO remove me when entity multliblock comes around
            final var sloopUnderConstructionEntity = new SloopUnderConstructionEntity(
                    AlekiShipsEntities.CONSTRUCTION_SLOOP.get(), level);
            final var registryAccess = level.registryAccess().registry(AlekiShipsRegistries.CONSTRUCTION_SLOOP_VARIANT)
                    .orElseThrow();
            sloopUnderConstructionEntity.setConstructionVariant(
                    registryAccess.getHolder(boatMaterial.sloopConstructionKey()).orElseThrow());

            sloopUnderConstructionEntity.setPos(spawnPosition);
            if (structureDirection == Direction.NORTH) {
                sloopUnderConstructionEntity.setYRot(180F);
            } else if (structureDirection == Direction.EAST) {
                sloopUnderConstructionEntity.setYRot(-90F);
            } else if (structureDirection == Direction.WEST) {
                sloopUnderConstructionEntity.setYRot(90F);
            }
            level.addFreshEntity(sloopUnderConstructionEntity);
        }


    }

}
