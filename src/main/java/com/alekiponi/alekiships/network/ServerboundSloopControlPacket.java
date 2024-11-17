package com.alekiponi.alekiships.network;

import com.alekiponi.alekiships.common.entity.vehicle.SloopEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ServerboundSloopControlPacket {
    private final float sheetLength;
    private final float boomRotation;
    private final float rudderAngle;
    private final int entityID;

    public ServerboundSloopControlPacket(float sheetLength, float boomRotation, float rudderAngle, int entityID) {
        this.sheetLength = sheetLength;
        this.boomRotation = boomRotation;
        this.rudderAngle = rudderAngle;
        this.entityID = entityID;
    }

    public ServerboundSloopControlPacket(FriendlyByteBuf buffer){
        this.sheetLength = buffer.readFloat();
        this.boomRotation = buffer.readFloat();
        this.rudderAngle = buffer.readFloat();
        this.entityID = buffer.readInt();
    }

    public void encoder(FriendlyByteBuf buffer){
        buffer.writeFloat(this.sheetLength);
        buffer.writeFloat(this.boomRotation);
        buffer.writeFloat(this.rudderAngle);
        buffer.writeInt(this.entityID);
    }

    public void handle(Supplier<NetworkEvent.Context> context){
        context.get().enqueueWork(() -> {
            Entity entity = context.get().getSender().level().getEntity(this.entityID);
            ServerPlayer player = context.get().getSender();
            if(entity instanceof SloopEntity sloop){
                assert player != null;
                if (player.distanceTo(sloop) < 5) {
                    sloop.setMainsheetLength(this.sheetLength);
                    sloop.setMainBoomRotation(this.boomRotation);
                    sloop.setRudderRotation(this.rudderAngle);
                }
            }
        });
    }
}