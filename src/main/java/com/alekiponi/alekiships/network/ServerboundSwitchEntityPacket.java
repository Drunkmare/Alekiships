package com.alekiponi.alekiships.network;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.common.entity.vehiclehelper.AbstractSwitchEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nullable;

public final class ServerboundSwitchEntityPacket implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ServerboundSwitchEntityPacket> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(AlekiShips.MOD_ID, "switch_entity"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundSwitchEntityPacket> CODEC = StreamCodec.ofMember(
            ServerboundSwitchEntityPacket::encoder, ServerboundSwitchEntityPacket::new);

    private final boolean switched;
    private final int entityId;

    public ServerboundSwitchEntityPacket(final boolean switched, final AbstractSwitchEntity switchEntity) {
        this.switched = switched;
        this.entityId = switchEntity.getId();
    }

    ServerboundSwitchEntityPacket(final FriendlyByteBuf buffer) {
        this.switched = buffer.readBoolean();
        this.entityId = buffer.readInt();
    }

    void encoder(final FriendlyByteBuf buffer) {
        buffer.writeBoolean(this.switched);
        buffer.writeInt(this.entityId);
    }

    void handle(@Nullable final ServerPlayer player) {
        if (player == null) return;
        if (player.level().getEntity(this.entityId) instanceof final AbstractSwitchEntity abstractSwitchEntity) {
            if (!(abstractSwitchEntity.distanceTo(player) < 10)) return;

            abstractSwitchEntity.setSwitched(this.switched);
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}