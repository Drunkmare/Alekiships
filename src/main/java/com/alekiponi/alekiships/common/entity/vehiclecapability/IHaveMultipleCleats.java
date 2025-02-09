package com.alekiponi.alekiships.common.entity.vehiclecapability;

import com.alekiponi.alekiships.common.entity.vehicle.AbstractVehicle;
import com.alekiponi.alekiships.common.entity.vehiclehelper.CleatEntity;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;

public interface IHaveMultipleCleats extends IHaveCleats {
    @Override
    default void tickCleatInput() {

        AbstractVehicle vehicle = (AbstractVehicle) this;

        int count = 0;
        ArrayList<CleatEntity> cleats = this.getCleats();
        ArrayList<CleatEntity> leashedCleats = new ArrayList<CleatEntity>();
        for (CleatEntity cleat : cleats) {
            if (cleat.isLeashed() && !vehicle.collectEntitesToTakeWith().contains(cleat.getLeashHolder())) {
                leashedCleats.add(cleat);
                count++;
            }
        }
        if (count == 2) {
            CleatEntity cleat1 = leashedCleats.get(0);
            CleatEntity cleat2 = leashedCleats.get(1);
            net.minecraft.world.entity.Entity leashHolder1 = cleat1.getLeashHolder();
            net.minecraft.world.entity.Entity leashHolder2 = cleat2.getLeashHolder();
            if (leashHolder1 != null && leashHolder2 != null) {
                if (leashHolder1.is(leashHolder2)) {
                    count = 1;
                } else {
                    double d0 = leashHolder1.getX() - leashHolder2.getX();
                    double d2 = leashHolder1.getZ() - leashHolder2.getZ();

                    float finalRotation = Mth.wrapDegrees(
                            (float) (Mth.atan2(d2, d0) * (double) (180F / (float) Math.PI)) - 90.0F);

                    float approach = Mth.approachDegrees(vehicle.getYRot(), finalRotation, 0.25f);
                    if (Mth.degreesDifferenceAbs(vehicle.getYRot(), finalRotation) < 1.0) {
                        vehicle.setDeltaRotation(0);
                        vehicle.setYRot(vehicle.getYRot());
                    } else {
                        vehicle.setDeltaRotation(-1 * (vehicle.getYRot() - approach));
                    }

                    Vec3 averageLeashHolderPosition =
                            new Vec3(
                                    (leashHolder1.getPosition(0).x + leashHolder2.getPosition(0).x) / 2.0,
                                    0,
                                    (leashHolder1.getPosition(0).z + leashHolder2.getPosition(0).z) / 2.0);

                    Vec3 averageCleatPosition = new Vec3(
                            (cleat1.getPosition(0).x + cleat2.getPosition(0).x) / 2.0,
                            0,
                            (cleat1.getPosition(0).z + cleat2.getPosition(0).z) / 2.0);

                    Vec3 vectorToVehicle = averageCleatPosition.vectorTo(averageLeashHolderPosition).normalize();

                    Vec3 movementVector = vectorToVehicle.multiply(0.04, 0, 0.04)
                            .add(0, vehicle.getDeltaMovement().y, 0);

                    if (averageCleatPosition.distanceTo(averageLeashHolderPosition) > 1.0) {
                        vehicle.setDeltaMovement(movementVector);
                    } else {
                        vehicle.setDeltaMovement(Vec3.ZERO);
                    }

                }
            }

        }
        if (count == 1) {
            CleatEntity cleat = leashedCleats.get(0);
            net.minecraft.world.entity.Entity leashHolder = cleat.getLeashHolder();
            if (leashHolder != null) {
                if (leashHolder instanceof Player) {
                    if (vehicle.distanceTo(leashHolder) > 4f) {
                        Vec3 vectorToVehicle = leashHolder.getPosition(0).vectorTo(vehicle.getPosition(0)).normalize();
                        Vec3 movementVector = new Vec3(vectorToVehicle.x * -0.03f, vehicle.getDeltaMovement().y,
                                vectorToVehicle.z * -0.03f);
                        double vehicleSize = Mth.clamp(vehicle.getBbWidth(), 1, 100);
                        movementVector = movementVector.multiply(1 / vehicleSize, 0, 1 / vehicleSize);
                        movementVector = movementVector.multiply(getCleatMovementMultiplier(), 0,
                                getCleatMovementMultiplier());
                        vehicle.setDeltaMovement(movementVector);

                    } else {
                        vehicle.setDeltaMovement(Vec3.ZERO);
                    }
                }
                if (leashHolder instanceof HangingEntity) {
                    Vec3 vectorToVehicle = leashHolder.getPosition(0).vectorTo(vehicle.getPosition(0)).normalize();
                    Vec3 movementVector = new Vec3(vectorToVehicle.x * -0.001f, vehicle.getDeltaMovement().y,
                            vectorToVehicle.z * -0.001f);
                    movementVector = movementVector.multiply(getCleatMovementMultiplier(), 0,
                            getCleatMovementMultiplier());

                    if (cleat.distanceTo(leashHolder) > 1) {
                        vehicle.setDeltaMovement(movementVector);
                    } else {
                        vehicle.setDeltaMovement(Vec3.ZERO);
                    }


                }
            }
        }
        if (count != 1 && count != 2) {
            for (CleatEntity cleat : leashedCleats) {
                net.minecraft.world.entity.Entity leashHolder = cleat.getLeashHolder();
                if (leashHolder != null) {
                    Vec3 vectorToVehicle = leashHolder.getPosition(0).vectorTo(vehicle.getPosition(0)).normalize();
                    Vec3 movementVector = new Vec3(vectorToVehicle.x * -0.01f / count, vehicle.getDeltaMovement().y,
                            vectorToVehicle.z * -0.01f / count);
                    movementVector = movementVector.multiply(getCleatMovementMultiplier(), 0,
                            getCleatMovementMultiplier());
                    if (cleat.distanceTo(leashHolder) > 1) {
                        vehicle.setDeltaMovement(movementVector);
                    } else {
                        vehicle.setDeltaMovement(Vec3.ZERO);
                    }
                }
            }
        }

    }
}
