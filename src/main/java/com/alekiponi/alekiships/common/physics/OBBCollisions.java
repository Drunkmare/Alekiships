package com.alekiponi.alekiships.common.physics;

import com.google.common.collect.Iterables;
import net.minecraft.core.AxisCycle;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockCollisions;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.List;

public class OBBCollisions {

    // getEntityCollisions

    // getBlockCollisions

    //List<VoxelShape> getEntityCollisions(@Nullable Entity pEntity, AABB pCollisionBox);

    public static Iterable<VoxelShape> getCollisions(Level level, @Nullable Entity pEntity, AABB pCollisionBox) {
        List<VoxelShape> list = level.getEntityCollisions(pEntity, pCollisionBox);
        Iterable<VoxelShape> iterable = level.getBlockCollisions(pEntity, pCollisionBox);
        return list.isEmpty() ? iterable : Iterables.concat(list, iterable);
    }

    public static Iterable<VoxelShape> getBlockCollisions(Level level, @Nullable Entity pEntity, AABB pCollisionBox, OBB pObb) {
        return () -> {
            return new OBBBlockCollisions<>(level, pEntity, pCollisionBox, pObb, false, (p_286215_, p_286216_) -> {
                return p_286216_;
            });
        };
    }

    /*
    public static double collide(Direction.Axis pMovementAxis, OBB pCollisionBox, Iterable<VoxelShape> pPossibleHits, double pDesiredOffset) {
        for (VoxelShape voxelshape : pPossibleHits) {
            if (Math.abs(pDesiredOffset) < 1.0E-7D) {
                return 0.0D;
            }

            pDesiredOffset = collide(voxelshape, pMovementAxis, pCollisionBox, pDesiredOffset);
        }

        return pDesiredOffset;
    }


    public static double collide(VoxelShape thisShape, Direction.Axis pMovementAxis, OBB pOBB, double desiredMovement) {
        return collideX(thisShape, AxisCycle.between(pMovementAxis, Direction.Axis.X), pOBB, desiredMovement);
    }*/

    public static double collideY(List<VoxelShape> shapeList, OBB pOBB, double desiredMovement){
        for(VoxelShape shape : shapeList){
            double movement = collideY(shape, pOBB, desiredMovement);
            if(Math.abs(movement) < Math.abs(desiredMovement)){
                desiredMovement = movement;
            }
        }
        return desiredMovement;
    }

    public static double collideY(VoxelShape thisShape, OBB pOBB, double desiredMovement){
        List<AABB> shapes = thisShape.toAabbs();
        for (AABB shape : shapes) {
            double movement = pOBB.collideY(shape, desiredMovement);
            if(Math.abs(movement) < Math.abs(desiredMovement)){
                desiredMovement = movement;
            }
        }
        return desiredMovement;
    }

    /*
    public static double collideX(VoxelShape thisShape, AxisCycle pMovementAxis, OBB pOBB, double pDesiredOffset) {
        if (thisShape.isEmpty()) {
            return pDesiredOffset;
        } else if (Math.abs(pDesiredOffset) < 1.0E-7D) {
            return 0.0D;
        }

        AxisCycle axiscycle = pMovementAxis.inverse();
        Direction.Axis direction$axis = axiscycle.cycle(Direction.Axis.X);
        Direction.Axis direction$axis1 = axiscycle.cycle(Direction.Axis.Y);
        Direction.Axis direction$axis2 = axiscycle.cycle(Direction.Axis.Z);

        List<AABB> shapes = thisShape.toAabbs();
        for (AABB shape : shapes) {
            pOBB.collide(shape);
        }
    }*/

}
