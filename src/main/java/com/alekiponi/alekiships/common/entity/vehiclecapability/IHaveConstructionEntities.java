package com.alekiponi.alekiships.common.entity.vehiclecapability;

import com.alekiponi.alekiships.common.entity.vehicle.AbstractVehicle;
import com.alekiponi.alekiships.common.entity.vehiclehelper.ConstructionEntity;

import java.util.ArrayList;

public interface IHaveConstructionEntities {

    int[] getConstructionIndices();

    default ArrayList<ConstructionEntity> getConstructionEntities(AbstractVehicle vehicle) {
        ArrayList<ConstructionEntity> list = new ArrayList<ConstructionEntity>();
        if (vehicle.getPassengers().size() == vehicle.getMaxPassengers()) {
            for (int i : this.getConstructionIndices()) {
                if (vehicle.getPassengers().get(i)
                        .getFirstPassenger() instanceof ConstructionEntity constructionEntity) {
                    list.add(constructionEntity);
                }
            }
        }
        return list;
    }


    default ArrayList<ConstructionEntity> getConstructionEntities() {
        return getConstructionEntities((AbstractVehicle) this);
    }

}
