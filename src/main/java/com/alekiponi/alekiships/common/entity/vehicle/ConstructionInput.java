package com.alekiponi.alekiships.common.entity.vehicle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import com.alekiponi.alekiships.common.entity.SloopConstructionState;
import com.alekiponi.alekiships.common.recipe.entity.EntityResult;
import com.alekiponi.alekiships.common.recipe.entity.SimpleResult;
import com.alekiponi.alekiships.util.AlekiShipsExtraCodecs;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.common.util.NeoForgeExtraCodecs;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;
import java.text.MessageFormat;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Singular;
import lombok.ToString;

/**
 * A Datapack object representing a collection of construction inputs.
 * These consist of discrete stages represented by {@link ProgressStage}.
 * When completed it spawns {@link #constructedEntity}, plays {@link #assembleSound} and creates
 * particles using {@link #assembleBlockState}.
 *
 * @param <E> An enum representing the construction stages
 */
@ToString
@EqualsAndHashCode
public final class ConstructionInput<E extends Enum<E> & ConstructionInput.ConstructionStage<E>> {

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static final ConstructionInput<?> EMPTY = new ConstructionInput(Map.of(), new SimpleResult(EntityType.PIG),
            SoundEvents.EMPTY, Blocks.STONE.defaultBlockState());

    public final SoundEvent assembleSound;
    public final BlockState assembleBlockState;
    public final EntityResult constructedEntity;
    private final Map<E, ProgressStage> stages;

