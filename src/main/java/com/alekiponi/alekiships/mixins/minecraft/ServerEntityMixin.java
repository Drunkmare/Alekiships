package com.alekiponi.alekiships.mixins.minecraft;

import com.alekiponi.alekiships.common.entity.vehicle.AbstractVehicle;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

@Mixin(ServerEntity.class)
public abstract class ServerEntityMixin {
    @Shadow
    private final Entity entity;

    @Shadow
    private final ServerLevel level;
    @Shadow
    private List<Entity> lastPassengers = Collections.emptyList();
    @Shadow
    private final Consumer<Packet<?>> broadcast;


    protected ServerEntityMixin(Entity entity, ServerLevel level, Consumer<Packet<?>> broadcast) {
        this.entity = entity;
        this.level = level;
        this.broadcast = broadcast;
    }


    @Inject(method = "sendChanges", at = @At(value = "HEAD"), cancellable = true)
    public void injectWaitToSendChanges(CallbackInfo ci) {
        if (this.entity instanceof AbstractVehicle vehicle){
            if (!vehicle.hasAllParts() && vehicle.isFunctional()){
                ci.cancel();
            }
        }
    }

    @Inject(method = "sendChanges", at = @At(value = "HEAD"))
    public void injectUpdatePassengersForFlaggedVehicles(CallbackInfo ci) {
        if (this.entity instanceof AbstractVehicle vehicle){
            if (vehicle.isFlaggedForPassengerUpdate() && vehicle.isFunctional()) {
                List<Entity> list = vehicle.getPassengers();
                this.broadcast.accept(new ClientboundSetPassengersPacket(this.entity));
                removedPassengers(list, this.lastPassengers).forEach((player) -> {
                    if (player instanceof ServerPlayer serverPlayer) {
                        serverPlayer.connection.teleport(serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ(), serverPlayer.getYRot(), serverPlayer.getXRot());
                    }

                });
                this.lastPassengers = list;
                vehicle.setFlaggedForPassengerUpdate(false);
            }
        }
    }

    @Shadow
    private static Stream<Entity> removedPassengers(List<Entity> pInitialPassengers, List<Entity> pCurrentPassengers) {
        return pCurrentPassengers.stream().filter((p_275361_) -> {
            return !pInitialPassengers.contains(p_275361_);
        });
    }
}
