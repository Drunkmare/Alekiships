package com.alekiponi.alekiships.client;

import com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.vanilla.JukeboxCompartmentEntity;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.EntityBoundSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.JukeboxPlayable;
import net.minecraft.world.item.JukeboxSong;

import static net.minecraft.core.component.DataComponents.*;


public final class JukeboxCompartmentMusicManager {

    private static final Int2ObjectMap<SoundInstance> PLAYING_RECORDS = new Int2ObjectOpenHashMap<>();

    public static void playMusic(final JukeboxCompartmentEntity entity, final Item recorditem)
    {

        JukeboxPlayable playable = recorditem.getDefaultInstance().getComponents().get(JUKEBOX_PLAYABLE);


        if (playable.song().asEither().left().isPresent())
        {
            JukeboxSong song = playable.song().asEither().left().get().value();

            Minecraft.getInstance().gui.setNowPlaying(song.description());
            final EntityBoundSoundInstance soundInstance = new EntityBoundSoundInstance(song.soundEvent().value(),
                SoundSource.RECORDS, 2, 1, entity, entity.level().random.nextLong());
            PLAYING_RECORDS.put(entity.getId(), soundInstance);
            Minecraft.getInstance().getSoundManager().play(soundInstance);
        }


    }

    public static void stopMusic(final JukeboxCompartmentEntity entity) {
        final SoundInstance soundInstance = PLAYING_RECORDS.get(entity.getId());
        if (soundInstance != null) {
            Minecraft.getInstance().getSoundManager().stop(soundInstance);
            PLAYING_RECORDS.remove(entity.getId());
        }
    }
}