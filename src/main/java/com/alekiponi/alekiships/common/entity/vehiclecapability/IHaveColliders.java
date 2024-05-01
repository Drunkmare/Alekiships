package com.alekiponi.alekiships.common.entity.vehiclecapability;

import com.alekiponi.alekiships.common.entity.vehicle.AbstractVehicle;
import com.alekiponi.alekiships.common.entity.vehiclehelper.VehicleCleatEntity;
import com.alekiponi.alekiships.common.entity.vehiclehelper.VehicleCollisionEntity;
import net.minecraft.world.entity.Entity;

import java.util.ArrayList;

public interface IHaveColliders {

    public abstract int[] getColliderIndices();

    default ArrayList<VehicleCollisionEntity> getColliders(AbstractVehicle vehicle) {
        ArrayList<VehicleCollisionEntity> list = new ArrayList<VehicleCollisionEntity>();
        if (vehicle.getPassengers().size() == vehicle.getMaxPassengers()) {
            for (int i : this.getColliderIndices()) {
                if (vehicle.getPassengers().get(i).getFirstPassenger() instanceof VehicleCollisionEntity collider) {
                    list.add(collider);
                }
            }
        }
        return list;
    }

    public default ArrayList<VehicleCollisionEntity> getColliders(){
        return getColliders((AbstractVehicle) this);
    }

}
