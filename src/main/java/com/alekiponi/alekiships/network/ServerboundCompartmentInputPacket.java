package com.alekiponi.alekiships.network;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.common.entity.compartment.EmptyCompartmentEntity;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

import javax.annotation.Nullable;

public final class ServerboundCompartmentInputPacket implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ServerboundCompartmentInputPacket> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(AlekiShips.MOD_ID, "compartment_input"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundCompartmentInputPacket> CODEC = StreamCodec.ofMember(
            ServerboundCompartmentInputPacket::encoder, ServerboundCompartmentInputPacket::new);

    private final boolean inputLeft;
    private final boolean inputRight;
    private final boolean inputUp;
    private final boolean inputDown;
    private final int entityId;

    public ServerboundCompartmentInputPacket(final EmptyCompartmentEntity emptyCompartment) {
        this.inputLeft = emptyCompartment.getInputLeft();
        this.inputRight = emptyCompartment.getInputRight();
        this.inputUp = emptyCompartment.getInputUp();
        this.inputDown = emptyCompartment.getInputDown();
        this.entityId = emptyCompartment.getId();
    }

    ServerboundCompartmentInputPacket(final RegistryFriendlyByteBuf buffer) {
        this.inputLeft = buffer.readBoolean();
        this.inputRight = buffer.readBoolean();
        this.inputUp = buffer.readBoolean();
        this.inputDown = buffer.readBoolean();
        this.entityId = buffer.readInt();
    }

    void encoder(final RegistryFriendlyByteBuf buffer) {
        buffer.writeBoolean(this.inputLeft);
        buffer.writeBoolean(this.inputRight);
        buffer.writeBoolean(this.inputUp);
        buffer.writeBoolean(this.inputDown);
        buffer.writeInt(this.entityId);
    }

    void handle(@Nullable final ServerPlayer player) {
        if (player == null) return;
        final Entity entity = player.level().getEntity(this.entityId);
        if (entity instanceof EmptyCompartmentEntity compartment) {
            if (player.distanceTo(compartment) < 2) {
                compartment.setInput(this.inputLeft, this.inputRight, this.inputUp, this.inputDown);
            }
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}