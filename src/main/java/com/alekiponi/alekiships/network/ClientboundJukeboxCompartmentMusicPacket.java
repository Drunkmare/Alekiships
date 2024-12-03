package com.alekiponi.alekiships.network;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.client.JukeboxCompartmentMusicManager;
import com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.vanilla.JukeboxCompartmentEntity;
import com.alekiponi.alekiships.util.ClientHelper;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.JukeboxSong;
import net.minecraft.world.level.Level;

import java.util.Optional;

public record ClientboundJukeboxCompartmentMusicPacket(int entityId,
                                                       Optional<Integer> songId) implements CustomPacketPayload {

    public static final Type<ClientboundJukeboxCompartmentMusicPacket> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(AlekiShips.MOD_ID, "jukebox_compartment_music"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundJukeboxCompartmentMusicPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ClientboundJukeboxCompartmentMusicPacket::entityId,
            ByteBufCodecs.optional(ByteBufCodecs.VAR_INT), ClientboundJukeboxCompartmentMusicPacket::songId,
            ClientboundJukeboxCompartmentMusicPacket::new);

    public static ClientboundJukeboxCompartmentMusicPacket stop(final JukeboxCompartmentEntity jukeboxCompartment) {
        return new ClientboundJukeboxCompartmentMusicPacket(jukeboxCompartment.getId(), Optional.empty());
    }

    public static ClientboundJukeboxCompartmentMusicPacket start(final JukeboxCompartmentEntity jukeboxCompartment,
            final JukeboxSong value) {
        return new ClientboundJukeboxCompartmentMusicPacket(jukeboxCompartment.getId(),
                Optional.of(jukeboxCompartment.registryAccess().registryOrThrow(Registries.JUKEBOX_SONG).getId(value)));
    }

    void handle() {
        final Level level = ClientHelper.getLevel();
        if (level == null) return;

        final Entity entity = level.getEntity(this.entityId);

        if (!(entity instanceof JukeboxCompartmentEntity jukeboxCompartment)) return;

        if (this.songId.isEmpty()) {
            JukeboxCompartmentMusicManager.stopMusic(jukeboxCompartment);
            return;
        }

        level.registryAccess().registryOrThrow(Registries.JUKEBOX_SONG).getHolder(this.songId.get()).ifPresent(
                songReference -> JukeboxCompartmentMusicManager.playMusic(jukeboxCompartment, songReference));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}