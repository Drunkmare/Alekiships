package com.alekiponi.alekiships.common.entity.vehiclehelper.compartment;

import com.alekiponi.alekiships.client.render.entity.vehicle.vehiclehelper.BlockCompartmentRenderer;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
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
     * The NBT tag key that should be used for serializing the blockstate.
     * You should use {@link NbtUtils#readBlockState(HolderGetter, CompoundTag)} and
     * {@link NbtUtils#writeBlockState(BlockState)} to have user-friendly NBT or delegate to
     * {@link #readBlockstate(BlockCompartment, CompoundTag)} and {@link #saveBlockstate(BlockCompartment, CompoundTag)}
     */
    String HELD_BLOCK_KEY = "heldBlock";

    /**
     * Reads a blockstate and sets it to the block compartment via {@link #setDisplayBlockState(BlockState)}
     *
     * @param blockCompartment The block compartment
     * @param compoundTag      The compound tag which the blockstate was saved to
     */
    static void readBlockstate(final BlockCompartment blockCompartment, final CompoundTag compoundTag) {
        blockCompartment.setDisplayBlockState(
                NbtUtils.readBlockState(blockCompartment.level().holderLookup(Registries.BLOCK),
                        compoundTag.getCompound(HELD_BLOCK_KEY)));
    }

    /**
     * Saves the return of {@link #getDisplayBlockState()} to the provided {@link CompoundTag}
     *
     * @param blockCompartment The block compartment to save
     * @param compoundTag      The tag to save to
     */
    static void saveBlockstate(final BlockCompartment blockCompartment, final CompoundTag compoundTag) {
        compoundTag.put(HELD_BLOCK_KEY, NbtUtils.writeBlockState(blockCompartment.getDisplayBlockState()));
    }

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

    Level level();

    void playSound(final SoundEvent soundEvent, final SoundSource soundSource, final float volume, final float pitch);
}