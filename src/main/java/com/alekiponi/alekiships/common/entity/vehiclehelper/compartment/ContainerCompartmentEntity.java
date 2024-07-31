package com.alekiponi.alekiships.common.entity.vehiclehelper.compartment;

import com.alekiponi.alekiships.common.entity.vehiclehelper.CompartmentType;
import com.alekiponi.alekiships.util.CommonHelper;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.*;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nullable;

/**
 * This can be thought of as similar to {@link BaseContainerBlockEntity} but for compartments.
 * Use {@link ContainerMenuCompartmentEntity} for a simple {@link MenuProvider} implementation
 */
public abstract class ContainerCompartmentEntity extends AbstractCompartmentEntity implements Container, CompartmentCloneable {

    public static final String CUSTOM_NAME_KEY = "CustomName";
    /**
     * The slot count of this container
     */
    protected final int slotCount;
    /**
     * The container contents. You shouldn't usually modify this directly, instead rely on the {@link Container} interface
     */
    protected final NonNullList<ItemStack> itemStacks;
    private LazyOptional<IItemHandler> itemHandler = LazyOptional.of(this::createItemHandler);

    /**
     * @param slotCount The amount of slots the compartment should have
     */
    protected ContainerCompartmentEntity(final CompartmentType<? extends ContainerCompartmentEntity> compartmentType,
            final Level level, final int slotCount) {
        super(compartmentType, level);
        this.slotCount = slotCount;
        this.itemStacks = NonNullList.withSize(slotCount, ItemStack.EMPTY);
    }

    /**
     * @param slotCount The amount of slots the compartment should have
     * @param itemStack The {@link ItemStack} the compartment is being constructed from. This stack will be used to set
     *                  the custom name and if {@link BlockItem#getBlockEntityData(ItemStack)} returns a tag it'll be
     *                  passed to {@link #loadFromStackNBT(CompoundTag)}
     */
    protected ContainerCompartmentEntity(final CompartmentType<? extends ContainerCompartmentEntity> compartmentType,
            final Level level, final int slotCount, final ItemStack itemStack) {
        this(compartmentType, level, slotCount);
        if (itemStack.hasCustomHoverName()) {
            this.setCustomName(itemStack.getHoverName());
        }

        final CompoundTag blockEntityTag = BlockItem.getBlockEntityData(itemStack);
        if (blockEntityTag != null) this.loadFromStackNBT(blockEntityTag);
    }

    /**
     * Called from {@link ContainerCompartmentEntity} during construction to load values from NBT
     */
    protected void loadFromStackNBT(final CompoundTag compoundTag) {
        this.readContents(compoundTag);
        if (compoundTag.contains(CUSTOM_NAME_KEY, Tag.TAG_STRING)) {
            this.setCustomName(Component.Serializer.fromJson(compoundTag.getString(CUSTOM_NAME_KEY)));
        }
    }

    @Override
    public CompoundTag saveForItemStack() {
        final CompoundTag compoundTag = new CompoundTag();
        this.saveContents(compoundTag);

        if (this.hasCustomName()) {
            compoundTag.putString(CUSTOM_NAME_KEY, Component.Serializer.toJson(this.getCustomName()));
        }

        return compoundTag;
    }

    @Override
    protected void destroy(final DamageSource damageSource) {
        super.destroy(damageSource);
        if (this.level().getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            Containers.dropContents(this.level(), this, this);
            if (!this.level().isClientSide) {
                final Entity entity = damageSource.getDirectEntity();
                if (entity != null && entity.getType() == EntityType.PLAYER) {
                    PiglinAi.angerNearbyPiglins((Player) entity, true);
                }
            }
        }
    }

    @Override
    protected void onBreak() {
        CommonHelper.dropContents(this.level(), this.getX(), CommonHelper.maxHeightOfCollidableEntities(this),
                this.getZ(), this);
    }

    @Override
    public boolean stillValid(final Player player) {
        return !this.isRemoved() && this.position().closerThan(player.position(), 8);
    }

    @Override
    public void stopOpen(final Player player) {
        this.level().gameEvent(GameEvent.CONTAINER_CLOSE, this.position(), GameEvent.Context.of(player));
    }

    @Override
    protected void addAdditionalSaveData(final CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        this.saveContents(compoundTag);
    }

    @Override
    protected void readAdditionalSaveData(final CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.readContents(compoundTag);
    }

