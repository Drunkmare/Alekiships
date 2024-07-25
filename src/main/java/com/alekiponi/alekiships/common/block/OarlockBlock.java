package com.alekiponi.alekiships.common.block;

import com.alekiponi.alekiships.common.entity.vehicle.AbstractVehicle;
import com.alekiponi.alekiships.util.BoatMaterial;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.stream.Stream;

import static com.alekiponi.alekiships.util.advancements.AlekiShipsAdvancements.ROWBOAT_COMPLETED;

public class OarlockBlock extends AbstractHullSideBlock {

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    private static final VoxelShape SHAPE_NORTH = Stream.of(
                    Block.box(3, 0, 0, 13, 3, 3))
            .reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    private static final VoxelShape SHAPE_SOUTH = Stream.of(
                    Block.box(3, 0, 13, 13, 3, 16))
            .reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    private static final VoxelShape SHAPE_WEST = Stream.of(
                    Block.box(0, 0, 3, 3, 3, 13))
            .reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    private static final VoxelShape SHAPE_EAST = Stream.of(
                    Block.box(13, 0, 3, 16, 3, 13))
            .reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    protected OarlockBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, false));
    }

    private static Vec3 getSpawnPosition(Level pLevel, BlockPos thispos, BlockState blockState) {
        Direction direction = blockState.getValue(FACING);
        thispos = thispos.below();
        BlockPos otherpos = thispos.relative(direction.getOpposite());
        Vec3 origin = new Vec3(((thispos.getX() + otherpos.getX()) / 2.0f) + 0.5f, thispos.getY() + 0.5f,
                ((thispos.getZ() + otherpos.getZ()) / 2.0f) + 0.5f);
        return origin;
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
        BlockState frameState = level.getBlockState(thispos.below());
        if (validateOarlocks(level, thispos, blockState) && validateFrames(level, thispos, blockState)) {
            Direction direction = blockState.getValue(FACING);
            Direction.Axis axis = direction.getClockWise().getAxis();
            if (frameState.getBlock() instanceof AngledBoatFrameBlock && frameState instanceof ProcessedBoatFrame boatFrameBlock) {
                boatFrameBlock.getBoatMaterial().getEntityType(BoatMaterial.BoatType.ROWBOAT).ifPresent(entityType -> {
                    final AbstractVehicle rowboat = entityType.create(level);
                    if (rowboat != null) {
                        rowboat.setPos(getSpawnPosition(level, thispos, blockState));
                        if (axis == Direction.Axis.X) {
                            rowboat.setYRot(90F);
                        }
                        level.addFreshEntity(rowboat);

                        for (ServerPlayer serverplayer : level.getEntitiesOfClass(ServerPlayer.class, rowboat.getBoundingBox().inflate(5.0D))) {
                            ROWBOAT_COMPLETED.trigger(serverplayer);
                        }

                    }
                });
            }
        }
    }

    public boolean validateOarlocks(Level level, BlockPos thispos, BlockState blockState) {
        Direction direction = blockState.getValue(FACING);
        thispos = thispos.relative(direction.getOpposite());
        if (level.getBlockState(thispos).is(AlekiShipsBlocks.OARLOCK.get())) {
            return (level.getBlockState(thispos)).getValue(FACING) == direction.getOpposite();
        }
        return false;
    }

    public boolean validateFrames(Level level, BlockPos thispos, BlockState blockState) {
        Direction structureDirection = blockState.getValue(FACING).getClockWise();
        Direction.Axis structureAxis = structureDirection.getAxis();
        Direction crossDirection = structureDirection.getClockWise();

        thispos = thispos.below();
        thispos = thispos.relative(structureDirection);

        BlockState frameState = level.getBlockState(thispos);

        final BoatMaterial boatMaterial = BoatFrame.fromBlockstate(frameState);
        if (boatMaterial == null) return false;

        return ShipbuildingMultiblocks.validateShipHull(level, thispos, structureDirection,
                ShipbuildingMultiblocks.Multiblock.ROWBOAT, boatMaterial);
    }

}
