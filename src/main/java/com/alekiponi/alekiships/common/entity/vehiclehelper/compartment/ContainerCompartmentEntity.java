package com.alekiponi.alekiships.common.entity.vehiclehelper.compartment;

import com.alekiponi.alekiships.common.entity.vehiclehelper.CompartmentType;
import com.alekiponi.alekiships.util.CommonHelper;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecartContainer;
import net.minecraft.world.entity.vehicle.ContainerEntity;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nullable;

/**
 * This can be thought of as similar to {@link AbstractMinecartContainer} but for compartments.
 * You should be aware of {@link #loadFromStackNBT(CompoundTag)} which will be called to load the contents of an {@link ItemStack}s
 * {@value BlockItem#BLOCK_ENTITY_TAG} tag if present on the stack passed into the constructor.
 * <p>
 * As this also implements {@link CompartmentCloneable} you may need to override {@link #saveForItemStack()} to ensure
 * the compartment is correctly cloned in creative mode.
 */
public abstract class ContainerCompartmentEntity extends AbstractCompartmentEntity implements ContainerEntity, CompartmentCloneable {

    public static final String CUSTOM_NAME_KEY = "CustomName";
    private final int slotCount;
    private NonNullList<ItemStack> itemStacks;
    @Nullable
    private ResourceLocation lootTable;
    private long lootTableSeed;
    private LazyOptional<?> itemHandler = LazyOptional.of(() -> new InvWrapper(this));

    protected ContainerCompartmentEntity(final CompartmentType<? extends ContainerCompartmentEntity> compartmentType,
            final Level level, final int slotCount) {
        super(compartmentType, level);
        this.slotCount = slotCount;
        this.itemStacks = NonNullList.withSize(slotCount, ItemStack.EMPTY);
    }

    protected ContainerCompartmentEntity(final CompartmentType<? extends ContainerCompartmentEntity> compartmentType,
            final Level level, final int slotCount, final ItemStack itemStack) {
        this(compartmentType, level, slotCount);
        if (itemStack.hasCustomHoverName()) {
            this.setCustomName(itemStack.getHoverName());
        }

        final CompoundTag blockEntityTag = itemStack.getTagElement(BlockItem.BLOCK_ENTITY_TAG);
        if (blockEntityTag != null) this.loadFromStackNBT(blockEntityTag);
    }

    /**
     * Called from {@link ContainerCompartmentEntity} during construction to load values from NBT
     */
    public void loadFromStackNBT(final CompoundTag compoundTag) {
        ContainerHelper.loadAllItems(compoundTag, this.getItemStacks());
        if (compoundTag.contains(CUSTOM_NAME_KEY, Tag.TAG_STRING)) {
            this.setCustomName(Component.Serializer.fromJson(compoundTag.getString(CUSTOM_NAME_KEY)));
        }
    }

    @Override
    public CompoundTag saveForItemStack() {
        final CompoundTag compoundTag = new CompoundTag();
        ContainerHelper.saveAllItems(compoundTag, this.getItemStacks(), false);

        if (this.hasCustomName()) {
            compoundTag.putString(CUSTOM_NAME_KEY, Component.Serializer.toJson(this.getCustomName()));
        }
        return compoundTag;
    }

    @Override
    public InteractionResult interact(final Player player, final InteractionHand hand) {
        final InteractionResult interactionResult = this.interactWithContainerVehicle(player);
        if (interactionResult.consumesAction()) {
            this.gameEvent(GameEvent.CONTAINER_OPEN, player);
        }

        return interactionResult;
    }

    @Override
    protected void destroy(final DamageSource damageSource) {
        super.destroy(damageSource);
        this.chestVehicleDestroyed(damageSource, this.level(), this);
    }

    @Override
    public void remove(final RemovalReason removalReason) {
        if (!this.level().isClientSide && removalReason.shouldDestroy()) {
            double y = this.getRootVehicle().getBoundingBox().maxY + 0.6;
            if(y > this.getY()){
                CommonHelper.dropContents(this.level(), this.getX(), y, this.getZ(), this);
            } else {
                Containers.dropContents(this.level(), this, this);
            }
        }

        super.remove(removalReason);
        this.invalidateCaps();
    }

    @Override
    public boolean stillValid(final Player player) {
        return this.isChestVehicleStillValid(player);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(final int id, final Inventory playerInventory, final Player player) {
        if (this.lootTable != null && player.isSpectator()) return null;

        this.unpackChestVehicleLootTable(playerInventory.player);
        return this.createMenu(id, playerInventory);
    }

    abstract protected AbstractContainerMenu createMenu(final int id, final Inventory playerInventory);

    @Override
    public void stopOpen(final Player player) {
        this.level().gameEvent(GameEvent.CONTAINER_CLOSE, this.position(), GameEvent.Context.of(player));
    }

    @Override
    protected void addAdditionalSaveData(final CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        this.addChestVehicleSaveData(compoundTag);
    }

    @Override
    protected void readAdditionalSaveData(final CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.readChestVehicleSaveData(compoundTag);
    }

    @Override
    public void setChanged() {
    }

    @Override
    public void clearContent() {
        this.clearChestVehicleContent();
    }

    @Override
    public int getContainerSize() {
        return this.slotCount;
    }

    @Override
    public NonNullList<ItemStack> getItemStacks() {
        return this.itemStacks;
    }

    @Override
    public void clearItemStacks() {
        this.itemStacks = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
    }

    @Override
    public ItemStack removeItem(final int slotIndex, final int amount) {
        return this.removeChestVehicleItem(slotIndex, amount);
    }

    @Override
    public ItemStack removeItemNoUpdate(final int slotIndex) {
        return this.removeChestVehicleItemNoUpdate(slotIndex);
    }

    @Override
    public void setItem(final int slotIndex, final ItemStack itemStack) {
        this.setChestVehicleItem(slotIndex, itemStack);
    }

    @Override
    public ItemStack getItem(final int slotIndex) {
        return this.getChestVehicleItem(slotIndex);
    }

    @Override
    public SlotAccess getSlot(final int slotIndex) {
        return this.getChestVehicleSlot(slotIndex);
    }

    @Nullable
    @Override
    public ResourceLocation getLootTable() {
        return this.lootTable;
    }

    @Override
    public void setLootTable(@Nullable final ResourceLocation lootTable) {
        this.lootTable = lootTable;
    }

    @Override
    public long getLootTableSeed() {
        return this.lootTableSeed;
    }

    @Override
    public void setLootTableSeed(final long lootTableSeed) {
        this.lootTableSeed = lootTableSeed;
    }

    @Override
    public <T> LazyOptional<T> getCapability(final Capability<T> capability, final @Nullable Direction facing) {
        if (this.isAlive() && capability == ForgeCapabilities.ITEM_HANDLER) return itemHandler.cast();
        return super.getCapability(capability, facing);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        itemHandler.invalidate();
    }

    @Override
    public void reviveCaps() {
        super.reviveCaps();
        itemHandler = LazyOptional.of(() -> new InvWrapper(this));
    }
}