package com.alekiponi.alekiships.common.entity.vehiclehelper.compartment;

import com.alekiponi.alekiships.common.entity.vehiclehelper.CompartmentType;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.ContainerEntity;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import org.jetbrains.annotations.Nullable;

/**
 * This can be thought of as similar to {@link RandomizableContainerBlockEntity} but for compartments.
 */
public abstract class RandomizableContainerCompartmentEntity extends ContainerCompartmentEntity implements ContainerEntity {

    public static final String LOOT_TABLE_TAG = RandomizableContainerBlockEntity.LOOT_TABLE_TAG;
    public static final String LOOT_TABLE_SEED_TAG = RandomizableContainerBlockEntity.LOOT_TABLE_SEED_TAG;
    @Nullable
    private ResourceLocation lootTable;
    private long lootTableSeed;

    /**
     * @see ContainerCompartmentEntity#ContainerCompartmentEntity(CompartmentType, Level, int)
     */
    protected RandomizableContainerCompartmentEntity(
            final CompartmentType<? extends RandomizableContainerCompartmentEntity> compartmentType, final Level level,
            final int slotCount) {
        super(compartmentType, level, slotCount);
    }

    /**
     * @see ContainerCompartmentEntity#ContainerCompartmentEntity(CompartmentType, Level, int, ItemStack)
     */
    protected RandomizableContainerCompartmentEntity(
            final CompartmentType<? extends RandomizableContainerCompartmentEntity> compartmentType, final Level level,
            final int slotCount, final ItemStack itemStack) {
        super(compartmentType, level, slotCount, itemStack);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(final int id, final Inventory playerInventory, final Player player) {
        if (this.lootTable != null && player.isSpectator()) return null;

        this.unpackChestVehicleLootTable(playerInventory.player);
        return this.createMenu(id, playerInventory);
    }

    @Override
    public ItemStack removeItem(final int slotIndex, final int amount) {
        this.unpackChestVehicleLootTable(null);
        return super.removeItem(slotIndex, amount);
    }

    @Override
    public void clearContent() {
        this.unpackChestVehicleLootTable(null);
        super.clearContent();
    }

    @Override
    public ItemStack removeItemNoUpdate(final int slotIndex) {
        this.unpackChestVehicleLootTable(null);
        return super.removeItemNoUpdate(slotIndex);
    }

    @Override
    public void setItem(final int slotIndex, final ItemStack itemStack) {
        this.unpackChestVehicleLootTable(null);
        super.setItem(slotIndex, itemStack);
    }

    @Override
    public ItemStack getItem(final int slotIndex) {
        this.unpackChestVehicleLootTable(null);
        return super.getItem(slotIndex);
    }

    @Override
    public SlotAccess getSlot(final int slotIndex) {
        this.unpackChestVehicleLootTable(null);
        return super.getSlot(slotIndex);
    }

    @Nullable
    @Override
    @SuppressWarnings("unused")
    public final ResourceLocation getLootTable() {
        return this.lootTable;
    }

    @Override
    public final void setLootTable(@Nullable final ResourceLocation lootTable) {
        this.lootTable = lootTable;
    }

    @Override
    @SuppressWarnings("unused")
    public final long getLootTableSeed() {
        return this.lootTableSeed;
    }

    @Override
    public final void setLootTableSeed(final long lootTableSeed) {
        this.lootTableSeed = lootTableSeed;
    }

    @Override
    public final NonNullList<ItemStack> getItemStacks() {
        return this.itemStacks;
    }

    @Override
    public final void clearItemStacks() {
        this.itemStacks.clear();
    }

    @Override
    protected void saveContents(final CompoundTag compoundTag) {
        if (this.lootTable == null) {
            super.saveContents(compoundTag);
            return;
        }

        compoundTag.putString(LOOT_TABLE_TAG, this.lootTable.toString());
        if (this.lootTableSeed != 0) {
            compoundTag.putLong(LOOT_TABLE_SEED_TAG, this.lootTableSeed);
        }
    }

    @Override
    protected void readContents(final CompoundTag compoundTag) {
        this.itemStacks.clear();
        if (!compoundTag.contains(LOOT_TABLE_TAG, Tag.TAG_STRING)) {
            ContainerHelper.loadAllItems(compoundTag, this.itemStacks);
            return;
        }

        this.setLootTable(new ResourceLocation(compoundTag.getString(LOOT_TABLE_TAG)));
        this.setLootTableSeed(compoundTag.getLong(LOOT_TABLE_SEED_TAG));
    }
}