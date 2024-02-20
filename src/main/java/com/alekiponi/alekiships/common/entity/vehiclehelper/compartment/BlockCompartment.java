package com.alekiponi.alekiships.common.entity.vehiclehelper.compartment;

import com.alekiponi.alekiships.client.render.entity.vehicle.vehiclehelper.BlockCompartmentRenderer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Interface for compartment entities that contain blocks.
 * See {@link BlockCompartmentEntity} for an example implementation as well as {@link BlockCompartmentRenderer}
 * which will render the contained blockstate.
 * <p>
 * You should sync the held blockstate to the client using {@link SynchedEntityData} taking advantage
 * of {@link EntityDataSerializers#BLOCK_STATE} or some other mechanism.
 */
public interface BlockCompartment {

    /**
     * Plays the hit sound of the held blockstate
     */
    default void playHitSound() {
        final SoundType soundType = this.getDisplayBlockState().getSoundType();
        this.playSound(soundType.getHitSound(), SoundSource.BLOCKS, (soundType.getVolume() + 1) / 8,
                soundType.getPitch() * 0.5F);
    }

    /**
     * Plays the break sound of the held blockstate
     */
    default void playBreakSound() {
        final SoundType soundType = this.getDisplayBlockState().getSoundType();
        this.playSound(soundType.getBreakSound(), SoundSource.BLOCKS, (soundType.getVolume() + 1) / 2,
                soundType.getPitch() * 0.8F);
    }

    /**
     * Plays the place sound of the held blockstate
     */
    default void playPlaceSound() {
        final SoundType soundType = this.getDisplayBlockState().getSoundType();
        this.playSound(soundType.getPlaceSound(), SoundSource.BLOCKS, (soundType.getVolume() + 1) / 2,
                soundType.getPitch() * 0.8F);
    }

    /**
     * Gets the display blockstate
     *
     * @return The current display blockstate for this block compartment
     */
    BlockState getDisplayBlockState();

    /**
     * Sets the display blockstate
     *
     * @param blockState The new display blockstate for this block compartment
     */
    void setDisplayBlockState(final BlockState blockState);

    void playSound(final SoundEvent soundEvent, final SoundSource soundSource, final float volume, final float pitch);
}