package com.alekiponi.alekiships.common.entity.vehiclehelper.compartment;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.client.IngameOverlays;
import com.alekiponi.alekiships.common.entity.AlekiShipsEntities;
import com.alekiponi.alekiships.common.entity.CannonEntity;
import com.alekiponi.alekiships.common.entity.vehicle.AbstractVehicle;
import com.alekiponi.alekiships.common.entity.vehicle.RowboatEntity;
import com.alekiponi.alekiships.common.entity.vehiclecapability.ICannonable;
import com.alekiponi.alekiships.common.entity.vehiclecapability.IHaveBlockOnlyCompartments;
import com.alekiponi.alekiships.common.entity.vehiclehelper.AbstractPassthroughHelper;
import com.alekiponi.alekiships.common.entity.vehiclehelper.CompartmentType;
import com.alekiponi.alekiships.common.entity.vehiclehelper.VehiclePart;
import com.alekiponi.alekiships.common.item.AlekiShipsItems;
import com.alekiponi.alekiships.network.PacketHandler;
import com.alekiponi.alekiships.network.ServerboundCompartmentInputPacket;
import com.alekiponi.alekiships.util.AlekiShipsTags;
import com.alekiponi.alekiships.util.advancements.AlekiShipsAdvancements;
import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.DismountHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EmptyCompartmentEntity extends AbstractCompartmentEntity {

    protected static final EntityDataAccessor<Boolean> DATA_ID_INPUT_LEFT = SynchedEntityData.defineId(
            EmptyCompartmentEntity.class, EntityDataSerializers.BOOLEAN);

    protected static final EntityDataAccessor<Boolean> DATA_ID_INPUT_RIGHT = SynchedEntityData.defineId(
            EmptyCompartmentEntity.class, EntityDataSerializers.BOOLEAN);

    protected static final EntityDataAccessor<Boolean> DATA_ID_INPUT_UP = SynchedEntityData.defineId(
            EmptyCompartmentEntity.class, EntityDataSerializers.BOOLEAN);

    protected static final EntityDataAccessor<Boolean> DATA_ID_INPUT_DOWN = SynchedEntityData.defineId(
            EmptyCompartmentEntity.class, EntityDataSerializers.BOOLEAN);

    protected boolean canAddNonPlayers;
    protected boolean canAddOnlyBlocks;
    protected boolean canAddCannons;


    public EmptyCompartmentEntity(final CompartmentType<? extends EmptyCompartmentEntity> compartmentType,
                                  final Level level) {
        super(compartmentType, level);
        canAddNonPlayers = true;
        canAddOnlyBlocks = false;
        canAddCannons = false;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_ID_INPUT_LEFT, false);
        this.entityData.define(DATA_ID_INPUT_RIGHT, false);
        this.entityData.define(DATA_ID_INPUT_UP, false);
        this.entityData.define(DATA_ID_INPUT_DOWN, false);
    }

    public boolean canAddNonPlayers() {
        return canAddNonPlayers;
    }

    public boolean canAddOnlyBLocks() {
        return canAddOnlyBlocks;
    }

    public boolean canAddCannons() {
        return canAddCannons;
    }


    protected void addPassenger(Entity pPassenger) {
        super.addPassenger(pPassenger);
        if (this.isPassenger()) {
            this.setYRot(this.getVehicle().getYRot());
        }
    }

    @Override
    protected boolean canAddPassenger(final Entity passenger) {
        return this.getPassengers().isEmpty() && !this.isRemoved();
    }

    @Override
    protected void positionRider(final Entity passenger, final Entity.MoveFunction moveFunction) {
        super.positionRider(passenger, moveFunction);
        float localX = 0.0F;
        float localZ = 0.0F;
        float localY = (float) ((this.isRemoved() ? 0.01 : this.getPassengersRidingOffset()) + passenger.getMyRidingOffset());
        if (passenger instanceof Player) {
            localY = 0;
            // TODO remove instance check and use abstraction / positionRiderByIndex()
            if (this.getTrueVehicle() instanceof RowboatEntity rowboatEntity) {
                localY = 0.25f;
                if (rowboatEntity.getPilotVehiclePartAsEntity() != ridingThisPart) {
                    localX = -0.25f;
                }
            }
        }

        if (passenger.getBbHeight() <= 0.7) {
            localY -= 0.2f;
        }
        if (passenger instanceof CannonEntity) {
            localY += 0.15f;
        }
        if (passenger.getBbWidth() > 0.9f) {
            localX += 0.2f;
            // TODO remove instance check and use abstraction
            if (this.getTrueVehicle() instanceof RowboatEntity) {
                localX -= 0.6f;
            }
        }


        final double eyepos = passenger.getEyePosition().get(Direction.Axis.Y);
        final double thisY = this.getY() + 1.1f;
        if (eyepos < thisY) {
            localY += (float) Math.abs(eyepos - thisY);
        }
        final Vec3 vec3 = this.positionPassengerLocally(localX, localY - 0.57f, localZ);
        moveFunction.accept(passenger, this.getX() + vec3.x, this.getY() - 0.57 + localY, this.getZ() + vec3.z);

        if (passenger instanceof LivingEntity livingEntity) {
            livingEntity.setYBodyRot(this.getYRot());
            livingEntity.setYHeadRot(livingEntity.getYHeadRot() + this.getYRot());
            this.clampRotation(livingEntity);
        } else if (passenger instanceof CannonEntity cannon) {
            cannon.setYRot(this.getYRot() - 180);
            if (cannon.getXRot() > 5) {
                cannon.setXRot(5);
            }
        }
        if (passenger instanceof ArmorStand armorStand) {
            armorStand.setYBodyRot(this.getYRot());
            armorStand.setYRot(this.getYRot());
            armorStand.setNoBasePlate(true);
        }
        //this.clampRotation(passenger);
    }

    @Override
    public double getPassengersRidingOffset() {
        return 0.6d * 0.75D;
    }

    protected Vec3 positionPassengerLocally(float localX, float localY, float localZ) {
        return (new Vec3(localX, localY, localZ)).yRot(
                -this.getYRot() * ((float) Math.PI / 180F) - ((float) Math.PI / 2F));
    }

    @Override
    public void tick() {
        if (this.getTrueVehicle() != null) {
            AbstractVehicle vehicle = this.getTrueVehicle();

            if (tickCount < 10 && vehicle
                    .getPilotVehiclePartAsEntity() != null && !vehicle.pilotCompartmentAcceptsNonPlayers()) {
                canAddNonPlayers = !(vehicle.getPilotVehiclePartAsEntity() == this.getVehicle());
            }
            if (tickCount < 10 && this.isPassenger()) {
                if (vehicle instanceof IHaveBlockOnlyCompartments) {
                    for (AbstractCompartmentEntity compartment : ((IHaveBlockOnlyCompartments) vehicle).getCanAddOnlyBlocks()) {
                        if (compartment.getVehicle() == this.getVehicle()) {
                            canAddOnlyBlocks = true;
                        }
                    }
                }

                if (vehicle instanceof ICannonable) {
                    for (AbstractCompartmentEntity compartment : ((ICannonable) vehicle).getCanAddCannons()) {
                        if (compartment.getVehicle() == this.getVehicle()) {
                            canAddCannons = true;
                        }
                    }
                }


            }
        }

        if (everyNthTickUnique(5)) {
            if (this.isVehicle() && !this.level().isClientSide()) {
                if (this.getFirstPassenger() != null && this.getTrueVehicle() != null && this.getFirstPassenger().getBbWidth() > this.getTrueVehicle().getPassengerSizeLimit()) {
                    this.ejectPassengers();
                }
            }

            final List<Entity> list = this.level()
                    .getEntities(this, this.getBoundingBox().inflate(0.2, -0.01, 0.2), EntitySelector.pushableBy(this));

            if (!list.isEmpty() && this.canAddNonPlayers() && !this.canAddOnlyBLocks() && !this.level()
                    .isClientSide() && this.getTrueVehicle() != null) {
                for (final Entity entity : list) {
                    if (!entity.hasPassenger(this)) {
                        float maxSize = 0.6f;
                        maxSize = this.getTrueVehicle().getPassengerSizeLimit();
                        if (this.getPassengers()
                                .size() == 0 && !entity.isPassenger() && entity.getBbWidth() <= maxSize) {
                            if (entity instanceof LivingEntity && !(entity instanceof WaterAnimal) && !(entity instanceof Player)) {
                                entity.startRiding(this);
                            }

                        }
                    }

                }
            }
        }


        if (!(this.getFirstPassenger() instanceof Player)) {
            this.setInputLeft(false);
            this.setInputRight(false);
            this.setInputUp(false);
            this.setInputDown(false);
        }

        super.tick();

    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {

        pCompound.getBoolean("inputLeft");
        pCompound.getBoolean("inputRight");
        pCompound.getBoolean("inputUp");
        pCompound.getBoolean("inputDown");

        super.readAdditionalSaveData(pCompound);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {

        pCompound.putBoolean("inputLeft", this.getInputLeft());
        pCompound.putBoolean("inputRight", this.getInputRight());
        pCompound.putBoolean("inputUp", this.getInputUp());
        pCompound.putBoolean("inputDown", this.getInputDown());

        super.readAdditionalSaveData(pCompound);
    }

    protected void clampRotation(final Entity entity) {
        entity.setYBodyRot(this.getYRot());
        final float f = Mth.wrapDegrees(entity.getYRot() - this.getYRot());
        final float f1 = Mth.clamp(f, -105.0F, 105.0F);
        entity.yRotO += f1 - f;
        entity.setYRot(entity.getYRot() + f1 - f);
        entity.setYHeadRot(entity.getYRot());
    }


    public void setInput(final boolean inputLeft, final boolean inputRight, final boolean inputUp,
                         final boolean inputDown) {
        if (this.getFirstPassenger() instanceof Player) {
            boolean shouldUpdateServer = false;
            if (this.getInputLeft() != inputLeft) {
                this.setInputLeft(inputLeft);
                shouldUpdateServer = true;
            }
            if (this.getInputRight() != inputRight) {
                this.setInputRight(inputRight);
                shouldUpdateServer = true;
            }
            if (this.getInputUp() != inputUp) {
                this.setInputUp(inputUp);
                shouldUpdateServer = true;
            }
            if (this.getInputDown() != inputDown) {
                this.setInputDown(inputDown);
                shouldUpdateServer = true;
            }
            if (this.level().isClientSide() && shouldUpdateServer) {
                PacketHandler.send(PacketDistributor.SERVER.noArg(),
                        new ServerboundCompartmentInputPacket(inputLeft, inputRight, inputUp, inputDown, this.getId()));
            }
        } else {
            this.setInputLeft(false);
            this.setInputRight(false);
            this.setInputUp(false);
            this.setInputDown(false);
            if (this.level().isClientSide()) {
                PacketHandler.send(PacketDistributor.SERVER.noArg(),
                        new ServerboundCompartmentInputPacket(false, false, false, false, this.getId()));
            }

        }

    }

    public boolean getInputLeft() {
        return this.entityData.get(DATA_ID_INPUT_LEFT);
    }

    public boolean getInputRight() {

        return this.entityData.get(DATA_ID_INPUT_RIGHT);
    }

    public boolean getInputUp() {
        return this.entityData.get(DATA_ID_INPUT_UP);
    }

    public boolean getInputDown() {

        return this.entityData.get(DATA_ID_INPUT_DOWN);
    }

    public void setInputLeft(boolean input) {

        this.entityData.set(DATA_ID_INPUT_LEFT, input);
    }

    public void setInputRight(boolean input) {

        this.entityData.set(DATA_ID_INPUT_RIGHT, input);
    }

    public void setInputUp(boolean input) {
        this.entityData.set(DATA_ID_INPUT_UP, input);
    }

    public void setInputDown(boolean input) {

        this.entityData.set(DATA_ID_INPUT_DOWN, input);
    }

    @Override
    public void onPassengerTurned(final Entity entity) {
        this.clampRotation(entity);
    }

    @Override
    public InteractionResult interact(final Player player, final InteractionHand hand) {
        final ItemStack heldStack = player.getItemInHand(hand);

        //TODO make generic for all oar-accepting boats
        if (heldStack.is(AlekiShipsItems.OAR.get())) {
            if (this.getTrueVehicle() instanceof RowboatEntity rowboat && !rowboat.getOars().equals(RowboatEntity.Oars.TWO)) {
                return rowboat.interact(player, hand);
            }
        }

        if (this.canAddNonPlayers() && !this.canAddOnlyBLocks() && heldStack.is(
                AlekiShipsItems.CANNON.get()) && this.getRootVehicle() instanceof ICannonable) {
            if (this.getVehicle() instanceof VehiclePart part && this.canAddCannons) {
                CannonEntity cannon = AlekiShipsEntities.CANNON_ENTITY.get().create(this.level());
                assert cannon != null;
                cannon.moveTo(this.position());
                cannon.setYRot(this.getYRot() - 180);

                if (!this.level().isClientSide()) {
                    this.level().addFreshEntity(cannon);
                    if (!cannon.startRiding(this)) {
                        AlekiShips.LOGGER.error("New Cannon: {} unable to ride Compartment: {}", cannon, this);
                    }
                }
                // TODO award stats properly elsewhere
                player.awardStat(Stats.ITEM_USED.get(AlekiShipsItems.CANNON.get()));
                if (!player.getAbilities().instabuild) {
                    heldStack.shrink(1);
                }
                return InteractionResult.SUCCESS;
            }
        }

        if (this.canAddNonPlayers() && !this.canAddOnlyBLocks() && heldStack.is(
                Items.ARMOR_STAND)) {
            ArmorStand armorStand = EntityType.ARMOR_STAND.create(this.level());
            assert armorStand != null;
            armorStand.moveTo(this.position());
            armorStand.setYRot(this.getYRot() - 180);
            armorStand.setYBodyRot(this.getYRot() - 180);
            armorStand.setYHeadRot(this.getYRot() - 180);

            if (!this.level().isClientSide()) {
                this.level().addFreshEntity(armorStand);
                if (!armorStand.startRiding(this)) {
                    AlekiShips.LOGGER.error("New Armor Stand: {} unable to ride Compartment: {}", armorStand, this);
                }
            }

            player.awardStat(Stats.ITEM_USED.get(Items.ARMOR_STAND));
            if (!player.getAbilities().instabuild) {
                heldStack.shrink(1);
            }
            if (player instanceof ServerPlayer serverPlayer) {
                AlekiShipsAdvancements.ARMOR_STAND_ON_BOAT.trigger(serverPlayer);
            }

            return InteractionResult.SUCCESS;
        }

        if (player.isSecondaryUseActive()) {
            if (!getPassengers().isEmpty() && !(getFirstPassenger() instanceof Player)) {
                this.ejectPassengers();
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        }

        if (ridingThisPart == null) return InteractionResult.FAIL;

        final Optional<CompartmentType<?>> compartmentType = CompartmentType.fromStack(heldStack);

        if (compartmentType.isPresent()) {
            if ((this.getRootVehicle() instanceof AbstractVehicle vehicle && !vehicle.pilotCompartmentAcceptsNonPlayers()) && vehicle.getPilotCompartment().is(this)) {
                return InteractionResult.FAIL;
            }

            final AbstractCompartmentEntity compartmentEntity = compartmentType.get()
                    .create(this.level(), heldStack.copy());
            // Didn't get back a compartment so creating it failed somehow so try and ride the compartment
            if (null == compartmentEntity) {
                if (!this.canAddOnlyBLocks()) {
                    return player.startRiding(this) ? InteractionResult.SUCCESS : InteractionResult.PASS;
                }
                return InteractionResult.FAIL;
            }

            heldStack.shrink(1);

            this.swapCompartments(compartmentEntity);
            compartmentEntity.onPlaced();
            this.gameEvent(GameEvent.EQUIP);
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }

        if (!this.canAddOnlyBLocks()) {
            return player.startRiding(this) ? InteractionResult.SUCCESS : InteractionResult.PASS;
        }

        return InteractionResult.FAIL;
    }

    @Override
    public Vec3 getDismountLocationForPassenger(final LivingEntity passenger) {
        if (this.getTrueVehicle().getDeltaMovement().length() > 0.01) {
            return this.getTrueVehicle().getDismountLocationForPassenger(passenger);
        }
        double y = this.getTrueVehicle().getDismountLocationForPassenger(passenger).y();

        final Vec3 escapeVector = getCollisionHorizontalEscapeVector(this.getBbWidth() * Mth.SQRT_OF_TWO,
                passenger.getBbWidth(), passenger.getYRot());

        final double escapeX = this.getX() + escapeVector.x;
        final double escapeZ = this.getZ() + escapeVector.z;

        final BlockPos escapePos = BlockPos.containing(escapeX, this.getBoundingBox().maxY, escapeZ);
        final BlockPos escapePosBelow = escapePos.below();

        if (this.level().isWaterAt(escapePosBelow)) return super.getDismountLocationForPassenger(passenger);

        final List<Vec3> dismountOffsets = Lists.newArrayList();
        {
            final double floorHeight = this.level().getBlockFloorHeight(escapePos);
            if (DismountHelper.isBlockFloorValid(floorHeight)) {
                dismountOffsets.add(new Vec3(escapeX, escapePos.getY() + floorHeight, escapeZ));
            }
        }
        {
            final double floorHeight = this.level().getBlockFloorHeight(escapePosBelow);
            if (DismountHelper.isBlockFloorValid(floorHeight)) {
                dismountOffsets.add(new Vec3(escapeX, escapePosBelow.getY() + floorHeight, escapeZ));
            }
        }

        for (final Pose dismountPose : passenger.getDismountPoses()) {
            for (final Vec3 output : dismountOffsets) {
                if (!DismountHelper.canDismountTo(this.level(), output, passenger, dismountPose)) continue;

                passenger.setPose(dismountPose);
                return new Vec3(output.x, y, output.z);
            }
        }

        return super.getDismountLocationForPassenger(passenger);
    }

    @Override
    public RidingPose getRidingPose() {
        // TODO fix, it not work
        if (this.getTrueVehicle() != null && vehiclePassengerIndex != -1) {
            return this.getTrueVehicle().getRidingPoses()[this.vehiclePassengerIndex];
        }
        return RidingPose.STANDARD;
    }

    @Override
    public boolean hurt(final DamageSource damageSource, final float amount) {
        return AbstractPassthroughHelper.hurt(this, damageSource, amount);
    }

    @Nullable
    @Override
    public LivingEntity getControllingPassenger() {
        final Entity entity = this.getFirstPassenger();

        if (!(entity instanceof LivingEntity livingentity)) return null;

        return livingentity;
    }

    @Override
    protected void onPlaced() {
        // Purposefully empty
    }

    @Override
    protected void onHurt(final DamageSource damageSource) {
        // Purposefully empty
    }

    @Override
    protected void onBreak() {
        // Purposefully empty
    }

    @Override
    public ItemStack getDropStack() {
        return ItemStack.EMPTY;
    }

    @Nullable
    @Override
    public ItemStack getPickResult() {
        return null;
    }

    @Override
    public ArrayList<IngameOverlays.IconState> getIconStates(Player player) {
        ArrayList<IngameOverlays.IconState> states = new ArrayList<>();
        ItemStack handItem = player.getItemInHand(player.getUsedItemHand());

        if (this.getTrueVehicle() != null && !this.isVehicle()) {

            if (this.getTrueVehicle().getPilotCompartment() != null && this.getTrueVehicle().getPilotCompartment().is(this)) {
                states.add(IngameOverlays.IconState.HELM);

                if (this.getTrueVehicle().pilotCompartmentAcceptsNonPlayers() && handItem.is(AlekiShipsTags.Items.CAN_PLACE_IN_COMPARTMENTS)) {
                    states.add(IngameOverlays.IconState.BLOCK);
                }
                return states;
            }

            if (handItem.is(AlekiShipsTags.Items.CAN_PLACE_IN_COMPARTMENTS) || handItem.is(AlekiShipsItems.CANNON.get()) || this.canAddOnlyBLocks()) {
                states.add(IngameOverlays.IconState.BLOCK);
                return states;
            }

            states.add(IngameOverlays.IconState.SEAT);

        }

        return states;
    }
}