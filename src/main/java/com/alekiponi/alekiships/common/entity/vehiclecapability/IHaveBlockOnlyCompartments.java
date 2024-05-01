package com.alekiponi.alekiships.common.entity.vehiclecapability;

import com.alekiponi.alekiships.common.entity.vehicle.AbstractVehicle;
import com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.AbstractCompartmentEntity;
import net.minecraft.world.entity.Entity;

import java.util.ArrayList;

public interface IHaveBlockOnlyCompartments {
    public int[] getCanAddOnlyBlocksIndices();

    default ArrayList<AbstractCompartmentEntity> getCanAddOnlyBlocks(AbstractVehicle vehicle){
        ArrayList<AbstractCompartmentEntity> list = new ArrayList<AbstractCompartmentEntity>();
        if(vehicle.getPassengers().size() == vehicle.getMaxPassengers()) {
            for (int i : this.getCanAddOnlyBlocksIndices()) {
                if (vehicle.getPassengers().get(i).getFirstPassenger() instanceof AbstractCompartmentEntity compartment) {
                    list.add(compartment);
                }
            }
        }
        return list;
    }

    public default ArrayList<AbstractCompartmentEntity> getCanAddOnlyBlocks(){
        return getCanAddOnlyBlocks((AbstractVehicle) this);
    }

}
