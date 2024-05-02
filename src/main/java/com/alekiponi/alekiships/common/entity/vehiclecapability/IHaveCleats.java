package com.alekiponi.alekiships.common.entity.vehiclecapability;

import com.alekiponi.alekiships.common.entity.vehicle.AbstractVehicle;
import com.alekiponi.alekiships.common.entity.vehiclehelper.VehicleCleatEntity;

import java.util.ArrayList;

public interface IHaveCleats {
    int[] getCleatIndices();

    //int[][] getCleatRotations();

    private ArrayList<VehicleCleatEntity> getCleats(AbstractVehicle vehicle) {
        ArrayList<VehicleCleatEntity> list = new ArrayList<VehicleCleatEntity>();
        if (vehicle.getPassengers().size() == vehicle.getMaxPassengers()) {
            for (int i : this.getCleatIndices()) {
                if (vehicle.getPassengers().get(i).getFirstPassenger() instanceof VehicleCleatEntity cleat) {
                    list.add(cleat);
                }
            }
        }
        return list;
    }

    default ArrayList<VehicleCleatEntity> getCleats() {
        return getCleats((AbstractVehicle) this);
    }

    default boolean isBeingTowed(AbstractVehicle vehicle) {
        if (vehicle.getPassengers().size() == vehicle.getMaxPassengers()) {
            for (VehicleCleatEntity cleat : this.getCleats()) {
                return cleat.isLeashed() && vehicle.getDeltaMovement().length() != 0;
            }
        }
        return false;
    }

    default boolean isBeingTowed() {
        return isBeingTowed((AbstractVehicle) this);
    }

    void tickCleatInput();
}
