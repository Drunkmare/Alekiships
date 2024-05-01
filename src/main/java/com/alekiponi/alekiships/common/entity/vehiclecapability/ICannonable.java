package com.alekiponi.alekiships.common.entity.vehiclecapability;

import com.alekiponi.alekiships.common.entity.vehicle.AbstractVehicle;
import com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.AbstractCompartmentEntity;

import java.util.ArrayList;

public interface ICannonable {

    public int[] getCanAddCannonsIndices();

    default ArrayList<AbstractCompartmentEntity> getCanAddCannons(AbstractVehicle vehicle){
        ArrayList<AbstractCompartmentEntity> list = new ArrayList<AbstractCompartmentEntity>();
        if(vehicle.getPassengers().size() == vehicle.getMaxPassengers()) {
            for (int i : this.getCanAddCannonsIndices()) {
                if (vehicle.getPassengers().get(i).getFirstPassenger() instanceof AbstractCompartmentEntity compartment) {
                    list.add(compartment);
                }
            }
        }
        return list;
    }

    public default ArrayList<AbstractCompartmentEntity> getCanAddCannons(){
        return getCanAddCannons((AbstractVehicle) this);
    }
}
