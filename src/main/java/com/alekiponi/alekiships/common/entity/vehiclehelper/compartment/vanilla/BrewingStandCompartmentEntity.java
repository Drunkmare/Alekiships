package com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.vanilla;

import com.alekiponi.alekiships.common.entity.vehiclehelper.CompartmentType;
import com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.BlockCompartment;
import com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.ContainerCompartmentEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.Containers;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.BrewingStandMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BrewingStandBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

import javax.annotation.Nullable;
import java.util.Arrays;

import static net.minecraft.world.level.block.entity.BrewingStandBlockEntity.NUM_DATA_VALUES;

public class BrewingStandCompartmentEntity extends ContainerCompartmentEntity implements WorldlyContainer, BlockCompartment {
    public static final int SLOT_COUNT = 5;
    private static final int INGREDIENT_SLOT = 3;
    private static final int FUEL_SLOT = 4;
    private static final int[] SLOTS_FOR_UP = new int[]{3};
    private static final int[] SLOTS_FOR_DOWN = new int[]{0, 1, 2, 3};
    private static final int[] SLOTS_FOR_SIDES = new int[]{0, 1, 2, 4};
    private static final EntityDataAccessor<BlockState> DATA_ID_DISPLAY_BLOCK = SynchedEntityData.defineId(
            BrewingStandCompartmentEntity.class, EntityDataSerializers.BLOCK_STATE);
    protected int brewTime;
    protected int fuel;
    protected final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(final int dataType) {
            return switch (dataType) {
                case 0 -> BrewingStandCompartmentEntity.this.brewTime;
                case 1 -> BrewingStandCompartmentEntity.this.fuel;
                default -> 0;
            };
        }

        @Override
        public void set(final int dataType, final int dataValue) {
            switch (dataType) {
                case 0:
                    BrewingStandCompartmentEntity.this.brewTime = dataValue;
                    break;
                case 1:
                    BrewingStandCompartmentEntity.this.fuel = dataValue;
            }
        }

