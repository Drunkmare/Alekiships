package com.alekiponi.alekiships.common.physics;

import com.google.common.collect.Iterables;
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

    public static double collide(Direction.Axis pMovementAxis, AABB pCollisionBox, Iterable<VoxelShape> pPossibleHits, double pDesiredOffset) {
        for(VoxelShape voxelshape : pPossibleHits) {
            if (Math.abs(pDesiredOffset) < 1.0E-7D) {
                return 0.0D;
            }

            pDesiredOffset = voxelshape.collide(pMovementAxis, pCollisionBox, pDesiredOffset);
        }

        return pDesiredOffset;
    }

}
