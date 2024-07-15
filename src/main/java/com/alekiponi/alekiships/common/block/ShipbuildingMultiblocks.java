package com.alekiponi.alekiships.common.block;

import com.alekiponi.alekiships.util.BoatMaterial;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class ShipbuildingMultiblocks {
    public static ShipbuildingBlockValidator[][] sloopMultiblock = {
            {
                    new ShipbuildingBlockValidator(false),
                    new ShipbuildingBlockValidator(AngledBoatFrameBlock.ConstantShape.INNER, AngledBoatFrameBlock.ConstantDirection.SOUTH_AND_EAST),
                    new ShipbuildingBlockValidator(AngledBoatFrameBlock.ConstantShape.INNER, AngledBoatFrameBlock.ConstantDirection.SOUTH_AND_WEST),
                    new ShipbuildingBlockValidator(false),
            },
            {
                    new ShipbuildingBlockValidator(false),
                    new ShipbuildingBlockValidator(Direction.WEST),
                    new ShipbuildingBlockValidator(Direction.EAST),
                    new ShipbuildingBlockValidator(false),
            },
            {
                    new ShipbuildingBlockValidator(AngledBoatFrameBlock.ConstantShape.INNER, AngledBoatFrameBlock.ConstantDirection.SOUTH_AND_EAST),
                    new ShipbuildingBlockValidator(AngledBoatFrameBlock.ConstantShape.OUTER, AngledBoatFrameBlock.ConstantDirection.NORTH_AND_WEST),
                    new ShipbuildingBlockValidator(AngledBoatFrameBlock.ConstantShape.OUTER, AngledBoatFrameBlock.ConstantDirection.NORTH_AND_EAST),
                    new ShipbuildingBlockValidator(AngledBoatFrameBlock.ConstantShape.INNER, AngledBoatFrameBlock.ConstantDirection.SOUTH_AND_WEST),
            },
            {
                    new ShipbuildingBlockValidator(Direction.WEST),
                    new ShipbuildingBlockValidator(true),
                    new ShipbuildingBlockValidator(true),
                    new ShipbuildingBlockValidator(Direction.EAST),
            },
            {
                    new ShipbuildingBlockValidator(Direction.WEST),
                    new ShipbuildingBlockValidator(true),
                    new ShipbuildingBlockValidator(true),
                    new ShipbuildingBlockValidator(Direction.EAST),
            },
            {
                    new ShipbuildingBlockValidator(Direction.WEST),
                    new ShipbuildingBlockValidator(true),
                    new ShipbuildingBlockValidator(true),
                    new ShipbuildingBlockValidator(Direction.EAST),
            },
            {
                    new ShipbuildingBlockValidator(AngledBoatFrameBlock.ConstantShape.INNER, AngledBoatFrameBlock.ConstantDirection.NORTH_AND_EAST),
                    new ShipbuildingBlockValidator(Direction.SOUTH),
                    new ShipbuildingBlockValidator(Direction.SOUTH),
                    new ShipbuildingBlockValidator(AngledBoatFrameBlock.ConstantShape.INNER, AngledBoatFrameBlock.ConstantDirection.NORTH_AND_WEST),
            },
    };

    public static ShipbuildingBlockValidator[][] rowboatMultiblock = {
            {
                    new ShipbuildingBlockValidator(AngledBoatFrameBlock.ConstantShape.INNER, AngledBoatFrameBlock.ConstantDirection.SOUTH_AND_EAST),
                    new ShipbuildingBlockValidator(AngledBoatFrameBlock.ConstantShape.INNER, AngledBoatFrameBlock.ConstantDirection.SOUTH_AND_WEST)
            },
            {
                    new ShipbuildingBlockValidator(Direction.WEST),
                    new ShipbuildingBlockValidator(Direction.EAST)
            },
            {
                    new ShipbuildingBlockValidator(AngledBoatFrameBlock.ConstantShape.INNER, AngledBoatFrameBlock.ConstantDirection.NORTH_AND_EAST),
                    new ShipbuildingBlockValidator(AngledBoatFrameBlock.ConstantShape.INNER, AngledBoatFrameBlock.ConstantDirection.NORTH_AND_WEST)
            }
    };

    public static enum Multiblock {
        ROWBOAT,
        SLOOP
    }


    public static boolean validateShipHull(final Level level, final BlockPos startPos,
            final Direction structureDirection, final Multiblock multiblock, final BoatMaterial boatMaterial) {
        final Direction crossDirection = structureDirection.getClockWise();

        final ShipbuildingBlockValidator[][] multiblockValidators = switch (multiblock) {
            case ROWBOAT -> rowboatMultiblock;
            case SLOOP -> sloopMultiblock;
        };

        boolean success = true;

        for (int y = 0; y < multiblockValidators.length; y++) {
            for (int x = 0; x < multiblockValidators[0].length; x++) {
                final BlockState blockState = level.getBlockState(
                        startPos.relative(structureDirection.getOpposite(), y).relative(crossDirection, x));
                if (!multiblockValidators[y][x].validate(blockState, structureDirection, boatMaterial)) {
                    success = false;
                    break;
                }
            }
        }

        if (!success) {
            Player player = level.getNearestPlayer(startPos.getX(), startPos.getY(), startPos.getZ(), 12, EntitySelector.NO_SPECTATORS);
            if (player != null && level.isClientSide()) {
                player.displayClientMessage(Component.translatable("failed_multiblock_detection"), true);
            }
            return false;
        }

        for (int y = 0; y < multiblockValidators.length; y++) {
            for (int x = 0; x < multiblockValidators[0].length; x++) {
                if (multiblockValidators[y][x].shouldDestroy()) {
                    level.destroyBlock(startPos.relative(structureDirection.getOpposite(), y).relative(crossDirection, x), false);
                }
            }
        }

        return true;
    }

}
