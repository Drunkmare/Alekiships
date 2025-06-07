package com.alekiponi.alekiships.common.entity.compartment.vanilla;

import com.alekiponi.alekiships.common.entity.compartment.AbstractCompartmentEntity;
import com.alekiponi.alekiships.common.entity.compartment.LidCompartment;
import com.alekiponi.alekiships.common.entity.compartment.SimpleBlockMenuCompartment;
import com.alekiponi.alekiships.util.CommonHelper;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.*;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.ChestLidController;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

import org.jetbrains.annotations.Nullable;
import lombok.AllArgsConstructor;
import lombok.experimental.Delegate;

public class EnderChestCompartmentEntity extends AbstractCompartmentEntity implements SimpleBlockMenuCompartment, LidCompartment {
    public static final byte CONTAINER_OPEN = 1;
    public static final byte CONTAINER_CLOSE = 2;
    private static final Component CONTAINER_TITLE = Component.translatable("container.enderchest");
    private final ChestLidController chestLidController = new ChestLidController();
    private final ContainerOpenersCounter openersCounter = new ContainerOpenersCounter() {
        @Override
        protected void onOpen(final Level level, final BlockPos blockPos, final BlockState blockState) {
            EnderChestCompartmentEntity.this.playSound(SoundEvents.ENDER_CHEST_OPEN, SoundSource.BLOCKS, 0.5F,
                    level.random.nextFloat() * 0.1F + 0.9F);
        }

        @Override
        protected void onClose(final Level level, final BlockPos blockPos, final BlockState blockState) {
            EnderChestCompartmentEntity.this.playSound(SoundEvents.ENDER_CHEST_CLOSE, SoundSource.BLOCKS, 0.5F,
                    level.random.nextFloat() * 0.1F + 0.9F);
        }

        @Override
        protected void openerCountChanged(final Level level, final BlockPos blockPos, final BlockState blockState,
                final int count, final int openCount) {
            EnderChestCompartmentEntity.this.signalOpenCount(level, (byte) openCount);
        }

        @Override
        protected boolean isOwnContainer(final Player player) {
            return false;
        }
    };

    public EnderChestCompartmentEntity(final EntityType<? extends EnderChestCompartmentEntity> entityType,
            final Level level) {
        super(entityType, level);
    }

    @Override
    public void tick() {
        super.tick();

        this.chestLidController.tickLid();

        if (!this.isRemoved() && this.level().isClientSide()) {
            for (int i = 0; i < 2; ++i) {
                this.level()
                        .addParticle(ParticleTypes.PORTAL, this.getRandomX(0.5D), this.getRandomY() - 0.25D,
                                this.getRandomZ(0.5D), (this.random.nextDouble() - 0.5D) * 2, -this.random.nextDouble(),
                                (this.random.nextDouble() - 0.5D) * 2);
            }

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
    public InteractionResult interact(final Player player, final InteractionHand hand) {
        player.openMenu(this.getMenuProvider());
        this.gameEvent(GameEvent.CONTAINER_OPEN, player);
        player.awardStat(Stats.OPEN_ENDERCHEST);
        PiglinAi.angerNearbyPiglins(player, true);
        return InteractionResult.sidedSuccess(player.level().isClientSide());
    }

    public void startOpen(final Player player) {
        if (!this.isRemoved() && !player.isSpectator() || !this.isPassenger()) {
            this.openersCounter.incrementOpeners(player, this.level(), this.blockPosition(),
                    Blocks.AIR.defaultBlockState());
        }
    }

    public void stopOpen(final Player player) {
        if (!this.isRemoved() && !player.isSpectator() || !this.isPassenger()) {
            this.openersCounter.decrementOpeners(player, this.level(), this.blockPosition(),
                    Blocks.AIR.defaultBlockState());
        }
    }

    @Override
    public float getOpenNess(final float partialTicks) {
        return this.chestLidController.getOpenness(partialTicks);
    }

    private void signalOpenCount(final Level level, final byte openCount) {
        level.broadcastEntityEvent(this, openCount > 0 ? CONTAINER_OPEN : CONTAINER_CLOSE);
    }

    @Override
    protected void onPlaced() {
        CommonHelper.playPlaceSound(this::playSound, SoundType.STONE);
    }

    @Override
    protected void onHurt(final DamageSource damageSource) {
        CommonHelper.playHitSound(this::playSound, SoundType.STONE);
    }

    @Override
    protected void onBreak() {
        CommonHelper.playBreakSound(this::playSound, SoundType.STONE);
    }

    @Override
    public ItemStack getDropStack() {
        return new ItemStack(Blocks.ENDER_CHEST);
    }

    @Nullable
    @Override
    public ItemStack getPickResult() {
        return new ItemStack(Blocks.ENDER_CHEST);
    }

    @Override
    public int getCompartmentBlockLight() {
        return 7;
    }

    public double getBuoyancy() {
        return -0.02;
    }

    @Override
    public MenuProvider getMenuProvider() {
        return new SimpleMenuProvider(this::createMenu, CONTAINER_TITLE);
    }

    private AbstractContainerMenu createMenu(final int id, final Inventory playerInventory, final Player player) {
        return ChestMenu.threeRows(id, playerInventory,
                new EnderChestContainerWrapper(player.getEnderChestInventory()));
    }

    @AllArgsConstructor
    private class EnderChestContainerWrapper implements Container {
        @Delegate
        private final PlayerEnderChestContainer enderChestInventory;

        @Override
        public boolean stillValid(final Player player) {
            return CommonHelper.stillValidEntity(EnderChestCompartmentEntity.this, player);
        }

        @Override
        public void startOpen(final Player player) {
            EnderChestCompartmentEntity.this.startOpen(player);
        }

        @Override
        public void stopOpen(final Player player) {
            EnderChestCompartmentEntity.this.stopOpen(player);
        }
    }
}