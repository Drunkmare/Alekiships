package com.alekiponi.alekiships.network;

import com.alekiponi.alekiships.util.ClientHelper;
import com.alekiponi.alekiships.common.entity.vehiclehelper.CleatEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundSetEntityLinkPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

/**
 * Replicates {@link ClientboundSetEntityLinkPacket} for {@link CleatEntity}.
 */
public class ClientboundCleatLinkPacket {
    private final int cleatId;
    private final int designationId;

    public ClientboundCleatLinkPacket(final CleatEntity cleat, @Nullable final Entity designation) {
        this.cleatId = cleat.getId();
        this.designationId = designation != null ? designation.getId() : 0;
    }

    public ClientboundCleatLinkPacket(final FriendlyByteBuf byteBuf) {
        this.cleatId = byteBuf.readInt();
        this.designationId = byteBuf.readInt();
    }


    public void encoder(final FriendlyByteBuf byteBuf) {
        byteBuf.writeInt(this.cleatId);
        byteBuf.writeInt(this.designationId);
    }

    public void handle() {
        final Level level = ClientHelper.getLevel();

        if (level == null) return;

        if (level.getEntity(this.cleatId) instanceof CleatEntity cleat) {
            cleat.setDelayedLeashHolderId(this.designationId);
        }
    }
}
