package com.alekiponi.alekiships.common.entity.vehicle;

import com.alekiponi.alekiships.common.entity.vehiclecapability.IHaveConstructionEntities;
import com.alekiponi.alekiships.util.ItemContents;

import com.mojang.serialization.Codec;

import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import net.neoforged.neoforge.common.util.Lazy;

import java.util.Arrays;

/**
 * @see ConstructionInput.ConstructedEntity
 */
public abstract class AbstractUnderConstructionEntity<E extends Enum<E> & ConstructionInput.ConstructionStage<E>, S extends ConstructionInput.ConstructionState<E, S>> extends AbstractVehicle implements IHaveConstructionEntities, ConstructionInput.ConstructedEntity<E, S> {

    public static final String CONSTRUCTION_CONTENTS_KEY = "ConstructionContents";
    /**
     * You typically shouldn't directly access this. Instead use {@link #getContentsCount(Enum)}
     */
    protected final ItemContents constructionContents;
    /**
     * Lazy as we expect only the client to ever need this
     */
    protected final Lazy<ItemStack[]> requiredItems;
    private final Codec<S> stateCodec;

    protected AbstractUnderConstructionEntity(
            final EntityType<? extends AbstractUnderConstructionEntity<E, S>> entityType, final Level level,
            final int constructionContentsCapacity, final Codec<S> stateCodec) {
        super(entityType, level);
        this.stateCodec = stateCodec;
        this.constructionContents = new ItemContents(constructionContentsCapacity);

        this.requiredItems = Lazy.of(() -> {
            final var constructionState = this.getConstructionState();
            if (this.isFinished()) return new ItemStack[0];

            final var ingredient = this.getConstructionInput().value().getIngredient(constructionState.stage());
            final int count = constructionState.remainingInputs();
            return Arrays.stream(ingredient.getItems()).map(itemStack -> itemStack.copyWithCount(count))
                    .toArray(ItemStack[]::new);
        });
    }

    @Override
    public int getMaxPassengers() {
        return 1;
    }

    @Override
    public InteractionResult interact(final Player player, final InteractionHand hand) {
        return InteractionResult.PASS;
    }

    @Override
    protected void readAdditionalSaveData(final CompoundTag compoundTag) {
        this.stateCodec.parse(NbtOps.INSTANCE, compoundTag.get("construction_state")).result()
                .ifPresent(this::setConstructionState);
        this.constructionContents.deserializeNBT(this.registryAccess(),
                compoundTag.getCompound(CONSTRUCTION_CONTENTS_KEY));
    }

    @Override
    protected void addAdditionalSaveData(final CompoundTag compoundTag) {
        this.stateCodec.encodeStart(NbtOps.INSTANCE, this.getConstructionState())
                .ifSuccess(tag -> compoundTag.put("construction_state", tag));
        compoundTag.put(CONSTRUCTION_CONTENTS_KEY, this.constructionContents.serializeNBT(this.registryAccess()));
    }

    public InteractionResult interactFromConstructionEntity(final Player player, final InteractionHand hand) {
        final ItemStack heldItem = player.getItemInHand(hand);

        final var previousStage = this.getConstructionState().stage();

        final var constructionInput = this.getConstructionInput().value();
        final var insertionResult = constructionInput.tryInsert(this, heldItem, player);
        if (!insertionResult.getResult().consumesAction()) return InteractionResult.PASS;

        player.setItemInHand(hand, insertionResult.getObject());

        final var currentStage = this.getConstructionState().stage();

        if (previousStage != currentStage) this.switchConstructionStage(constructionInput, previousStage, currentStage);

        if (this.isFinished()) {
            this.finalizeConstruction(player, constructionInput);
            return InteractionResult.SUCCESS;
        }

        this.playSound(constructionInput.getProgressSound(currentStage));

        return insertionResult.getResult();
    }

    /**
     * Handler for switching to a new construction stage
     *
     * @param constructionInput The current {@link ConstructionInput}
     * @param previousStage     The stage we are switching from
     * @param currentStage      The new (current) stage we are now at
     */
    protected abstract void switchConstructionStage(final ConstructionInput<E> constructionInput, final E previousStage,
            final E currentStage);

    /**
     * Handler for finalizing the construction process
     *
     * @param player            The player finalizing the construction
     * @param constructionInput The current {@link ConstructionInput}
     */
    protected abstract void finalizeConstruction(final Player player, final ConstructionInput<E> constructionInput);

    /**
     * @return A potentially empty array of accepted ItemStack inputs
     */
    public ItemStack[] getRequiredItems() {
        return this.requiredItems.get();
    }

    @Override
    protected void dropCustomDestructionLoot(final DamageSource damageSource) {
        super.dropCustomDestructionLoot(damageSource);
        this.constructionContents.forEach(this::spawnAtLocation);
    }

    @Override
    public ItemStack insert(final E stage, final ItemStack insertStack) {
        return this.constructionContents.insert(stage.ordinal(), insertStack);
    }

    @Override
    public int getContentsCount(final E stage) {
        return this.constructionContents.getCount(stage.ordinal());
    }

    public abstract Holder<ConstructionInput<E>> getConstructionInput();
}