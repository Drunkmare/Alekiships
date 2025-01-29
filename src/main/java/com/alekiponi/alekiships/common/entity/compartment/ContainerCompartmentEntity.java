package com.alekiponi.alekiships.common.entity.compartment;

import com.alekiponi.alekiships.util.CommonHelper;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.*;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.neoforge.items.ItemStackHandler;

import javax.annotation.Nullable;
import java.util.function.Supplier;

/**
 * This can be thought of as similar to {@link BaseContainerBlockEntity} but for compartments.
 * Use {@link ContainerMenuCompartmentEntity} for a simple {@link MenuProvider} implementation
 */
public abstract class ContainerCompartmentEntity extends AbstractCompartmentEntity implements Container, CompartmentCloneable {

    /**
     * The slot count of this container
     */
    protected final int slotCount;
    /**
     * The container contents. You shouldn't usually modify this directly, instead rely on the {@link Container} interface
     */
    protected final NonNullList<ItemStack> itemStacks;

    /**
     * @param slotCount The amount of slots the compartment should have
     */
    protected ContainerCompartmentEntity(final EntityType<? extends ContainerCompartmentEntity> entityType,
            final Level level, final int slotCount) {
        super(entityType, level);
        this.slotCount = slotCount;
        this.itemStacks = NonNullList.withSize(slotCount, ItemStack.EMPTY);
    }

    /**
     * Applies the applicable {@link DataComponentType}s to the {@link ContainerCompartmentEntity}.
     * <p>
     * If you override this you'll want to override {@link #collectImplicitComponents(DataComponentMap.Builder)} too
     */
    protected void applyImplicitComponents(final DataComponentInput componentInput) {
        this.loadBlockEntityData(
                componentInput.getOrDefault(DataComponents.BLOCK_ENTITY_DATA, CustomData.EMPTY).copyTag());
        this.setCustomName(componentInput.get(DataComponents.CUSTOM_NAME));
        componentInput.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).copyInto(this.itemStacks);
    }

    @Override
    public final void applyComponentsFromItemStack(final ItemStack itemStack) {
        this.applyComponents(itemStack.getPrototype(), itemStack.getComponentsPatch());
    }

    /**
     * Called to load custom data serialized as {@link DataComponents#BLOCK_ENTITY_DATA} NBT
     */
    protected void loadBlockEntityData(final CompoundTag compoundTag) {
    }

    /**
     * Called to save custom data serialized as {@link DataComponents#BLOCK_ENTITY_DATA} NBT
     */
    protected void saveBlockEntityData(final CompoundTag compoundTag) {
    }

    @Override
    public final DataComponentMap collectComponents() {
        final var builder = DataComponentMap.builder();
        this.collectImplicitComponents(builder);
        return builder.build();
    }

    /**
     * Collects the components that are put into the cloned {@link ItemStack}
     * <p>
     * If you override this you'll want to override {@link #applyImplicitComponents(DataComponentInput)}} too
     */
    protected void collectImplicitComponents(final DataComponentMap.Builder builder) {
        builder.set(DataComponents.CUSTOM_NAME, this.getCustomName());
        builder.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(this.itemStacks));
        final CompoundTag compoundTag = new CompoundTag();
        this.saveBlockEntityData(compoundTag);
        builder.set(DataComponents.BLOCK_ENTITY_DATA, CustomData.of(compoundTag));
    }

    private void applyComponents(final DataComponentMap components, final DataComponentPatch patch) {
        final DataComponentMap componentPatch = PatchedDataComponentMap.fromPatch(components, patch);
        this.applyImplicitComponents(new DataComponentInput() {
            @Nullable
            @Override
            public <T> T get(final DataComponentType<T> component) {
                return componentPatch.get(component);
            }

            @Override
            public <T> T getOrDefault(final DataComponentType<? extends T> component, T defaultValue) {
                return componentPatch.getOrDefault(component, defaultValue);
            }
        });
    }

    @Override
    protected void destroy(final DamageSource damageSource) {
        super.destroy(damageSource);
        if (!this.level().isClientSide) {
            final Entity entity = damageSource.getDirectEntity();
            if (entity != null && entity.getType() == EntityType.PLAYER) {
                PiglinAi.angerNearbyPiglins((Player) entity, true);
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
        ContainerHelper.saveAllItems(compoundTag, this.itemStacks, false, this.registryAccess());
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
        ContainerHelper.loadAllItems(compoundTag, this.itemStacks, this.registryAccess());
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

    protected interface DataComponentInput {
        @Nullable
        <T> T get(final DataComponentType<T> component);

        <T> T getOrDefault(final DataComponentType<? extends T> component, final T defaultValue);

        @Nullable
        default <T> T get(final Supplier<? extends DataComponentType<T>> component) {
            return get(component.get());
        }

        @SuppressWarnings("unused")
        default <T> T getOrDefault(final Supplier<? extends DataComponentType<T>> component, final T defaultValue) {
            return getOrDefault(component.get(), defaultValue);
        }
    }

    /**
     * Simple {@link MenuProvider} implementation for {@link ContainerCompartmentEntity}
     */
    public abstract static class ContainerMenuCompartmentEntity extends ContainerCompartmentEntity implements MenuProvider {

        /**
         * @see ContainerCompartmentEntity#ContainerCompartmentEntity(EntityType, Level, int)
         */
        protected ContainerMenuCompartmentEntity(final EntityType<? extends ContainerMenuCompartmentEntity> entityType,
                final Level level, final int slotCount) {
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
        public AbstractContainerMenu createMenu(final int id, final Inventory inventory, final Player player) {
            return this.createMenu(id, inventory);
        }

        abstract protected AbstractContainerMenu createMenu(final int id, final Inventory playerInventory);
    }
}