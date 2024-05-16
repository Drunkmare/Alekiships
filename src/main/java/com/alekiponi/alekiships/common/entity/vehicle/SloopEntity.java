package com.alekiponi.alekiships.common.entity.vehicle;

import com.alekiponi.alekiships.client.IngameOverlays;
import com.alekiponi.alekiships.common.entity.vehiclecapability.*;
import com.alekiponi.alekiships.common.entity.vehiclehelper.SailSwitchEntity;
import com.alekiponi.alekiships.common.entity.vehiclehelper.VehiclePart;
import com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.EmptyCompartmentEntity;
import com.alekiponi.alekiships.network.CustomEntityDataSerializers;
import com.alekiponi.alekiships.network.PacketHandler;
import com.alekiponi.alekiships.network.ServerBoundSloopPacket;
import com.alekiponi.alekiships.util.AlekiShipsTags;
import com.alekiponi.alekiships.util.BoatMaterial;
import com.alekiponi.alekiships.util.CommonHelper;
import com.alekiponi.alekiships.util.advancements.AlekiShipsAdvancements;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Optional;

public class SloopEntity extends AbstractAlekiBoatEntity implements IBreakIce, IPaintable, IHaveAnchorWindlass, IHaveSailSwitches, IHaveMasts, ICannonable, IHaveBlockOnlyCompartments, IDestroyPlants, IHaveMultipleCleats {

