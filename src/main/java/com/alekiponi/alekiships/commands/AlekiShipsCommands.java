package com.alekiponi.alekiships.commands;

import com.alekiponi.alekiships.commands.server.EntityMultiblockCommands;

import net.neoforged.neoforge.event.RegisterCommandsEvent;

public final class AlekiShipsCommands {

    public static void onRegisterCommand(final RegisterCommandsEvent event) {
        EntityMultiblockCommands.register(event.getDispatcher());
    }
}