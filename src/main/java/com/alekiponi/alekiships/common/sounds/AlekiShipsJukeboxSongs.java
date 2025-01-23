package com.alekiponi.alekiships.common.sounds;

import com.alekiponi.alekiships.AlekiShips;
import net.minecraft.Util;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.JukeboxSong;

public final class AlekiShipsJukeboxSongs {

    public static final ResourceKey<JukeboxSong> PIRATE_CRAFTING = create("pirate_crafting");

    private static ResourceKey<JukeboxSong> create(@SuppressWarnings("SameParameterValue") final String name) {
        return ResourceKey.create(Registries.JUKEBOX_SONG, AlekiShips.location(name));
    }

    public static void bootstrap(final BootstrapContext<JukeboxSong> context) {
        context.register(PIRATE_CRAFTING,
                new JukeboxSong(AlekiShipsSounds.MUSIC_DISC_PIRATE_CRAFTING, description(PIRATE_CRAFTING), 6340, 2));
    }

    private static Component description(
            @SuppressWarnings("SameParameterValue") final ResourceKey<JukeboxSong> resourceKey) {
        return Component.translatable(Util.makeDescriptionId("jukebox_song", resourceKey.location()));
    }
}