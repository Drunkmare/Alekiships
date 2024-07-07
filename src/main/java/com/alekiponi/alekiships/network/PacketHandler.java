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

        CHANNEL.messageBuilder(ServerBoundSloopPacket.class, id++)
                .encoder(ServerBoundSloopPacket::encoder)
                .decoder(ServerBoundSloopPacket::new)
                .consumerMainThread(ServerBoundSloopPacket::handle)
                .add();

        CHANNEL.messageBuilder(ServerBoundPickCompartmentPacket.class, id++)
                .encoder(ServerBoundPickCompartmentPacket::encoder)
                .decoder(ServerBoundPickCompartmentPacket::new)
                .consumerMainThread(ServerBoundPickCompartmentPacket::handle)
                .add();

        CHANNEL.messageBuilder(ClientBoundCleatLinkPacket.class, id++)
                .encoder(ClientBoundCleatLinkPacket::encoder)
                .decoder(ClientBoundCleatLinkPacket::new)
                .consumerMainThread(
                        (clientBoundCleatLinkPacket, contextSupplier) -> clientBoundCleatLinkPacket.handle())
                .add();

        CHANNEL.messageBuilder(ServerBoundFlagVehicleForUpdatePacket.class, id++)
                .encoder(ServerBoundFlagVehicleForUpdatePacket::encoder)
                .decoder(ServerBoundFlagVehicleForUpdatePacket::new)
                .consumerMainThread(ServerBoundFlagVehicleForUpdatePacket::handle)
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