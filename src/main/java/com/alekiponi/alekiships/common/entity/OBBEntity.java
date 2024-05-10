package com.alekiponi.alekiships.common.entity;

import com.alekiponi.alekiships.common.physics.OBB;
import com.alekiponi.alekiships.common.physics.OBBCollisions;
import com.google.common.collect.ImmutableList;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static com.alekiponi.alekiships.common.physics.OBB.intersects;

public abstract class OBBEntity extends Entity {

    private static final OBB INITIAL_OBB = new OBB(new Vec2[0], new Vec3(0, 0, 0), 0, 0);
    private final EntityOBBDimensions obbDimensions;
    private OBB obb = INITIAL_OBB;

    private static final EntityDataAccessor<Float> DATA_ID_OBB_ROT = SynchedEntityData.defineId(OBBEntity.class, EntityDataSerializers.FLOAT);
    private boolean isColliding;

    public OBBEntity(EntityType<?> pEntityType, Level pLevel, EntityOBBDimensions dimensions) {
        super(pEntityType, pLevel);
        this.obbDimensions = dimensions;
        this.obb = new OBB(dimensions.getPlanarVertices(), this.position(), dimensions.height, 0);
    }

    public final OBB getOBB() {
        return this.obb;
    }

    public final void setOBB(OBB pBb) {
        setBoundingBox(pBb.getExtent());
        this.obb = pBb;
    }

    public boolean isColliding() {
        return isColliding;
    }

    @Override
    public void tick(){
        isColliding = false;

        List<Entity> entities = this.level()
                .getEntities(this, this.getBoundingBox().inflate(0, -this.getBoundingBox().getYsize() + 2, 0).move(0, this.getBoundingBox().getYsize(), 0), EntitySelector.NO_SPECTATORS);

        entities.removeIf(entity -> entity.getRootVehicle().is(this));
        entities.removeIf(entity -> entity instanceof OBBEntity);

        for(Entity entity : entities){
            if(intersects(this.getOBB(), entity.getBoundingBox())){
                isColliding = true;
                break;
            }
        }

        /*
        for(Entity entity : entities){
            if(entity instanceof LocalPlayer player){
                Vec3 pos = OBB.collide(this.getOBB(), player.getBoundingBox());
                pos = pos.multiply(1,0,1).add(0,player.position().y,0);
                player.setPos(pos);
            }
        }*/

        super.tick();

    }

    @Override
    public void setPos(double p_20210_, double p_20211_, double p_20212_) {
        this.setPosRaw(p_20210_, p_20211_, p_20212_);
        this.setOBB(this.makeOBB());
        this.setBoundingBox(this.makeBoundingBox());
    }

    protected OBB makeOBB() {
        if (this.obbDimensions == null) {
            return new OBB(new Vec2[]{}, this.position(), 0, this.getYRot());
        }
        OBB obb = this.obbDimensions.makePolyBoundingBox(this.position(), this.getYRot());
        return obb;
    }

    @Override
    public void setYRot(float pYRot) {
        if (!Float.isFinite(pYRot)) {
            Util.logAndPauseIfInIde("Invalid entity rotation: " + pYRot + ", discarding.");
        } else {
            super.setYRot(pYRot);
            this.setOBB(new OBB(this.makeOBB(), this.getYRot()));
        }
    }

    @Override
    protected AABB makeBoundingBox() {
        return this.getOBB().getExtent();
    }

    protected Vec3 collide(Vec3 pVec) {
        AABB aabb = this.getBoundingBox();
        OBB obb = this.getOBB();
        List<VoxelShape> list = this.level().getEntityCollisions(this, aabb.expandTowards(pVec));
        Vec3 vec3 = pVec.lengthSqr() == 0.0D ? pVec : collideBoundingBox(this, pVec, aabb, obb, this.level(), list);

        return vec3;
    }

    public static Vec3 collideBoundingBox(@Nullable Entity pEntity, Vec3 pVec, AABB pCollisionBox, OBB pOBB, Level pLevel, List<VoxelShape> pPotentialHits) {
        ImmutableList.Builder<VoxelShape> builder = ImmutableList.builderWithExpectedSize(pPotentialHits.size() + 1);
        if (!pPotentialHits.isEmpty()) {
            builder.addAll(pPotentialHits);
        }

        WorldBorder worldborder = pLevel.getWorldBorder();
        boolean flag = pEntity != null && worldborder.isInsideCloseToBorder(pEntity, pCollisionBox.expandTowards(pVec));
        if (flag) {
            builder.add(worldborder.getCollisionShape());
        }

        builder.addAll(pLevel.getBlockCollisions(pEntity, pCollisionBox.expandTowards(pVec)));
        return collideWithShapes(pVec, pOBB, builder.build());
    }

    private static Vec3 collideWithShapes(Vec3 pDeltaMovement, OBB pOBB, List<VoxelShape> pShapes) {
        if (pShapes.isEmpty()) {
            return pDeltaMovement;
        } else {
            double movementX = pDeltaMovement.x;
            double movementY = pDeltaMovement.y;
            double movementZ = pDeltaMovement.z;


            /*
            if (movementY != 0.0D) {
                movementY = Shapes.collide(Direction.Axis.Y, pOBB.getExtent(), pShapes, movementY);
                if (movementY != 0.0D) {
                    pOBB = pOBB.move(0.0D, movementY, 0.0D);
                }
            }*/


            if (movementY != 0.0D) {
                movementY = OBBCollisions.collideY(pShapes, pOBB, movementY);
                if (movementY != 0.0D) {
                    pOBB = pOBB.move(0.0D, movementY, 0.0D);
                }
            }

            /*
            boolean flag = Math.abs(movementX) < Math.abs(movementZ);


            if (flag && movementZ != 0.0D) {
                movementZ = Shapes.collide(Direction.Axis.Z, pOBB.getExtent(), pShapes, movementZ);
                if (movementZ != 0.0D) {
                    pOBB = pOBB.move(0.0D, 0.0D, movementZ);
                }
            }

            if (movementX != 0.0D) {
                movementX = Shapes.collide(Direction.Axis.X, pOBB.getExtent(), pShapes, movementX);
                if (!flag && movementX != 0.0D) {
                    pOBB = pOBB.move(movementX, 0.0D, 0.0D);
                }
            }

            if (!flag && movementZ != 0.0D) {
                movementZ = Shapes.collide(Direction.Axis.Z, pOBB.getExtent(), pShapes, movementZ);
            }*/

            return new Vec3(movementX, movementY, movementZ);
        }
    }


}
