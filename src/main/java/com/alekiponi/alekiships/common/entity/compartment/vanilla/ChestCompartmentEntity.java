package com.alekiponi.alekiships.common.entity.compartment.vanilla;

import com.alekiponi.alekiships.common.entity.AlekiShipsEntities;
import com.alekiponi.alekiships.common.entity.compartment.CompartmentCloneable;
import com.alekiponi.alekiships.common.entity.compartment.ContainerOpenersCounter;
import com.alekiponi.alekiships.common.entity.compartment.LidCompartment;
import com.alekiponi.alekiships.common.entity.compartment.RandomizableContainerCompartmentEntity;
import com.alekiponi.alekiships.common.recipe.util.ItemStackProvider;
import com.alekiponi.alekiships.network.AlekiShipsEntityDataSerializers;
import com.alekiponi.alekiships.util.AlekiShipsExtraCodecs;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
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
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.ChestLidController;
import net.minecraft.world.phys.Vec3;

import java.util.Objects;
import org.jetbrains.annotations.Nullable;

public final class ChestCompartmentEntity extends RandomizableContainerCompartmentEntity.RandomizableContainerMenuCompartmentEntity implements LidCompartment {

    public static final byte CONTAINER_OPEN = 1;
    public static final byte CONTAINER_CLOSE = 2;
    public static final String CHEST_DATA_KEY = "chest_data";
    public static final String DROP_STACK_KEY = "drop_stack";

    private static final EntityDataAccessor<ChestCompartmentData> DATA_ID_CHEST_COMPARTMENT_DATA = SynchedEntityData.defineId(
            ChestCompartmentEntity.class, AlekiShipsEntityDataSerializers.CHEST_COMPARTMENT_DATA.get());
    private static final EntityDataAccessor<ItemStackProvider> DATA_ID_DROP_STACK = SynchedEntityData.defineId(
            ChestCompartmentEntity.class, AlekiShipsEntityDataSerializers.ITEM_STACK_PROVIDER.get());

    private final ChestLidController chestLidController = new ChestLidController();
    private final ContainerOpenersCounter openersCounter = new ContainerOpenersCounter() {
        @Override
        protected void onOpen(final Level level, final Vec3 pos) {
            ChestCompartmentEntity.this.playSound(SoundEvents.CHEST_OPEN, SoundSource.BLOCKS, 0.5F,
                    level.random.nextFloat() * 0.1F + 0.9F);
        }

        @Override
        protected void onClose(final Level level, final Vec3 pos) {
            ChestCompartmentEntity.this.playSound(SoundEvents.CHEST_CLOSE, SoundSource.BLOCKS, 0.5F,
                    level.random.nextFloat() * 0.1F + 0.9F);
        }

        @Override
        protected void openerCountChanged(final Level level, final int count, final int openCount) {
            ChestCompartmentEntity.this.signalOpenCount(level, (byte) openCount);
        }

        @Override
        protected boolean isOwnContainer(final Player player) {
            if (!(player.containerMenu instanceof ChestMenu)) return false;

            final Container container = ((ChestMenu) player.containerMenu).getContainer();
            return container == ChestCompartmentEntity.this;
        }
    };

    public ChestCompartmentEntity(final EntityType<ChestCompartmentEntity> entityType, final Level level) {
        this(entityType, level, ChestCompartmentData.VANILLA_CHEST_NORMAL);
    }

    private ChestCompartmentEntity(final EntityType<ChestCompartmentEntity> entityType, final Level level,
            final ChestCompartmentData chestCompartmentData) {
        super(entityType, level, chestCompartmentData.slotCount());
        this.entityData.set(DATA_ID_CHEST_COMPARTMENT_DATA, chestCompartmentData);
    }

    public static ChestCompartmentEntity create(final Level level,
            final @Nullable ChestCompartmentData chestCompartmentData, final ItemStack itemStack) {
        final var chestCompartment = new ChestCompartmentEntity(AlekiShipsEntities.CHEST_COMPARTMENT_ENTITY.get(),
                level, Objects.requireNonNullElse(chestCompartmentData, ChestCompartmentData.VANILLA_CHEST_NORMAL));
        CompartmentCloneable.initialize(chestCompartment, itemStack);
        chestCompartment.entityData.set(DATA_ID_DROP_STACK, ItemStackProvider.of(itemStack.getItem()));
        return chestCompartment;
    }

