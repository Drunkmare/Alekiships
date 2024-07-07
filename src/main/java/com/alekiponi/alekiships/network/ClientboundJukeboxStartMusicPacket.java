package com.alekiponi.alekiships.network;

import com.alekiponi.alekiships.client.JukeboxCompartmentMusicManager;
import com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.vanilla.JukeboxCompartmentEntity;
import com.alekiponi.alekiships.util.ClientHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.level.Level;

public final class ClientboundJukeboxStartMusicPacket {

    private final int entityId;
    private final int itemId;

    public ClientboundJukeboxStartMusicPacket(final JukeboxCompartmentEntity jukeboxCompartment, final Item item) {
        this.entityId = jukeboxCompartment.getId();
        this.itemId = Item.getId(item);
    }

    ClientboundJukeboxStartMusicPacket(final FriendlyByteBuf friendlyByteBuf) {
        this.entityId = friendlyByteBuf.readVarInt();
        this.itemId = friendlyByteBuf.readVarInt();
    }

    void encoder(final FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeVarInt(this.entityId);
        friendlyByteBuf.writeVarInt(this.itemId);
    }

    void handle() {
        final Level level = ClientHelper.getLevel();
        if (level == null) return;

        final Entity entity = level.getEntity(this.entityId);

        if (!(entity instanceof JukeboxCompartmentEntity jukeboxCompartment)) return;

        final Item item = Item.byId(this.itemId);

        if (!(item instanceof RecordItem recordItem)) return;

        JukeboxCompartmentMusicManager.playMusic((jukeboxCompartment), recordItem);
    }
}