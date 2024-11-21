package com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.vanilla;

import com.alekiponi.alekiships.common.entity.vehiclehelper.CompartmentType;
import com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.BlockCompartment;
import com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.ContainerCompartmentEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.Containers;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.BrewingStandMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BrewingStandBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.EventHooks;

import javax.annotation.Nullable;
import java.util.Arrays;

import static net.minecraft.world.level.block.entity.BrewingStandBlockEntity.*;

public class BrewingStandCompartmentEntity extends ContainerCompartmentEntity.ContainerMenuCompartmentEntity implements WorldlyContainer, BlockCompartment {
    public static final int SLOT_COUNT = 5;
    public static final String FUEL_KEY = "Fuel";
    public static final String BREW_TIME_KEY = "BrewTime";
    public static final int INGREDIENT_SLOT = 3;
    public static final int FUEL_SLOT = 4;
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
                case DATA_BREW_TIME -> BrewingStandCompartmentEntity.this.brewTime;
                case DATA_FUEL_USES -> BrewingStandCompartmentEntity.this.fuel;
                default -> 0;
            };
        }

        @Override
        public void set(final int dataType, final int dataValue) {
            switch (dataType) {
                case DATA_BREW_TIME:
                    BrewingStandCompartmentEntity.this.brewTime = dataValue;
                    break;
                case DATA_FUEL_USES:
                    BrewingStandCompartmentEntity.this.fuel = dataValue;
            }
        }

        @Override
        public int getCount() {
            return NUM_DATA_VALUES;
        }
    };
    @Nullable
    private boolean[] lastPotionCount;
    @Nullable
    private Item ingredient;

    public BrewingStandCompartmentEntity(final CompartmentType<? extends BrewingStandCompartmentEntity> compartmentType,
            final Level level) {
        super(compartmentType, level, SLOT_COUNT);
    }

    public BrewingStandCompartmentEntity(final CompartmentType<? extends BrewingStandCompartmentEntity> compartmentType,
            final Level level, final ItemStack itemStack) {
        super(compartmentType, level, SLOT_COUNT, itemStack);

        this.setDisplayBlockState(Blocks.BREWING_STAND.defaultBlockState());
    }

    private static void doBrew(final Level level, final BlockPos pos, final NonNullList<ItemStack> items) {
        if (EventHooks.onPotionAttemptBrew(items)) return;
        ItemStack ingredientStack = items.get(INGREDIENT_SLOT);
        final PotionBrewing potionBrewing = level.potionBrewing();

        for (int bottleSlot = 0; bottleSlot < 3; bottleSlot++) {
            items.set(bottleSlot, potionBrewing.mix(ingredientStack, items.get(bottleSlot)));
        }

        EventHooks.onPotionBrewed(items);
        if (ingredientStack.hasCraftingRemainingItem()) {
            final ItemStack craftingRemainder = ingredientStack.getCraftingRemainingItem();
            ingredientStack.shrink(1);
            if (ingredientStack.isEmpty()) {
                ingredientStack = craftingRemainder;
            } else {
                Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), craftingRemainder);
            }
        } else ingredientStack.shrink(1);

        items.set(INGREDIENT_SLOT, ingredientStack);
        level.levelEvent(1035, pos, 0);
    }

    private static boolean isBrewable(final PotionBrewing potionBrewing, final NonNullList<ItemStack> items) {
        final ItemStack ingredientStack = items.get(INGREDIENT_SLOT);
        if (ingredientStack.isEmpty()) {
            return false;
        }
        if (!potionBrewing.isIngredient(ingredientStack)) {
            return false;
        }

        for (int bottleIndex = 0; bottleIndex < 3; bottleIndex++) {
            final ItemStack itemStack = items.get(bottleIndex);
            if (!itemStack.isEmpty() && potionBrewing.hasMix(itemStack, ingredientStack)) {
                return true;
            }
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

        final boolean canBrew = isBrewable(this.level().potionBrewing(), this.itemStacks);
        final ItemStack ingredientStack = this.getItem(INGREDIENT_SLOT);
        if (this.brewTime > 0) {
            --this.brewTime;
            if (this.brewTime == 0 && canBrew) {
                doBrew(this.level(), this.blockPosition(), this.itemStacks);
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
    protected void defineSynchedData(final SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_ID_DISPLAY_BLOCK, Blocks.AIR.defaultBlockState());
    }

    @Override
    protected void onHurt(final DamageSource damageSource) {
        BlockCompartment.playHitSound(this);
    }

    @Override
    protected void onPlaced() {
        BlockCompartment.playPlaceSound(this);
    }

    @Override
    protected void onBreak() {
        super.onBreak();
        BlockCompartment.playBreakSound(this);
    }

    @Override
    public boolean canPlaceItem(final int slotIndex, final ItemStack itemStack) {
        final PotionBrewing potionbrewing = this.level().potionBrewing();

        if (slotIndex == INGREDIENT_SLOT) return potionbrewing.isIngredient(itemStack);

        if (slotIndex == FUEL_SLOT) return itemStack.is(Items.BLAZE_POWDER);

        return (potionbrewing.isInput(itemStack) || itemStack.is(Items.GLASS_BOTTLE)) && this.getItem(slotIndex)
                .isEmpty();
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
        BlockCompartment.readBlockstate(this, compoundTag);
    }

    @Override
    protected void addAdditionalSaveData(final CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        this.saveCommonNBTData(compoundTag);
        BlockCompartment.saveBlockstate(this, compoundTag);
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
        if (compoundTag.contains(BREW_TIME_KEY, Tag.TAG_SHORT)) this.brewTime = compoundTag.getShort(BREW_TIME_KEY);
        if (compoundTag.contains(FUEL_KEY, Tag.TAG_BYTE)) this.fuel = compoundTag.getByte(FUEL_KEY);
    }

    private void saveCommonNBTData(final CompoundTag compoundTag) {
        compoundTag.putShort(BREW_TIME_KEY, (short) this.brewTime);
        compoundTag.putByte(FUEL_KEY, (byte) this.fuel);
    }

    public int getFuel() {
        return fuel;
    }

    public int getBrewTime() {
        return brewTime;
    }

    @Override
    protected ItemStack getDropStack() {
        return new ItemStack(Items.BREWING_STAND);
    }

    @Nullable
    @Override
    public ItemStack getPickResult() {
        return new ItemStack(Items.BREWING_STAND);
    }

    @Override
    protected AbstractContainerMenu createMenu(final int id, final Inventory playerInventory) {
        return new BrewingStandMenu(id, playerInventory, this, this.dataAccess);
    }
}