package com.alekiponi.alekiships.common.entity.vehiclecapability;

import com.alekiponi.alekiships.common.entity.vehicle.AbstractVehicle;
import com.alekiponi.alekiships.common.entity.vehiclehelper.VehicleColliderEntity;

import java.util.ArrayList;

public interface IHaveColliders {

    public abstract int[] getColliderIndices();

    default ArrayList<VehicleColliderEntity> getColliders(AbstractVehicle vehicle) {
        ArrayList<VehicleColliderEntity> list = new ArrayList<VehicleColliderEntity>();
        if (vehicle.getPassengers().size() == vehicle.getMaxPassengers()) {
            for (int i : this.getColliderIndices()) {
                if (vehicle.getPassengers().get(i).getFirstPassenger() instanceof VehicleColliderEntity collider) {
                    list.add(collider);
                }
            }
        }
        return list;
    }

    public default ArrayList<VehicleColliderEntity> getColliders(){
        return getColliders((AbstractVehicle) this);
    }

}
