package com.alekiponi.alekiships.common.entity.compartment;

import com.alekiponi.alekiships.client.render.entity.vehicle.vehiclehelper.BlockCompartmentRenderer;
import com.alekiponi.alekiships.common.item.components.AlekiShipsComponents;
import com.alekiponi.alekiships.util.CommonHelper;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
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
     * Creates a {@link CompartmentType.CompartmentFactory} using a {@link BlockCompartmentFactory}
     *
     * @param blockCompartmentFactory A {@link BlockCompartmentFactory} which is invoked with the {@link BlockItem}s
     *                                {@link Block}s {@link Block#defaultBlockState()}
     */
    static <E extends AbstractCompartmentEntity & BlockCompartment> CompartmentType.CompartmentFactory<E> create(
            final BlockCompartmentFactory<E> blockCompartmentFactory) {
        return (entityType, level, itemStack) -> {
            final var compartmentData = itemStack.get(AlekiShipsComponents.BLOCK_COMPARTMENT_DATA);
            if (compartmentData != null) {
                return blockCompartmentFactory.create(entityType, level, compartmentData.displayState());
            }

            if (itemStack.getItem() instanceof BlockItem blockItem) {
                return blockCompartmentFactory.create(entityType, level, blockItem.getBlock().defaultBlockState());
            }

            return null;
        };
    }

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
     * Plays the hit sound for the passed {@link BlockCompartment}
     */
    static void playHitSound(final BlockCompartment blockCompartment) {
        //noinspection deprecation
        CommonHelper.playHitSound(blockCompartment::playSound, blockCompartment.getDisplayBlockState().getSoundType());
    }

    /**
     * Plays the break sound for the passed {@link BlockCompartment}
     */
    static void playBreakSound(final BlockCompartment blockCompartment) {
        //noinspection deprecation
        CommonHelper.playBreakSound(blockCompartment::playSound,
                blockCompartment.getDisplayBlockState().getSoundType());
    }

    /**
     * Plays the place sound for the passed {@link BlockCompartment}
     */
    static void playPlaceSound(final BlockCompartment blockCompartment) {
        //noinspection deprecation
        CommonHelper.playPlaceSound(blockCompartment::playSound,
                blockCompartment.getDisplayBlockState().getSoundType());
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

    /**
     * A factory for a compartment which takes in a {@link BlockState}. See
     * {@link BlockCompartmentEntity#BlockCompartmentEntity(EntityType, Level, BlockState)} as an example
     * constructor usage
     */
    @FunctionalInterface
    interface BlockCompartmentFactory<E extends AbstractCompartmentEntity & BlockCompartment> {
        E create(EntityType<E> entityType, Level level, BlockState blockState);
    }
}