package com.alekiponi.alekiships.common.entity.vehiclehelper;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.common.entity.AlekiShipsEntities;
import com.alekiponi.alekiships.common.entity.vehicle.AbstractAlekiBoatEntity;
import com.alekiponi.alekiships.common.entity.vehicle.AbstractVehicle;
import com.alekiponi.alekiships.common.entity.vehiclecapability.IHaveAnchorWindlass;
import com.alekiponi.alekiships.common.entity.vehiclecapability.IHaveCleats;
import com.alekiponi.alekiships.common.entity.vehiclecapability.IHaveMasts;
import com.alekiponi.alekiships.common.entity.vehiclecapability.IHaveSailSwitches;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class BoatVehiclePart extends AbstractVehiclePart{
    public BoatVehiclePart(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void tickAddAppropriateHelper(AbstractVehicle vehicle) {
        if(vehicle instanceof AbstractAlekiBoatEntity boatEntity){
            //TODO oof
            boolean alreadyHasPassenger = false;
            // Try adding a cleat
            alreadyHasPassenger = tickAddCleat(vehicle);

            //Try adding a collider
            if (!alreadyHasPassenger) {
                alreadyHasPassenger = tickAddCollider(vehicle);
            }

            // Try adding a switch
            if (!alreadyHasPassenger) {
                alreadyHasPassenger = tickAddSailSwitch(boatEntity);
            }

            // Try adding a Windlass
            if (!alreadyHasPassenger) {
                tickAddWindlass(boatEntity);
                alreadyHasPassenger = tickAddWindlass(boatEntity);
            }

            // Try adding a Mast
            if(!alreadyHasPassenger){
                tickAddMast(boatEntity);
            }

            // Try adding a compartment
            if (!alreadyHasPassenger) {
                alreadyHasPassenger = tickAddCompartment(vehicle);
            }
        }

    }

    protected boolean tickAddSailSwitch(AbstractAlekiBoatEntity boat) {
        if(boat instanceof IHaveSailSwitches){
            for (int i : ((IHaveSailSwitches)boat).getSailSwitchIndices()) {
                if (boat.getPassengers().get(i).is(this) && !boat.getPassengers().get(i).isVehicle()) {

                    final AbstractSwitchEntity switchEntity = AlekiShipsEntities.SAIL_SWITCH_ENTITY.get()
                            .create(this.level());
                    assert switchEntity != null;
                    switchEntity.setPos(this.getX(), this.getY(), this.getZ());
                    if (!switchEntity.startRiding(this)) {
                        AlekiShips.LOGGER.error("New Switch: {} unable to ride Vehicle Part: {}", switchEntity, this);
                    }
                    this.level().addFreshEntity(switchEntity);
                    return true;

                }
            }
        }

        return false;
    }

    protected boolean tickAddWindlass(AbstractAlekiBoatEntity boat) {
        if(boat instanceof IHaveAnchorWindlass){
            for (int i : ((IHaveAnchorWindlass)boat).getWindlassIndices()) {
                if (boat.getPassengers().get(i).is(this) && !boat.getPassengers().get(i).isVehicle()) {


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
        }

        return false;
    }

    protected boolean tickAddMast(AbstractAlekiBoatEntity boat) {
        if(boat instanceof IHaveMasts){
            for (int i : ((IHaveMasts)boat).getMastIndices()) {
                if (boat.getPassengers().get(i).is(this) && !boat.getPassengers().get(i).isVehicle()) {

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
        }

        return false;
    }
}
