package com.alekiponi.alekiships.common.sounds;

import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.JukeboxSong;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.alekiponi.alekiships.AlekiShips.MOD_ID;

public final class AlekiShipsJukeboxSongs {
    public static final DeferredRegister<JukeboxSong> SONGS = DeferredRegister.create(Registries.JUKEBOX_SONG, MOD_ID);
    public static final DeferredHolder<JukeboxSong, JukeboxSong> PIRATE_CRAFTING = register("pirate_crafting",
            AlekiShipsSounds.MUSIC_DISC_PIRATE_CRAFTING, 6340, 2);


    private static DeferredHolder<JukeboxSong, JukeboxSong> register(final String name,
            final Holder<SoundEvent> soundEvent, final int lengthInSeconds, final int comparatorOutput) {
        return SONGS.register(name, () -> new JukeboxSong(soundEvent, Component.translatable(
                Util.makeDescriptionId("jukebox_song", ResourceLocation.fromNamespaceAndPath(MOD_ID, name))),
                lengthInSeconds, comparatorOutput));
    }
}
