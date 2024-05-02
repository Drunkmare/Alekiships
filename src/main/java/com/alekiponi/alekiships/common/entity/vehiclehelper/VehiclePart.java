package com.alekiponi.alekiships.common.entity.vehiclehelper;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.common.entity.AlekiShipsEntities;
import com.alekiponi.alekiships.common.entity.vehicle.AbstractVehicle;
import com.alekiponi.alekiships.common.entity.vehiclecapability.*;
import com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.AbstractCompartmentEntity;
import com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.EmptyCompartmentEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class VehiclePart extends Entity {

    protected static final EntityDataAccessor<Float> DATA_ID_COMPARTMENT_ROTATION = SynchedEntityData.defineId(
            VehiclePart.class, EntityDataSerializers.FLOAT);
    private int selfDestructTicks = 0;

    public VehiclePart(final EntityType<?> entityType, final Level level) {
        super(entityType, level);
    }

    @Override
    public void tick() {

        // Most of vehiclepart's ticking actions should only be on the server
        if (!this.level().isClientSide()) {
            if (!this.isPassenger()) {
                selfDestructTicks++;
                if (selfDestructTicks++ >= 5) {
                    this.ejectPassengers();
                    this.remove(RemovalReason.DISCARDED);
                }
            }

            if (!(this.getVehicle() instanceof final AbstractVehicle vehicle)) {
                return;
            }

            if (tickCount < 30) {
                if (vehicle.getPassengers().size() == vehicle.getMaxPassengers()) {
                    for (int[] i : vehicle.getCompartmentRotationsArray()) {
                        if (vehicle.getPassengers().get(i[0]) == this) {
                            this.setCompartmentRotation(i[1]);
                        }
                    }
                }

            }

            // Try not to be empty
            if (this.getPassengers().isEmpty() && vehicle.getPassengers().size() == vehicle.getMaxPassengers()) {
                tickAddAppropriateHelper(vehicle);
            }

        }

        super.tick();
    }

    @Override
    protected void positionRider(final net.minecraft.world.entity.Entity passenger, final net.minecraft.world.entity.Entity.MoveFunction moveFunction) {

        if (!(this.getVehicle() instanceof AbstractVehicle abstractVehicle)) return;

        final double localY = ((this.isRemoved() ? 0.01 : this.getPassengersRidingOffset()) + passenger.getMyRidingOffset());

        moveFunction.accept(passenger, this.getX(), this.getY() + localY, this.getZ());
        passenger.setPos(this.getX(), this.getY() + localY, this.getZ());

        if ((passenger instanceof AbstractCompartmentEntity || passenger instanceof VehicleCleatEntity)) {
            this.setYRot(abstractVehicle.getYRot());
            passenger.setYRot(this.getYRot() + this.getCompartmentRotation());
        }
    }

    protected void tickAddAppropriateHelper(AbstractVehicle vehicle) {

        //Try adding a collider
        if (tickAddCollider(vehicle)) {
            return;
        }

        // Try adding a cleat
        if (vehicle instanceof IHaveCleats && tickAddCleat((IHaveCleats) vehicle)) {
            return;
        }

        // Try adding a Sail Switch
        if (vehicle instanceof IHaveSailSwitches && tickAddSailSwitch((IHaveSailSwitches) vehicle)) {
            return;
        }

        // Try adding a Windlass
        if (vehicle instanceof IHaveAnchorWindlass && tickAddWindlass((IHaveAnchorWindlass) vehicle)) {
            return;
        }

        // Try adding a Mast
        if (vehicle instanceof IHaveMasts && tickAddMast((IHaveMasts) vehicle)) {
            return;
        }

        //Try adding a construction entity
        if (vehicle instanceof IHaveConstructionEntities && tickAddConstruction((IHaveConstructionEntities) vehicle)) {
            return;
        }

        // Try adding a compartment
        if (tickAddCompartment(vehicle)) {
            return;
        }
    }

    protected boolean tickAddCleat(IHaveCleats vehicle) {
        for (int i : vehicle.getCleatIndices()) {
            if (((AbstractVehicle) vehicle).getPassengers().get(i).is(this) && !((AbstractVehicle) vehicle).getPassengers().get(i).isVehicle()) {

                final VehicleCleatEntity cleat = AlekiShipsEntities.VEHICLE_CLEAT_ENTITY.get()
                        .create(this.level());
                assert cleat != null;
                cleat.setPos(this.getX(), this.getY(), this.getZ());
                cleat.setYRot(this.getVehicle().getYRot());
                if (!cleat.startRiding(this)) {
                    AlekiShips.LOGGER.error("New Cleat: {} unable to ride Vehicle Part: {}", cleat, this);
                }
                this.level().addFreshEntity(cleat);
                return true;
            }
        }

        return false;

    }

    protected boolean tickAddCollider(IHaveColliders vehicle) {
        for (int i : vehicle.getColliderIndices()) {
            if (((AbstractVehicle) vehicle).getPassengers().get(i).is(this) && !((AbstractVehicle) vehicle).getPassengers().get(i).isVehicle()) {
                final VehicleColliderEntity collider = AlekiShipsEntities.VEHICLE_COLLIDER_ENTITY.get()
                        .create(this.level());

                assert collider != null;
                collider.setPos(this.getX(), this.getY(), this.getZ());
                if (!collider.startRiding(this)) {
                    AlekiShips.LOGGER.error("New Collider: {} unable to ride Vehicle Part: {}", collider, this);
                }
                this.level().addFreshEntity(collider);
                return true;

            }
        }
        return false;
    }

    protected boolean tickAddCompartment(AbstractVehicle vehicle) {

        for (int i : vehicle.getCompartmentIndices()) {
            if (vehicle.getPassengers().get(i).is(this) && !vehicle.getPassengers().get(i).isVehicle()) {
                final EmptyCompartmentEntity newCompartment = AlekiShipsEntities.EMPTY_COMPARTMENT_ENTITY.get()
                        .create(this.level());

                if (newCompartment != null) {
                    newCompartment.setYRot(this.getVehicle().getYRot() + this.getCompartmentRotation());
                    newCompartment.setPos(this.getX(), this.getY(), this.getZ());
                    if (!newCompartment.startRiding(this)) {
                        AlekiShips.LOGGER.error("New Compartment: {} unable to ride Vehicle Part: {}", newCompartment,
                                this);
                    }
                    this.level().addFreshEntity(newCompartment);
                    return true;
                }
            }

        }


        return false;
    }

    protected boolean tickAddSailSwitch(IHaveSailSwitches vehicle) {
        for (int i : vehicle.getSailSwitchIndices()) {
            if (((AbstractVehicle) vehicle).getPassengers().get(i).is(this) && !((AbstractVehicle) vehicle).getPassengers().get(i).isVehicle()) {

                final AbstractSwitchEntity switchEntity = AlekiShipsEntities.SAIL_SWITCH_ENTITY.get()
                        .create(this.level());
                assert switchEntity != null;
                switchEntity.setPos(this.getX(), this.getY(), this.getZ());
                if (!switchEntity.startRiding(this)) {
                    AlekiShips.LOGGER.error("New Sail Switch: {} unable to ride Vehicle Part: {}", switchEntity, this);
                }
                this.level().addFreshEntity(switchEntity);
                return true;

            }
        }

        return false;
    }

    protected boolean tickAddWindlass(IHaveAnchorWindlass vehicle) {
        for (int i : vehicle.getWindlassIndices()) {
            if (((AbstractVehicle) vehicle).getPassengers().get(i).is(this) && !((AbstractVehicle) vehicle).getPassengers().get(i).isVehicle()) {

                final WindlassSwitchEntity windlass = AlekiShipsEntities.WINDLASS_SWITCH_ENTITY.get()
                        .create(this.level());
                assert windlass != null;
                windlass.setPos(this.getX(), this.getY(), this.getZ());
                if (!windlass.startRiding(this)) {
                    AlekiShips.LOGGER.error("New Windlass: {} unable to ride Vehicle Part: {}", windlass, this);
                }
                this.level().addFreshEntity(windlass);
                return true;
            }
        }

        return false;
    }

    protected boolean tickAddMast(IHaveMasts vehicle) {
        for (int i : vehicle.getMastIndices()) {
            if (((AbstractVehicle) vehicle).getPassengers().get(i).is(this) && !((AbstractVehicle) vehicle).getPassengers().get(i).isVehicle()) {

                final MastEntity mast = AlekiShipsEntities.MAST_ENTITY.get()
                        .create(this.level());
                assert mast != null;
                mast.setPos(this.getX(), this.getY(), this.getZ());
                mast.setYRot(this.getVehicle().getYRot());
                if (!mast.startRiding(this)) {
                    AlekiShips.LOGGER.error("New Mast: {} unable to ride Vehicle Part: {}", mast, this);
                }
                this.level().addFreshEntity(mast);
                return true;
            }
        }

        return false;
    }

    protected boolean tickAddConstruction(IHaveConstructionEntities vehicle) {
        for (int i : vehicle.getConstructionIndices()) {
            if (((AbstractVehicle) vehicle).getPassengers().get(i).is(this) && !((AbstractVehicle) vehicle).getPassengers().get(i).isVehicle()) {

                final ConstructionEntity constructionEntity = AlekiShipsEntities.CONSTRUCTION_ENTITY.get()
                        .create(this.level());
                assert constructionEntity != null;
                constructionEntity.setPos(this.getX(), this.getY(), this.getZ());
                constructionEntity.setYRot(this.getVehicle().getYRot());
                if (!constructionEntity.startRiding(this)) {
                    AlekiShips.LOGGER.error("New Construction Entity: {} unable to ride Vehicle Part: {}", constructionEntity, this);
                }
                this.level().addFreshEntity(constructionEntity);
                return true;
            }
        }

        return false;
    }


    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_ID_COMPARTMENT_ROTATION, 0f);
    }

    public void setCompartmentRotation(float rotation) {
        this.entityData.set(DATA_ID_COMPARTMENT_ROTATION, rotation);
    }

    public float getCompartmentRotation() {
        return this.entityData.get(DATA_ID_COMPARTMENT_ROTATION);
    }

    @Override
    protected void readAdditionalSaveData(final CompoundTag compoundTag) {
        this.setCompartmentRotation(compoundTag.getFloat("compartmentRotation"));
    }

    @Override
    protected void addAdditionalSaveData(final CompoundTag compoundTag) {
        compoundTag.putFloat("compartmentRotation", this.getCompartmentRotation());
    }

    @Override
    public Component getName() {
        if (this.getRootVehicle() instanceof AbstractVehicle vehicle) {
            return vehicle.getName();
        } else {
            return super.getName();
        }
    }
}