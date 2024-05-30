package com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.vanilla;

import com.alekiponi.alekiships.common.entity.vehiclehelper.CompartmentType;
import com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.BlockCompartment;
import com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.ContainerCompartmentEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

import static com.alekiponi.alekiships.util.advancements.AlekiShipsAdvancements.RIDE_BARREL;

public class BarrelCompartmentEntity extends ContainerCompartmentEntity implements BlockCompartment {
    public static final int SLOT_COUNT = 27;
    private static final EntityDataAccessor<BlockState> DATA_ID_DISPLAY_BLOCK = SynchedEntityData.defineId(
            BarrelCompartmentEntity.class, EntityDataSerializers.BLOCK_STATE);
    private final ContainerOpenersCounter openersCounter = new ContainerOpenersCounter() {
        @Override
        protected void onOpen(final Level level, final BlockPos blockPos, final BlockState blockState) {
            BarrelCompartmentEntity.this.playSound(SoundEvents.BARREL_OPEN);
            BarrelCompartmentEntity.this.setDisplayBlockState(blockState.setValue(BarrelBlock.OPEN, true));
        }

        @Override
        protected void onClose(final Level level, final BlockPos blockPos, final BlockState blockState) {
            BarrelCompartmentEntity.this.playSound(SoundEvents.BARREL_CLOSE);
            BarrelCompartmentEntity.this.setDisplayBlockState(blockState.setValue(BarrelBlock.OPEN, false));
        }

        @Override
        protected void openerCountChanged(final Level level, final BlockPos blockPos, final BlockState blockState,
                final int count, final int openCount) {
        }

        @Override
        protected boolean isOwnContainer(final Player player) {
            if (!(player.containerMenu instanceof ChestMenu)) return false;

            final Container container = ((ChestMenu) player.containerMenu).getContainer();
            return container == BarrelCompartmentEntity.this;
        }
    };

    public BarrelCompartmentEntity(final CompartmentType<? extends BarrelCompartmentEntity> compartmentType,
            final Level level) {
        super(compartmentType, level, SLOT_COUNT);
    }

    public BarrelCompartmentEntity(final CompartmentType<? extends BarrelCompartmentEntity> compartmentType,
            final Level level, final ItemStack itemStack) {
        super(compartmentType, level, SLOT_COUNT, itemStack);

        this.setDisplayBlockState(Blocks.BARREL.defaultBlockState().setValue(BarrelBlock.FACING, Direction.UP)
                .setValue(BarrelBlock.OPEN, false));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_ID_DISPLAY_BLOCK, Blocks.AIR.defaultBlockState());
    }

    @Override
    public InteractionResult interact(final Player player, final InteractionHand hand) {
        // Silly easter egg
        if (!this.isPassenger() && !player.isSecondaryUseActive()) {
            return player.startRiding(this) ? InteractionResult.CONSUME : InteractionResult.PASS;
        }

        final InteractionResult interactionResult = super.interact(player, hand);
        if (interactionResult.consumesAction()) {
            player.awardStat(Stats.OPEN_BARREL);
            PiglinAi.angerNearbyPiglins(player, true);
        }

        return interactionResult;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.isVehicle() && !this.isPassenger() && everyNthTickUnique(5)) {
            if (this.getFirstPassenger() instanceof ServerPlayer serverPlayer && this.isInWater()) {
                RIDE_BARREL.trigger(serverPlayer);
            }
        }
    }

    @Override
    public void startOpen(final Player player) {
        if (!player.isSpectator() || !this.isPassenger()) {
            this.openersCounter.incrementOpeners(player, this.level(), this.blockPosition(),
                    this.getDisplayBlockState());
        }
    }

    @Override
    public void stopOpen(final Player player) {
        if (!player.isSpectator() || !this.isPassenger()) {
            this.openersCounter.decrementOpeners(player, this.level(), this.blockPosition(),
                    this.getDisplayBlockState());
        }
    }

    @Override
    protected void addAdditionalSaveData(final CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        BlockCompartment.saveBlockstate(this, compoundTag);
    }

    @Override
    protected void readAdditionalSaveData(final CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        BlockCompartment.readBlockstate(this, compoundTag);
    }

    @Override
    protected void onHurt(final DamageSource damageSource) {
        this.playHitSound();
    }

    @Override
    public double getPassengersRidingOffset() {
        // Offset so players stand on us
        return 0.4;
    }

    @Override
    public BlockState getDisplayBlockState() {
        return this.isPassenger() ? this.entityData.get(DATA_ID_DISPLAY_BLOCK) : this.entityData.get(
                DATA_ID_DISPLAY_BLOCK).setValue(BarrelBlock.OPEN, true);
    }

    @Override
    public void setDisplayBlockState(final BlockState blockState) {
        this.entityData.set(DATA_ID_DISPLAY_BLOCK, blockState);
    }

    @Override
    protected void onPlaced() {
        this.playPlaceSound();
    }

    @Override
    protected void onBreak() {
        this.playBreakSound();
    }

    @Override
    public double getBuoyancy() {
        return this.tickCount % 21 > 10 ? -0.01 : 0.01;
    }

    @Override
    public ItemStack getDropStack() {
        return new ItemStack(this.getDisplayBlockState().getBlock());
    }

    @Nullable
    @Override
    public ItemStack getPickResult() {
        return new ItemStack(this.getDisplayBlockState().getBlock());
    }

    @Override
    public RidingPose getRidingPose() {
        return RidingPose.STANDING;
    }

    @Override
    protected AbstractContainerMenu createMenu(final int id, final Inventory playerInventory) {
        return ChestMenu.threeRows(id, playerInventory, this);
    }
}