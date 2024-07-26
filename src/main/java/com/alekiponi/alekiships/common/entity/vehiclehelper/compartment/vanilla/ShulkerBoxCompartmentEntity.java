package com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.vanilla;

import com.alekiponi.alekiships.common.entity.vehiclehelper.CompartmentType;
import com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.RandomizableContainerCompartmentEntity;
import com.alekiponi.alekiships.util.CommonHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ShulkerBoxMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.ChestLidController;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.stream.IntStream;

public class ShulkerBoxCompartmentEntity extends RandomizableContainerCompartmentEntity implements WorldlyContainer, IEntityAdditionalSpawnData {

    public static final byte CONTAINER_OPEN = 1;
    public static final byte CONTAINER_CLOSE = 2;
    public static final String COLOR_KEY = "Color";
    public static final int SLOT_COUNT = 27;
    private static final int NULL_COLOR = -1;
    private static final int[] SLOTS = IntStream.range(0, SLOT_COUNT).toArray();
    private final ChestLidController chestLidController = new ChestLidController();
    private final ContainerOpenersCounter openersCounter = new ContainerOpenersCounter() {
        @Override
        protected void onOpen(final Level level, final BlockPos blockPos, final BlockState blockState) {
            ShulkerBoxCompartmentEntity.this.playSound(SoundEvents.SHULKER_BOX_OPEN, SoundSource.BLOCKS, 0.5F,
                    level.random.nextFloat() * 0.1F + 0.9F);
        }

        @Override
        protected void onClose(final Level level, final BlockPos blockPos, final BlockState blockState) {
            ShulkerBoxCompartmentEntity.this.playSound(SoundEvents.SHULKER_BOX_CLOSE, SoundSource.BLOCKS, 0.5F,
                    level.random.nextFloat() * 0.1F + 0.9F);
        }

        @Override
        protected void openerCountChanged(final Level level, final BlockPos blockPos, final BlockState blockState,
                final int count, final int openCount) {
            ShulkerBoxCompartmentEntity.this.signalOpenCount(level, openCount);
        }

        @Override
        protected boolean isOwnContainer(final Player player) {
            if (!(player.containerMenu instanceof ShulkerBoxMenu)) return false;

            final Container container = ((ShulkerBoxMenu) player.containerMenu).container;
            return container == ShulkerBoxCompartmentEntity.this;
        }
    };
    @Nullable
    private DyeColor color;

    @Override
    public double getBuoyancy() {
        return this.tickCount % 21 > 10 ? -0.01 : 0.01;
    }

    public ShulkerBoxCompartmentEntity(final CompartmentType<? extends ShulkerBoxCompartmentEntity> compartmentType,
            final Level level) {
        super(compartmentType, level, SLOT_COUNT);
    }

    public ShulkerBoxCompartmentEntity(final CompartmentType<? extends ShulkerBoxCompartmentEntity> compartmentType,
            final Level level, final ItemStack itemStack) {
        super(compartmentType, level, SLOT_COUNT, itemStack);

        if (itemStack.getItem() instanceof BlockItem blockItem) {
            if (blockItem.getBlock() instanceof ShulkerBoxBlock shulkerBoxBlock) {
                this.color = shulkerBoxBlock.getColor();
            }
        }
    }

    @Override
    public void tick() {
        super.tick();

        this.chestLidController.tickLid();

        if (!this.isRemoved() && this.level().isClientSide()) {
            this.openersCounter.recheckOpeners(this.level(), this.blockPosition(), Blocks.AIR.defaultBlockState());
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
        if (this.color == null) {
            compoundTag.putInt(COLOR_KEY, NULL_COLOR);
        } else {
            compoundTag.putInt(COLOR_KEY, color.getId());
        }
    }

    @Override
    protected void readAdditionalSaveData(final CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        final int colorID = compoundTag.getInt(COLOR_KEY);

        if (NULL_COLOR != colorID) {
            this.color = DyeColor.byId(colorID);
        }
    }

    @Override
    public void writeSpawnData(final FriendlyByteBuf buffer) {
        buffer.writeByte(this.color == null ? NULL_COLOR : color.getId());
    }

    @Override
    public void readSpawnData(final FriendlyByteBuf additionalData) {
        final byte colorID = additionalData.readByte();
        if (NULL_COLOR != colorID) {
            this.color = DyeColor.byId(colorID);
        }
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
        final CompoundTag compoundTag = new CompoundTag();

        ContainerHelper.saveAllItems(compoundTag, this.getItemStacks(), false);

        if (!compoundTag.isEmpty()) {
            dropStack.addTagElement(BlockItem.BLOCK_ENTITY_TAG, compoundTag);
        }

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
        if (this.level().getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            final ItemStack itemStack = this.getDropStack();
            if (this.hasCustomName()) {
                itemStack.setHoverName(this.getCustomName());
            }

            Containers.dropItemStack(this.level(), this.getX(), CommonHelper.maxHeightOfCollidableEntities(this),
                    this.getZ(), itemStack);
        }
    }

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

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Nullable
    public DyeColor getColor() {
        return this.color;
    }

    @Override
    protected IItemHandler createItemHandler() {
        return new SidedInvWrapper(this, Direction.UP);
    }
}