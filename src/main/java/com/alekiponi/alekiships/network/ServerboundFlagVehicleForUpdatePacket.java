package com.alekiponi.alekiships.network;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.common.entity.vehicle.AbstractVehicle;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nullable;

public final class ServerboundFlagVehicleForUpdatePacket implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ServerboundFlagVehicleForUpdatePacket> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(AlekiShips.MOD_ID, "vehicle_passenger_update_flag"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundFlagVehicleForUpdatePacket> CODEC = StreamCodec.ofMember(
            ServerboundFlagVehicleForUpdatePacket::encoder, ServerboundFlagVehicleForUpdatePacket::new);

    private final boolean flag;
    private final int entityId;

    public ServerboundFlagVehicleForUpdatePacket(final boolean flag, final AbstractVehicle vehicle) {
        this.flag = flag;
        this.entityId = vehicle.getId();
    }

    ServerboundFlagVehicleForUpdatePacket(final FriendlyByteBuf buffer) {
        this.flag = buffer.readBoolean();
        this.entityId = buffer.readInt();
    }

    void encoder(final FriendlyByteBuf buffer) {
        buffer.writeBoolean(this.flag);
        buffer.writeInt(this.entityId);
    }

    void handle(@Nullable final ServerPlayer player) {
        if (player == null) return;

        if (player.level().getEntity(this.entityId) instanceof final AbstractVehicle vehicle) {
            vehicle.setFlaggedForPassengerUpdate(this.flag);
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}