package com.alekiponi.alekiships.common.entity.compartment.vanilla;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

import com.alekiponi.alekiships.common.entity.compartment.BlockCompartment;
import com.alekiponi.alekiships.common.entity.compartment.CompartmentCloneable;
import com.alekiponi.alekiships.common.entity.compartment.CompartmentType;
import com.alekiponi.alekiships.common.entity.compartment.ContainerCompartmentEntity;
import com.alekiponi.alekiships.util.CommonHelper;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.RecipeCraftingHolder;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;

import static net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity.*;

/**
 * This compartment entity mimics vanillas {@link AbstractFurnaceBlockEntity}. If your BE extends from that class you'll
 * want to extend from this for your compartment
 */
public abstract class AbstractFurnaceCompartmentEntity extends ContainerCompartmentEntity.ContainerMenuCompartmentEntity implements WorldlyContainer, RecipeCraftingHolder, StackedContentsCompatible, BlockCompartment {
    public static final int SLOT_COUNT = 3;
    public static final String BURN_TIME_KEY = "BurnTime";
    public static final String COOK_TIME_KEY = "CookTime";
    public static final String COOK_TIME_TOTAL_KEY = "CookTimeTotal";
    public static final String RECIPES_USED_KEY = "RecipesUsed";
    public static final int SLOT_INPUT = 0;
    public static final int SLOT_FUEL = 1;
    public static final int SLOT_RESULT = 2;
    private static final EntityDataAccessor<BlockState> DATA_ID_DISPLAY_BLOCK = SynchedEntityData.defineId(
            AbstractFurnaceCompartmentEntity.class, EntityDataSerializers.BLOCK_STATE);
    private static final int[] SLOTS_FOR_UP = new int[]{SLOT_INPUT};
    private static final int[] SLOTS_FOR_DOWN = new int[]{SLOT_RESULT, SLOT_FUEL};
    private static final int[] SLOTS_FOR_SIDES = new int[]{SLOT_FUEL};
    private final RecipeType<? extends AbstractCookingRecipe> recipeType;
    private final RecipeManager.CachedCheck<SingleRecipeInput, ? extends AbstractCookingRecipe> quickCheck;
    private final Object2IntOpenHashMap<ResourceLocation> recipesUsed = new Object2IntOpenHashMap<>();
    protected int litTime;
    protected int litDuration;
    protected int cookingProgress;
    protected int cookingTotalTime;
    protected final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(final int dataType) {
            return switch (dataType) {
                case DATA_LIT_TIME -> AbstractFurnaceCompartmentEntity.this.litTime;
                case DATA_LIT_DURATION -> AbstractFurnaceCompartmentEntity.this.litDuration;
                case DATA_COOKING_PROGRESS -> AbstractFurnaceCompartmentEntity.this.cookingProgress;
                case DATA_COOKING_TOTAL_TIME -> AbstractFurnaceCompartmentEntity.this.cookingTotalTime;
                default -> 0;
            };
        }

        @Override
        public void set(final int dataType, final int dataValue) {
            switch (dataType) {
                case DATA_LIT_TIME:
                    AbstractFurnaceCompartmentEntity.this.litTime = dataValue;
                    break;
                case DATA_LIT_DURATION:
                    AbstractFurnaceCompartmentEntity.this.litDuration = dataValue;
                    break;
                case DATA_COOKING_PROGRESS:
                    AbstractFurnaceCompartmentEntity.this.cookingProgress = dataValue;
                    break;
                case DATA_COOKING_TOTAL_TIME:
                    AbstractFurnaceCompartmentEntity.this.cookingTotalTime = dataValue;
            }
        }

