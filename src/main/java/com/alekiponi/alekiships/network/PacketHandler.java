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
                .decoder(ServerboundCompartmentInputPacket::decoder)
                .consumerMainThread(ServerboundCompartmentInputPacket::handle)
                .add();

        CHANNEL.messageBuilder(ServerboundSwitchEntityPacket.class, id++)
                .encoder(ServerboundSwitchEntityPacket::encoder)
                .decoder(ServerboundSwitchEntityPacket::decoder)
                .consumerMainThread(ServerboundSwitchEntityPacket::handle)
                .add();

        CHANNEL.messageBuilder(ServerBoundSloopPacket.class, id++)
                .encoder(ServerBoundSloopPacket::encoder)
                .decoder(ServerBoundSloopPacket::decoder)
                .consumerMainThread(ServerBoundSloopPacket::handle)
                .add();

        CHANNEL.messageBuilder(ServerBoundPickCompartmentPacket.class, id++)
                .encoder(ServerBoundPickCompartmentPacket::encoder)
                .decoder(ServerBoundPickCompartmentPacket::decoder)
                .consumerMainThread(ServerBoundPickCompartmentPacket::handle)
                .add();

        CHANNEL.messageBuilder(ClientBoundCleatLinkPacket.class, id++)
                .encoder(ClientBoundCleatLinkPacket::encoder)
                .decoder(ClientBoundCleatLinkPacket::new)
                .consumerMainThread(
                        (clientBoundCleatLinkPacket, contextSupplier) -> clientBoundCleatLinkPacket.handle())
                .add();
    }
}