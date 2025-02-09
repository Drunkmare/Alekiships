package com.alekiponi.alekiships.common.entity.vehiclecapability;

import com.alekiponi.alekiships.common.entity.vehicle.AbstractVehicle;
import com.alekiponi.alekiships.common.entity.vehiclehelper.SailSwitchEntity;

import java.util.ArrayList;

public interface IHaveSailSwitches {
    int[] getSailSwitchIndices();

    default ArrayList<SailSwitchEntity> getSailSwitches(AbstractVehicle vehicle) {
        ArrayList<SailSwitchEntity> list = new ArrayList<SailSwitchEntity>();
        if (vehicle.getPassengers().size() == vehicle.getMaxPassengers()) {
            for (int i : this.getSailSwitchIndices()) {
                if (vehicle.getPassengers().get(i).getFirstPassenger() instanceof SailSwitchEntity switchEntity) {
                    list.add(switchEntity);
                }
            }
        }
        return list;
    }

    default ArrayList<SailSwitchEntity> getSailSwitches() {
        return getSailSwitches((AbstractVehicle) this);
    }

}
