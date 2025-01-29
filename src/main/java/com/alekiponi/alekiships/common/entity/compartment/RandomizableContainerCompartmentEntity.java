package com.alekiponi.alekiships.common.entity.compartment;

import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.ContainerEntity;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.SeededContainerLoot;
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
     * @see ContainerCompartmentEntity#ContainerCompartmentEntity(EntityType, Level, int)
     */
    protected RandomizableContainerCompartmentEntity(
            final EntityType<? extends RandomizableContainerCompartmentEntity> entityType, final Level level,
            final int slotCount) {
        super(entityType, level, slotCount);
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
    protected void applyImplicitComponents(final DataComponentInput componentInput) {
        super.applyImplicitComponents(componentInput);
        final SeededContainerLoot seededContainerLoot = componentInput.get(DataComponents.CONTAINER_LOOT);
        if (seededContainerLoot != null) {
            this.lootTable = seededContainerLoot.lootTable();
            this.lootTableSeed = seededContainerLoot.seed();
        }
    }

    @Override
    protected void collectImplicitComponents(final DataComponentMap.Builder builder) {
        super.collectImplicitComponents(builder);
        if (this.lootTable != null) {
            builder.set(DataComponents.CONTAINER_LOOT, new SeededContainerLoot(this.lootTable, this.lootTableSeed));
        }
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
         * @see RandomizableContainerCompartmentEntity#RandomizableContainerCompartmentEntity(EntityType, Level, int)
         */
        protected RandomizableContainerMenuCompartmentEntity(
                final EntityType<? extends RandomizableContainerMenuCompartmentEntity> entityType, final Level level,
                final int slotCount) {
            super(entityType, level, slotCount);
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