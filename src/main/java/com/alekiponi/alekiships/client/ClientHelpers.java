package com.alekiponi.alekiships.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public final class ClientHelpers {

    @Nullable
    public static Level getLevel() {
        return Minecraft.getInstance().level;
    }
}