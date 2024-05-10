package com.alekiponi.alekiships.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class ClientHelper {

    @Nullable
    public static Level getLevel() {
        return Minecraft.getInstance().level;
    }

    public static void tickHopPlayersOnboard(Entity thisEntity){
        if(thisEntity.level().isClientSide()){
            final List<Entity> entitiesToHop = thisEntity.level()
                    .getEntities(thisEntity, thisEntity.getBoundingBox().inflate(0.1, -0.01, 0.1), EntitySelector.pushableBy(thisEntity));

            if (!entitiesToHop.isEmpty()) {
                for (final Entity entity : entitiesToHop) {
                    if (entity instanceof LocalPlayer player && !player.isPassenger()) {
                        if (player.input.jumping) {
                            Vec3 newPlayerPos = player.getPosition(0).multiply(1, 0, 1);
                            newPlayerPos = newPlayerPos.add(0, thisEntity.getY() + thisEntity.getBoundingBox().getYsize() + 0.05, 0);
                            newPlayerPos = newPlayerPos.add((thisEntity.getX() - newPlayerPos.x()) * 0.2, 0, (thisEntity.getZ() - newPlayerPos.z()) * 0.2);
                            player.setPos(newPlayerPos);
                        }
                    }
                }
            }
        }

    }
}