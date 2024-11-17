package com.alekiponi.alekiships.network;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.common.entity.vehiclehelper.CleatEntity;
import com.alekiponi.alekiships.util.ClientHelper;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.game.ClientboundSetEntityLinkPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Leashable;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

/**
 * Replicates {@link ClientboundSetEntityLinkPacket} for {@link CleatEntity}.
 * TODO is this still needed? {@link Leashable}
 */
public record ClientboundCleatLinkPacket(int cleatId, int designationId) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ClientboundCleatLinkPacket> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(AlekiShips.MOD_ID, "link_cleat"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundCleatLinkPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ClientboundCleatLinkPacket::cleatId, ByteBufCodecs.VAR_INT,
            ClientboundCleatLinkPacket::designationId, ClientboundCleatLinkPacket::new);

    public ClientboundCleatLinkPacket(final CleatEntity cleat, @Nullable final Entity designation) {
        this(cleat.getId(), designation != null ? designation.getId() : 0);
    }

    void handle() {
        final Level level = ClientHelper.getLevel();

        if (level == null) return;

        if (level.getEntity(this.cleatId) instanceof CleatEntity cleat) {
            cleat.setDelayedLeashHolderId(this.designationId);
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}