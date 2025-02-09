package com.alekiponi.alekiships.common.entity.vehiclecapability;

import com.alekiponi.alekiships.common.entity.compartment.AbstractCompartmentEntity;
import com.alekiponi.alekiships.common.entity.vehicle.AbstractVehicle;

import java.util.ArrayList;

public interface IHaveCompartments {
    int[] getCompartmentIndices();

    // TODO make this into generic helper rotation
    int[][] getCompartmentRotationsArray();

    int getCompartmentRotation(int i);

    private ArrayList<AbstractCompartmentEntity> getCompartments(AbstractVehicle vehicle) {
        ArrayList<AbstractCompartmentEntity> list = new ArrayList<AbstractCompartmentEntity>();
        if (vehicle.getPassengers().size() == vehicle.getMaxPassengers()) {
            for (int i : this.getCompartmentIndices()) {
                if (vehicle.getPassengers().get(i)
                        .getFirstPassenger() instanceof AbstractCompartmentEntity compartment) {
                    list.add(compartment);
                }
            }
        }
        return list;
    }

    default ArrayList<AbstractCompartmentEntity> getCompartments() {
        return getCompartments((AbstractVehicle) this);
    }
}
