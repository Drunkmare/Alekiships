package com.alekiponi.alekiships.common.entity;

import com.alekiponi.alekiships.common.entity.vehicle.AbstractAlekiBoatEntity;
import com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.EmptyCompartmentEntity;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class CannonballEntity extends Entity {

    protected int lerpSteps;
    protected double lerpX;
    protected double lerpY;
    protected double lerpZ;
    protected double lerpYRot;
    protected double lerpXRot;

    public CannonballEntity(final EntityType<? extends CannonballEntity> entityType, final Level level) {
        super(entityType, level);
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

    @Override
    public void tick() {
        super.tick();
        this.setDeltaMovement(this.getDeltaMovement().add(0, -0.02, 0));
        if (this.isInWater() || this.level().getFluidState(this.blockPosition()).is(TFCFluids.SALT_WATER.getSource())) {
            this.setDeltaMovement(0, -0.05, 0);
            this.setYRot(this.getYRot() + 0.4F);
        }
        if (!this.onGround() || this.getDeltaMovement().horizontalDistanceSqr() > (double) 1.0E-5F) {
            this.move(MoverType.SELF, this.getDeltaMovement());
            final float f1 = 0.98F;

            this.setDeltaMovement(this.getDeltaMovement().multiply(f1, 0.98D, f1));
            if (this.onGround()) {
                final Vec3 vec31 = this.getDeltaMovement();
                if (vec31.y < 0) {
                    this.setDeltaMovement(vec31.multiply(1, -0.5, 1));
                }
            }
        }
        //this.checkInsideBlocks();
        if (tickCount > 0) {
            // hit entities
            final List<Entity> entities = this.level()
                    .getEntities(this, this.getBoundingBox().inflate(1.5, 1.5, 1.5), EntitySelector.NO_SPECTATORS);

            entities.remove(this);

            if (!entities.isEmpty()) {
                this.discard();
                this.explode(Mth.clamp((float) Math.ceil(this.getDeltaMovement().length() / 2), 1, 10));
                for (final Entity entity : entities) {
                    if (!(entity instanceof CannonEntity) && !(entity instanceof EmptyCompartmentEntity) && !(entity instanceof CannonballEntity)) {
                        if (entity instanceof AbstractAlekiBoatEntity) {
                            entity.hurt(this.damageSources().explosion(this, entity), 100);
                        }
                        if (entity instanceof Boat) {
                            entity.hurt(this.damageSources().explosion(this, entity), 1000);
                        }
                    }
                }
                return;
            }

            //velocity based
            if (this.getDeltaMovement().multiply(1, 0, 1).length() < 0.1 || Math.abs(this.getDeltaMovement().y) == 0) {
                this.discard();
                this.explode(4);

                return;
            }
        }

        this.level()
                .addParticle(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, this.getX(), this.getY() + 0.5D, this.getZ(), 0, 0,
                        0);
        //this.tickLerp();

        this.updateInWaterStateAndDoFluidPushing();
    }

    protected void explode(final float radius) {
        this.level()
                .explode(this, this.getX(), this.getY(0.0625D), this.getZ(), radius, Level.ExplosionInteraction.TNT);
    }

    @Override
    public AABB getBoundingBoxForCulling() {
        float bbRadius = 2;
        final Vec3 startingPoint = new Vec3(this.getX() - bbRadius, this.getY() - bbRadius, this.getZ() - bbRadius);
        final Vec3 endingPoint = new Vec3(this.getX() + bbRadius, this.getY() + bbRadius, this.getZ() + bbRadius);
        return new AABB(startingPoint, endingPoint);
    }

    @Override
    public void lerpTo(final double posX, final double posY, final double posZ, final float yaw, final float pitch,
            final int pPosRotationIncrements, final boolean teleport) {
        this.lerpX = posX;
        this.lerpY = posY;
        this.lerpZ = posZ;
        this.lerpYRot = yaw;
        this.lerpXRot = pitch;
        this.lerpSteps = 10;
    }
}