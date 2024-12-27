package com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.vanilla;

import com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.BlockCompartmentEntity;
import com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.CompartmentCloneable;
import com.alekiponi.alekiships.network.ClientboundJukeboxCompartmentMusicPacket;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.JukeboxPlayable;
import net.minecraft.world.item.JukeboxSong;
import net.minecraft.world.item.JukeboxSongPlayer;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.JukeboxBlock;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.ticks.ContainerSingleItem;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.Optional;

public class JukeboxCompartmentEntity extends BlockCompartmentEntity implements ContainerSingleItem, CompartmentCloneable {

    private final JukeboxCompartmentSongPlayer jukeboxCompartmentSongPlayer = new JukeboxCompartmentSongPlayer(
            this::setChanged, this);
    private ItemStack itemStack = ItemStack.EMPTY;

    public JukeboxCompartmentEntity(final EntityType<? extends JukeboxCompartmentEntity> entityType,
            final Level level) {
        super(entityType, level);
    }

    @Override
    public void tick() {
        super.tick();
        this.jukeboxCompartmentSongPlayer.tick(this.level());
    }

    @Override
    public InteractionResult interact(final Player player, final InteractionHand hand) {
        final ItemStack heldStack = player.getItemInHand(hand);
        if (heldStack.isEmpty()) {
            if (!this.hasRecord()) return InteractionResult.PASS;
            this.popOutTheItem();
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }

        final JukeboxPlayable jukeboxPlayable = heldStack.get(DataComponents.JUKEBOX_PLAYABLE);
        if (jukeboxPlayable == null) return InteractionResult.PASS;

        if (this.hasRecord()) return InteractionResult.PASS;

        this.setTheItem(heldStack.consumeAndReturn(1, player));

        player.awardStat(Stats.PLAY_RECORD);

        return InteractionResult.sidedSuccess(this.level().isClientSide);
    }

    @Override
    protected void onBreak() {
        super.onBreak();
        this.popOutTheItem();
    }

    @Override
    protected void readAdditionalSaveData(final CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);