        @Override
        public int getCount() {
            return NUM_DATA_VALUES;
        }
    };
    private LazyOptional<? extends IItemHandler>[] directionalHandlers = SidedInvWrapper.create(this, Direction.UP,
            Direction.DOWN, Direction.NORTH);
    @Nullable
    private boolean[] lastPotionCount;
    @Nullable
    private Item ingredient;

    public BrewingStandCompartmentEntity(final EntityType<? extends ContainerCompartmentEntity> entityType,
            final Level level) {
        super(entityType, level, SLOT_COUNT);
    }

    public BrewingStandCompartmentEntity(final CompartmentType<? extends ContainerCompartmentEntity> entityType,
            final Level level, final ItemStack itemStack) {
        super(entityType, level, SLOT_COUNT, itemStack);

        if (itemStack.getItem() instanceof BlockItem blockItem) {
            this.setDisplayBlockState(blockItem.getBlock().defaultBlockState());
        }
    }

    private static void doBrew(final Level level, final BlockPos blockPos, final NonNullList<ItemStack> itemStacks) {
        if (ForgeEventFactory.onPotionAttemptBrew(itemStacks)) return;
        ItemStack itemstack = itemStacks.get(INGREDIENT_SLOT);

        BrewingRecipeRegistry.brewPotions(itemStacks, itemstack, SLOTS_FOR_SIDES);
        ForgeEventFactory.onPotionBrewed(itemStacks);
        if (itemstack.hasCraftingRemainingItem()) {
            final ItemStack craftingRemainder = itemstack.getCraftingRemainingItem();
            itemstack.shrink(1);

            if (itemstack.isEmpty()) {
                itemStacks.set(INGREDIENT_SLOT, craftingRemainder);
                level.levelEvent(1035, blockPos, 0);
                return;
            }

            Containers.dropItemStack(level, blockPos.getX(), blockPos.getY(), blockPos.getZ(), craftingRemainder);

            itemStacks.set(INGREDIENT_SLOT, itemstack);
            level.levelEvent(1035, blockPos, 0);
            return;
        }

        itemstack.shrink(1);
        itemStacks.set(INGREDIENT_SLOT, itemstack);
        level.levelEvent(1035, blockPos, 0);
    }

    private static boolean isBrewable(final NonNullList<ItemStack> itemStacks) {
        final ItemStack itemstack = itemStacks.get(INGREDIENT_SLOT);
        if (!itemstack.isEmpty()) return BrewingRecipeRegistry.canBrew(itemStacks, itemstack, SLOTS_FOR_SIDES);

        if (itemstack.isEmpty()) return false;

        if (!PotionBrewing.isIngredient(itemstack)) {
            return false;
        }

        for (int i = 0; i < 3; ++i) {
            final ItemStack itemStack = itemStacks.get(i);
            if (!itemStack.isEmpty() && PotionBrewing.hasMix(itemStack, itemstack)) return true;
        }

        return false;
    }

    @Override
    public void tick() {
        super.tick();

        // Client should only do rendering stuff and not worry about the brewing logic
        if (this.level().isClientSide()) {
            if (this.isAlive() && this.tickCount % 5 == 0) {
                final double x = this.getX() - 0.1 + this.random.nextDouble() * 0.2;
                final double y = this.getY() + 0.5 + this.random.nextDouble() * 0.3;
                final double z = this.getZ() - 0.1 + this.random.nextDouble() * 0.2;
                this.level().addParticle(ParticleTypes.SMOKE, x, y, z, 0, 0, 0);
            }
            return;
        }

        {
            final ItemStack fuelStack = this.getItem(FUEL_SLOT);

            if (this.fuel <= 0 && fuelStack.is(Items.BLAZE_POWDER)) {
                this.fuel = 20;
                fuelStack.shrink(1);
            }
        }

        final boolean canBrew = isBrewable(this.getItemStacks());
        final ItemStack ingredientStack = this.getItem(INGREDIENT_SLOT);
        if (this.brewTime > 0) {
            --this.brewTime;
            if (this.brewTime == 0 && canBrew) {
                doBrew(this.level(), this.blockPosition(), this.getItemStacks());
            } else if (!canBrew || !ingredientStack.is(this.ingredient)) {
                this.brewTime = 0;
            }
        } else if (canBrew && this.fuel > 0) {
            --this.fuel;
            this.brewTime = 400;
            this.ingredient = ingredientStack.getItem();
        }

        final boolean[] potionBits = this.getPotionBits();
        if (!Arrays.equals(potionBits, this.lastPotionCount)) {
            this.lastPotionCount = potionBits;
            BlockState blockstate = this.getDisplayBlockState();
            if (!(blockstate.getBlock() instanceof BrewingStandBlock)) {
                return;
            }

            for (int i = 0; i < BrewingStandBlock.HAS_BOTTLE.length; ++i) {
                blockstate = blockstate.setValue(BrewingStandBlock.HAS_BOTTLE[i], potionBits[i]);
            }

            this.setDisplayBlockState(blockstate);
        }
    }

    @Override
    public void remove(final RemovalReason removalReason) {
        if (!this.level().isClientSide() && removalReason.shouldDestroy()) {
            this.playBreakSound();
        }
        super.remove(removalReason);
    }

    /**
     * @return an array of size 3 where every element represents whether the respective slot is not empty
     */
    private boolean[] getPotionBits() {
        final boolean[] potionBits = new boolean[3];

        for (int i = 0; i < 3; ++i) {
            if (!this.getItem(i).isEmpty()) {
                potionBits[i] = true;
            }
        }

        return potionBits;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_ID_DISPLAY_BLOCK, Blocks.AIR.defaultBlockState());
    }

    @Override
    protected void playHurtSound(final DamageSource damageSource) {
        this.playHitSound();
    }

    @Override
    protected void onPlaced() {
        this.playPlaceSound();
    }

    @Override
    public boolean canPlaceItem(final int slotIndex, final ItemStack itemStack) {
        if (slotIndex == INGREDIENT_SLOT) {
            return BrewingRecipeRegistry.isValidIngredient(itemStack);
        }

        if (slotIndex == FUEL_SLOT) {
            return itemStack.is(Items.BLAZE_POWDER);
        }

        return BrewingRecipeRegistry.isValidInput(itemStack) && this.getItem(slotIndex).isEmpty();
    }

    @Override
    public int[] getSlotsForFace(final Direction direction) {
        if (direction == Direction.UP) return SLOTS_FOR_UP;

        return direction == Direction.DOWN ? SLOTS_FOR_DOWN : SLOTS_FOR_SIDES;
    }

    @Override
    public boolean canPlaceItemThroughFace(final int slotIndex, final ItemStack itemStack,
            @Nullable final Direction direction) {
        return this.canPlaceItem(slotIndex, itemStack);
    }

    @Override
    public boolean canTakeItemThroughFace(final int slotIndex, final ItemStack itemStack, final Direction direction) {
        return slotIndex != INGREDIENT_SLOT || itemStack.is(Items.GLASS_BOTTLE);
    }

    @Override
    public BlockState getDisplayBlockState() {
        return this.entityData.get(DATA_ID_DISPLAY_BLOCK);
    }

    @Override
    public void setDisplayBlockState(final BlockState blockState) {
        this.entityData.set(DATA_ID_DISPLAY_BLOCK, blockState);
    }

    @Override
    protected void readAdditionalSaveData(final CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.loadCommonNBTData(compoundTag);
        this.setDisplayBlockState(NbtUtils.readBlockState(this.level().holderLookup(Registries.BLOCK),
                compoundTag.getCompound(HELD_BLOCK_KEY)));
    }

    @Override
    protected void addAdditionalSaveData(final CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        this.saveCommonNBTData(compoundTag);
        compoundTag.put(HELD_BLOCK_KEY, NbtUtils.writeBlockState(this.getDisplayBlockState()));
    }

    @Override
    public void loadFromStackNBT(final CompoundTag compoundTag) {
        super.loadFromStackNBT(compoundTag);
        this.loadCommonNBTData(compoundTag);
    }

    @Override
    public CompoundTag saveForItemStack() {
        final CompoundTag compoundTag = super.saveForItemStack();

        this.saveCommonNBTData(compoundTag);

        return compoundTag;
    }

    private void loadCommonNBTData(final CompoundTag compoundTag) {
        if (compoundTag.contains("BrewTime", Tag.TAG_SHORT)) this.brewTime = compoundTag.getShort("BrewTime");
        if (compoundTag.contains("Fuel", Tag.TAG_BYTE)) this.fuel = compoundTag.getByte("Fuel");
    }

    private void saveCommonNBTData(final CompoundTag compoundTag) {
        compoundTag.putShort("BrewTime", (short) this.brewTime);
        compoundTag.putByte("Fuel", (byte) this.fuel);
    }

    @Override
    protected ItemStack getDropStack() {
        return new ItemStack(Blocks.BREWING_STAND.asItem());
    }

    @Nullable
    @Override
    public ItemStack getPickResult() {
        return new ItemStack(Blocks.BREWING_STAND.asItem());
    }

    @Override
    protected AbstractContainerMenu createMenu(final int id, final Inventory playerInventory) {
        return new BrewingStandMenu(id, playerInventory, this, this.dataAccess);
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
        if (!this.isAlive() || facing == null || capability != ForgeCapabilities.ITEM_HANDLER)
            return super.getCapability(capability, facing);

        if (facing == Direction.UP) return directionalHandlers[0].cast();

        if (facing == Direction.DOWN) return directionalHandlers[1].cast();

        return directionalHandlers[2].cast();
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        Arrays.stream(directionalHandlers).forEach(LazyOptional::invalidate);
    }

    @Override
    public void reviveCaps() {
        super.reviveCaps();
        this.directionalHandlers = SidedInvWrapper.create(this, Direction.UP, Direction.DOWN, Direction.NORTH);
    }
}
