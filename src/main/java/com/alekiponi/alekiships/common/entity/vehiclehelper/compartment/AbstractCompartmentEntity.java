package com.alekiponi.alekiships.common.entity.vehiclehelper.compartment;

import com.alekiponi.alekiships.client.IngameOverlays;
import com.alekiponi.alekiships.common.entity.IHaveIcons;
import com.alekiponi.alekiships.common.entity.vehicle.AbstractVehicle;
import com.alekiponi.alekiships.common.entity.vehiclehelper.VehiclePart;
import com.alekiponi.alekiships.common.entity.vehiclehelper.CompartmentType;
import com.alekiponi.alekiships.util.CommonHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.ArrayList;

public abstract class AbstractCompartmentEntity extends Entity implements IHaveIcons {
    private static final EntityDataAccessor<Integer> DATA_ID_HURT = SynchedEntityData.defineId(
            AbstractCompartmentEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_ID_HURT_DIR = SynchedEntityData.defineId(
            AbstractCompartmentEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> DATA_ID_DAMAGE = SynchedEntityData.defineId(
            AbstractCompartmentEntity.class, EntityDataSerializers.FLOAT);
    private static final float DAMAGE_TO_BREAK = 10.0f;
    private static final float DAMAGE_RECOVERY = 0.5f;
    public int lifespan = 6000;
    protected int lerpSteps;
    protected double lerpX;
    protected double lerpY;
    protected double lerpZ;
    protected double lerpYRot;
    protected double lerpXRot;
    @Nullable
    protected VehiclePart ridingThisPart = null;

    protected int vehiclePassengerIndex = -1;
    private int notRidingTicks = 0;

    public AbstractCompartmentEntity(final CompartmentType<? extends AbstractCompartmentEntity> compartmentType,
                                     final Level level) {
        super(compartmentType, level);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_ID_HURT, 0);
        this.entityData.define(DATA_ID_HURT_DIR, 1);
        this.entityData.define(DATA_ID_DAMAGE, 0F);
    }

    /**
     * Replaces this object with the passed in CompartmentEntity
     *
     * @param newCompartment The compartment entity which is replacing this object
     * @return The compartment passed in
     */
    protected AbstractCompartmentEntity swapCompartments(final AbstractCompartmentEntity newCompartment) {
        this.spawnAtLocation(this.getDropStack(), 1);
        this.stopRiding();
        this.discard();
        newCompartment.setYRot(this.getYRot());
        newCompartment.setPos(this.getX(), this.getY(), this.getZ());
        newCompartment.ridingThisPart = this.ridingThisPart;
        assert ridingThisPart != null;
        newCompartment.startRiding(ridingThisPart);
        this.level().addFreshEntity(newCompartment);
        return newCompartment;
    }

    @Override
    public void tick() {
        if (ridingThisPart == null && this.isPassenger() && this.getVehicle() instanceof VehiclePart) {
            ridingThisPart = (VehiclePart) this.getVehicle();
        }
        if (vehiclePassengerIndex == -1 && this.isPassenger() && this.getRootVehicle() instanceof AbstractVehicle vehicle && vehicle.getPassengers().size() == vehicle.getMaxPassengers()) {
            vehiclePassengerIndex = vehicle.getPassengers().indexOf(this.getVehicle());
        }

        if (!this.isPassenger()) {
            vehiclePassengerIndex = -1;
            this.checkInsideBlocks();
            if (!(this instanceof EmptyCompartmentEntity)) {
                this.setDeltaMovement(this.getDeltaMovement().add(0, -0.04D, 0));
                if (this.isInWater()) {
                    if (this.getFluidTypeHeight(this.getEyeInFluidType()) > this.getEyeHeight() - 0.25) {
                        this.setDeltaMovement(0, this.getBuoyancy(), 0);
                    }
                    if (!this.onGround()) {
                        this.setYRot(this.getYRot() + 0.4f);
                    }
                }
                if (!this.onGround() || this.getDeltaMovement()
                        .horizontalDistanceSqr() > (double) 1.0E-5F || (this.tickCount + this.getId()) % 4 == 0) {
                    this.move(MoverType.SELF, this.getDeltaMovement());
                    float f1 = 0.98F;

                    this.setDeltaMovement(this.getDeltaMovement().multiply(f1, 0.98D, f1));
                    if (this.onGround()) {
                        Vec3 vec31 = this.getDeltaMovement();
                        if (vec31.y < 0.0D) {
                            this.setDeltaMovement(vec31.multiply(1, -0.5D, 1));
                        }
                    }
                }
                if (!this.level().isClientSide()) {
                    notRidingTicks++;
                    if (notRidingTicks > lifespan) {
                        this.spawnAtLocation(this.getDropStack(), 1);
                        this.discard();
                    }
                }

                this.updateInWaterStateAndDoFluidPushing();
            } else if (!this.level().isClientSide()) {
                notRidingTicks++;
                if (notRidingTicks > 1) {
                    this.spawnAtLocation(this.getDropStack(), 1);
                    this.discard();
                }
            }
        } else if (this.level().isClientSide()) {
            notRidingTicks = 0;
        }

        if (this.getHurtTime() > 0) {
            this.setHurtTime(this.getHurtTime() - 1);
        }

        if (this.getDamage() > 0) {
            this.setDamage(this.getDamage() - DAMAGE_RECOVERY);
        }

        super.tick();
        this.tickLerp();
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

    @Override
    public boolean isInvulnerableTo(DamageSource pSource) {
        if (pSource.is(DamageTypeTags.IS_EXPLOSION)) {
            return true;
        }
        return super.isInvulnerableTo(pSource);
    }

    public boolean everyNthTickUnique(int n) {
        return CommonHelper.everyNthTickUnique(this.getId(), this.tickCount, n);
    }

    @Override
    public boolean hurt(final DamageSource damageSource, final float amount) {
        if (this.level().isClientSide || this.isRemoved()) return true;

        if (this.isInvulnerableTo(damageSource)) return false;

        this.setHurtDir(-this.getHurtDir());
        this.setHurtTime(10);
        this.setDamage(this.getDamage() + amount * 5);

        this.markHurt();
        this.gameEvent(GameEvent.ENTITY_DAMAGE, damageSource.getEntity());
        final boolean instantKill = damageSource.getEntity() instanceof Player && ((Player) damageSource.getEntity()).getAbilities().instabuild;

        // Don't kill
        if (!instantKill && !(this.getDamage() > DAMAGE_TO_BREAK)) {
            this.onHurt(damageSource);
            return true;
        }

        this.ejectPassengers();
        if (!instantKill || this.hasCustomName()) {
            this.destroy(damageSource);
        }

        this.discard();
        return true;
    }

    protected void destroy(final DamageSource damageSource) {
        this.kill();
        if (this.level().getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            final ItemStack itemStack = this.getDropStack();
            if (this.hasCustomName()) {
                itemStack.setHoverName(this.getCustomName());
            }

            double y = this.getRootVehicle().getBoundingBox().maxY + 0.6;
            if(y > this.getY()){
                this.spawnAtLocation(itemStack, (float) (y-this.getY()));
            } else {
                this.spawnAtLocation(itemStack);
            }
        }
    }

    @Override
    protected void readAdditionalSaveData(final CompoundTag compoundTag) {
        this.lifespan = compoundTag.getInt("Lifespan");
        this.notRidingTicks = compoundTag.getInt("notRidingTicks");
    }

    @Override
    protected void addAdditionalSaveData(final CompoundTag compoundTag) {
        compoundTag.putInt("Lifespan", this.lifespan);
        compoundTag.putInt("notRidingTicks", this.notRidingTicks);
    }

    /**
     * Allows children to easily override the buoyancy.
     * This value is used as is so to have a compartment which floats you should return a positive value
     */
    public double getBuoyancy() {
        return -0.01;
    }

    @Override
    public double getMyRidingOffset() {
        return 0.125D;
    }

    @Nullable
    public AbstractVehicle getTrueVehicle() {
        if (this.getRootVehicle() instanceof AbstractVehicle vehicle) {
            return vehicle;
        }
        return null;
    }

    public float getDamage() {
        return this.entityData.get(DATA_ID_DAMAGE);
    }

    public void setDamage(final float damageTaken) {
        this.entityData.set(DATA_ID_DAMAGE, damageTaken);
    }

    public int getHurtTime() {
        return this.entityData.get(DATA_ID_HURT);
    }

    public void setHurtTime(final int hurtTime) {
        this.entityData.set(DATA_ID_HURT, hurtTime);
    }

    public int getHurtDir() {
        return this.entityData.get(DATA_ID_HURT_DIR);
    }

    public void setHurtDir(final int pHurtDirection) {
        this.entityData.set(DATA_ID_HURT_DIR, pHurtDirection);
    }

    public RidingPose getRidingPose() {
        return RidingPose.STANDARD;
    }

    public ArrayList<IngameOverlays.IconState> getIconStates(Player player) {
        ArrayList<IngameOverlays.IconState> states = new ArrayList<>();
        return states;
    }

    @Override
    public void remove(final RemovalReason removalReason) {
        if (!this.level().isClientSide && removalReason.shouldDestroy()) {
            this.onBreak();
        }

        super.remove(removalReason);
    }

    @Override
    public boolean isPickable() {
        return !this.isRemoved();
    }

    public boolean shouldFaceOtherWay() {
        return false;
    }

    /**
     * Gets the {@link ItemStack} that should be dropped when this compartment is destroyed
     * This is so compartments can easily control the output stack or add NBT
     *
     * @return The {@link ItemStack} that should be dropped in world when the compartment is destroyed
     */
    protected abstract ItemStack getDropStack();

    @Nullable
    @Override
    public abstract ItemStack getPickResult();

    /**
     * Called after the compartment is placed into the world by {@link EmptyCompartmentEntity}
     */
    protected abstract void onPlaced();

    /**
     * Called whenever the compartment is hurt but not broken
     *
     * @param damageSource The source of the damage
     */
    protected abstract void onHurt(final DamageSource damageSource);

    /**
     * Called when the compartment is broken
     */
    protected abstract void onBreak();

    public void playSound(final SoundEvent soundEvent, final SoundSource soundSource, final float volume,
            final float pitch) {
        this.level().playSound(null, this.getX(), this.getY(), this.getZ(), soundEvent, soundSource, volume, pitch);
    }

    public static enum RidingPose {
        ULTRA_COMPACT,
        COMPACT,
        STANDING,
        STANDARD
    }
}