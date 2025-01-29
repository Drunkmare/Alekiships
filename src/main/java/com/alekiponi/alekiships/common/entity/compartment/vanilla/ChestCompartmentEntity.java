package com.alekiponi.alekiships.common.entity.compartment.vanilla;

import com.alekiponi.alekiships.common.entity.compartment.LidCompartment;
import com.alekiponi.alekiships.common.entity.compartment.RandomizableContainerCompartmentEntity;
import com.alekiponi.alekiships.util.CommonHelper;
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
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.ChestLidController;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class ChestCompartmentEntity extends RandomizableContainerCompartmentEntity.RandomizableContainerMenuCompartmentEntity implements LidCompartment {

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

    /**
     * Protected constructor so children can have their own size
     */
    protected ChestCompartmentEntity(final EntityType<? extends ChestCompartmentEntity> entityType, final Level level,
            final int slotCount) {
        super(entityType, level, slotCount);
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
    protected void onHurt(final DamageSource damageSource) {
        CommonHelper.playHitSound(this::playSound, SoundType.WOOD);
    }

    @Override
    protected void onPlaced() {
        CommonHelper.playPlaceSound(this::playSound, SoundType.WOOD);
    }

    @Override
    protected void onBreak() {
        super.onBreak();
        CommonHelper.playBreakSound(this::playSound, SoundType.WOOD);
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

    @Override
    public ItemStack getDropStack() {
        return new ItemStack(Blocks.CHEST.asItem());
    }

    @Nullable
    @Override
    public ItemStack getPickResult() {
        return new ItemStack(Blocks.CHEST.asItem());
    }
}