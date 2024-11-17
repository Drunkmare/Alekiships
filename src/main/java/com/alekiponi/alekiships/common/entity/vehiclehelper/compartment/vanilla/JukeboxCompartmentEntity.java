package com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.vanilla;

import com.alekiponi.alekiships.common.entity.vehiclehelper.CompartmentType;
import com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.BlockCompartmentEntity;
import com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.CompartmentCloneable;
import com.alekiponi.alekiships.network.ClientboundJukeboxStartMusicPacket;
import com.alekiponi.alekiships.network.ClientboundJukeboxStopMusicPacket;
import com.alekiponi.alekiships.network.PacketHandler;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.JukeboxBlock;
import net.minecraft.world.ticks.ContainerSingleItem;
import net.minecraftforge.network.PacketDistributor;

import java.util.Objects;

public class JukeboxCompartmentEntity extends BlockCompartmentEntity implements ContainerSingleItem, CompartmentCloneable {

    private static final int SONG_END_PADDING = 20;
    private final NonNullList<ItemStack> items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
    private int ticksSinceLastEvent;
    private long tickCount;
    private long recordStartedTick;
    private boolean isPlaying;

    public JukeboxCompartmentEntity(final CompartmentType<? extends JukeboxCompartmentEntity> compartmentType,
            final Level level) {
        super(compartmentType, level);
    }

    public JukeboxCompartmentEntity(final CompartmentType<? extends JukeboxCompartmentEntity> compartmentType,
            final Level level, final ItemStack itemStack) {
        super(compartmentType, level, itemStack);

        final CompoundTag compoundtag = BlockItem.getBlockEntityData(itemStack);
        if (compoundtag != null && compoundtag.contains("RecordItem")) {
            this.readCommonNBTData(compoundtag);
        }
    }

    @Override
    public void tick() {
        super.tick();

        ++this.ticksSinceLastEvent;
        if (this.isRecordPlaying()) {
            if (this.getFirstItem().getItem() instanceof final RecordItem recorditem) {
                if (this.shouldRecordStopPlaying(recorditem)) {
                    this.stopPlaying();
                } else if (this.shouldSendJukeboxPlayingEvent()) {
                    this.ticksSinceLastEvent = 0;
                    if (this.level() instanceof ServerLevel serverLevel) {
                        serverLevel.sendParticles(ParticleTypes.NOTE, this.getX(), this.getY() + 1.2, this.getZ(), 0,
                                (float) serverLevel.getRandom().nextInt(4) / 24, 0, 0, 1);
                    }
                }
            }
        }

        ++this.tickCount;
    }

    @Override
    public InteractionResult interact(final Player player, final InteractionHand hand) {
        if (this.getDisplayBlockState().getValue(JukeboxBlock.HAS_RECORD)) {
            this.popOutRecord();
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }

        final ItemStack heldItem = player.getItemInHand(hand);
        if (heldItem.is(ItemTags.MUSIC_DISCS)) {
            this.setFirstItem(heldItem.split(1));
            player.awardStat(Stats.PLAY_RECORD);
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }

        return InteractionResult.PASS;
    }

    @Override
    protected void onBreak() {
        super.onBreak();
        this.popOutRecord();
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
        if (compoundTag.contains("RecordItem", Tag.TAG_COMPOUND)) {
            this.items.set(0, ItemStack.of(compoundTag.getCompound("RecordItem")));
            this.setDisplayBlockState(this.getDisplayBlockState().setValue(JukeboxBlock.HAS_RECORD, true));
        }

        this.isPlaying = compoundTag.getBoolean("IsPlaying");
        this.recordStartedTick = compoundTag.getLong("RecordStartTick");
        this.tickCount = compoundTag.getLong("TickCount");
    }

    private void writeCommonNBTData(final CompoundTag compoundTag) {
        if (!this.getFirstItem().isEmpty()) {
            compoundTag.put("RecordItem", this.getFirstItem().save(new CompoundTag()));
        }

        compoundTag.putBoolean("IsPlaying", this.isPlaying);
        compoundTag.putLong("RecordStartTick", this.recordStartedTick);
        compoundTag.putLong("TickCount", this.tickCount);
    }

    @Override
    public CompoundTag saveForItemStack() {
        final CompoundTag compoundTag = new CompoundTag();
        this.writeCommonNBTData(compoundTag);
        return compoundTag;
    }

    @Override
    public ItemStack getItem(int slotIndex) {
        return this.items.get(slotIndex);
    }

    @Override
    public void setItem(final int slotIndex, final ItemStack itemStack) {
        if (itemStack.is(ItemTags.MUSIC_DISCS)) {
            this.items.set(slotIndex, itemStack);
            this.setDisplayBlockState(this.getDisplayBlockState().setValue(JukeboxBlock.HAS_RECORD, true));
            this.startPlaying();
        }
    }

    @Override
    public ItemStack removeItem(final int slotIndex, final int amount) {
        final ItemStack itemStack = Objects.requireNonNullElse(this.items.get(slotIndex), ItemStack.EMPTY);
        this.items.set(slotIndex, ItemStack.EMPTY);
        if (!itemStack.isEmpty()) {
            this.setDisplayBlockState(this.getDisplayBlockState().setValue(JukeboxBlock.HAS_RECORD, false));
            this.stopPlaying();
        }

        return itemStack;
    }

    @Override
    public boolean canPlaceItem(final int slotIndex, final ItemStack itemStack) {
        return itemStack.is(ItemTags.MUSIC_DISCS) && this.getItem(slotIndex).isEmpty();
    }

    @Override
    public boolean canTakeItem(final Container container, final int slotIndex, final ItemStack itemStack) {
        return container.hasAnyMatching(ItemStack::isEmpty);
    }

    @Override
    public boolean stillValid(final Player player) {
        return !this.isRemoved() && this.position().closerThan(player.position(), 8);
    }

    public final void popOutRecord() {
        final ItemStack itemStack = this.getFirstItem();
        if (!itemStack.isEmpty()) {
            this.removeFirstItem();
            double yPos = this.getY();
            for (final Entity entity : this.level()
                    .getEntities(this, this.getBoundingBox(), Entity::canBeCollidedWith)) {
                if (entity.getBoundingBox().maxY > yPos) yPos = entity.getBoundingBox().maxY;
            }

            Containers.dropItemStack(this.level(), this.getX(), yPos, this.getZ(), itemStack);
        }
    }

    private void startPlaying() {
        this.recordStartedTick = this.tickCount;
        this.isPlaying = true;
//        if (!this.level().isClientSide) {
//            PacketHandler.send(PacketDistributor.TRACKING_ENTITY.with(() -> this),
//                    new ClientboundJukeboxStartMusicPacket(this, this.getFirstItem().getItem()));
//        }
        this.setChanged();
    }

    private void stopPlaying() {
        this.isPlaying = false;
//        if (!this.level().isClientSide) {
//            PacketHandler.send(PacketDistributor.TRACKING_ENTITY.with(() -> this),
//                    new ClientboundJukeboxStopMusicPacket(this));
//        }
        this.setChanged();
    }

    private boolean shouldRecordStopPlaying(final RecordItem recorditem) {
        return this.tickCount >= this.recordStartedTick + (long) recorditem.getLengthInTicks() + SONG_END_PADDING;
    }

    private boolean shouldSendJukeboxPlayingEvent() {
        return this.ticksSinceLastEvent >= SONG_END_PADDING;
    }

    private boolean isRecordPlaying() {
        return !this.getFirstItem().isEmpty() && this.isPlaying;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public void setChanged() {

    }
}