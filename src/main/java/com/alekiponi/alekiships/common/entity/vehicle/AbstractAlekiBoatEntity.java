package com.alekiponi.alekiships.common.entity.vehicle;

import java.util.ArrayList;
import com.alekiponi.alekiships.client.IngameOverlays;
import com.alekiponi.alekiships.common.entity.vehiclecapability.IAmTiny;
import com.alekiponi.alekiships.common.entity.vehiclecapability.IHaveAnchorWindlass;
import com.alekiponi.alekiships.common.entity.vehiclecapability.IHaveCleats;
import com.alekiponi.alekiships.common.entity.vehiclecapability.IPaintable;
import com.alekiponi.alekiships.network.AlekiShipsEntityDataSerializers;
import com.alekiponi.alekiships.util.BoatMaterial;
import com.alekiponi.alekiships.util.ClientHelper;
import com.alekiponi.alekiships.wind.Wind;
import com.alekiponi.alekiships.wind.WindModel;
import com.alekiponi.alekiships.wind.WindModels;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ServerboundPaddleBoatPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.Tags;

public abstract class AbstractAlekiBoatEntity extends AbstractVehicle {
    public static final int PADDLE_LEFT = 0;
    public static final int PADDLE_RIGHT = 1;
    public static final double PADDLE_SOUND_TIME = Math.PI / 4;
    protected static final EntityDataAccessor<Boolean> DATA_ID_PADDLE_LEFT = SynchedEntityData.defineId(
            AbstractAlekiBoatEntity.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Boolean> DATA_ID_PADDLE_RIGHT = SynchedEntityData.defineId(
            AbstractAlekiBoatEntity.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Wind> DATA_ID_WIND_VECTOR = SynchedEntityData.defineId(
            AbstractAlekiBoatEntity.class, AlekiShipsEntityDataSerializers.WIND.get());
    protected static final EntityDataAccessor<Boolean> DATA_ID_IMMOBILE = SynchedEntityData.defineId(
            AbstractAlekiBoatEntity.class, EntityDataSerializers.BOOLEAN);

    public static final int WIND_UPDATE_TICKS = 40;

    protected final float[] paddlePositions = new float[2];

    protected Wind oldWind = Wind.ZERO;

    protected int windLerpTicks = 0;

    protected WindModel windModel;

    protected final BoatMaterial boatMaterial;

    public AbstractAlekiBoatEntity(final EntityType<? extends AbstractAlekiBoatEntity> entityType, final Level level, BoatMaterial boatMaterial) {
        super(entityType, level);
        this.windModel = WindModels.get(level);
        this.boatMaterial = boatMaterial;
    }

    @Override
    protected void defineSynchedData(final SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_ID_PADDLE_LEFT, false);
        builder.define(DATA_ID_PADDLE_RIGHT, false);
        builder.define(DATA_ID_WIND_VECTOR, Wind.ZERO);
        builder.define(DATA_ID_IMMOBILE, false);
    }

    public float renderSizeForCompartments() {
        return 0.6875f;
    }

    @Override
    public void tick() {
        super.tick();
        this.oldStatus = this.status;
        this.status = this.getStatus();

        if (this.getHurtTime() > 0) {
            this.setHurtTime(this.getHurtTime() - 1);
        }

        if (!this.isFunctional()) {
            if (this.status == MediumStatus.IN_WATER) {
                this.setDeltaMovement(this.getDeltaMovement().add(0, -0.1, 0));
            }
            for (Entity entity : this.getPassengers()) {
                entity.unRide();
                entity.kill();
            }
            if (this.getDamage() > this.getDeathDamageThreshold()) {
                this.kill();
            }
        }

        if ((this.status == MediumStatus.UNDER_WATER) && this.isFunctional() && this.tickCount % 20 == 0) {
            this.hurt(this.damageSources().drown(), this.getDamageRecovery());

        }

        if (everyNthTickUnique(5)) {
            if (this.level().getBlockState(this.blockPosition()).is(Blocks.ICE)) {
                if (this.level().getBlockState(this.blockPosition().above()).is(Blocks.AIR)) {
                    this.setPos(this.getPosition(0).add(0, 1, 0));
                }
            }
        }


        this.tickEffects();

        this.tickLerp();

        this.tickWindInput();
        if (this instanceof IHaveCleats) {
            ((IHaveCleats) this).tickCleatInput();
        }
        if (this instanceof IHaveAnchorWindlass) {
            ((IHaveAnchorWindlass) this).tickAnchorInput();
        }

        this.tickFloatBoat();
        this.tickControlBoat();
        if (this.isControlledByLocalInstance()) {
            if (this.level().isClientSide()) {
                this.level().sendPacketToServer(new ServerboundPaddleBoatPacket(this.getPaddleState(0), this.getPaddleState(1)));
            }
        }
        if (this.everyNthTickUnique(4)) {
            Player player = this.level().getNearestPlayer(this, 9 * 16);
            if (player != null) {
                if (this.distanceTo(player) < 8 * 16) {
                    this.setImmobile(false);
                }
            } else {
                this.setImmobile(true);
            }
        }
        if (!this.getImmobile()) {
            this.move(MoverType.SELF, this.getDeltaMovement());
        }

        this.tickPaddlingEffects();

        // all movement code should happen before collision check-
        //this.checkInsideBlocks();

        this.tickUpdateWind(true);

        //This should ALWAYS be the only time the Y rotation is set during any given tick
        this.setYRot(this.getYRot() + this.getDeltaRotation());

        // all code that moves other entities should happen after collision check
        if (this.level().isClientSide()) {
            ClientHelper.tickHopPlayersOnboard(this);
        }

        this.tickTakeEntitiesForARide();

    }

    public float getDeathDamageThreshold() {
        return getDamageThreshold() * 1.25f;
    }

    protected void tickWindInput() {
        if (this.status == MediumStatus.IN_WATER || this.status == MediumStatus.IN_AIR) {
            double windFunction = Mth.clamp(this.getLocalWindAngleAndSpeed()[1], 0.001, 0.002 * this.getBoundingBox().getXsize()) * windDriftMultiplier();

            // TODO add a config for enabling / disabling wind drift

            float windDifference = Mth.degreesDifference(this.getLocalWindAngleAndSpeed()[0], Mth.wrapDegrees(this.getYRot()));


            if (Math.abs(windDifference) < 90) {
                float angleMultiplier = Math.abs((Math.abs(windDifference) - 90) / 90);
                this.setDeltaMovement(this.getDeltaMovement()
                        .add(Mth.sin(-this.getYRot() * ((float) Math.PI / 180F)) * windFunction * 0.45 * angleMultiplier, 0.0D,
                                Mth.cos(this.getYRot() * ((float) Math.PI / 180F)) * windFunction * 0.45 * angleMultiplier));
            }


            this.setDeltaMovement(this.getDeltaMovement()
                    .add(Mth.sin(-this.getLocalWindAngleAndSpeed()[0] * ((float) Math.PI / 180F)) * windFunction * 0.55, 0.0D,
                            Mth.cos(this.getLocalWindAngleAndSpeed()[0] * ((float) Math.PI / 180F)) * windFunction * 0.55));


            if (this.status == MediumStatus.IN_WATER) {
                if (windDifference > 1) {
                    this.setDeltaRotation(this.getDeltaRotation() - 0.1f);
                } else if (windDifference < -1) {
                    this.setDeltaRotation(this.getDeltaRotation() + 0.1f);
                }
            }


        }
    }

    /**
     * @return a double used to multiply the base wind drift speed
     */

    protected abstract double windDriftMultiplier();

    protected void tickUpdateWind(boolean waitForWindUpdateTick) {
        if (this.everyNthTickUnique(WIND_UPDATE_TICKS) || !waitForWindUpdateTick) {
            Wind wind = this.windModel.getWind(this.blockPosition());
            //windVector = new Vec2(0.05f,0.05f);
            if (wind.speed == 0) {
                wind = new Wind(-0.03F, 0F);
            }
            this.setWind(wind);
            updateLocalWindAngleAndSpeed();
        }
        if (this.windLerpTicks > 0 && this.level().isClientSide()) {
            updateLocalWindAngleAndSpeed();
        }

    }

    protected void tickFloatBoat() {

        double gravityAccel = this.isNoGravity() ? 0.0D : (double) -0.04F;

        double d2 = 0.0D;
        this.invFriction = 0.05F;
        if (this.oldStatus == MediumStatus.IN_AIR && this.status != MediumStatus.IN_AIR && this.status != MediumStatus.ON_LAND) {
            this.waterLevel = this.getY(1.0D);
            this.setPos(this.getX(), (double) (this.getWaterLevelAbove() - this.getBbHeight()) + 0.101D, this.getZ());
            this.setDeltaMovement(this.getDeltaMovement().multiply(1.0D, 0.0D, 1.0D));
            this.lastYd = 0.0D;
            this.status = MediumStatus.IN_WATER;
        } else {
            if (this.status == MediumStatus.IN_WATER || this.status == MediumStatus.UNDER_FLOWING_WATER) {
                d2 = ((this.waterLevel - this.getY()) / (double) this.getBbHeight()) + 0.1;
                this.invFriction = 0.9F;
            } else if (this.status == MediumStatus.UNDER_WATER) {
                d2 = 0.01F;
                this.invFriction = 0.45F;
            } else if (this.status == MediumStatus.IN_AIR) {
                this.invFriction = 0.9F;
            } else if (this.status == MediumStatus.ON_LAND) {
                this.invFriction = this.landFriction;
                if (invFriction > 0.5F) {
                    invFriction = 0.5F;
                }
                if (this.getControllingPassenger() instanceof Player) {
                    this.landFriction /= 2.0F;
                }
            }

            if (Math.abs(this.getDeltaRotation()) > 0) {
                float rotationalFriction = (Math.abs(this.getDeltaRotation()) / 48.0F);

                float modifiedFriction = this.invFriction - rotationalFriction;
                if (modifiedFriction > 2.0F) {
                    modifiedFriction = 2.0F;
                } else if (modifiedFriction < 0.0F) {
                    modifiedFriction = 0.0F;
                }
                this.invFriction = modifiedFriction;
            }

            this.tickTurnSpeedFactor();

            Vec3 vec3 = this.getDeltaMovement();

            this.setDeltaMovement(vec3.x * (double) this.invFriction, vec3.y + gravityAccel, vec3.z * (double) this.invFriction);

            if (d2 > 0.0D) {
                Vec3 movement = this.getDeltaMovement();
                this.setDeltaMovement(movement.x, (movement.y + d2 * 0.06153846016296973D) * 0.75D, movement.z);
            }

            if (status == MediumStatus.UNDER_WATER && this.level().getFluidState(this.blockPosition().above(3)).isEmpty() && this.isFunctional()) {
                this.setDeltaMovement(getDeltaMovement().x, 1 / 20.0, getDeltaMovement().z);
            }

        }

    }

    protected void tickTurnSpeedFactor() {
        if (this.getPilotCompartment() != null) {
            double turnSpeedFactor = this.getDeltaMovement().length() * 12.0F;

            if (this.getPilotCompartment().getInputLeft() || this.getPilotCompartment()
                    .getInputRight()) {
                this.setDeltaRotation(((this.invFriction / 3.0F)) * this.getDeltaRotation());
                this.setDeltaRotation((float) (turnSpeedFactor * this.getDeltaRotation()));

            } else {
                this.setDeltaRotation(this.getDeltaRotation() * (this.invFriction / 2.0F));
            }
        }
    }

    protected void tickControlBoat() {
        if (getPilotCompartment() != null) {
            boolean inputUp = this.getPilotCompartment().getInputUp();
            boolean inputDown = this.getPilotCompartment().getInputDown();
            boolean inputLeft = this.getPilotCompartment().getInputLeft();
            boolean inputRight = this.getPilotCompartment().getInputRight();
            float acceleration = 0;
            float paddleMultiplier = this.getPaddleMultiplier();

            float forward = getPaddleAcceleration()[0];
            float backward = getPaddleAcceleration()[1];
            float turning = getPaddleAcceleration()[2];

            if (inputLeft) {
                this.setDeltaRotation(this.getDeltaRotation() - this.getTurnSpeed());
            }

            if (inputRight) {
                this.setDeltaRotation(this.getDeltaRotation() + this.getTurnSpeed());
            }

            if (inputRight != inputLeft && !inputUp && !inputDown) {
                acceleration += turning * paddleMultiplier;
            }


            if (inputUp) {
                acceleration += forward * paddleMultiplier;
            }

            if (inputDown) {
                acceleration -= backward * paddleMultiplier;
            }

            if (Math.abs(acceleration) > Math.abs(this.getAcceleration())) {
                this.setAcceleration(acceleration);
            } else {
                if (Math.abs(this.getAcceleration()) < 1) {
                    this.setAcceleration(0);
                } else if (this.getAcceleration() > 0) {
                    this.setAcceleration(this.getAcceleration() - this.getMomentumSubtractor());
                } else if (this.getAcceleration() < 0) {
                    this.setAcceleration(this.getAcceleration() + this.getMomentumSubtractor());
                }
                acceleration = this.getAcceleration();
            }

            this.setDeltaMovement(this.getDeltaMovement()
                    .add(Mth.sin(-this.getYRot() * ((float) Math.PI / 180F)) * acceleration, 0.0D,
                            Mth.cos(this.getYRot() * ((float) Math.PI / 180F)) * acceleration));

            this.setPaddleState(
                    inputRight && !inputLeft || inputUp, inputLeft && !inputRight || inputUp);

        }
    }

    protected abstract float getPaddleMultiplier();

    protected float[] getPaddleAcceleration() {
        float forward = 0.0275F * 2;
        float backward = 0.0125F * 2;
        float turning = 0.0025F * 2;
        return new float[]{forward, backward, turning};
    }

    protected float getTurnSpeed() {
        return 1;
    }

    @Override
    public InteractionResult interact(final Player player, final InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.is(this.getDropItem())) {
            if (player.getAbilities().instabuild) {
                this.setDamage(0);
                return InteractionResult.SUCCESS;
            }
            if (this.getDamage() > 0.0F) {
                this.setDamage(this.getDamage() - getDamageRecovery());
                stack.split(1);
                player.swing(hand);
                this.level().playSound(null, this, SoundEvents.WOOD_PLACE, SoundSource.BLOCKS, 1.5F,
                        this.level().getRandom().nextFloat() * 0.1F + 0.9F);

                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    public boolean fireImmune() {
        return this.boatMaterial.withstandsLava();
    }

    @Override
    public ArrayList<IngameOverlays.IconState> getIconStates(Player player) {
        ArrayList<IngameOverlays.IconState> states = new ArrayList<>();

        if (this instanceof IAmTiny) {
            return states;
        }

        for (final ItemStack itemStack : player.getHandSlots()) {
            if (itemStack.isEmpty()) {
                return states;
            }

            if (itemStack.is(this.getDropItem()) && this.getDamage() > 0) {
                states.add(IngameOverlays.IconState.HAMMER);
                return states;
            }

            if ((itemStack.is(Tags.Items.DYES) || itemStack.is(Items.WATER_BUCKET)) && this instanceof IPaintable paintable) {
                if (paintable.getPaintColor().isEmpty() || paintable.getPaintColor().get() != ((DyeItem) itemStack.getItem()).getDyeColor()) {
                    states.add(IngameOverlays.IconState.BRUSH);
                    return states;
                }
            }
        }

        return states;
    }

    protected abstract float getMomentumSubtractor();

    protected void tickEffects() {
        if (this.status == MediumStatus.IN_WATER && !this.getPassengers().isEmpty()) {
            if (Math.abs(this.getDeltaRotation()) > 2) {
                this.level().addParticle(ParticleTypes.SPLASH, this.getX() + (double) this.random.nextFloat(),
                        this.getY() + 0.7D, this.getZ() + (double) this.random.nextFloat(), 0.0D, 0.0D, 0.0D);
                if (this.random.nextInt(20) == 0) {
                    this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), this.getSwimSound(),
                            this.getSoundSource(), 0.2F, 0.8F + 0.4F * this.random.nextFloat(), false);
                }
                if (this.getPilotCompartment() != null && Math.abs(
                        this.getDeltaRotation()) > 5 && (this.getPilotCompartment()
                        .getInputRight() || this.getPilotCompartment().getInputLeft())) {
                    this.level()
                            .playLocalSound(this.getX(), this.getY(), this.getZ(), this.getSwimHighSpeedSplashSound(),
                                    this.getSoundSource(), 0.2F, 0.8F + 0.4F * this.random.nextFloat(), false);


                    Vec3 splashOffset = this.getDeltaMovement().yRot(45);
                    if (this.getPilotCompartment().getInputLeft()) {
                        splashOffset = this.getDeltaMovement().yRot(-45);
                    }
                    splashOffset.normalize();

                    for (int i = 0; i < 8; i++) {
                        this.level().addParticle(ParticleTypes.BUBBLE_POP,
                                this.getX() + (double) this.random.nextFloat() + splashOffset.x * 2 + this.getDeltaMovement().x * i,
                                this.getY() + 0.7D,
                                this.getZ() + (double) this.random.nextFloat() + splashOffset.z * 2 + this.getDeltaMovement().x * i,
                                0.0D, 0.0D, 0.0D);
                        this.level().addParticle(ParticleTypes.SPLASH,
                                this.getX() + (double) this.random.nextFloat() + splashOffset.x * 2 + this.getDeltaMovement().x * i,
                                this.getY() + 0.7D,
                                this.getZ() + (double) this.random.nextFloat() + splashOffset.z * 2 + this.getDeltaMovement().x * i,
                                0.0D, 0.0D, 0.0D);
                    }
                }

            } else if (this.getDeltaMovement().length() > 0.10) {
                if (this.random.nextInt(8) == 0) {
                    this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), this.getSwimSound(),
                            this.getSoundSource(), 0.1F, 0.8F + 0.4F * this.random.nextFloat(), false);
                    this.level().addParticle(ParticleTypes.SPLASH, this.getX() + (double) this.random.nextFloat(),
                            this.getY() + 0.7D, this.getZ() + (double) this.random.nextFloat(), 0.0D, 0.0D, 0.0D);
                }
            }
        }
    }

    protected void tickPaddlingEffects() {
        for (int i = 0; i <= 1; ++i) {
            if (this.getPaddleState(i)) {
                if (!this.isSilent() && (double) (this.paddlePositions[i] % ((float) Math.PI * 2F)) <= (double) ((float) Math.PI / 4F) && (double) ((this.paddlePositions[i] + ((float) Math.PI / 8F)) % ((float) Math.PI * 2F)) >= (double) ((float) Math.PI / 4F)) {
                    SoundEvent soundevent = this.getPaddleSound();
                    if (soundevent != null) {
                        Vec3 vec3 = this.getViewVector(1.0F);
                        double d0 = i == 1 ? -vec3.z : vec3.z;
                        double d1 = i == 1 ? vec3.x : -vec3.x;
                        this.level().playSound(null, this.getX() + d0, this.getY(), this.getZ() + d1, soundevent,
                                this.getSoundSource(), 1.0F, 0.8F + 0.4F * this.random.nextFloat());
                        this.level().gameEvent(this.getControllingPassenger(), GameEvent.SPLASH,
                                new BlockPos((int) (this.getX() + d0), (int) this.getY(), (int) (this.getZ() + d1)));
                    }
                }

                this.paddlePositions[i] += ((float) Math.PI / 8F);
            } else {
                this.paddlePositions[i] = 0.0F;
            }
        }
    }

    public void updateLocalWindAngleAndSpeed() {
        final double newDirection = this.getWind().angle;

        if (!this.level().isClientSide()) return;

        if (this.windLerpTicks > 0) {
            final float lerpStep = ((WIND_UPDATE_TICKS) - this.windLerpTicks) / ((float) WIND_UPDATE_TICKS);
            final double lerpedRot = Math.round(Mth.rotLerp(lerpStep, this.oldWind.angle, (float) newDirection));

            this.setWind(new Wind(this.oldWind.speed, Mth.wrapDegrees((float) Math.round(lerpedRot))));

            this.windLerpTicks--;
            return;
        }

        if (newDirection != this.getWind().angle) {
            this.oldWind = this.getWind();
            this.windLerpTicks = WIND_UPDATE_TICKS;
        }
    }

    /**
     * This allocates a new array for each call simply to bundle wind angle and speed together in a single return value.
     * Our {@link Wind} object has both of these values ({@link #getWind()}) or the alternative
     * {@link #getLocalWindAngle()} and {@link #getLocalWindSpeed()} can be used instead.
     * No matter what you call you'll get the same values
     */
    @Deprecated(forRemoval = true)
    public float[] getLocalWindAngleAndSpeed() {
        return new float[]{this.getLocalWindAngle(), this.getLocalWindSpeed()};
    }

    public float getLocalWindAngle() {
        return this.getWind().angle;
    }

    public float getLocalWindSpeed() {
        return Mth.clamp(this.getWind().speed, 0, 0.2F);
    }

    @Nullable
    protected SoundEvent getPaddleSound() {
        switch (this.getStatus()) {
            case IN_WATER:
            case UNDER_WATER:
            case UNDER_FLOWING_WATER:
                return SoundEvents.BOAT_PADDLE_WATER;
            case ON_LAND:
                return SoundEvents.BOAT_PADDLE_LAND;
            case IN_AIR:
            default:
                return null;
        }
    }


    public void setPaddleState(boolean pLeft, boolean pRight) {
        this.entityData.set(DATA_ID_PADDLE_LEFT, pLeft);
        this.entityData.set(DATA_ID_PADDLE_RIGHT, pRight);
    }

    public float getRowingTime(int pSide, float pLimbSwing) {
        return this.getPaddleState(pSide) ? Mth.clampedLerp(this.paddlePositions[pSide] - ((float) Math.PI / 8F),
                this.paddlePositions[pSide], pLimbSwing) : 0.0F;
    }

    public boolean getPaddleState(final int side) {
        return this.entityData.<Boolean>get(
                side == 0 ? DATA_ID_PADDLE_LEFT : DATA_ID_PADDLE_RIGHT) && this.getControllingPassenger() != null;
    }


    public final void setWind(final Wind wind) {
        this.entityData.set(DATA_ID_WIND_VECTOR, wind);
    }

    public final Wind getWind() {
        return this.entityData.get(DATA_ID_WIND_VECTOR);
    }

    public void setImmobile(boolean immobile) {
        this.entityData.set(DATA_ID_IMMOBILE, immobile);
    }

    public boolean getImmobile() {
        return this.entityData.get(DATA_ID_IMMOBILE);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.tickUpdateWind(false);
        this.setImmobile(pCompound.getBoolean("immobile"));
    }


    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putBoolean("immobile", this.getImmobile());

    }

    public float getWindLocalRotation() {
        return Mth.wrapDegrees(getLocalWindAngleAndSpeed()[0] - Mth.wrapDegrees(this.getYRot()));
    }

    @Nullable
    @Override
    public Entity changeDimension(final DimensionTransition transition)
    {
        final Entity entity = super.changeDimension(transition);



        if (entity instanceof AbstractAlekiBoatEntity alekiBoat) {
            // Update our wind model when the dimension changes
            alekiBoat.windModel = WindModels.get(transition);
        }
        return entity;
    }
}