    @Override
    protected void defineSynchedData(final SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_ID_CHEST_COMPARTMENT_DATA, ChestCompartmentData.VANILLA_CHEST_NORMAL);
        builder.define(DATA_ID_DROP_STACK, ItemStackProvider.of(Items.CHEST));
    }

    @Override
    public void tick() {
        super.tick();

        this.chestLidController.tickLid();

        if (!this.isRemoved() && !this.level().isClientSide()) {
            this.openersCounter.recheckOpeners(this.level(), this.position());
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
        this.playSound(this.getChestCompartmentData().hurtSound(), SoundSource.BLOCKS,
                (this.getChestCompartmentData().soundVolume() + 1) / 8,
                this.getChestCompartmentData().soundPitch() * 0.5F);
    }

    @Override
    protected void onPlaced() {
        this.playSound(this.getChestCompartmentData().placeSound(), SoundSource.BLOCKS,
                (this.getChestCompartmentData().soundVolume() + 1) / 2,
                this.getChestCompartmentData().soundPitch() * 0.8F);
    }

    @Override
    protected void onBreak() {
        super.onBreak();
        this.playSound(this.getChestCompartmentData().breakSound(), SoundSource.BLOCKS,
                (this.getChestCompartmentData().soundVolume() + 1) / 2,
                this.getChestCompartmentData().soundPitch() * 0.8F);
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
    public boolean canPlaceItem(final int slot, final ItemStack stack) {
        return this.getChestCompartmentData().isItemValid(stack);
    }

    @Override
    protected AbstractContainerMenu createMenu(final int id, final Inventory playerInventory) {
        return this.getChestCompartmentData().createMenu(id, playerInventory, this);
    }

    @Override
    protected void addAdditionalSaveData(final CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        final var serializationContext = this.registryAccess().createSerializationContext(NbtOps.INSTANCE);
        AlekiShipsExtraCodecs.save(ChestCompartmentData.CODEC, serializationContext, this.getChestCompartmentData(),
                tag -> compoundTag.put(CHEST_DATA_KEY, tag));
        AlekiShipsExtraCodecs.save(ItemStackProvider.CODEC, serializationContext,
                this.entityData.get(DATA_ID_DROP_STACK), tag -> compoundTag.put(DROP_STACK_KEY, tag));
    }

    @Override
    protected void readAdditionalSaveData(final CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        final var serializationContext = this.registryAccess().createSerializationContext(NbtOps.INSTANCE);
        AlekiShipsExtraCodecs.load(ChestCompartmentData.CODEC, serializationContext,
                compoundTag.getCompound(CHEST_DATA_KEY),
                compartmentData -> this.entityData.set(DATA_ID_CHEST_COMPARTMENT_DATA, compartmentData));
        AlekiShipsExtraCodecs.load(ItemStackProvider.CODEC, serializationContext,
                compoundTag.getCompound(DROP_STACK_KEY),
                stackProvider -> this.entityData.set(DATA_ID_DROP_STACK, stackProvider));
    }

    @Override
    public float getOpenNess(final float partialTicks) {
        return this.chestLidController.getOpenness(partialTicks);
    }

    private void signalOpenCount(final Level level, final byte openCount) {
        level.broadcastEntityEvent(this, openCount > 0 ? CONTAINER_OPEN : CONTAINER_CLOSE);
    }

    public ChestCompartmentData getChestCompartmentData() {
        return this.entityData.get(DATA_ID_CHEST_COMPARTMENT_DATA);
    }

    @Override
    public ItemStack getDropStack() {
        return this.entityData.get(DATA_ID_DROP_STACK).get();
    }

    @Override
    public ItemStack getPickResult() {
        return this.entityData.get(DATA_ID_DROP_STACK).get();
    }
}