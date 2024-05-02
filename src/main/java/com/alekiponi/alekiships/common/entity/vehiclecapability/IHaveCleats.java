package com.alekiponi.alekiships.common.entity.vehiclecapability;

import com.alekiponi.alekiships.common.entity.vehicle.AbstractVehicle;
import com.alekiponi.alekiships.common.entity.vehiclehelper.VehicleCleatEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;

public interface IHaveCleats {
    int[] getCleatIndices();

    //int[][] getCleatRotations();

    private ArrayList<VehicleCleatEntity> getCleats(AbstractVehicle vehicle) {
        ArrayList<VehicleCleatEntity> list = new ArrayList<VehicleCleatEntity>();
        if (vehicle.getPassengers().size() == vehicle.getMaxPassengers()) {
            for (int i : this.getCleatIndices()) {
                if (vehicle.getPassengers().get(i).getFirstPassenger() instanceof VehicleCleatEntity cleat) {
                    list.add(cleat);
                }
            }
        }
        return list;
    }

    default ArrayList<VehicleCleatEntity> getCleats() {
        return getCleats((AbstractVehicle) this);
    }

    default boolean isBeingTowed(AbstractVehicle vehicle) {
        if (vehicle.getPassengers().size() == vehicle.getMaxPassengers()) {
            for (VehicleCleatEntity cleat : this.getCleats()) {
                return cleat.isLeashed() && vehicle.getDeltaMovement().length() != 0;
            }
        }
        return false;
    }

    default boolean isBeingTowed() {
        return isBeingTowed((AbstractVehicle) this);
    }


    default void tickCleatInput() {
        AbstractVehicle vehicle = (AbstractVehicle)this;
        for (VehicleCleatEntity cleat : this.getCleats()) {
            if (cleat.isLeashed()) {
                net.minecraft.world.entity.Entity leashHolder = cleat.getLeashHolder();
                if (leashHolder != null) {
                    if (leashHolder instanceof Player player) {
                        if (vehicle.collectEntitesToTakeWith().contains(player)) {
                            return;
                        }
                        if (cleat.distanceTo(leashHolder) > 4f) {
                            Vec3 vectorToVehicle = leashHolder.getPosition(0).vectorTo(cleat.getPosition(0)).normalize();
                            Vec3 movementVector = new Vec3(vectorToVehicle.x * -0.04f, vehicle.getDeltaMovement().y,
                                    vectorToVehicle.z * -0.04f);
                            double vehicleSize = Mth.clamp(vehicle.getBbWidth(), 1, 100);
                            movementVector = movementVector.multiply(1 / vehicleSize, 0, 1 / vehicleSize);

                            double d0 = leashHolder.getPosition(0).x - vehicle.getX();
                            double d2 = leashHolder.getPosition(0).z - vehicle.getZ();

                            float finalRotation = Mth.wrapDegrees(
                                    (float) (Mth.atan2(d2, d0) * (double) (180F / (float) Math.PI)) - 90.0F);

                            double difference = (leashHolder.getY()) - vehicle.getY();


                            if (leashHolder.getY() > vehicle.getY() && difference >= 0.4 && difference <= 1.0 && vehicle.getDeltaMovement()
                                    .length() < 0.02f) {
                                vehicle.setPos(vehicle.getX(), vehicle.getY() + 0.55f, vehicle.getZ());
                            }

                            float approach = Mth.approachDegrees(vehicle.getYRot(), finalRotation, 6);

                            vehicle.setDeltaMovement(vehicle.getDeltaMovement().add(movementVector));
                            vehicle.setDeltaRotation(-1 * (vehicle.getYRot() - approach));

                        }
                    }
                    if (leashHolder instanceof HangingEntity && vehicle.getStatus() != AbstractVehicle.MediumStatus.ON_LAND) {
                        Vec3 vectorToVehicle = leashHolder.getPosition(0).vectorTo(cleat.getPosition(0)).normalize();
                        Vec3 movementVector = new Vec3(vectorToVehicle.x * -0.005f, vehicle.getDeltaMovement().y,
                                vectorToVehicle.z * -0.005f);
                        double d0 = leashHolder.getPosition(0).x - vehicle.getX();
                        double d2 = leashHolder.getPosition(0).z - vehicle.getZ();

                        float finalRotation = Mth.wrapDegrees(
                                (float) (Mth.atan2(d2, d0) * (double) (180F / (float) Math.PI)) - 90.0F);

                        float approach = Mth.approachDegrees(vehicle.getYRot(), finalRotation, 0.5f);
                        if (Mth.degreesDifferenceAbs(vehicle.getYRot(), finalRotation) < 4) {
                            vehicle.setDeltaRotation(0);
                            vehicle.setYRot(vehicle.getYRot());
                        } else {
                            vehicle.setDeltaRotation(-1 * (vehicle.getYRot() - approach));
                        }
                        if (cleat.distanceTo(leashHolder) > 2) {
                            vehicle.setDeltaMovement(movementVector);
                        } else {
                            Player player = vehicle.level().getNearestPlayer(vehicle, 6 * 16);
                            vehicle.setDeltaMovement(Vec3.ZERO);
                        }


                    }
                }

            }
        }

    }
}
