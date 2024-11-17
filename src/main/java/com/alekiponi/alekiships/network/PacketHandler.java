package com.alekiponi.alekiships.network;

import com.alekiponi.alekiships.AlekiShips;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public final class PacketHandler {
    private static final String PROTOCOL_VERSION = ModList.get().getModFileById(AlekiShips.MOD_ID).versionString();

    private static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(AlekiShips.MOD_ID, "network"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals);

    public static void send(final PacketDistributor.PacketTarget target, final Object message) {
        CHANNEL.send(target, message);
    }

    public static void init() {
        int id = 0;
        CHANNEL.messageBuilder(ServerboundCompartmentInputPacket.class, id++)
                .encoder(ServerboundCompartmentInputPacket::encoder)
                .decoder(ServerboundCompartmentInputPacket::new)
                .consumerMainThread(ServerboundCompartmentInputPacket::handle)
                .add();

        CHANNEL.messageBuilder(ServerboundSwitchEntityPacket.class, id++)
                .encoder(ServerboundSwitchEntityPacket::encoder)
                .decoder(ServerboundSwitchEntityPacket::new)
                .consumerMainThread(ServerboundSwitchEntityPacket::handle)
                .add();

        CHANNEL.messageBuilder(ServerboundSloopControlPacket.class, id++)
                .encoder(ServerboundSloopControlPacket::encoder)
                .decoder(ServerboundSloopControlPacket::new)
                .consumerMainThread(ServerboundSloopControlPacket::handle)
                .add();

        CHANNEL.messageBuilder(ServerboundPickCompartmentPacket.class, id++)
                .encoder(ServerboundPickCompartmentPacket::encoder)
                .decoder(ServerboundPickCompartmentPacket::new)
                .consumerMainThread(ServerboundPickCompartmentPacket::handle)
                .add();

        CHANNEL.messageBuilder(ClientboundCleatLinkPacket.class, id++)
                .encoder(ClientboundCleatLinkPacket::encoder)
                .decoder(ClientboundCleatLinkPacket::new)
                .consumerMainThread(
                        (clientBoundCleatLinkPacket, contextSupplier) -> clientBoundCleatLinkPacket.handle())
                .add();

        CHANNEL.messageBuilder(ServerboundFlagVehicleForUpdatePacket.class, id++)
                .encoder(ServerboundFlagVehicleForUpdatePacket::encoder)
                .decoder(ServerboundFlagVehicleForUpdatePacket::new)
                .consumerMainThread(ServerboundFlagVehicleForUpdatePacket::handle)
                .add();

        CHANNEL.messageBuilder(ClientboundJukeboxStartMusicPacket.class, id++)
                .encoder(ClientboundJukeboxStartMusicPacket::encoder)
                .decoder(ClientboundJukeboxStartMusicPacket::new)
                .consumerMainThread(
                        (clientboundJukeboxStartMusicPacket, contextSupplier) -> clientboundJukeboxStartMusicPacket.handle())
                .add();

        CHANNEL.messageBuilder(ClientboundJukeboxStopMusicPacket.class, id++)
                .encoder(ClientboundJukeboxStopMusicPacket::encoder)
                .decoder(ClientboundJukeboxStopMusicPacket::new)
                .consumerMainThread(
                        (clientboundJukeboxStopMusicPacket, contextSupplier) -> clientboundJukeboxStopMusicPacket.handle())
                .add();
    }
}