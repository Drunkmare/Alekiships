package com.alekiponi.alekiships.common.entity.vehiclecapability;

import com.alekiponi.alekiships.common.entity.vehicle.AbstractVehicle;
import com.alekiponi.alekiships.common.entity.vehiclehelper.MastEntity;
import com.alekiponi.alekiships.common.entity.vehiclehelper.VehicleCleatEntity;
import com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.AbstractCompartmentEntity;

import java.util.ArrayList;

public interface IHaveMasts {

    public int[] getMastIndices();

    public default ArrayList<MastEntity> getMasts(AbstractVehicle vehicle) {
        ArrayList<MastEntity> list = new ArrayList<MastEntity>();
        if (vehicle.getPassengers().size() == vehicle.getMaxPassengers()) {
            for (int i : this.getMastIndices()) {
                if (vehicle.getPassengers().get(i).getFirstPassenger() instanceof MastEntity mast) {
                    list.add(mast);
                }
            }
        }
        return list;
    }

    default ArrayList<MastEntity> getMasts(){
        return getMasts((AbstractVehicle) this);
    }

}
