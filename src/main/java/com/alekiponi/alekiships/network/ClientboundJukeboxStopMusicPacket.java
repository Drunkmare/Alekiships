package com.alekiponi.alekiships.network;

import com.alekiponi.alekiships.client.JukeboxCompartmentMusicManager;
import com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.vanilla.JukeboxCompartmentEntity;
import com.alekiponi.alekiships.util.ClientHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

// TODO, this doesn't appear to be needed anymore
public final class ClientboundJukeboxStopMusicPacket {

    private final int entityId;

    public ClientboundJukeboxStopMusicPacket(final JukeboxCompartmentEntity jukeboxCompartment) {
        this.entityId = jukeboxCompartment.getId();
    }

    ClientboundJukeboxStopMusicPacket(final FriendlyByteBuf friendlyByteBuf) {
        this.entityId = friendlyByteBuf.readVarInt();
    }

    void encoder(final FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeVarInt(this.entityId);
    }

    void handle() {
        final Level level = ClientHelper.getLevel();
        if (level == null) return;

        final Entity entity = level.getEntity(this.entityId);

        if (!(entity instanceof JukeboxCompartmentEntity jukeboxCompartment)) return;

        JukeboxCompartmentMusicManager.stopMusic((jukeboxCompartment));
    }
}