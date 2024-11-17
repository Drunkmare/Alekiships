package com.alekiponi.alekiships.network;

import com.alekiponi.alekiships.common.entity.vehicle.AbstractVehicle;
import com.alekiponi.alekiships.common.entity.vehiclehelper.AbstractSwitchEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;
public class ServerboundFlagVehicleForUpdatePacket {
    private final boolean flag;
    private final int entityID;

    public ServerboundFlagVehicleForUpdatePacket(boolean flag, int entityID) {
        this.flag = flag;
        this.entityID = entityID;
    }

    public ServerboundFlagVehicleForUpdatePacket(FriendlyByteBuf buffer) {
        this.flag = buffer.readBoolean();
        this.entityID = buffer.readInt();
    }

    public void encoder(FriendlyByteBuf buffer) {
        buffer.writeBoolean(this.flag);
        buffer.writeInt(this.entityID);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            Entity entity = context.get().getSender().level().getEntity(this.entityID);
            if(entity instanceof AbstractVehicle) {
                ((AbstractVehicle) entity).setFlaggedForPassengerUpdate(this.flag);
            }
        });
    }
}
