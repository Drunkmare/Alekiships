package com.alekiponi.alekiships.network;

import com.alekiponi.alekiships.AlekiShips;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public final class PacketHandler {

    public static void init(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(
                ModList.get().getModFileById(AlekiShips.MOD_ID).versionString());

        // Client -> Server
        registrar.playToServer(ServerboundCompartmentInputPacket.TYPE, ServerboundCompartmentInputPacket.CODEC,
                onServer(ServerboundCompartmentInputPacket::handle));
        registrar.playToServer(ServerboundSwitchEntityPacket.TYPE, ServerboundSwitchEntityPacket.CODEC,
                onServer(ServerboundSwitchEntityPacket::handle));
        registrar.playToServer(ServerboundSloopControlPacket.TYPE, ServerboundSloopControlPacket.CODEC,
                onServer(ServerboundSloopControlPacket::handle));
        registrar.playToServer(ServerboundPickCompartmentPacket.TYPE, ServerboundPickCompartmentPacket.CODEC,
                onServer(ServerboundPickCompartmentPacket::handle));
        registrar.playToServer(ServerboundFlagVehicleForUpdatePacket.TYPE, ServerboundFlagVehicleForUpdatePacket.CODEC,
                onServer(ServerboundFlagVehicleForUpdatePacket::handle));

        // Server -> Client
        registrar.playToClient(ClientboundCleatLinkPacket.TYPE, ClientboundCleatLinkPacket.CODEC,
                onClient(ClientboundCleatLinkPacket::handle));
//        registrar.playToClient(ClientboundJukeboxStartMusicPacket.TYPE, ClientboundJukeboxStartMusicPacket.CODEC,
//                onClient(ClientboundJukeboxStartMusicPacket::handle));
//        registrar.playToClient(ClientboundJukeboxStopMusicPacket.TYPE, ClientboundJukeboxStopMusicPacket.CODEC,
//                onClient(ClientboundJukeboxStopMusicPacket::handle));
    }

    private static <T extends CustomPacketPayload> IPayloadHandler<T> onClient(final Consumer<T> handler) {
        return (payload, context) -> context.enqueueWork(() -> handler.accept(payload));
    }

    private static <T extends CustomPacketPayload> IPayloadHandler<T> onServer(
            final BiConsumer<T, ServerPlayer> handler) {
        return (payload, context) -> context.enqueueWork(
                () -> handler.accept(payload, (ServerPlayer) context.player()));
    }
}