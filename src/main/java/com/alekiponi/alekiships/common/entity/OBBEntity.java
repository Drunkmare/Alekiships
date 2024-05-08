package com.alekiponi.alekiships.common.entity;

import com.alekiponi.alekiships.common.physics.OBB;
import com.alekiponi.alekiships.common.physics.OBBCollisions;
import com.google.common.collect.ImmutableList;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.Util;
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

        for(Entity entity : entities){
            if(this.getOBB().intersects(entity.getBoundingBox())){
                isColliding = true;
                break;
            }
        }

        super.tick();


    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {

    }

    @Override
    protected void defineSynchedData() {

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


}
