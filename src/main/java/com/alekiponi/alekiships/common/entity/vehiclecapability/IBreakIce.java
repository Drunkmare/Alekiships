package com.alekiponi.alekiships.common.entity.vehiclecapability;

import com.alekiponi.alekiships.common.entity.vehicle.AbstractVehicle;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public interface IBreakIce {
    default void tickBreakIce() {
        if (!this.breaksIce()) {
            return;
        }
        if (!((AbstractVehicle) this).isFunctional()) {
            return;
        }
        final BlockPos.MutableBlockPos blockPos = ((Entity) this).blockPosition().mutable();

        final int size;
        {
            int sizeTemp = (int) Math.ceil(((Entity) this).getBoundingBox().getXsize());
            if (sizeTemp % 2 != 0) {
                sizeTemp++;
            }
            size = sizeTemp / 2;
        }

        // Move to our destroy "origin"
        blockPos.move(-size, 0, -size);

        boolean flag = false;
        for (int x = -size; x <= size; x++) {
            for (int z = -size; z <= size; z++) {
                for (int y = 0; y < 2; y++) {
                    final BlockState blockState = ((Entity) this).level().getBlockState(blockPos);
                    if (blockState.is(Blocks.ICE)) {
                        flag = true;
                        ((Entity) this).level().destroyBlock(blockPos, false);
                        ((Entity) this).level().setBlock(blockPos, Blocks.WATER.defaultBlockState(), 2);
                    }
                    // Move down a block
                    blockPos.setY(blockPos.getY() - 1);
                }
                // Move us back to our y "origin"
                blockPos.setY(blockPos.getY() + 2);
                // Move positive z
                blockPos.setZ(blockPos.getZ() + 1);
            }
            // Move us back to our z "origin"
            blockPos.setZ(blockPos.getZ() - (1 + size * 2));
            // Move positive x
            blockPos.setX(blockPos.getX() + 1);
        }
        if (flag) {
            ((Entity) this).setDeltaMovement(((Entity) this).getDeltaMovement().multiply(0.7, 1, 0.7));
        }

    }

    boolean breaksIce();
}
