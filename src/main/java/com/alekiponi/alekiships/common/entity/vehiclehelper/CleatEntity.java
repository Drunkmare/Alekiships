package com.alekiponi.alekiships.common.entity.vehiclehelper;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Leashable;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

// TODO, ensure this works as expected when relying on Leashable
public class CleatEntity extends AbstractPassthroughHelper implements Leashable {

    @Nullable
    private Leashable.LeashData leashData;

    protected int lerpSteps;
    protected double lerpX;
    protected double lerpY;
    protected double lerpZ;
    protected double lerpYRot;
    protected double lerpXRot;

    public CleatEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    protected void defineSynchedData(final SynchedEntityData.Builder builder) {

    }

    public void tick() {
        if (!this.isPassenger()) {
            this.dropLeash(true, true);
            this.kill();
        }
        super.tick();

        tickLerp();
        Leashable.tickLeash(this);
    }

    protected void tickLerp() {
        if (this.isControlledByLocalInstance()) {
            this.lerpSteps = 0;
            this.syncPacketPositionCodec(this.getX(), this.getY(), this.getZ());
        }

        if (this.lerpSteps > 0) {
            double d0 = this.getX() + (this.lerpX - this.getX()) / (double) this.lerpSteps;
            double d1 = this.getY() + (this.lerpY - this.getY()) / (double) this.lerpSteps;
            double d2 = this.getZ() + (this.lerpZ - this.getZ()) / (double) this.lerpSteps;
            double d3 = Mth.wrapDegrees(this.lerpYRot - (double) this.getYRot());
            this.setYRot(this.getYRot() + (float) d3 / (float) this.lerpSteps);
            this.setXRot(this.getXRot() + (float) (this.lerpXRot - (double) this.getXRot()) / (float) this.lerpSteps);
            --this.lerpSteps;
            this.setPos(d0, d1, d2);
            //this.setRot(this.getYRot(), this.getXRot());
        }
    }

    public boolean isPickable() {
        return !this.isRemoved();
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
        this.writeLeashData(pCompound, this.leashData);
    }

    @Override
    public Vec3 getLeashOffset(float pPartialTick) {
        return new Vec3(0.0f, 0f, 0f);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {
        this.leashData = this.readLeashData(pCompound);
    }

    @Override
    public AABB getBoundingBoxForCulling() {
        float bbRadius = 10 * 2 + 1;
        Vec3 startingPoint = new Vec3(this.getX() - bbRadius, this.getY() - bbRadius, this.getZ() - bbRadius);
        Vec3 endingPoint = new Vec3(this.getX() + bbRadius, this.getY() + bbRadius, this.getZ() + bbRadius);
        return new AABB(startingPoint, endingPoint);
    }

    @Nullable
    @Override
    public Leashable.LeashData getLeashData() {
        return this.leashData;
    }

    @Override
    public void setLeashData(@Nullable Leashable.LeashData leashData) {
        this.leashData = leashData;
    }
}