        this.readCommonNBTData(compoundTag);
    }

    @Override
    protected void addAdditionalSaveData(final CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);

        this.writeCommonNBTData(compoundTag);
    }

    private void readCommonNBTData(final CompoundTag compoundTag) {
        if (compoundTag.contains(JukeboxBlockEntity.SONG_ITEM_TAG_ID, Tag.TAG_COMPOUND)) {
            this.itemStack = ItemStack.parseOptional(this.registryAccess(),
                    compoundTag.getCompound(JukeboxBlockEntity.SONG_ITEM_TAG_ID));
        } else {
            this.itemStack = ItemStack.EMPTY;
        }

        if (compoundTag.contains(JukeboxBlockEntity.TICKS_SINCE_SONG_STARTED_TAG_ID, Tag.TAG_LONG)) {
            JukeboxSong.fromStack(this.registryAccess(), this.itemStack).ifPresent(
                    songHolder -> this.jukeboxCompartmentSongPlayer.setSongWithoutPlaying(songHolder,
                            compoundTag.getLong(JukeboxBlockEntity.TICKS_SINCE_SONG_STARTED_TAG_ID)));
        }
    }

    private void writeCommonNBTData(final CompoundTag compoundTag) {
        if (!this.getTheItem().isEmpty()) {
            compoundTag.put(JukeboxBlockEntity.SONG_ITEM_TAG_ID, this.getTheItem().saveOptional(this.registryAccess()));
        }

        if (this.jukeboxCompartmentSongPlayer.getSong() != null) {
            compoundTag.putLong(JukeboxBlockEntity.TICKS_SINCE_SONG_STARTED_TAG_ID,
                    this.jukeboxCompartmentSongPlayer.getTicksSinceSongStarted());
        }
    }

    @Override
    public ItemStack getTheItem() {
        return this.itemStack;
    }

    @Override
    public void setTheItem(final ItemStack itemStack) {
        this.itemStack = itemStack;
        final Optional<Holder<JukeboxSong>> maybeSong = JukeboxSong.fromStack(this.registryAccess(), this.itemStack);
        this.setDisplayBlockState(
                this.getDisplayBlockState().setValue(JukeboxBlock.HAS_RECORD, !this.itemStack.isEmpty()));
        if (!this.itemStack.isEmpty() && maybeSong.isPresent()) {
            this.jukeboxCompartmentSongPlayer.play(this.level(), maybeSong.get());
        } else {
            this.jukeboxCompartmentSongPlayer.stop();
        }
    }

    @Override
    public ItemStack splitTheItem(final int amount) {
        final ItemStack itemStack1 = this.itemStack;
        this.setTheItem(ItemStack.EMPTY);
        return itemStack1;
    }

    @Override
    public boolean canPlaceItem(final int slotIndex, final ItemStack itemStack) {
        return itemStack.has(DataComponents.JUKEBOX_PLAYABLE) && this.getItem(slotIndex).isEmpty();
    }

    @Override
    public boolean canTakeItem(final Container target, final int slotIndex, final ItemStack itemStack) {
        return target.hasAnyMatching(ItemStack::isEmpty);
    }

    @Override
    public boolean stillValid(final Player player) {
        return !this.isRemoved() && this.position().closerThan(player.position(), 8);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public void setChanged() {

    }

    private boolean hasRecord() {
        return this.getDisplayBlockState().getValue(JukeboxBlock.HAS_RECORD);
    }

    private void popOutTheItem() {
        if (!this.level().isClientSide) {
            final ItemStack itemStack = this.removeTheItem();
            if (!itemStack.isEmpty()) {
                double yPos = this.getY();
                for (final Entity entity : this.level()
                        .getEntities(this, this.getBoundingBox(), Entity::canBeCollidedWith)) {
                    if (entity.getBoundingBox().maxY > yPos) yPos = entity.getBoundingBox().maxY;
                }

                Containers.dropItemStack(this.level(), this.getX(), yPos, this.getZ(), itemStack);
            }
        }
    }

    @Override
    public void applyComponentsFromItemStack(final ItemStack itemStack) {
        this.readCommonNBTData(itemStack.getOrDefault(DataComponents.BLOCK_ENTITY_DATA, CustomData.EMPTY).copyTag());
    }

    @Override
    public DataComponentMap collectComponents() {
        final var builder = DataComponentMap.builder();
        final CompoundTag compoundTag = new CompoundTag();
        this.writeCommonNBTData(compoundTag);
        builder.set(DataComponents.BLOCK_ENTITY_DATA, CustomData.of(compoundTag));
        return builder.build();
    }

    public static class JukeboxCompartmentSongPlayer {
        private final JukeboxCompartmentEntity jukeboxCompartment;
        private final JukeboxSongPlayer.OnSongChanged onSongChanged;
        private long ticksSinceSongStarted;
        @Nullable
        private Holder<JukeboxSong> song;

        public JukeboxCompartmentSongPlayer(final JukeboxSongPlayer.OnSongChanged onSongChanged,
                final JukeboxCompartmentEntity jukeboxCompartment) {
            this.onSongChanged = onSongChanged;
            this.jukeboxCompartment = jukeboxCompartment;
        }

        private static void spawnMusicParticles(final LevelAccessor level, final Vec3 vec3) {
            if (level instanceof ServerLevel serverlevel) {
                float f = (float) level.getRandom().nextInt(4) / 24.0F;
                serverlevel.sendParticles(ParticleTypes.NOTE, vec3.x(), vec3.y(), vec3.z(), 0, f, 0.0, 0.0, 1.0);
            }
        }

        public boolean isPlaying() {
            return this.song != null;
        }

        @Nullable
        public JukeboxSong getSong() {
            return this.song == null ? null : this.song.value();
        }

        public long getTicksSinceSongStarted() {
            return this.ticksSinceSongStarted;
        }

        public void setSongWithoutPlaying(final Holder<JukeboxSong> song, final long ticksSinceSongStarted) {
            if (!song.value().hasFinished(ticksSinceSongStarted)) {
                this.song = song;
                this.ticksSinceSongStarted = ticksSinceSongStarted;
            }
        }

        public void play(final LevelAccessor level, final Holder<JukeboxSong> song) {
            this.song = song;
            this.ticksSinceSongStarted = 0;
            PacketDistributor.sendToPlayersTrackingEntity(this.jukeboxCompartment,
                    ClientboundJukeboxCompartmentMusicPacket.start(this.jukeboxCompartment, this.song.value()));
            this.onSongChanged.notifyChange();
        }

        public void stop() {
            if (this.song != null) {
                this.song = null;
                this.ticksSinceSongStarted = 0;
                PacketDistributor.sendToPlayersTrackingEntity(this.jukeboxCompartment,
                        ClientboundJukeboxCompartmentMusicPacket.stop(this.jukeboxCompartment));
                this.onSongChanged.notifyChange();
            }
        }

        public void tick(final LevelAccessor level) {
            if (this.song != null) {
                if (this.song.value().hasFinished(this.ticksSinceSongStarted)) {
                    this.stop();
                } else {
                    if (this.shouldEmitJukeboxPlayingEvent()) {
                        spawnMusicParticles(level, this.jukeboxCompartment.position().add(0, 1.2, 0));
                    }

                    this.ticksSinceSongStarted++;
                }
            }
        }

        private boolean shouldEmitJukeboxPlayingEvent() {
            return this.ticksSinceSongStarted % JukeboxSongPlayer.PLAY_EVENT_INTERVAL_TICKS == 0;
        }
    }
}