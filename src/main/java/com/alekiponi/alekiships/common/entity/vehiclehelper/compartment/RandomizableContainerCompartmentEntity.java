package com.alekiponi.alekiships.common.entity.vehiclehelper.compartment;

import com.alekiponi.alekiships.common.entity.vehiclehelper.CompartmentType;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.ContainerEntity;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.Nullable;

/**
 * This can be thought of as similar to {@link RandomizableContainerBlockEntity} but for compartments.
 * Use {@link RandomizableContainerMenuCompartmentEntity} for a simple {@link MenuProvider} implementation
 */
public abstract class RandomizableContainerCompartmentEntity extends ContainerCompartmentEntity implements ContainerEntity {

    @Nullable
    private ResourceKey<LootTable> lootTable;
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
    public final ResourceKey<LootTable> getLootTable() {
        return this.lootTable;
    }

    @Override
    public final void setLootTable(@Nullable final ResourceKey<LootTable> lootTable) {
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
        this.addChestVehicleSaveData(compoundTag, this.registryAccess());
    }

    @Override
    protected void readContents(final CompoundTag compoundTag) {
        this.readChestVehicleSaveData(compoundTag, this.registryAccess());
    }

    /**
     * Simple {@link MenuProvider} implementation for {@link RandomizableContainerCompartmentEntity}
     */
    public abstract static class RandomizableContainerMenuCompartmentEntity extends RandomizableContainerCompartmentEntity implements MenuProvider {

        /**
         * @see RandomizableContainerCompartmentEntity#RandomizableContainerCompartmentEntity(CompartmentType, Level, int)
         */
        protected RandomizableContainerMenuCompartmentEntity(
                final CompartmentType<? extends RandomizableContainerMenuCompartmentEntity> compartmentType,
                final Level level, final int slotCount) {
            super(compartmentType, level, slotCount);
        }

        /**
         * @see RandomizableContainerCompartmentEntity#RandomizableContainerCompartmentEntity(CompartmentType, Level, int, ItemStack)
         */
        protected RandomizableContainerMenuCompartmentEntity(
                final CompartmentType<? extends RandomizableContainerMenuCompartmentEntity> compartmentType,
                final Level level, final int slotCount, final ItemStack itemStack) {
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
        public AbstractContainerMenu createMenu(final int id, final Inventory playerInventory, final Player player) {
            if (this.getLootTable() != null && player.isSpectator()) return null;

            this.unpackChestVehicleLootTable(playerInventory.player);
            return this.createMenu(id, playerInventory);
        }

        abstract protected AbstractContainerMenu createMenu(final int id, final Inventory playerInventory);
    }
}