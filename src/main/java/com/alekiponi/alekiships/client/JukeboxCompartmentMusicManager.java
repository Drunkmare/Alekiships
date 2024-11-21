package com.alekiponi.alekiships.client;

import com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.vanilla.JukeboxCompartmentEntity;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.JukeboxSong;

public final class JukeboxCompartmentMusicManager {

    private static final Int2ObjectMap<JukeboxCompartmentSoundInstance> PLAYING = new Int2ObjectOpenHashMap<>();

    public static void playMusic(final JukeboxCompartmentEntity entity, final Holder<JukeboxSong> songHolder) {
        final JukeboxSong song = songHolder.value();
        final var soundInstance = new JukeboxCompartmentSoundInstance(entity, song.soundEvent().value());
        PLAYING.put(entity.getId(), soundInstance);
        Minecraft.getInstance().getSoundManager().play(soundInstance);
        Minecraft.getInstance().gui.setNowPlaying(song.description());
    }

    public static void stopMusic(final JukeboxCompartmentEntity entity) {
        final var soundInstance = PLAYING.get(entity.getId());
        if (soundInstance != null) {
            Minecraft.getInstance().getSoundManager().stop(soundInstance);
            PLAYING.remove(entity.getId());
        }
    }

    public static class JukeboxCompartmentSoundInstance extends AbstractTickableSoundInstance {

        private final JukeboxCompartmentEntity jukeboxCompartment;

        protected JukeboxCompartmentSoundInstance(final JukeboxCompartmentEntity jukeboxCompartment,
                final SoundEvent soundEvent) {
            super(soundEvent, SoundSource.RECORDS, SoundInstance.createUnseededRandom());
            this.jukeboxCompartment = jukeboxCompartment;
            this.volume = 4;
            this.pitch = 1;
            this.x = (float) this.jukeboxCompartment.getX();
            this.y = (float) this.jukeboxCompartment.getY();
            this.z = (float) this.jukeboxCompartment.getZ();
        }

        @Override
        public void tick() {
            if (this.jukeboxCompartment.isRemoved()) {
                this.stop();
            } else {
                this.x = (float) this.jukeboxCompartment.getX();
                this.y = (float) this.jukeboxCompartment.getY();
                this.z = (float) this.jukeboxCompartment.getZ();
            }
        }
    }
}