        @Override
        public int getCount() {
            return NUM_DATA_VALUES;
        }
    };

    protected AbstractFurnaceCompartmentEntity(final EntityType<? extends AbstractFurnaceCompartmentEntity> entityType,
            final Level level, final RecipeType<? extends AbstractCookingRecipe> recipeType) {
        super(entityType, level, SLOT_COUNT);
        this.quickCheck = RecipeManager.createCheck(recipeType);
        this.recipeType = recipeType;
    }

    public static <E extends AbstractFurnaceCompartmentEntity> CompartmentType.CompartmentFactory<E> create(
            final BlockCompartment.BlockCompartmentFactory<E> factory) {
        return BlockCompartment.create(factory).postInit(CompartmentCloneable::initialize);
    }

    private static void createExperience(final ServerLevel level, final Vec3 vec3, final int recipeIndex,
            final float experience) {
        int i = Mth.floor(recipeIndex * experience);
        float f = Mth.frac(recipeIndex * experience);
        if (f != 0 && Math.random() < f) {
            ++i;
        }

        ExperienceOrb.award(level, vec3, i);
    }

    private static int getTotalCookTime(final Level pLevel, final AbstractFurnaceCompartmentEntity furnaceCompartment) {
        return furnaceCompartment.quickCheck.getRecipeFor(new SingleRecipeInput(furnaceCompartment.getItem(SLOT_INPUT)),
                pLevel).map(recipeHolder -> recipeHolder.value().getCookingTime()).orElse(BURN_TIME_STANDARD);
    }

    @Override
    protected void defineSynchedData(final SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_ID_DISPLAY_BLOCK, Blocks.AIR.defaultBlockState());
    }

    @Override
    public void tick() {
        super.tick();
        final boolean isLitStart = this.isLit();
        if (this.isLit()) {
            --this.litTime;
        }

        if (this.level().isClientSide()) {
            if (!this.isRemoved() && CommonHelper.everyNthTickUnique(this.getId(), this.tickCount, 10)) {
                this.animateTick();
            }
            return;
        }

        final ItemStack fuelStack = this.getItem(SLOT_FUEL);
        final ItemStack inputStack = this.getItem(SLOT_INPUT);
        final boolean inputSlotEmpty = !inputStack.isEmpty();
        final boolean fuelSlotEmpty = !fuelStack.isEmpty();
        if (this.isLit() || fuelSlotEmpty && inputSlotEmpty) {
            final RecipeHolder<? extends AbstractCookingRecipe> recipe;
            if (inputSlotEmpty) {
                recipe = this.quickCheck.getRecipeFor(new SingleRecipeInput(inputStack), this.level()).orElse(null);
            } else {
                recipe = null;
            }

            final int maxStackSize = this.getMaxStackSize();
            if (!this.isLit() && this.canBurn(this.level().registryAccess(), recipe, this.itemStacks, maxStackSize)) {
                this.litTime = this.getBurnDuration(fuelStack);
                this.litDuration = this.litTime;
                if (this.isLit()) {
                    if (fuelStack.hasCraftingRemainingItem()) {
                        this.setItem(SLOT_FUEL, fuelStack.getCraftingRemainingItem());
                    } else if (fuelSlotEmpty) {
                        fuelStack.shrink(1);
                        if (fuelStack.isEmpty()) this.setItem(SLOT_FUEL, fuelStack.getCraftingRemainingItem());
                    }
                }
            }

            if (this.isLit() && this.canBurn(this.level().registryAccess(), recipe, this.itemStacks, maxStackSize)) {
                ++this.cookingProgress;
                if (this.cookingProgress == this.cookingTotalTime) {
                    this.cookingProgress = 0;
                    this.cookingTotalTime = getTotalCookTime(this.level(), this);
                    if (this.burn(this.level().registryAccess(), recipe, this.itemStacks, maxStackSize)) {
                        this.setRecipeUsed(recipe);
                    }
                }
            } else {
                this.cookingProgress = 0;
            }
        } else if (!this.isLit() && this.cookingProgress > 0) {
            this.cookingProgress = Mth.clamp(this.cookingProgress - BURN_COOL_SPEED, 0, this.cookingTotalTime);
        }

        // Switch blockstate if we changed our lit status
        if (isLitStart != this.isLit()) {
            this.setDisplayBlockState(this.getDisplayBlockState().setValue(AbstractFurnaceBlock.LIT, this.isLit()));
        }
    }

    @Override
    public void fillStackedContents(final StackedContents stackedContents) {
        for (final ItemStack itemstack : this.itemStacks) {
            stackedContents.accountStack(itemstack);
        }
    }

    @Override
    @Nullable
    public RecipeHolder<?> getRecipeUsed() {
        return null;
    }

    @Override
    public void setRecipeUsed(final @Nullable RecipeHolder<?> recipe) {
        if (recipe != null) {
            final ResourceLocation resourceLocation = recipe.id();
            this.recipesUsed.addTo(resourceLocation, 1);
        }
    }

    private boolean burn(final RegistryAccess registryAccess,
            final @Nullable RecipeHolder<? extends AbstractCookingRecipe> cookingRecipe,
            final NonNullList<ItemStack> itemStacks, final int maxStackSize) {
        if (cookingRecipe == null || !this.canBurn(registryAccess, cookingRecipe, itemStacks, maxStackSize)) {
            return false;
        }

        final ItemStack inputStack = itemStacks.getFirst();
        final ItemStack recipeOutput = cookingRecipe.value()
                .assemble(new SingleRecipeInput(inputStack), registryAccess);
        final ItemStack outputSlot = itemStacks.get(SLOT_RESULT);
        if (outputSlot.isEmpty()) {
            itemStacks.set(SLOT_RESULT, recipeOutput.copy());
        } else if (outputSlot.is(recipeOutput.getItem())) {
            outputSlot.grow(recipeOutput.getCount());
        }

        if (inputStack.is(Blocks.WET_SPONGE.asItem()) && !itemStacks.get(SLOT_FUEL).isEmpty() && itemStacks.get(
                SLOT_FUEL).is(Items.BUCKET)) {
            itemStacks.set(SLOT_FUEL, Items.WATER_BUCKET.getDefaultInstance());
        }

        inputStack.shrink(1);
        return true;
    }

    protected int getBurnDuration(final ItemStack fuelStack) {
        if (fuelStack.isEmpty()) {
            return 0;
        } else {
            return fuelStack.getBurnTime(this.recipeType);
        }
    }

    private boolean canBurn(final RegistryAccess registryAccess,
            final @Nullable RecipeHolder<? extends AbstractCookingRecipe> cookingRecipe,
            final NonNullList<ItemStack> itemStacks, final int maxStackSize) {
        if (itemStacks.getFirst().isEmpty() || cookingRecipe == null) return false;

        final ItemStack itemstack = cookingRecipe.value()
                .assemble(new SingleRecipeInput(itemStacks.getFirst()), registryAccess);
        if (itemstack.isEmpty()) {
            return false;
        }

        final ItemStack outputStack = itemStacks.get(SLOT_RESULT);
        if (outputStack.isEmpty()) {
            return true;
        }

        if (!ItemStack.isSameItem(outputStack, itemstack)) {
            return false;
        }

        // Forge fix: make furnace respect stack sizes in furnace recipes
        if (outputStack.getCount() + itemstack.getCount() <= maxStackSize && outputStack.getCount() + itemstack.getCount() <= outputStack.getMaxStackSize()) {
            return true;
        }

        // Forge fix: make furnace respect stack sizes in furnace recipes
        return outputStack.getCount() + itemstack.getCount() <= itemstack.getMaxStackSize();
    }

    public void awardUsedRecipesAndPopExperience(final ServerPlayer player) {
        final List<RecipeHolder<?>> list = this.getRecipesToAwardAndPopExperience(player.serverLevel(),
                player.position());
        player.awardRecipes(list);

        for (final var recipe : list) {
            if (recipe != null) {
                player.triggerRecipeCrafted(recipe, this.itemStacks);
            }
        }

        this.recipesUsed.clear();
    }

    public List<RecipeHolder<?>> getRecipesToAwardAndPopExperience(final ServerLevel level, final Vec3 vec3) {
        final List<RecipeHolder<?>> list = Lists.newArrayList();

        for (final var entry : this.recipesUsed.object2IntEntrySet()) {
            level.getRecipeManager().byKey(entry.getKey()).ifPresent((recipe) -> {
                list.add(recipe);
                createExperience(level, vec3, entry.getIntValue(),
                        ((AbstractCookingRecipe) recipe.value()).getExperience());
            });
        }

        return list;
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
    public int[] getSlotsForFace(final Direction direction) {
        if (direction == Direction.DOWN) {
            return SLOTS_FOR_DOWN;
        } else {
            return direction == Direction.UP ? SLOTS_FOR_UP : SLOTS_FOR_SIDES;
        }
    }

    @Override
    public boolean canPlaceItemThroughFace(final int slotIndex, final ItemStack itemStack,
            final @Nullable Direction direction) {
        return this.canPlaceItem(slotIndex, itemStack);
    }

    @Override
    public boolean canTakeItemThroughFace(final int slotIndex, final ItemStack itemStack, final Direction direction) {
        if (direction == Direction.DOWN && slotIndex == SLOT_FUEL) {
            return itemStack.is(Items.WATER_BUCKET) || itemStack.is(Items.BUCKET);
        }

        return true;
    }

    @Override
    public boolean canPlaceItem(final int slotIndex, final ItemStack itemStack) {
        if (slotIndex == SLOT_RESULT) {
            return false;
        }

        if (slotIndex != SLOT_FUEL) {
            return true;
        }

        final ItemStack itemstack = this.itemStacks.get(SLOT_FUEL);
        return itemStack.getBurnTime(this.recipeType) > 0 || itemStack.is(Items.BUCKET) && !itemstack.is(Items.BUCKET);
    }

    @Override
    public void setItem(final int slotIndex, final ItemStack itemStack) {
        final ItemStack currentStack = this.getItem(slotIndex);
        final boolean flag = !itemStack.isEmpty() && ItemStack.isSameItemSameComponents(currentStack, itemStack);
        this.itemStacks.set(slotIndex, itemStack);
        itemStack.limitSize(this.getMaxStackSize());
        if (slotIndex == SLOT_INPUT && !flag) {
            this.cookingTotalTime = getTotalCookTime(this.level(), this);
            this.cookingProgress = 0;
            this.setChanged();
        }
    }

    public int getCookTime() {
        return this.cookingProgress;
    }

    public int getTotalCookTime() {
        return cookingTotalTime;
    }

    public boolean isLit() {
        return this.litTime > 0;
    }

    @Override
    public double getBuoyancy() {
        return -0.03;
    }

    @Override
    public int getCompartmentBlockLight() {
        return getDisplayBlockState().getValue(BlockStateProperties.LIT) ? 13 : 0;
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
    protected void loadBlockEntityData(final CompoundTag compoundTag) {
        this.loadCommonNBTData(compoundTag);
    }

    @Override
    protected void saveBlockEntityData(final CompoundTag compoundTag) {
        this.saveCommonNBTData(compoundTag);
    }

    private void loadCommonNBTData(final CompoundTag compoundTag) {
        if (compoundTag.contains(BURN_TIME_KEY, Tag.TAG_INT)) this.litTime = compoundTag.getInt(BURN_TIME_KEY);
        if (compoundTag.contains(COOK_TIME_KEY, Tag.TAG_INT)) this.cookingProgress = compoundTag.getInt(COOK_TIME_KEY);
        if (compoundTag.contains(COOK_TIME_TOTAL_KEY, Tag.TAG_INT)) {
            this.cookingTotalTime = compoundTag.getInt(COOK_TIME_TOTAL_KEY);
        }

        this.litDuration = this.getBurnDuration(this.getItem(SLOT_FUEL));

        if (compoundTag.contains(RECIPES_USED_KEY, Tag.TAG_COMPOUND)) {
            final CompoundTag compoundtag = compoundTag.getCompound(RECIPES_USED_KEY);

            for (final String recipeKey : compoundtag.getAllKeys()) {
                this.recipesUsed.put(ResourceLocation.tryParse(recipeKey), compoundtag.getInt(recipeKey));
            }
        }
    }

    private void saveCommonNBTData(final CompoundTag compoundTag) {
        compoundTag.putInt(BURN_TIME_KEY, this.litTime);
        compoundTag.putInt(COOK_TIME_KEY, this.cookingProgress);
        compoundTag.putInt(COOK_TIME_TOTAL_KEY, this.cookingTotalTime);
        CompoundTag compoundtag = new CompoundTag();
        this.recipesUsed.forEach((recipeKey, integer) -> compoundtag.putInt(recipeKey.toString(), integer));
        compoundTag.put(RECIPES_USED_KEY, compoundtag);
    }

    @Override
    public ItemStack getDropStack() {
        return new ItemStack(this.getDisplayBlockState().getBlock());
    }

    @Nullable
    @Override
    public ItemStack getPickResult() {
        return new ItemStack(this.getDisplayBlockState().getBlock());
    }

    /**
     * Called on the client to do animation stuff. This is attempting to replicate what
     * {@link Block#animateTick(BlockState, Level, BlockPos, RandomSource)} does but you'll have to play with it
     * as we call this every couple ticks
     */
    protected abstract void animateTick();

    @Override
    protected abstract AbstractFurnaceMenu createMenu(final int id, final Inventory playerInventory);
}