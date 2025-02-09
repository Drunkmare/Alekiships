package com.alekiponi.alekiships.common.entity.vehiclecapability;

import com.alekiponi.alekiships.common.entity.CannonEntity;
import com.alekiponi.alekiships.common.entity.compartment.AbstractCompartmentEntity;
import com.alekiponi.alekiships.common.entity.vehicle.AbstractVehicle;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

import static com.alekiponi.alekiships.util.advancements.AlekiShipsAdvancements.FULL_BROADSIDE;

public interface ICannonable {

    int[] getCanAddCannonsIndices();

    default ArrayList<CannonEntity> getCannons(AbstractVehicle vehicle) {
        ArrayList<CannonEntity> list = new ArrayList<CannonEntity>();
        for (AbstractCompartmentEntity compartment : getCanAddCannons()) {
            if (compartment.getFirstPassenger() instanceof CannonEntity cannonEntity) {
                list.add(cannonEntity);
            }
        }
        return list;
    }

    default ArrayList<AbstractCompartmentEntity> getCanAddCannons(AbstractVehicle vehicle) {
        ArrayList<AbstractCompartmentEntity> list = new ArrayList<AbstractCompartmentEntity>();
        if (vehicle.getPassengers().size() == vehicle.getMaxPassengers()) {
            for (int i : this.getCanAddCannonsIndices()) {
                if (vehicle.getPassengers().get(i)
                        .getFirstPassenger() instanceof AbstractCompartmentEntity compartment) {
                    list.add(compartment);
                }
            }
        }
        return list;
    }

    default ArrayList<AbstractCompartmentEntity> getCanAddCannons() {
        return getCanAddCannons((AbstractVehicle) this);
    }

    default ArrayList<CannonEntity> getCannons() {
        return getCannons((AbstractVehicle) this);
    }

    default void checkIfRecentlyFiredBroadside() {
        if (((AbstractVehicle) this).level().isClientSide()) {
            return;
        }
        ArrayList<CannonEntity> list = getCannons();
        if (list.size() < broadsideCount()) {
            return;
        }
        ArrayList<CannonEntity> firedCannons = new ArrayList<>();
        for (CannonEntity cannon : list) {
            if (cannon.recentlyFired()) {
                firedCannons.add(cannon);
            }
        }
        if (firedCannons.size() < broadsideCount()) {
            return;
        }
        float rYot = firedCannons.get(0).getYRot();
        firedCannons.removeIf(cannon -> cannon.getYRot() != rYot);
        if (firedCannons.size() < broadsideCount()) {
            return;
        }

        List<Player> playersAboard = ((AbstractVehicle) this).collectPlayerPassengers();
        for (Player player : playersAboard) {
            if (player instanceof ServerPlayer serverPlayer) {
                FULL_BROADSIDE.trigger(serverPlayer);
            }
        }

        playersAboard = ((AbstractVehicle) this).collectPlayersToTakeWith();
        for (Player player : playersAboard) {
            if (player instanceof ServerPlayer serverPlayer) {
                FULL_BROADSIDE.trigger(serverPlayer);
            }
        }

    }

    int broadsideCount();
}