    public final int PASSENGER_NUMBER = 25;
    public final int[] CLEATS = {18, 19, 20, 21};
    public final int[] COLLIDERS = {14, 15, 16};
    public final int[] SAIL_SWITCHES = {17, 24};
    public final int[] WINDLASSES = {22};
    public final int[] MASTS = {23};
    public final int[] CAN_ADD_CANNONS = {7, 8, 9, 10, 11, 12};
    public final int[] CAN_ADD_ONLY_BLOCKS = {1, 2, 3, 4, 5, 6};
    public final int[] COMPARTMENTS = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13};

    protected static final EntityDataAccessor<Float> DATA_ID_MAIN_BOOM_ROTATION = SynchedEntityData.defineId(
            SloopEntity.class, EntityDataSerializers.FLOAT);

    protected static final EntityDataAccessor<Float> DATA_ID_MAINSHEET_LENGTH = SynchedEntityData.defineId(
            SloopEntity.class, EntityDataSerializers.FLOAT);

    protected static final EntityDataAccessor<Float> DATA_ID_RUDDER_ROTATION = SynchedEntityData.defineId(
            SloopEntity.class, EntityDataSerializers.FLOAT);

    protected static final EntityDataAccessor<Boolean> DATA_ID_MAINSAIL_ACTIVE = SynchedEntityData.defineId(
            SloopEntity.class, EntityDataSerializers.BOOLEAN);

    protected static final EntityDataAccessor<Boolean> DATA_ID_ICEBREAKER = SynchedEntityData.defineId(
            SloopEntity.class, EntityDataSerializers.BOOLEAN);

    protected static final EntityDataAccessor<Boolean> DATA_ID_JIBSAIL_ACTIVE = SynchedEntityData.defineId(
            SloopEntity.class, EntityDataSerializers.BOOLEAN);

    protected static final EntityDataAccessor<Integer> DATA_ID_TICKS_NO_RIDERS = SynchedEntityData.defineId(
            SloopEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<DyeColor> DATA_ID_MAINSAIL_DYE = SynchedEntityData.defineId(SloopEntity.class,
            CustomEntityDataSerializers.DYE_COLOR);
    private static final EntityDataAccessor<DyeColor> DATA_ID_JIBSAIL_DYE = SynchedEntityData.defineId(SloopEntity.class,
            CustomEntityDataSerializers.DYE_COLOR);
    private static final EntityDataAccessor<Optional<DyeColor>> DATA_ID_PAINT_COLOR = SynchedEntityData.defineId(SloopEntity.class,
            CustomEntityDataSerializers.OPTIONAL_DYE_COLOR);

    public final int[][] COMPARTMENT_ROTATIONS = {{7, 85}, {8, 85}, {9, 85}, {10, -85}, {11, -85}, {12, -85}};


    protected final float PASSENGER_SIZE_LIMIT = 1.4F;

    protected final int SAIL_TOGGLE_TICKS = 20;
    protected final float DAMAGE_THRESHOLD = 512.0f;
    protected final float DAMAGE_RECOVERY = 5.333f;

    public SloopEntity(final EntityType<? extends SloopEntity> entityType, final Level level,
                       final BoatMaterial boatMaterial) {
        super(entityType, level, boatMaterial);
    }

    @Override
    public float getPassengerSizeLimit() {
        return PASSENGER_SIZE_LIMIT;
    }

    @Override
    public int getMaxPassengers() {
        return this.PASSENGER_NUMBER;
    }


    @Override
    public int[] getCleatIndices() {
        return this.CLEATS;
    }

    @Override
    public int[] getSailSwitchIndices() {
        return SAIL_SWITCHES;
    }

    @Override
    public int[] getMastIndices() {
        return MASTS;
    }

    @Override
    public int[] getCompartmentIndices() {
        return COMPARTMENTS;
    }

    @Override
    public int[] getCanAddCannonsIndices() {
        return CAN_ADD_CANNONS;
    }

    @Override
    public int broadsideCount() {
        return 3;
    }

    @Override
    public int[] getColliderIndices() {
        return COLLIDERS;
    }

    @Override
    public int[] getCanAddOnlyBlocksIndices() {
        return CAN_ADD_ONLY_BLOCKS;
    }

    @Override
    public int getCompartmentRotation(int i) {
        return COMPARTMENT_ROTATIONS[i][0];
    }

    @Override
    public int[][] getCompartmentRotationsArray() {
        return COMPARTMENT_ROTATIONS;
    }

    public float[] getDefaultColliderDimensions() {
        return new float[]{1.5f, 0.75f};
    }

    @Override
    protected Vec3 positionRiderByIndex(int index) {
        float localX = 0.0F;
        float localZ = 0.0F;
        float localY = (float) ((this.isRemoved() ? (double) 0.01F : this.getPassengersRidingOffset()));
        float holdLevel = -0.05f;
        float deckLevel = 0.625f;
        switch (index) {
            case 0 -> {
                // aft pilot / tiller seat
                localZ = 0.6f;
                localX = -2.3f;
                localY += deckLevel;
            }
            case 1 -> {
                // hold aft port
                localZ = -0.4075f;
                localX = 1.2555f - (30.0f / 16.0f);
                localY += holdLevel;
            }
            case 2 -> {
                // hold aft starboard
                localZ = 0.4075f;
                localX = 1.2555f - (30.0f / 16.0f);
                localY += holdLevel;
            }
            case 3 -> {
                // hold mid port
                localZ = -0.4075f;
                localX = 1.2555f - (15f / 16.0f);
                localY += holdLevel;
            }
            case 4 -> {
                //hold mid starboard
                localZ = 0.4075f;
                localX = 1.2555f - (15f / 16.0f);
                localY += holdLevel;
            }
            case 5 -> {
                //hold fore port
                localZ = -0.4075f;
                localX = 1.2555f - (0.0f / 16.0f);
                localY += holdLevel;
            }
            case 6 -> {
                //hold fore starboard
                localZ = 0.4075f;
                localX = 1.2555f - (0.0f / 16.0f);
                localY += holdLevel;
            }
            case 7 -> {
                //port side fore
                localX = 0.3f;
                localZ = -1.4f;
                localY += deckLevel;
            }
            case 8 -> {
                //port side mid
                localX = -0.5f;
                localZ = -1.33f;
                localY += deckLevel;
            }
            case 9 -> {
                //port side aft
                localX = -1.3f;
                localZ = -1.26f;
                localY += deckLevel;

            }
            case 10 -> {
                //starboard side fore
                localX = 0.3f;
                localZ = 1.4f;
                localY += deckLevel;
            }
            case 11 -> {
                //starboard side mid
                localX = -0.5f;
                localZ = 1.33f;
                localY += deckLevel;
            }
            case 12 -> {
                //starboard side aft
                localX = -1.3f;
                localZ = 1.26f;
                localY += deckLevel;
            }
            case 13 -> {
                //sailing station
                localZ = -0.6f;
                localX = -2.3f;
                localY += deckLevel;
            }
            case 14 -> {
                //collider 1
                localZ = 0.6f;
                localX = -2.0f;
                localY += -0.00f;
            }
            case 15 -> {
                //collider 2
                localZ = -0.6f;
                localX = -2.0f;
                localY += -0.00f;
            }
            case 16 -> {
                //collider 3
                localZ = 0f;
                localX = 2.0f;
                localY += -0.00f;
            }
            case 17 -> {
                //mainsail switch
                localZ = 0f;
                localX = 1.8f;
                localY += 2.625f;
            }
            case 18 -> {
                //cleat port fore
                localZ = -1.75f;
                localX = 1.33f;
                localY += 1.124f;
            }
            case 19 -> {
                //cleat starboard fore
                localZ = 1.75f;
                localX = 1.33f;
                localY += 1.124f;
            }
            case 20 -> {
                //cleat port aft
                localZ = -1.75f;
                localX = -2.405f;
                localY += 1.1875f;
            }
            case 21 -> {
                //cleat starboard aft
                localZ = 1.75f;
                localX = -2.405f;
                localY += 1.1875f;
            }
            case 22 -> {
                //windlass
                localZ = -1.34f;
                localX = 2.52f;
                localY += 1.0f;
            }
            case 23 -> {
                //mast
                localZ = 0f;
                localX = 2.1f;
                localY += 2.9f;
            }
            case 24 -> {
                // jibsail switch
                localZ = 0f;
                localX = 3.1f;
                localY += 1.0f;
            }
        }
        return new Vec3(localX, localY, localZ);
    }

    @Override
    public float getDamageThreshold() {
        return DAMAGE_THRESHOLD;
    }

    @Override
    public float getDamageRecovery() {
        return DAMAGE_RECOVERY;
    }

    @Override
    public void tick() {

        if (this.status == MediumStatus.IN_WATER || this.status == MediumStatus.IN_AIR) {
            if (this.status == MediumStatus.IN_WATER) {
                this.setDeltaRotation((float) (-1 * this.getRudderRotation() * 0.25f *
                        (Mth.clamp(this.getDeltaMovement().length(), 0.05f, 1))));
                this.setDeltaRotation(Mth.clamp(this.getDeltaRotation(), -1f, 1f));
            }

            if (this.getMainsailActive() || this.getJibsailActive()) {
                float rotationImpact = 0;

                float windDifference = Mth.degreesDifference(getMainsailWindAngleAndForce()[0], Mth.wrapDegrees(this.getYRot()));

                if (windDifference > 4) {
                    rotationImpact = 1f;
                } else if (windDifference < -4) {
                    rotationImpact = -1f;
                }

                rotationImpact = Mth.clamp((float) (rotationImpact * this.getDeltaMovement().length()), 0, 0.5f);

                this.setDeltaRotation(this.getDeltaRotation() + rotationImpact);

                float boomWindDifference = Mth.degreesDifference(this.getLocalWindAngleAndSpeed()[0], Mth.wrapDegrees(this.getSailWorldRotation()));

                float sheet = this.getMainsheetLength();
                float boom = this.getMainBoomRotation();


                if (boomWindDifference < -171) {
                    boomWindDifference = Mth.wrapDegrees(boomWindDifference - 180);
                }
                if (boomWindDifference > 5) {
                    boom += 2f;
                }
                if (boomWindDifference < -5) {
                    boom -= 2f;
                }

                if (sheet > Math.abs(boom)) {
                    if (boom < 2) {
                        boom--;
                    } else if (boom > 2) {
                        boom++;
                    }
                }

                this.setMainBoomRotation(boom);
            }

        }


        if (this.everyNthTickUnique(2)) {
            this.tickBreakIce();
            this.tickDestroyPlants();

            int ind = 0;
            for (SailSwitchEntity switchEntity : this.getSailSwitches()) {
                if (ind == 0) {
                    this.setMainsailActive(switchEntity.getSwitched() && !this.isUnderWater());
                }
                if (ind == 1) {
                    this.setJibsailActive(switchEntity.getSwitched() && !this.isUnderWater());
                }
                ind++;
            }
            if (this.collectPlayersToTakeWith().isEmpty() && this.collectPlayerPassengers().isEmpty()) {
                this.setTicksNoRiders(this.getTicksNoRiders() + 2);
                if (this.getTicksNoRiders() >= SAIL_TOGGLE_TICKS) {
                    for (SailSwitchEntity switchEntity : this.getSailSwitches()) {
                        switchEntity.setSwitched(false);
                    }
                    this.setJibsailActive(false);
                    this.setMainsailActive(false);
                }
            } else {
                this.setTicksNoRiders(0);
            }
            if (!this.getMainsailActive() && !this.getJibsailActive()) {
                this.setMainsheetLength(0);
            }
        }

        if (this.everyNthTickUnique(10)) {
            checkIfRecentlyFiredBroadside();
        }

        super.tick();

    }

    @Override
    public void tickCleatInput() {
        if (this.getJibsailActive() || this.getMainsailActive()) {
            return;
        }
        IHaveMultipleCleats.super.tickCleatInput();
    }

    @Override
    public InteractionResult interact(final Player player, final InteractionHand hand) {
        InteractionResult result = this.interactPaint(player, hand);
        final ItemStack heldItem = player.getItemInHand(hand);

        if (heldItem.is(AlekiShipsTags.Items.ICEBREAKER_UPGRADES) && !this.breaksIce()) {
            this.setIceBreaker(true);
            if (!player.getAbilities().instabuild) {
                heldItem.shrink(1);
            }
            this.level().playSound(null, this, SoundEvents.METAL_PLACE, SoundSource.BLOCKS, 1.5F,
                    this.level().getRandom().nextFloat() * 0.1F + 0.9F);
            return InteractionResult.SUCCESS;

        }

        if (heldItem.is(Items.NAME_TAG)) {
            if (heldItem.hasCustomHoverName() && !this.getName().equals(heldItem.getHoverName())) {
                if (!this.level().isClientSide() && this.isAlive() && this.isFunctional()) {
                    this.setCustomName(heldItem.getHoverName());
                    if (!player.getAbilities().instabuild){
                        heldItem.shrink(1);
                    }
                    if (this.getName().getString().equals("The Black Pearl") && player instanceof ServerPlayer serverPlayer){
                        if(this.getPaintColor().isPresent()){
                            if(this.getPaintColor().get().equals(DyeColor.BLACK) && this.getJibsailDye().equals(DyeColor.BLACK) && this.getMainsailDye().equals(DyeColor.BLACK)){
                                AlekiShipsAdvancements.DYE_SHIP_BLACK.trigger(serverPlayer);
                            }
                        }
                    }
                }

                return InteractionResult.sidedSuccess(player.level().isClientSide);
            } else {
                return InteractionResult.PASS;
            }
        }

        return result == null ? super.interact(player, hand) : result;
    }

    @Override
    public float getCleatMovementMultiplier() {
        return 5;
    }

    @Override
    protected void tickTurnSpeedFactor() {
        // TODO make this cleaner in the inheritance structure
        // shouldn't be used for larger boats...
    }


    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_ID_MAIN_BOOM_ROTATION, 0f);
        this.entityData.define(DATA_ID_RUDDER_ROTATION, 0f);
        this.entityData.define(DATA_ID_MAINSAIL_ACTIVE, false);
        this.entityData.define(DATA_ID_JIBSAIL_ACTIVE, false);
        this.entityData.define(DATA_ID_TICKS_NO_RIDERS, 0);
        this.entityData.define(DATA_ID_MAINSHEET_LENGTH, 0f);
        this.entityData.define(DATA_ID_JIBSAIL_DYE, DyeColor.WHITE);
        this.entityData.define(DATA_ID_MAINSAIL_DYE, DyeColor.WHITE);
        this.entityData.define(DATA_ID_PAINT_COLOR, Optional.empty());
        this.entityData.define(DATA_ID_ICEBREAKER, false);
    }

    @Override
    public int[] getWindlassIndices() {
        return WINDLASSES;
    }

    public float getSailWorldRotation() {
        return Mth.wrapDegrees(getMainBoomRotation() + Mth.wrapDegrees(this.getYRot()));
    }


    @Nullable
    public net.minecraft.world.entity.Entity getSailingVehiclePartAsEntity() {
        if (this.isVehicle() && this.getPassengers().size() == this.getMaxPassengers()) {
            return this.getPassengers().get(13);
        }
        return null;
    }

    @Override
    public Item getDropItem() {
        return this.boatMaterial.getDeckItem();
    }

    @Nullable
    public EmptyCompartmentEntity getSailingCompartment() {
        final net.minecraft.world.entity.Entity vehiclePart = this.getSailingVehiclePartAsEntity();

        if (!(vehiclePart instanceof VehiclePart) || !vehiclePart.isVehicle()) {
            return null;
        }

        if (!(vehiclePart.getFirstPassenger() instanceof EmptyCompartmentEntity emptyCompartmentEntity)) {
            return null;
        }
        return emptyCompartmentEntity;
    }

    @Override
    protected void tickPaddlingEffects() {
    }

    @Override
    protected void tickControlBoat() {
        if (getPilotCompartment() != null) {
            boolean inputUp = this.getPilotCompartment().getInputUp();
            boolean inputDown = this.getPilotCompartment().getInputDown();
            boolean inputLeft = this.getPilotCompartment().getInputLeft();
            boolean inputRight = this.getPilotCompartment().getInputRight();

            float rudder = this.getRudderRotation();
            if (inputLeft) {
                if (rudder < 45) {
                    if (rudder < 0) {
                        rudder += 2;
                    } else {
                        rudder += 1;
                    }
                }
            }

            if (inputRight) {
                if (rudder > -45) {
                    if (rudder > 0) {
                        rudder -= 2;
                    } else {
                        rudder -= 1;
                    }

                }
            }

            if (!inputRight && !inputLeft) {
                if (rudder > 0) {
                    rudder -= 0.3f;
                }
                if (rudder < 0) {
                    rudder += 0.3f;
                }
                if (Math.abs(rudder) < 1) {
                    rudder = 0;
                }
            }
            this.setRudderRotation(rudder);

            tickSailBoat();

            if (this.level().isClientSide() && this.getControllingPassenger() != null) {
                PacketHandler.send(PacketDistributor.SERVER.noArg(),
                        new ServerBoundSloopPacket(this.getMainsheetLength(), this.getRudderRotation(), this.getId()));
            }
        }
    }

    @Override
    protected float getPaddleMultiplier() {
        return 0;
    }

    @Override
    protected float[] getPaddleAcceleration() {
        return new float[]{0, 0, 0};
    }

    @Override
    protected float getMomentumSubtractor() {
        return 0.0005f;
    }

    protected void tickSailBoat() {
        if (getPilotCompartment() != null) {
            boolean inputUp = this.getPilotCompartment().getInputUp();
            boolean inputDown = this.getPilotCompartment().getInputDown();
            boolean inputLeft = this.getPilotCompartment().getInputLeft();
            boolean inputRight = this.getPilotCompartment().getInputRight();
            float sheet = this.getMainsheetLength();
            if (inputUp) {
                if (sheet < 45) {
                    sheet++;
                }
            }

            if (inputDown) {
                if (sheet > 0) {
                    sheet--;
                }
            }

            this.setMainsheetLength(sheet);

        }
    }

    protected void tickWindInput() {
        super.tickWindInput();
        if (this.status == MediumStatus.IN_WATER || this.status == MediumStatus.IN_AIR) {
            float windFunction = (float) (Mth.clamp(this.getLocalWindAngleAndSpeed()[1], 0.02, 1.0) * 0.45);

            float sailForce = this.getMainsailWindAngleAndForce()[1];
            float sailForceAngle = Mth.wrapDegrees(this.getMainsailWindAngleAndForce()[0]);

            float acceleration = windFunction * sailForce;
            if (this.getMainsailActive()) {
                if (acceleration > this.getAcceleration()) {
                    this.setAcceleration(acceleration);
                } else if (this.getAcceleration() > this.getMomentumSubtractor()) {
                    this.setAcceleration(this.getAcceleration() - this.getMomentumSubtractor());
                    acceleration = this.getAcceleration();
                }

                float keelFactor = 0.75f;
                float sailFactor = 1 - keelFactor;

                Vec3 sailAccelerationWithKeel = new Vec3(
                        Mth.sin(-this.getYRot() * ((float) Math.PI / 180F)) * acceleration * keelFactor,
                        0.0D,
                        Mth.cos(this.getYRot() * ((float) Math.PI / 180F)) * acceleration * keelFactor);

                Vec3 sailAccelerationWithSail = new Vec3(
                        Mth.sin(-(Mth.wrapDegrees(sailForceAngle)) * ((float) Math.PI / 180F)) * acceleration * sailFactor,
                        0.0D,
                        Mth.cos((Mth.wrapDegrees(sailForceAngle)) * ((float) Math.PI / 180F)) * acceleration * sailFactor);

                this.setDeltaMovement(this.getDeltaMovement()
                        .add(sailAccelerationWithKeel).add(sailAccelerationWithSail));
            }
            if (this.getJibsailActive()) {
                windFunction = (float) (Mth.clamp(this.getLocalWindAngleAndSpeed()[1], 0.02, 1.0) * 0.1);
                acceleration = windFunction * sailForce;

                if (!this.getMainsailActive()) {
                    if (acceleration > this.getAcceleration()) {
                        this.setAcceleration(acceleration);
                    } else if (this.getAcceleration() > this.getMomentumSubtractor()) {
                        this.setAcceleration(this.getAcceleration() - this.getMomentumSubtractor());
                        acceleration = this.getAcceleration();
                    }
                }

                float keelFactor = 0.9f;
                float sailFactor = 1 - keelFactor;

                Vec3 sailAccelerationWithKeel = new Vec3(
                        Mth.sin(-this.getYRot() * ((float) Math.PI / 180F)) * acceleration * keelFactor,
                        0.0D,
                        Mth.cos(this.getYRot() * ((float) Math.PI / 180F)) * acceleration * keelFactor);

                Vec3 sailAccelerationWithSail = new Vec3(
                        Mth.sin(-(Mth.wrapDegrees(sailForceAngle)) * ((float) Math.PI / 180F)) * acceleration * sailFactor,
                        0.0D,
                        Mth.cos((Mth.wrapDegrees(sailForceAngle)) * ((float) Math.PI / 180F)) * acceleration * sailFactor);

                this.setDeltaMovement(this.getDeltaMovement()
                        .add(sailAccelerationWithKeel).add(sailAccelerationWithSail));
            }
            if (isAnchorDown()) {
                this.setDeltaMovement(this.getDeltaMovement().multiply(0.5, 1, 0.5));
            }
        }
    }

    @Override
    protected double windDriftMultiplier() {
        return 0.3;
    }

    @Override
    public void tickAnchorInput() {
        if (this.getMainsailActive() || this.getJibsailActive()) {
            return;
        }
        IHaveAnchorWindlass.super.tickAnchorInput();
    }

    public float[] getMainsailWindAngleAndForce() {
        float windDifference = Mth.degreesDifference(this.getWindLocalRotation(), Mth.wrapDegrees(this.getMainBoomRotation()));

        // calculate wind force for lifting scenario
        float windForceAngle = Mth.wrapDegrees(2 * windDifference + this.getYRot());

        if (Math.abs(windDifference) < 120) {
            // calculate wind force for dragging scenario
            windForceAngle = this.getLocalWindAngleAndSpeed()[0];
        }

        float windForce = CommonHelper.sailForceMultiplierTable(windDifference);
        return new float[]{(float) windForceAngle, (float) windForce};
    }

    public float getMainBoomRotation() {
        return Mth.wrapDegrees(this.entityData.get(DATA_ID_MAIN_BOOM_ROTATION));
    }

    public void setMainBoomRotation(float rotation) {
        this.entityData.set(DATA_ID_MAIN_BOOM_ROTATION, Mth.clamp(rotation, -1 * getMainsheetLength(), getMainsheetLength()));
    }

    public float getMainsheetLength() {
        return Mth.wrapDegrees(this.entityData.get(DATA_ID_MAINSHEET_LENGTH));
    }

    public void setMainsheetLength(float length) {
        this.entityData.set(DATA_ID_MAINSHEET_LENGTH, Mth.clamp(length, 0, 45));
    }

    public void setMainsailActive(boolean mainsail) {
        this.entityData.set(DATA_ID_MAINSAIL_ACTIVE, mainsail);
    }

    public boolean getMainsailActive() {
        return this.entityData.get(DATA_ID_MAINSAIL_ACTIVE);
    }

    public void setJibsailActive(boolean jibsail) {
        this.entityData.set(DATA_ID_JIBSAIL_ACTIVE, jibsail);
    }

    public boolean getJibsailActive() {
        return this.entityData.get(DATA_ID_JIBSAIL_ACTIVE);
    }

    public void setTicksNoRiders(int ticks) {
        this.entityData.set(DATA_ID_TICKS_NO_RIDERS, ticks);
    }

    public int getTicksNoRiders() {
        return this.entityData.get(DATA_ID_TICKS_NO_RIDERS);
    }

    public void setRudderRotation(float rotation) {
        this.entityData.set(DATA_ID_RUDDER_ROTATION, Mth.clamp(rotation, -45, 45));
    }

    public float getRudderRotation() {
        return this.entityData.get(DATA_ID_RUDDER_ROTATION);
    }

    /**
     * @return The color of the mainsail
     */
    public DyeColor getMainsailDye() {
        return this.entityData.get(DATA_ID_MAINSAIL_DYE);
    }

    public void setMainsailDye(final DyeColor paintColor) {
        this.entityData.set(DATA_ID_MAINSAIL_DYE, paintColor);
    }

    public void clearMainsailDye() {
        this.entityData.set(DATA_ID_MAINSAIL_DYE, DyeColor.WHITE);
    }

    /**
     * @return The color of the Jibsail
     */
    public DyeColor getJibsailDye() {
        return this.entityData.get(DATA_ID_JIBSAIL_DYE);
    }

    public void setJibsailDye(final DyeColor paintColor) {
        this.entityData.set(DATA_ID_JIBSAIL_DYE, paintColor);
    }

    public void clearJibsailDye() {
        this.entityData.set(DATA_ID_JIBSAIL_DYE, DyeColor.WHITE);
    }

    public void setPaintColor(final DyeColor paintColor) {
        this.entityData.set(DATA_ID_PAINT_COLOR, Optional.of(paintColor));
    }

    public void setIceBreaker(final boolean icebreaker) {
        this.entityData.set(DATA_ID_ICEBREAKER, icebreaker);
    }

    @Override
    public Optional<DyeColor> getPaintColor() {
        return this.entityData.get(DATA_ID_PAINT_COLOR);
    }

    public void clearPaint() {
        this.entityData.set(DATA_ID_PAINT_COLOR, Optional.empty());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.setMainBoomRotation(pCompound.getFloat("mainBoomRotation"));
        this.setRudderRotation(pCompound.getFloat("rudderRotation"));
        this.setMainsailActive(pCompound.getBoolean("mainsailActive"));
        this.setJibsailActive(pCompound.getBoolean("jibsailActive"));
        this.setTicksNoRiders(pCompound.getInt("ticksNoRiders"));
        this.setMainsheetLength(pCompound.getFloat("mainSheetLength"));
        this.setIceBreaker(pCompound.getBoolean("icebreaker"));

        if (pCompound.contains("jibsailDye", Tag.TAG_BYTE)) {
            this.setMainsailDye(DyeColor.byId(pCompound.getByte("jibsailDye")));
        }

        if (pCompound.contains("mainsailDye", Tag.TAG_BYTE)) {
            this.setMainsailDye(DyeColor.byId(pCompound.getByte("mainsailDye")));
        }

        if (pCompound.contains("paint", Tag.TAG_BYTE)) {
            this.setPaintColor(DyeColor.byId(pCompound.getByte("paint")));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putFloat("mainBoomRotation", this.getMainBoomRotation());
        pCompound.putFloat("rudderRotation", this.getRudderRotation());
        pCompound.putBoolean("mainsailActive", this.getMainsailActive());
        pCompound.putBoolean("jibsailActive", this.getJibsailActive());
        pCompound.putInt("ticksNoRiders", this.getTicksNoRiders());
        pCompound.putFloat("mainSheetLength", this.getMainsheetLength());
        pCompound.putBoolean("icebreaker", this.breaksIce());

        {
            final DyeColor paintColor = this.getJibsailDye();
            if (paintColor != DyeColor.WHITE) {
                pCompound.putByte("jibsailDye", (byte) paintColor.getId());
            }
        }

        {
            final DyeColor paintColor = this.getMainsailDye();
            if (paintColor != DyeColor.WHITE) {
                pCompound.putByte("mainsailDye", (byte) paintColor.getId());
            }
        }

        this.getPaintColor().ifPresent(dyeColor -> pCompound.putByte("paint", (byte) dyeColor.getId()));
    }

    @Override
    public ArrayList<IngameOverlays.IconState> getIconStates(Player player) {
        ArrayList<IngameOverlays.IconState> states = new ArrayList<>();

        for (final ItemStack itemStack : player.getHandSlots()) {
            if (itemStack.is(AlekiShipsTags.Items.ICEBREAKER_UPGRADES) && !this.breaksIce()) {
                states.add(IngameOverlays.IconState.HAMMER);
                return states;
            }
        }

        for (final ItemStack itemStack : player.getHandSlots()) {
            if (itemStack.is(Items.NAME_TAG)) {
                states.add(IngameOverlays.IconState.BRUSH);
                return states;
            }
        }

        return super.getIconStates(player);
    }

    @Override
    public boolean breaksIce() {
        return entityData.get(DATA_ID_ICEBREAKER);
    }
}
