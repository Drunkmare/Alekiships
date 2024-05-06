package com.alekiponi.alekiships.common.entity;

import com.alekiponi.alekiships.common.physics.OBB;
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
    private EntityPolygonDimensions polyDimensions;
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
    protected void defineSynchedData() {
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {

    }
}
