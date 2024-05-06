package com.alekiponi.alekiships.common.entity;

import com.alekiponi.alekiships.common.physics.OBB;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;

public class OBBEntity extends Entity {

    private static final OBB INITIAL_OBB = new OBB(new ArrayList<Vec2>(), new Vec3(0,0,0), 0);
    private final EntityPolygonDimensions polyDimensions;
    private OBB obb = INITIAL_OBB;

    public OBBEntity(EntityType<?> pEntityType, Level pLevel, EntityPolygonDimensions dimensions) {
        super(pEntityType, pLevel);
        this.polyDimensions = dimensions;
        this.obb = new OBB(dimensions.getPlanarVertices(), this.position(), dimensions.height);
    }

    public final OBB getPolyBoundingBox() {
        return this.obb;
    }

    public final void setPolyBoundingBox(OBB pBb) {
        setBoundingBox(pBb.getExtent());
        this.obb = pBb;
    }

    @Override
    public void setPos(double p_20210_, double p_20211_, double p_20212_) {
        this.setPosRaw(p_20210_, p_20211_, p_20212_);
        this.setPolyBoundingBox(this.makePolyBoundingBox());
        this.setBoundingBox(this.makeBoundingBox());
    }

    protected OBB makePolyBoundingBox() {
        if(this.polyDimensions == null){
            return new OBB(new Vec2[]{}, this.position(), 0, this.getYRot());
        }
        return this.polyDimensions.makePolyBoundingBox(this.position(), this.getYRot());
    }

    @Override
    public void setYRot(float pYRot) {
        if (!Float.isFinite(pYRot)) {
            Util.logAndPauseIfInIde("Invalid entity rotation: " + pYRot + ", discarding.");
        } else {
            super.setYRot(pYRot);
            this.setPolyBoundingBox(this.makePolyBoundingBox().rotateToY(pYRot));
        }
    }

    @Override
    protected AABB makeBoundingBox() {
        return this.getPolyBoundingBox().getExtent();
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {
    }
}
