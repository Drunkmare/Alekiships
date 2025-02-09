package com.alekiponi.alekiships.network;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.common.entity.vehicle.SloopEntity;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nullable;

public final class ServerboundSloopControlPacket implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ServerboundSloopControlPacket> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(AlekiShips.MOD_ID, "control_sloop"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundSloopControlPacket> CODEC = StreamCodec.ofMember(
            ServerboundSloopControlPacket::encoder, ServerboundSloopControlPacket::new);

    private final float sheetLength;
    private final float boomRotation;
    private final float rudderAngle;
    private final int entityId;

    public ServerboundSloopControlPacket(final SloopEntity sloop) {
        this.sheetLength = sloop.getMainsheetLength();
        this.boomRotation = sloop.getMainBoomRotation();
        this.rudderAngle = sloop.getRudderRotation();
        this.entityId = sloop.getId();
    }

    ServerboundSloopControlPacket(final FriendlyByteBuf buffer) {
        this.sheetLength = buffer.readFloat();
        this.boomRotation = buffer.readFloat();
        this.rudderAngle = buffer.readFloat();
        this.entityId = buffer.readInt();
    }

    void encoder(final FriendlyByteBuf buffer) {
        buffer.writeFloat(this.sheetLength);
        buffer.writeFloat(this.boomRotation);
        buffer.writeFloat(this.rudderAngle);
        buffer.writeInt(this.entityId);
    }

    void handle(final @Nullable ServerPlayer player) {
        if (player == null) return;
        if (!(player.level().getEntity(this.entityId) instanceof SloopEntity sloop)) return;
        if (player.distanceTo(sloop) < 5) {
            sloop.setMainsheetLength(this.sheetLength);
            sloop.setMainBoomRotation(this.boomRotation);
            sloop.setRudderRotation(this.rudderAngle);
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}