    private ConstructionInput(final Map<E, ProgressStage> stages, final EntityResult constructedEntity,
            final SoundEvent assembleSound, final BlockState assembleBlockState) {
        this.stages = stages;
        this.constructedEntity = constructedEntity;
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
                        ConstructionInput.stagesCodec(stageCodec, stagesSupplier)
                                .fieldOf("stages")
                                .forGetter(constructionInput -> constructionInput.stages),
                        EntityResult.CODEC.fieldOf("constructed_entity")
                                .forGetter(constructionInput -> constructionInput.constructedEntity),
                        BuiltInRegistries.SOUND_EVENT.byNameCodec()
                                .fieldOf("assemble_sound")
                                .forGetter(constructionInput -> constructionInput.assembleSound),
                        AlekiShipsExtraCodecs.BLOCK_STATE_CODEC.fieldOf("assemble_blockstate")
                                .forGetter(constructionInput -> constructionInput.assembleBlockState))
                .apply(instance, ConstructionInput::of));
    }

    public static <E extends Enum<E> & ConstructionStage<E>> StreamCodec<RegistryFriendlyByteBuf, ConstructionInput<E>> streamCodec(
            final Class<E> enumClass) {
        return StreamCodec.composite(
                ByteBufCodecs.map(Object2ObjectOpenHashMap::new, NeoForgeStreamCodecs.enumCodec(enumClass),
                        ProgressStage.STREAM_CODEC), constructionInput -> constructionInput.stages,
                EntityResult.STREAM_CODEC, constructionInput -> constructionInput.constructedEntity,
                ByteBufCodecs.registry(Registries.SOUND_EVENT), constructionInput -> constructionInput.assembleSound,
                ByteBufCodecs.idMapper(Block::stateById, Block::getId),
                constructionInput -> constructionInput.assembleBlockState, ConstructionInput::of);
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

    @Builder
    public static <E extends Enum<E> & ConstructionStage<E>> ConstructionInput<E> of(
            @Singular final Map<E, ProgressStage> stages, final EntityResult constructedEntity,
            final SoundEvent assembleSound, final BlockState assembleBlockState) {
        return stages.isEmpty() ? empty() : new ConstructionInput<>(stages, constructedEntity, assembleSound,
                assembleBlockState);
    }

    @SuppressWarnings("unchecked")
    public static <E extends Enum<E> & ConstructionStage<E>> ConstructionInput<E> empty() {
        return ((ConstructionInput<E>) EMPTY);
    }

    @CheckReturnValue
    private static <E extends Enum<E> & ConstructionStage<E>> ItemStack doInsert(
            final ConstructedEntity<E, ?> constructedEntity, final ItemStack insertStack,
            final @Nullable LivingEntity entity, @SuppressWarnings("SameParameterValue") final int increment) {
        final var stage = constructedEntity.getConstructionState()
                .stage();
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
         *
         * @return The remainder of the insertion
         */
        ItemStack insert(E stage, ItemStack insertStack);

        /**
         * Gets the current contents in the backing storage for the current stage
         *
         * @param stage The stage
         *
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
            final var stage = this.getConstructionState()
                    .stage();
            return stage.compareTo(stage.end()) == 0;
        }
    }

    /**
     * @param <E> The backing enum to represent the current {@link ConstructionStage}
     * @param <S> The self type
     *
     * @apiNote This should typically be implemented on a record similar to {@link SloopConstructionState}.
     * @see ConstructionState#streamCodec(Class, BiFunction)
     * @see ConstructionState#codec(Codec, Enum, BiFunction)
     */
    public interface ConstructionState<E extends Enum<E> & ConstructionStage<E>, S extends ConstructionState<E, S>> {

        /**
         * Helper for creating a {@link Codec}
         *
         * @param stageCodec    A codec for the construction stage
         * @param startingStage The starting construction stage
         * @param factory       A factory for the {@link ConstructionState} object
         *
         * @see #streamCodec(Class, BiFunction)
         */
        static <E extends Enum<E> & ConstructionStage<E>, S extends ConstructionState<E, S>> Codec<S> codec(
                final Codec<E> stageCodec, final E startingStage, final BiFunction<E, Integer, S> factory) {
            return RecordCodecBuilder.create(instance -> instance.group(
                            stageCodec.optionalFieldOf("current_stage", startingStage).forGetter(ConstructionState::stage),
                            Codec.INT.optionalFieldOf("remaining_input_count", 0).forGetter(ConstructionState::remainingInputs))
                    .apply(instance, factory));
        }

        /**
         * Helper for creating a {@link StreamCodec}
         *
         * @param clazz   The enum class
         * @param factory The factory for the {@link ConstructionState} object
         *
         * @see ConstructionState#codec(Codec, Enum, BiFunction)
         */
        static <E extends Enum<E> & ConstructionStage<E>, S extends ConstructionState<E, S>> StreamCodec<FriendlyByteBuf, S> streamCodec(
                final Class<E> clazz, final BiFunction<E, Integer, S> factory) {
            return StreamCodec.composite(NeoForgeStreamCodecs.enumCodec(clazz), ConstructionState::stage,
                    ByteBufCodecs.VAR_INT, ConstructionState::remainingInputs, factory);
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

        public static final Codec<ProgressStage> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                NeoForgeExtraCodecs.withAlternative(SizedIngredient.FLAT_CODEC, SizedIngredient.NESTED_CODEC)
                        .fieldOf("input")
                        .forGetter(ProgressStage::ingredient), BuiltInRegistries.SOUND_EVENT.byNameCodec()
                        .fieldOf("progress_sound")
                        .forGetter(ProgressStage::progressSound), BuiltInRegistries.SOUND_EVENT.byNameCodec()
                        .fieldOf("switch_sound")
                        .forGetter(ProgressStage::switchSound),
                AlekiShipsExtraCodecs.BLOCK_STATE_CODEC.fieldOf("switch_blockstate")
                        .forGetter(ProgressStage::switchBlockState)).apply(instance, ProgressStage::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, ProgressStage> STREAM_CODEC = StreamCodec.composite(
                SizedIngredient.STREAM_CODEC, ProgressStage::ingredient, ByteBufCodecs.registry(Registries.SOUND_EVENT),
                ProgressStage::progressSound, ByteBufCodecs.registry(Registries.SOUND_EVENT),
                ProgressStage::switchSound, ByteBufCodecs.idMapper(Block::stateById, Block::getId),
                ProgressStage::switchBlockState, ProgressStage::new);
    }
}