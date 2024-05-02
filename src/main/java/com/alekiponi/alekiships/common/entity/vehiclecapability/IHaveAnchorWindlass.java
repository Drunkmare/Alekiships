package com.alekiponi.alekiships.common.entity.vehiclecapability;

import com.alekiponi.alekiships.common.entity.vehicle.AbstractVehicle;
import com.alekiponi.alekiships.common.entity.vehiclehelper.WindlassSwitchEntity;

import java.util.ArrayList;

public interface IHaveAnchorWindlass {

    int[] getWindlassIndices();
    default ArrayList<WindlassSwitchEntity> getWindlasses(AbstractVehicle vehicle) {
        ArrayList<WindlassSwitchEntity> list = new ArrayList<WindlassSwitchEntity>();
        if (vehicle.getPassengers().size() == vehicle.getMaxPassengers()) {
            for (int i : this.getWindlassIndices()) {
                if (vehicle.getPassengers().get(i).getFirstPassenger() instanceof WindlassSwitchEntity windlass) {
                    list.add(windlass);
                }
            }
        }
        return list;
    }

    default ArrayList<WindlassSwitchEntity> getWindlasses(){
        return getWindlasses((AbstractVehicle) this);
    }

    void tickAnchorInput();
}