    /**
     * Saves the container contents.
     * <p>
     * Abstracted to allow children to easily use alternative forms of serialization.
     * Primarily useful for when the source compartment item uses something like {@link ItemStackHandler} or friends
     *
     * @param compoundTag The tag to save the container contents to
     */
    protected void saveContents(final CompoundTag compoundTag) {
        ContainerHelper.saveAllItems(compoundTag, this.itemStacks, false);
    }

    /**
     * Reads the container contents.
     * <p>
     * Abstracted to allow children to easily use alternative forms of serialization.
     * Primarily useful for when the source compartment item uses something like {@link ItemStackHandler} or friends
     *
     * @param compoundTag The tag to read the contents to
     */
    protected void readContents(final CompoundTag compoundTag) {
        ContainerHelper.loadAllItems(compoundTag, this.itemStacks);
    }

    @Override
    public void setChanged() {
    }

    @Override
    public void clearContent() {
        this.itemStacks.clear();
    }

    @Override
    public final int getContainerSize() {
        return this.slotCount;
    }

    @Override
    public ItemStack removeItem(final int slotIndex, final int amount) {
        final ItemStack itemStack = ContainerHelper.removeItem(this.itemStacks, slotIndex, amount);
        if (!itemStack.isEmpty()) {
            this.setChanged();
        }
        return itemStack;
    }

    @Override
    public ItemStack removeItemNoUpdate(final int slotIndex) {
        return ContainerHelper.takeItem(this.itemStacks, slotIndex);
    }

    @Override
    public void setItem(final int slotIndex, final ItemStack itemStack) {
        this.itemStacks.set(slotIndex, itemStack);
        if (!itemStack.isEmpty() && itemStack.getCount() > this.getMaxStackSize()) {
            itemStack.setCount(this.getMaxStackSize());
        }
        this.setChanged();
    }

    @Override
    public ItemStack getItem(final int slotIndex) {
        return this.itemStacks.get(slotIndex);
    }

    @Override
    public SlotAccess getSlot(final int slotIndex) {
        return slotIndex >= 0 && slotIndex < this.getContainerSize() ? new SlotAccess() {
            public ItemStack get() {
                return ContainerCompartmentEntity.this.getItem(slotIndex);
            }

            public boolean set(final ItemStack itemStack) {
                ContainerCompartmentEntity.this.setItem(slotIndex, itemStack);
                return true;
            }
        } : SlotAccess.NULL;
    }

    @Override
    public boolean isEmpty() {
        return this.itemStacks.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public <T> LazyOptional<T> getCapability(final Capability<T> capability, final @Nullable Direction facing) {
        if (this.isAlive() && capability == ForgeCapabilities.ITEM_HANDLER) return this.itemHandler.cast();
        return super.getCapability(capability, facing);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        this.itemHandler.invalidate();
    }

    @Override
    public void reviveCaps() {
        super.reviveCaps();
        this.itemHandler = LazyOptional.of(this::createItemHandler);
    }

    /**
     * Supplier for the generic, non sided {@link IItemHandler}
     *
     * @return An {@link IItemHandler} for this container.
     */
    protected IItemHandler createItemHandler() {
        return new InvWrapper(this);
    }

    /**
     * Simple {@link MenuProvider} implementation for {@link ContainerCompartmentEntity}
     */
    public abstract static class ContainerMenuCompartmentEntity extends ContainerCompartmentEntity implements MenuProvider {

        /**
         * @see ContainerCompartmentEntity#ContainerCompartmentEntity(CompartmentType, Level, int)
         */
        protected ContainerMenuCompartmentEntity(
                final CompartmentType<? extends ContainerMenuCompartmentEntity> compartmentType, final Level level,
                final int slotCount) {
            super(compartmentType, level, slotCount);
        }

        /**
         * @see ContainerCompartmentEntity#ContainerCompartmentEntity(CompartmentType, Level, int, ItemStack)
         */
        protected ContainerMenuCompartmentEntity(
                final CompartmentType<? extends ContainerMenuCompartmentEntity> compartmentType, final Level level,
                final int slotCount, final ItemStack itemStack) {
            super(compartmentType, level, slotCount, itemStack);
        }

        @Override
        public InteractionResult interact(final Player player, final InteractionHand hand) {
            player.openMenu(this);
            this.gameEvent(GameEvent.CONTAINER_OPEN, player);
            return InteractionResult.sidedSuccess(player.level().isClientSide);
        }

        @Nullable
        @Override
        public AbstractContainerMenu createMenu(final int id, final Inventory inventory, final Player player) {
            return this.createMenu(id, inventory);
        }

        abstract protected AbstractContainerMenu createMenu(final int id, final Inventory playerInventory);
    }
}