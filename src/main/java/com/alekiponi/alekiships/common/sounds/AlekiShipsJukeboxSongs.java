package com.alekiponi.alekiships.common.sounds;

import com.alekiponi.alekiships.AlekiShips;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.JukeboxSong;

public final class AlekiShipsJukeboxSongs {

    public static final ResourceKey<JukeboxSong> PIRATE_CRAFTING = create("pirate_crafting");

    private static ResourceKey<JukeboxSong> create(@SuppressWarnings("SameParameterValue") final String name) {
        return ResourceKey.create(Registries.JUKEBOX_SONG, AlekiShips.location(name));
    }
}