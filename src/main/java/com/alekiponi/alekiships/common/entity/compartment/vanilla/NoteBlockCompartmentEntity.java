package com.alekiponi.alekiships.common.entity.compartment.vanilla;

import com.alekiponi.alekiships.common.entity.compartment.BlockCompartmentEntity;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.NoteBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.gameevent.GameEvent;

import javax.annotation.Nullable;

public class NoteBlockCompartmentEntity extends BlockCompartmentEntity {
    public static final byte PLAY_NOTE_EVENT = 10;
    private static final String TAG_NOTE_BLOCK_SOUND = "note_block_sound";
    @Nullable
    private ResourceLocation noteBlockSound;

    public NoteBlockCompartmentEntity(final EntityType<? extends NoteBlockCompartmentEntity> entityType,
            final Level level) {
        super(entityType, level);
    }

    public NoteBlockCompartmentEntity(final EntityType<? extends NoteBlockCompartmentEntity> entityType,
            final Level level, final BlockState blockState) {
        super(entityType, level, blockState);
    }

    @Override
    protected void addAdditionalSaveData(final CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);

        if (this.noteBlockSound != null) {
            compoundTag.putString(TAG_NOTE_BLOCK_SOUND, this.noteBlockSound.toString());
        }
    }

    @Override
    protected void readAdditionalSaveData(final CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);

        this.readCommonSaveData(compoundTag);
    }

    private void readCommonSaveData(final CompoundTag compoundTag) {
        if (compoundTag.contains(TAG_NOTE_BLOCK_SOUND, Tag.TAG_STRING)) {
            this.noteBlockSound = ResourceLocation.tryParse(compoundTag.getString(TAG_NOTE_BLOCK_SOUND));
        }
    }

    @Override
    protected void onHurt(final DamageSource damageSource) {
        if (!this.level().isClientSide && damageSource.getDirectEntity() instanceof Player player) {
            this.playNote(player);
            player.awardStat(Stats.PLAY_NOTEBLOCK);
        }
    }

    @Override
    public InteractionResult interact(final Player player, final InteractionHand hand) {
        if (this.level().isClientSide) return InteractionResult.SUCCESS;

        final ItemStack heldStack = player.getItemInHand(hand);

        if (player.isShiftKeyDown() && heldStack.isEmpty()) {
            this.setDisplayBlockState(
                    this.getDisplayBlockState().setValue(NoteBlock.INSTRUMENT, NoteBlockInstrument.HARP));
            this.playNote(player);
            return InteractionResult.CONSUME;
        }

        if (heldStack.getItem() instanceof BlockItem blockItem) {
            this.setDisplayBlockState(this.getDisplayBlockState()
                    .setValue(NoteBlock.INSTRUMENT, blockItem.getBlock().defaultBlockState().instrument()));
            //noinspection DataFlowIssue
            this.noteBlockSound = heldStack.getOrDefault(DataComponents.NOTE_BLOCK_SOUND, this.noteBlockSound);

            this.playNote(player);
            return InteractionResult.CONSUME;
        }

        this.setDisplayBlockState(this.getDisplayBlockState().cycle(NoteBlock.NOTE));
        this.playNote(player);
        player.awardStat(Stats.TUNE_NOTEBLOCK);

        return InteractionResult.CONSUME;
    }

    @Override
    public void handleEntityEvent(final byte eventId) {
        if (eventId != PLAY_NOTE_EVENT) {
            super.handleEntityEvent(eventId);
        }

        final int noteIndex = this.getDisplayBlockState().getValue(NoteBlock.NOTE);
        this.level()
                .addParticle(ParticleTypes.NOTE, this.getX(), this.getY() + 1.2D, this.getZ(), (double) noteIndex / 24,
                        0, 0);
    }

    private void playNote(final Entity player) {
        if (this.getDisplayBlockState().getValue(NoteBlock.INSTRUMENT).isTunable()) {
            this.level().broadcastEntityEvent(this, PLAY_NOTE_EVENT);
        }

        final NoteBlockInstrument instrument = this.getDisplayBlockState().getValue(NoteBlock.INSTRUMENT);
        final float pitch;
        if (instrument.isTunable()) {
            final int noteIndex = this.getDisplayBlockState().getValue(NoteBlock.NOTE);
            pitch = NoteBlock.getPitchFromNote(noteIndex);
        } else {
            pitch = 1;
        }

        final Holder<SoundEvent> soundEvent;
        if (instrument.hasCustomSound() && this.noteBlockSound != null) {
            soundEvent = Holder.direct(SoundEvent.createVariableRangeEvent(this.noteBlockSound));
        } else {
            soundEvent = instrument.getSoundEvent();
        }

        this.playSound(soundEvent.value(), SoundSource.RECORDS, NoteBlock.NOTE_VOLUME, pitch);

        this.gameEvent(GameEvent.NOTE_BLOCK_PLAY, player);
    }
}