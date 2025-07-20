package com.alekiponi.alekiships.common.entity.compartment.vanilla;

import com.alekiponi.alekiships.common.entity.AlekiShipsEntities;
import com.alekiponi.alekiships.common.entity.compartment.CompartmentCloneable;
import com.alekiponi.alekiships.common.entity.compartment.ContainerOpenersCounter;
import com.alekiponi.alekiships.common.entity.compartment.RandomizableContainerCompartmentEntity;
import com.alekiponi.alekiships.mixins.accessors.ShulkerBoxMenuAccessor;
import com.alekiponi.alekiships.util.AlekiShipsExtraCodecs;
import com.alekiponi.alekiships.util.CommonHelper;

import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ShulkerBoxMenu;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.ChestLidController;
import net.minecraft.world.phys.Vec3;

import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;

import javax.annotation.Nullable;
import java.util.stream.IntStream;

public class ShulkerBoxCompartmentEntity extends RandomizableContainerCompartmentEntity.RandomizableContainerMenuCompartmentEntity implements WorldlyContainer, IEntityWithComplexSpawn {

    public static final byte CONTAINER_OPEN = 1;
    public static final byte CONTAINER_CLOSE = 2;
    public static final String COLOR_KEY = "Color";
    public static final int SLOT_COUNT = 27;
    private static final int NULL_COLOR = -1;
    private static final int[] SLOTS = IntStream.range(0, SLOT_COUNT)
            .toArray();
    private final ChestLidController chestLidController = new ChestLidController();
    private final ContainerOpenersCounter openersCounter = new ContainerOpenersCounter() {
        @Override
        protected void onOpen(final Level level, final Vec3 pos) {
            ShulkerBoxCompartmentEntity.this.playSound(SoundEvents.SHULKER_BOX_OPEN, SoundSource.BLOCKS, 0.5F,
                    level.random.nextFloat() * 0.1F + 0.9F);
        }

        @Override
        protected void onClose(final Level level, final Vec3 pos) {
            ShulkerBoxCompartmentEntity.this.playSound(SoundEvents.SHULKER_BOX_CLOSE, SoundSource.BLOCKS, 0.5F,
                    level.random.nextFloat() * 0.1F + 0.9F);
        }

        @Override
        protected void openerCountChanged(final Level level, final int count, final int openCount) {
            ShulkerBoxCompartmentEntity.this.signalOpenCount(level, openCount);
        }

        @Override
        protected boolean isOwnContainer(final Player player) {
            if (!(player.containerMenu instanceof ShulkerBoxMenu)) return false;

            final Container container = ((ShulkerBoxMenuAccessor) player.containerMenu).getContainer();
            return container == ShulkerBoxCompartmentEntity.this;
        }
    };
    @Nullable
    private DyeColor color;

    public ShulkerBoxCompartmentEntity(final EntityType<? extends ShulkerBoxCompartmentEntity> entityType,
            final Level level) {
        super(entityType, level, SLOT_COUNT);
    }

    public static ShulkerBoxCompartmentEntity create(final Level level, final ItemStack itemStack,
            final @Nullable DyeColor color) {
        final ShulkerBoxCompartmentEntity shulkerBoxCompartmentEntity = new ShulkerBoxCompartmentEntity(
                AlekiShipsEntities.SHULKER_BOX_COMPARTMENT_ENTITY.get(), level);
        CompartmentCloneable.initialize(shulkerBoxCompartmentEntity, itemStack);
        shulkerBoxCompartmentEntity.color = color;

        return shulkerBoxCompartmentEntity;
    }

    @Override
    public double getBuoyancy() {
        return this.tickCount % 21 > 10 ? -0.01 : 0.01;
    }

    @Override
    public void tick() {
        super.tick();

        this.chestLidController.tickLid();

        if (!this.isRemoved() && this.level().isClientSide()) {
            this.openersCounter.recheckOpeners(this.level(), this.position());
        }
    }

    @Override
    public void chestVehicleDestroyed(final DamageSource damageSource, final Level level, final Entity entity) {
        if (level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            if (!level.isClientSide) {
                final Entity directEntity = damageSource.getDirectEntity();
                if (directEntity != null && directEntity.getType() == EntityType.PLAYER) {
                    PiglinAi.angerNearbyPiglins((Player) directEntity, true);
                }
            }
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
    protected void addAdditionalSaveData(final CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        if (this.color != null) {
            AlekiShipsExtraCodecs.save(DyeColor.CODEC, NbtOps.INSTANCE, this.color,
                    tag -> compoundTag.put(COLOR_KEY, tag));
        }
    }

    @Override
    protected void readAdditionalSaveData(final CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        final var color = compoundTag.get(COLOR_KEY);
        if (color != null) {
            AlekiShipsExtraCodecs.load(DyeColor.CODEC, NbtOps.INSTANCE, color, c -> this.color = c);
        }
    }

    @Override
    public void writeSpawnData(final RegistryFriendlyByteBuf buffer) {
        buffer.writeByte(this.color == null ? NULL_COLOR : color.getId());
    }

    @Override
    public void readSpawnData(final RegistryFriendlyByteBuf additionalData) {
        final byte colorID = additionalData.readByte();
        if (NULL_COLOR != colorID) {
            this.color = DyeColor.byId(colorID);
        }
    }

    @Override
    public void startOpen(final Player player) {
        if (!this.isRemoved() && !player.isSpectator() || !this.isPassenger()) {
            this.openersCounter.incrementOpeners(player, this.level(), this.position());
        }
    }

    @Override
    public void stopOpen(final Player player) {
        if (!this.isRemoved() && !player.isSpectator() || !this.isPassenger()) {
            this.openersCounter.decrementOpeners(player, this.level(), this.position());
        }
    }

    @Override
    public boolean canPlaceItem(final int slotIndex, final ItemStack itemStack) {
        return !(Block.byItem(itemStack.getItem()) instanceof ShulkerBoxBlock) && itemStack.getItem()
                .canFitInsideContainerItems();
    }

    @Override
    protected AbstractContainerMenu createMenu(final int id, final Inventory playerInventory) {
        return new ShulkerBoxMenu(id, playerInventory, this);
    }

    @Override
    public ItemStack getDropStack() {
        final ItemStack dropStack = new ItemStack(ShulkerBoxBlock.getBlockByColor(this.color));

        dropStack.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(this.itemStacks));

        return dropStack;
    }

    @Override
    public ItemStack getPickResult() {
        return new ItemStack(ShulkerBoxBlock.getBlockByColor(this.color));
    }

    public float getOpenNess(final float partialTicks) {
        return this.chestLidController.getOpenness(partialTicks);
    }

    private void signalOpenCount(final Level level, final int openCount) {
        level.broadcastEntityEvent(this, openCount > 0 ? CONTAINER_OPEN : CONTAINER_CLOSE);
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
    public int[] getSlotsForFace(Direction pSide) {
        return SLOTS;
    }

    @Override
    public boolean canPlaceItemThroughFace(final int slotIndex, final ItemStack itemStack,
            final @Nullable Direction direction) {
        return !(Block.byItem(itemStack.getItem()) instanceof ShulkerBoxBlock) && itemStack.getItem()
                .canFitInsideContainerItems(); // FORGE: Make shulker boxes respect Item#canFitInsideContainerItems
    }

    @Override
    public boolean canTakeItemThroughFace(final int slotIndex, final ItemStack itemStack, final Direction direction) {
        return true;
    }

    @Override
    protected void onPlaced() {
        CommonHelper.playPlaceSound(this::playSound, SoundType.STONE);
    }

    @Nullable
    public DyeColor getColor() {
        return this.color;
    }
}