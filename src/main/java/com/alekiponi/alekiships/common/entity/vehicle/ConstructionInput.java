package com.alekiponi.alekiships.common.entity.vehicle;

import com.alekiponi.alekiships.common.entity.SloopConstructionState;
import com.mojang.datafixers.util.Function3;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.common.util.NeoForgeExtraCodecs;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Nullable;

import java.text.MessageFormat;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * A Datapack object representing a collection of construction inputs.
 * These consist of discrete stages represented by {@link ProgressStage}.
 * When completed it spawns {@link #constructedEntityType}, plays {@link #assembleSound} and creates
 * particles using {@link #assembleBlockState}.
 *
 * @param <E> An enum representing the construction stages
 */
public final class ConstructionInput<E extends Enum<E> & ConstructionInput.ConstructionStage<E>> {

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static final ConstructionInput<?> EMPTY = new ConstructionInput(Map.of(), EntityType.PIG, SoundEvents.EMPTY,
            Blocks.STONE.defaultBlockState());

    private static final Codec<BlockState> BLOCK_STATE_CODEC = NeoForgeExtraCodecs.withAlternative(
            BuiltInRegistries.BLOCK.byNameCodec()
                    .xmap(Block::defaultBlockState, BlockBehaviour.BlockStateBase::getBlock), BlockState.CODEC);

    public final SoundEvent assembleSound;
    public final BlockState assembleBlockState;
    public final EntityType<?> constructedEntityType;
    private final Map<E, ProgressStage> stages;

    private ConstructionInput(final Map<E, ProgressStage> stages, final EntityType<?> constructedEntityType,
            final SoundEvent assembleSound, final BlockState assembleBlockState) {
        this.stages = stages;
        this.constructedEntityType = constructedEntityType;
        this.assembleSound = assembleSound;
        this.assembleBlockState = assembleBlockState;
    }

    /**
     * A helper to create a {@link Codec} for {@link ConstructionInput}
     *
     * @param stageCodec     The codec for the stages
     * @param stagesSupplier The supplier for all the stages
     * @param <E>            An enum representing the construction stages
     */
    public static <E extends Enum<E> & ConstructionStage<E>> Codec<ConstructionInput<E>> codec(
            final StringRepresentable.StringRepresentableCodec<E> stageCodec, final Supplier<E[]> stagesSupplier) {
        return RecordCodecBuilder.create(instance -> instance.group(
                        ConstructionInput.stagesCodec(stageCodec, stagesSupplier).fieldOf("stages")
                                .forGetter(constructionInput -> constructionInput.stages),
                        BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("constructed_entity")
                                .forGetter(constructionInput -> constructionInput.constructedEntityType),
                        BuiltInRegistries.SOUND_EVENT.byNameCodec().fieldOf("assemble_sound")
                                .forGetter(constructionInput -> constructionInput.assembleSound),
                        BLOCK_STATE_CODEC.fieldOf("assemble_blockstate")
                                .forGetter(constructionInput -> constructionInput.assembleBlockState))
                .apply(instance, ConstructionInput::of));
    }

    private static <E extends Enum<E> & ConstructionStage<E>> Codec<Map<E, ProgressStage>> stagesCodec(
            final StringRepresentable.StringRepresentableCodec<E> stageCodec, final Supplier<E[]> stagesSupplier) {
        final var stages = stagesSupplier.get();
        return Codec.simpleMap(stageCodec, ProgressStage.CODEC, StringRepresentable.keys(stages)).validate(map -> {
            for (final E constructionStage : stages) {
                final var stage = map.get(constructionStage);
                if (constructionStage == constructionStage.end()) {
                    if (stage == null) continue;
                    return DataResult.error(() -> MessageFormat.format("End stage {0} cannot be present",
                            constructionStage.end().getSerializedName()));
                }
                if (stage == null) {
                    return DataResult.error(() -> MessageFormat.format("Required stage {0} not present",
                            constructionStage.getSerializedName()));
                }
            }

            return DataResult.success(map);
        }).codec();
    }

    public static <E extends Enum<E> & ConstructionStage<E>> ConstructionInput<E> of(final Map<E, ProgressStage> map,
            final EntityType<?> constructedEntityType, final SoundEvent assembleSound,
            final BlockState assembleBlockState) {
        return map.isEmpty() ? empty() : new ConstructionInput<>(map, constructedEntityType, assembleSound,
                assembleBlockState);
    }

    @SuppressWarnings("unchecked")
    public static <E extends Enum<E> & ConstructionStage<E>> ConstructionInput<E> empty() {
        return ((ConstructionInput<E>) EMPTY);
    }

    public static <E extends Enum<E> & ConstructionStage<E>> ConstructionInput<E> getConstructionInput(
            final ResourceKey<? extends Registry<? extends ConstructionInput<E>>> registryKey,
            final HolderLookup.Provider provider,
            final ResourceKey<ConstructionInput<E>> constructionInputResourceKey) {
        return provider.lookup(registryKey).flatMap(registryLookup -> registryLookup.get(constructionInputResourceKey))
                .map(Holder::value).orElse(ConstructionInput.empty());
    }

    @CheckReturnValue
    private static <E extends Enum<E> & ConstructionStage<E>> ItemStack doInsert(
            final ConstructedEntity<E, ?> constructedEntity, final ItemStack insertStack,
            final @Nullable LivingEntity entity, @SuppressWarnings("SameParameterValue") final int increment) {
        final var stage = constructedEntity.getConstructionState().stage();
        if (entity != null && entity.hasInfiniteMaterials()) {
            constructedEntity.insert(stage, insertStack.copyWithCount(increment));
            return insertStack;
        }

        return constructedEntity.insert(stage, insertStack);
    }

    /**
     * @param constructedEntity The constructed entity to try insert into
     * @param insertStack       The insert stack
     * @param entity            The nullable entity doing the insertion
     */
    public <S extends ConstructionState<E, S>> InteractionResultHolder<ItemStack> tryInsert(
            final ConstructedEntity<E, S> constructedEntity, final ItemStack insertStack,
            final @Nullable LivingEntity entity) {
        if (constructedEntity.isFinished()) return InteractionResultHolder.pass(insertStack);

        final var constructionState = constructedEntity.getConstructionState();
        final var stage = constructionState.stage();
        assert stage != stage.end();
        final var ingredient = this.getIngredient(stage);

        // We only care to match the Item, not it's size
        if (!ingredient.ingredient().test(insertStack)) return InteractionResultHolder.pass(insertStack);

        final ItemStack remainder = ConstructionInput.doInsert(constructedEntity, insertStack, entity, 1);
        if (ingredient.count() > constructedEntity.getContentsCount(stage)) {
            constructedEntity.setConstructionState(
                    constructionState.withRemaining(constructionState.remainingInputs() - 1));
            return InteractionResultHolder.success(remainder);
        }

        constructedEntity.setConstructionState(constructionState.nextStateOf(this));
        return InteractionResultHolder.success(remainder);
    }

    private void validate(final E constructionStage) {
        Objects.requireNonNull(constructionStage, "Construction Stage cannot be null");
        assert constructionStage != constructionStage.end() : MessageFormat.format(
                "End Construction Stage {0} cannot have values", constructionStage.end().getSerializedName());
    }

    /**
     * @return A sized ingredient representing what it takes to complete the provided stage
     */
    public SizedIngredient getIngredient(final E constructionStage) {
        this.validate(constructionStage);
        return this.stages.get(constructionStage).ingredient;
    }

    /**
     * @return The progress sound
     */
    public SoundEvent getProgressSound(final E constructionStage) {
        this.validate(constructionStage);
        return this.stages.get(constructionStage).progressSound;
    }

    /**
     * @return The switch sound
     */
    public SoundEvent getSwitchSound(final E constructionStage) {
        this.validate(constructionStage);
        return this.stages.get(constructionStage).switchSound;
    }

    /**
     * @return The switch blockstate
     */
    public BlockState getSwitchBlockState(final E constructionStage) {
        this.validate(constructionStage);
        return this.stages.get(constructionStage).switchBlockState;
    }

    public interface ConstructionStage<E extends Enum<E>> extends StringRepresentable, Comparable<E> {
        /**
         * @return The next stage
         */
        E next();

        /**
         * @return The end stage
         */
        E end();
    }

    /**
     * A type of entity which is constructed, item by item overtime like our {@link SloopUnderConstructionEntity}
     */
    public interface ConstructedEntity<E extends Enum<E> & ConstructionStage<E>, S extends ConstructionState<E, S>> {

        /**
         * Inserts a stack in the backing storage for the current stage.
         *
         * @param stage       The stage
         * @param insertStack The {@link ItemStack} to insert <strong>Do not mutate this</strong>
         * @return The remainder of the insertion
         */
        ItemStack insert(E stage, ItemStack insertStack);

        /**
         * Gets the current contents in the backing storage for the current stage
         *
         * @param stage The stage
         * @return How many contents this stage has
         */
        int getContentsCount(E stage);

        /**
         * @return The construction state
         */
        S getConstructionState();

        /**
         * Sets the current construction state
         *
         * @param constructionState The new construction state
         */
        void setConstructionState(S constructionState);

        default boolean isFinished() {
            final var stage = this.getConstructionState().stage();
            return stage.compareTo(stage.end()) == 0;
        }
    }

    /**
     * @param <E> The backing enum to represent the current {@link ConstructionStage}
     * @param <S> The self type
     * @apiNote This should typically be implemented on a record similar to {@link SloopConstructionState}.
     * @see ConstructionState#streamCodec(ResourceKey, Class, Function3)
     * @see ConstructionState#codec(ResourceKey, Codec, Enum, Function3)
     */
    public interface ConstructionState<E extends Enum<E> & ConstructionStage<E>, S extends ConstructionState<E, S>> {
        String CONSTRUCTION_INPUT_KEY = "ConstructionInputKey";
        String CURRENT_STAGE_KEY = "CurrentStage";
        String REMAINING_INPUT_COUNT = "RemainingInputCount";
        String CONSTRUCTION_STATE_KEY = "ConstructionState";

        /**
         * Helper for creating a {@link StreamCodec}
         *
         * @param registryKey The registry key
         * @param clazz       The enum class
         * @param factory     The factory for the {@link ConstructionState} object
         * @see ConstructionState#codec(ResourceKey, Codec, Enum, Function3)
         */
        static <E extends Enum<E> & ConstructionStage<E>, S extends ConstructionState<E, S>> StreamCodec<FriendlyByteBuf, S> streamCodec(
                final ResourceKey<? extends Registry<ConstructionInput<E>>> registryKey, final Class<E> clazz,
                final Function3<ResourceKey<ConstructionInput<E>>, E, Integer, S> factory) {
            return StreamCodec.composite(ResourceKey.streamCodec(registryKey), ConstructionState::constructionInputKey,
                    NeoForgeStreamCodecs.enumCodec(clazz), ConstructionState::stage, ByteBufCodecs.VAR_INT,
                    ConstructionState::remainingInputs, factory);
        }

        /**
         * Helper for creating a {@link Codec}
         *
         * @param registryKey   The registry key
         * @param stageCodec    A codec for the construction stage
         * @param startingStage The starting construction stage
         * @param factory       A factory for the {@link ConstructionState} object
         * @see ConstructionState#streamCodec(ResourceKey, Class, Function3)
         */
        static <E extends Enum<E> & ConstructionStage<E>, S extends ConstructionState<E, S>> Codec<S> codec(
                final ResourceKey<? extends Registry<ConstructionInput<E>>> registryKey, final Codec<E> stageCodec,
                final E startingStage, final Function3<ResourceKey<ConstructionInput<E>>, E, Integer, S> factory) {
            return RecordCodecBuilder.create(instance -> instance.group(
                            ResourceKey.codec(registryKey).fieldOf(CONSTRUCTION_INPUT_KEY)
                                    .forGetter(ConstructionState::constructionInputKey),
                            stageCodec.optionalFieldOf(CURRENT_STAGE_KEY, startingStage).forGetter(ConstructionState::stage),
                            Codec.INT.optionalFieldOf(REMAINING_INPUT_COUNT, 0).forGetter(ConstructionState::remainingInputs))
                    .apply(instance, factory));
        }

        static <E extends Enum<E> & ConstructionStage<E>, S extends ConstructionState<E, S>> S getInitialState(
                final Function3<ResourceKey<ConstructionInput<E>>, E, Integer, S> factory, final E startingStage,
                final ResourceKey<Registry<ConstructionInput<E>>> key, final HolderLookup.Provider provider,
                final ResourceLocation location) {
            final var constructionInputKey = ResourceKey.create(key, location);
            final var constructionInput = ConstructionInput.getConstructionInput(key, provider, constructionInputKey);
            final var count = constructionInput.getIngredient(startingStage).count();

            return factory.apply(constructionInputKey, startingStage, count);
        }

        /**
         * @param remaining Remaining items
         */
        S withRemaining(final int remaining);

        /**
         * @param constructionInput The current construction input
         */
        S nextStateOf(final ConstructionInput<E> constructionInput);

        /**
         * @return The current construction input key
         */
        ResourceKey<ConstructionInput<E>> constructionInputKey();

        /**
         * @return The current stage
         */
        E stage();

        /**
         * @return The remaining inputs for this stage
         */
        int remainingInputs();
    }

    /**
     * @param ingredient       An ingredient
     * @param progressSound    The progress sound
     * @param switchSound      The switch sound
     * @param switchBlockState The switch blockstate
     */
    public record ProgressStage(SizedIngredient ingredient, SoundEvent progressSound, SoundEvent switchSound,
                                BlockState switchBlockState) {

        private static final Codec<ProgressStage> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                        NeoForgeExtraCodecs.withAlternative(SizedIngredient.FLAT_CODEC, SizedIngredient.NESTED_CODEC)
                                .fieldOf("input").forGetter(ProgressStage::ingredient),
                        BuiltInRegistries.SOUND_EVENT.byNameCodec().fieldOf("progress_sound")
                                .forGetter(ProgressStage::progressSound),
                        BuiltInRegistries.SOUND_EVENT.byNameCodec().fieldOf("switch_sound")
                                .forGetter(ProgressStage::switchSound),
                        BLOCK_STATE_CODEC.fieldOf("switch_blockstate").forGetter(ProgressStage::switchBlockState))
                .apply(instance, ProgressStage::new));
    }
}