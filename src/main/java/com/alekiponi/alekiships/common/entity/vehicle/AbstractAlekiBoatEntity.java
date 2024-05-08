package com.alekiponi.alekiships.common.entity.vehicle;

import com.alekiponi.alekiships.client.IngameOverlays;
import com.alekiponi.alekiships.common.entity.AlekiShipsEntities;
import com.alekiponi.alekiships.common.entity.EntityOBBDimensions;
import com.alekiponi.alekiships.common.entity.vehiclecapability.*;
import com.alekiponi.alekiships.common.entity.vehiclehelper.*;
import com.alekiponi.alekiships.util.AlekiShipsHelper;
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
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.ArrayList;

public abstract class AbstractAlekiBoatEntity extends AbstractVehicle {
    public static final int PADDLE_LEFT = 0;
    public static final int PADDLE_RIGHT = 1;
    public static final double PADDLE_SOUND_TIME = Math.PI / 4;
    protected static final EntityDataAccessor<Boolean> DATA_ID_PADDLE_LEFT = SynchedEntityData.defineId(
            AbstractAlekiBoatEntity.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Boolean> DATA_ID_PADDLE_RIGHT = SynchedEntityData.defineId(
            AbstractAlekiBoatEntity.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Vector3f> DATA_ID_WIND_VECTOR = SynchedEntityData.defineId(
            AbstractAlekiBoatEntity.class, EntityDataSerializers.VECTOR3);
    protected static final EntityDataAccessor<Float> DATA_ID_WIND_ANGLE = SynchedEntityData.defineId(
            AbstractAlekiBoatEntity.class, EntityDataSerializers.FLOAT);
    protected static final EntityDataAccessor<Float> DATA_ID_WIND_SPEED = SynchedEntityData.defineId(
            AbstractAlekiBoatEntity.class, EntityDataSerializers.FLOAT);
    protected static final EntityDataAccessor<Boolean> DATA_ID_IMMOBILE = SynchedEntityData.defineId(
            AbstractAlekiBoatEntity.class, EntityDataSerializers.BOOLEAN);

    public final int WIND_UPDATE_TICKS = 40;

    protected final float[] paddlePositions = new float[2];

    protected double windAngle;

    protected double windSpeed;
    protected double oldWindAngle;

    protected double oldWindSpeed;

    protected int windLerpTicks = 0;

    public AbstractAlekiBoatEntity(final EntityType<? extends AbstractAlekiBoatEntity> entityType, final Level level, EntityOBBDimensions dimensions) {
        super(entityType, level, dimensions);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_ID_PADDLE_LEFT, false);
        this.entityData.define(DATA_ID_PADDLE_RIGHT, false);

        this.entityData.define(DATA_ID_WIND_VECTOR, new Vector3f(0, 0, 0));
        this.entityData.define(DATA_ID_WIND_ANGLE, 0f);
        this.entityData.define(DATA_ID_WIND_SPEED, 0f);
        this.entityData.define(DATA_ID_IMMOBILE, false);
    }

    public float renderSizeForCompartments(){
        return 0.6875f;
    }

    @Override
    public void tick() {

        if (this.getPassengers().size() < this.getMaxPassengers()) {
            final VehiclePart newPart = AlekiShipsEntities.VEHICLE_PART.get().create(this.level());
            newPart.setPos(this.getX(), this.getY(), this.getZ());
            this.level().addFreshEntity(newPart);
            newPart.startRiding(this);
        }


        this.oldStatus = this.status;
        this.status = this.getStatus();

        if (this.getHurtTime() > 0) {
            this.setHurtTime(this.getHurtTime() - 1);
        }

        if (this.getDamage() > this.getDamageThreshold()) {
            if (this.status == MediumStatus.IN_WATER) {
                this.setDeltaMovement(this.getDeltaMovement().add(0, -0.1, 0));
            }
            for (Entity entity : this.getPassengers()) {
                entity.kill();
            }
            if (this.getDamage() > this.getDamageThreshold() * 2) {
                this.kill();
            }
        }

        if ((this.status == MediumStatus.UNDER_FLOWING_WATER || this.status == MediumStatus.UNDER_WATER) && this.getDamage() <= this.getDamageThreshold() && this.tickCount % 10 == 0) {
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

        super.tick();
        this.tickLerp();

        this.tickWindInput();
        if(this instanceof IHaveCleats){
            ((IHaveCleats)this).tickCleatInput();
        }
        if(this instanceof IHaveAnchorWindlass){
            ((IHaveAnchorWindlass)this).tickAnchorInput();
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

        // all movement code should happen before collision check
        //this.checkInsideBlocks();

        this.tickUpdateWind(true);

        //This should ALWAYS be the only time the Y rotation is set during any given tick
        this.setYRot(this.getYRot() + this.getDeltaRotation());

        // all code that moves other entities should happen after collision check
        AlekiShipsHelper.tickHopPlayersOnboard(this);

        this.tickTakeEntitiesForARide();

    }

    protected void tickWindInput() {
        if (this.status == MediumStatus.IN_WATER || this.status == MediumStatus.IN_AIR) {
            double windFunction = Mth.clamp(this.getLocalWindAngleAndSpeed()[1], 0.001, 0.002 * this.getBoundingBox().getXsize());

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

    protected void tickUpdateWind(boolean waitForWindUpdateTick) {
        if (this.everyNthTickUnique(WIND_UPDATE_TICKS) || !waitForWindUpdateTick) {
            Vec2 windVector = this.getWindVectorAt(this.level(), this.blockPosition());
            //windVector = new Vec2(0.05f,0.05f);
            if (windVector.length() == 0) {
                windVector = new Vec2(-0.03f, 0f);
            }
            /*
            float subtractWeatherMultiplier = -(0.4F * this.level().getRainLevel(0.0F) + 0.3F * this.level().getThunderLevel(0.0F));
            windVector = new Vec2(windVector.x*subtractWeatherMultiplier,windVector.y*subtractWeatherMultiplier);*/

            this.setWindVector(windVector);
            updateLocalWindAngleAndSpeed();
        }
        if (this.windLerpTicks > 0 && this.level().isClientSide()) {
            updateLocalWindAngleAndSpeed();
        }

    }

    /**
     * Gets the wind vector for the given level at the block position. This is a simple ideally temporary way of
     * handling different wind models like the one found in TFC
     *
     * @param level The level
     * @param blockPos The block pos at which the wind is being queried
     * @return A Vec2 containing the winds x (x) and z (y) components.
     */
    protected Vec2 getWindVectorAt(@SuppressWarnings("unused") final Level level, @SuppressWarnings("unused") final BlockPos blockPos) {
        return new Vec2(0.25F, 0.25F);
    }

    protected void tickFloatBoat() {

        double gravAccel = -0.04F;
        double d1 = this.isNoGravity() ? 0.0D : (double) gravAccel;

        double d2 = 0.0D;
        this.invFriction = 0.05F;
        if (this.oldStatus == MediumStatus.IN_AIR && this.status != MediumStatus.IN_AIR && this.status != MediumStatus.ON_LAND) {
            this.waterLevel = this.getY(1.0D);
            this.setPos(this.getX(), (double) (this.getWaterLevelAbove() - this.getBbHeight()) + 0.101D, this.getZ());
            this.setDeltaMovement(this.getDeltaMovement().multiply(1.0D, 0.0D, 1.0D));
            this.lastYd = 0.0D;
            this.status = MediumStatus.IN_WATER;
        } else {

            if (this.status == MediumStatus.IN_WATER) {
                d2 = ((this.waterLevel - this.getY()) / (double) this.getBbHeight()) + 0.1;
                this.invFriction = 0.9F;
            } else if (this.status == MediumStatus.UNDER_FLOWING_WATER) {
                d1 = -7.0E-4D;
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

            this.setDeltaMovement(vec3.x * (double) this.invFriction, vec3.y + d1, vec3.z * (double) this.invFriction);


            if (d2 > 0.0D) {
                Vec3 vec31 = this.getDeltaMovement();
                this.setDeltaMovement(vec31.x, (vec31.y + d2 * 0.06153846016296973D) * 0.75D, vec31.z);
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
        float forward = 0.0275F*2;
        float backward = 0.0125F*2;
        float turning = 0.0025F*2;
        return new float[]{forward, backward, turning};
    }

    protected float getTurnSpeed() {
        return 1;
    }

    @Override
    public InteractionResult interact(final Player player, final InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.is(this.getDropItem())) {
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

    @Override
    public ArrayList<IngameOverlays.IconState> getIconStates(Player player) {
        ArrayList<IngameOverlays.IconState> states = new ArrayList<>();
        ItemStack handItem = player.getItemInHand(player.getUsedItemHand());

        if(this instanceof IAmTiny){
            return states;
        }

        for (final ItemStack itemStack : player.getHandSlots()) {
            if (itemStack.is(this.getDropItem())) {
                states.add(IngameOverlays.IconState.HAMMER);
                return states;
            }

            if (itemStack.is(Tags.Items.DYES) || itemStack.is(Items.WATER_BUCKET)) {
                states.add(IngameOverlays.IconState.BRUSH);
                return states;
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

        double newDirection = AlekiShipsHelper.vec2ToWrappedDegrees(this.getWindVector());
        double newSpeed = Math.abs(this.getWindVector().length());

        if (this.level().isClientSide()) {
            if (this.windLerpTicks > 0) {
                float lerpStep = ((WIND_UPDATE_TICKS) - this.windLerpTicks) / ((float) WIND_UPDATE_TICKS);
                double lerpedRot = Math.round(Mth.rotLerp(lerpStep, (float) this.oldWindAngle, (float) newDirection));

                this.windLerpTicks--;

                this.windAngle = Mth.wrapDegrees((float) Math.round(lerpedRot));

                this.windSpeed = this.oldWindSpeed;
                return;
            }

            if (newDirection != this.windAngle) {
                this.oldWindAngle = this.windAngle;
                this.oldWindSpeed = this.windSpeed;
                this.windLerpTicks = WIND_UPDATE_TICKS;
                return;
            }
        }

        this.windAngle = Mth.wrapDegrees((float) Math.round(newDirection));
        this.windSpeed = newSpeed;
    }

    public float[] getLocalWindAngleAndSpeed() {
        return new float[]{(float) this.windAngle, (float) Mth.clamp(this.windSpeed, 0, 0.2f)};
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


    public void setWindVector(final Vec2 windVector) {
        this.entityData.set(DATA_ID_WIND_VECTOR, new Vector3f(windVector.x, 0, windVector.y));
    }

    public Vec2 getWindVector() {
        float x = this.entityData.get(DATA_ID_WIND_VECTOR).x;
        float y = this.entityData.get(DATA_ID_WIND_VECTOR).z;
        return new Vec2(x, y);
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

}