package com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.vanilla;

import com.alekiponi.alekiships.common.entity.vehiclehelper.CompartmentType;
import com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.ContainerCompartmentEntity;
import com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.LidCompartment;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.ChestLidController;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.state.BlockState;

public class ChestCompartmentEntity extends ContainerCompartmentEntity implements LidCompartment {

    public static final byte CONTAINER_OPEN = 1;
    public static final byte CONTAINER_CLOSE = 2;
    public static final int SLOT_COUNT = 27;

    private final ChestLidController chestLidController = new ChestLidController();
    private final ContainerOpenersCounter openersCounter = new ContainerOpenersCounter() {
        @Override
        protected void onOpen(final Level level, final BlockPos blockPos, final BlockState blockState) {
            ChestCompartmentEntity.this.playSound(SoundEvents.CHEST_OPEN, SoundSource.BLOCKS, 0.5F,
                    level.random.nextFloat() * 0.1F + 0.9F);
        }

        @Override
        protected void onClose(final Level level, final BlockPos blockPos, final BlockState blockState) {
            ChestCompartmentEntity.this.playSound(SoundEvents.CHEST_CLOSE, SoundSource.BLOCKS, 0.5F,
                    level.random.nextFloat() * 0.1F + 0.9F);
        }

        @Override
        protected void openerCountChanged(final Level level, final BlockPos blockPos, final BlockState blockState,
                final int count, final int openCount) {
            ChestCompartmentEntity.this.signalOpenCount(level, (byte) openCount);
        }

        @Override
        protected boolean isOwnContainer(final Player player) {
            if (!(player.containerMenu instanceof ChestMenu)) return false;

            final Container container = ((ChestMenu) player.containerMenu).getContainer();
            return container == ChestCompartmentEntity.this;
        }
    };

    public ChestCompartmentEntity(final EntityType<? extends ChestCompartmentEntity> entityType, final Level level) {
        this(entityType, level, SLOT_COUNT);
    }

    public ChestCompartmentEntity(final CompartmentType<? extends ChestCompartmentEntity> entityType, final Level level,
            final ItemStack itemStack) {
        this(entityType, level, SLOT_COUNT, itemStack);
    }

    /**
     * Protected constructor so children can have their own size
     */
    protected ChestCompartmentEntity(final EntityType<? extends ChestCompartmentEntity> entityType, final Level level,
            final int slotCount) {
        super(entityType, level, slotCount);
    }

    /**
     * Protected constructor so children can have their own size
     */
    protected ChestCompartmentEntity(final CompartmentType<? extends ChestCompartmentEntity> entityType,
            final Level level, final int slotCount, final ItemStack itemStack) {
        super(entityType, level, slotCount, itemStack);
    }

    @Override
    public void tick() {
        super.tick();

        this.chestLidController.tickLid();

        if (!this.isRemoved() && !this.level().isClientSide()) {
            this.openersCounter.recheckOpeners(this.level(), this.blockPosition(), Blocks.AIR.defaultBlockState());
        }
    }

    @Override
    public void handleEntityEvent(final byte dataID) {
        switch (dataID) {
            case CONTAINER_OPEN -> this.chestLidController.shouldBeOpen(true);
            case CONTAINER_CLOSE -> this.chestLidController.shouldBeOpen(false);
        }

        super.handleEntityEvent(dataID);
    }

    @Override
    public void remove(final RemovalReason removalReason) {
        if (!this.level().isClientSide() && removalReason.shouldDestroy()) {
            this.playSound(SoundEvents.WOOD_BREAK, SoundSource.BLOCKS, 1, 0.8F);
        }
        super.remove(removalReason);
    }

    @Override
    protected void playHurtSound(final DamageSource damageSource) {
        this.playSound(SoundEvents.WOOD_HIT, SoundSource.BLOCKS, 1, 0.5F);
    }

    @Override
    protected void onPlaced() {
        this.playSound(SoundEvents.WOOD_PLACE, SoundSource.BLOCKS, 1, 0.8F);
    }

    @Override
    public void startOpen(final Player player) {
        if (!this.isRemoved() && !player.isSpectator() || !this.isPassenger()) {
            this.openersCounter.incrementOpeners(player, this.level(), this.blockPosition(),
                    Blocks.AIR.defaultBlockState());
        }
    }

    @Override
    public void stopOpen(final Player player) {
        if (!this.isRemoved() && !player.isSpectator() || !this.isPassenger()) {
            this.openersCounter.decrementOpeners(player, this.level(), this.blockPosition(),
                    Blocks.AIR.defaultBlockState());
        }
    }

    @Override
    protected AbstractContainerMenu createMenu(final int id, final Inventory playerInventory) {
        return ChestMenu.threeRows(id, playerInventory, this);
    }

    @Override
    public float getOpenNess(final float partialTicks) {
        return this.chestLidController.getOpenness(partialTicks);
    }

    private void signalOpenCount(final Level level, final byte openCount) {
        level.broadcastEntityEvent(this, openCount > 0 ? CONTAINER_OPEN : CONTAINER_CLOSE);
    }
}