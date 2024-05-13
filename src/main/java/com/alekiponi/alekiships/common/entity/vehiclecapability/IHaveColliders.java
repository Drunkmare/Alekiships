package com.alekiponi.alekiships.common.entity.vehiclecapability;

import com.alekiponi.alekiships.common.entity.vehicle.AbstractVehicle;
import com.alekiponi.alekiships.common.entity.vehiclehelper.ColliderEntity;

import java.util.ArrayList;

public interface IHaveColliders {

    public abstract int[] getColliderIndices();

    default ArrayList<ColliderEntity> getColliders(AbstractVehicle vehicle) {
        ArrayList<ColliderEntity> list = new ArrayList<ColliderEntity>();
        if (vehicle.getPassengers().size() == vehicle.getMaxPassengers()) {
            for (int i : this.getColliderIndices()) {
                if (vehicle.getPassengers().get(i).getFirstPassenger() instanceof ColliderEntity collider) {
                    list.add(collider);
                }
            }
        }
        return list;
    }

    public default ArrayList<ColliderEntity> getColliders(){
        return getColliders((AbstractVehicle) this);
    }

}
