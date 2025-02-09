package com.alekiponi.alekiships.util;

import com.alekiponi.alekiships.common.entity.vehicle.AbstractVehicle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import org.jetbrains.annotations.Nullable;

public final class ClientHelper {

    @Nullable
    public static Level getLevel() {
        return Minecraft.getInstance().level;
    }

    public static void tickHopPlayersOnboard(Entity thisEntity) {
        if (thisEntity.level().isClientSide()) {
            final List<Entity> entitiesToHop = thisEntity.level()
                    .getEntities(thisEntity, thisEntity.getBoundingBox().inflate(0.1, -0.01, 0.1),
                            EntitySelector.pushableBy(thisEntity));

            if (!entitiesToHop.isEmpty()) {
                for (final Entity entity : entitiesToHop) {
                    if (entity instanceof LocalPlayer player && !player.isPassenger()) {
                        if (player.input.jumping) {
                            Vec3 newPlayerPos = player.getPosition(0).multiply(1, 0, 1);
                            newPlayerPos = newPlayerPos.add(0,
                                    thisEntity.getY() + thisEntity.getBoundingBox().getYsize() + 0.05, 0);
                            newPlayerPos = newPlayerPos.add((thisEntity.getX() - newPlayerPos.x()) * 0.2, 0,
                                    (thisEntity.getZ() - newPlayerPos.z()) * 0.2);
                            player.setPos(newPlayerPos);
                        }
                    }
                }
            }
        }

    }

    public static void tickTakeClientPlayersForARide(AbstractVehicle vehicle, Entity entity) {
        if (vehicle.level()
                .isClientSide() && entity instanceof LocalPlayer player && !entity.isPassenger() && (Math.abs(
                vehicle.getBoundingBox().maxY - player.getBoundingBox().minY) < 0.01)) {
            if (vehicle.getSmoothSpeedMS() > 4) {
                if (!player.input.jumping || Math.abs(player.getDeltaMovement().y) > 0.05) {
                    player.setPos(vehicle.getDismountLocationForPassenger(player));
                }
                //player.setPos(player.getPosition(0).add(this.getDeltaMovement()));
            } else {
                if (player.input.jumping || player.input.left || player.input.right || player.input.up || player.input.down) {
                    player.setDeltaMovement(player.getDeltaMovement().multiply(1.0, 1, 1.0)
                            .add(vehicle.getDeltaMovement().multiply(0.45, 0, 0.45)));
                } else {
                    player.setPos(player.getPosition(0).add(vehicle.getDeltaMovement()));
                    if (player.getDeltaMovement().length() > vehicle.getDeltaMovement().length() + 0.01) {
                        player.setDeltaMovement(Vec3.ZERO);
                    }
                }
            }

        }
    }
}