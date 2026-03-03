package com.alekiponi.alekiships.common.recipe.ingredient.block;

import com.google.common.collect.ImmutableList;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;

import com.alekiponi.alekiships.common.recipe.ingredient.block.matcher.PropertyMatcher;
import com.alekiponi.alekiships.common.recipe.ingredient.block.matcher.StateMatcher;

import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.StairsShape;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Unmodifiable;
import lombok.*;

/**
 * A class replicating {@link Ingredient} but for {@link BlockState}s.
 * <p>
 * These have similar semantics and cannot be constructed directly. Use the factory functions {@link #anyState(Block...)},
 * {@link #anyState(TagKey)} {@link #exactState(BlockState)} or the builder {@link #builder(Block...)},
 * {@link #builder(Supplier...)}, {@link #builder(TagKey)}
 *
 * @see #stairs(Block, Direction, StairsShape, UnaryOperator)
 */
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class BlockStateIngredient implements Predicate<BlockState> {

    private static final List<StateMatcher> PROPERTIES_ANY = List.of();

    /**
     * The {@link BlockStateIngredient} used for representing air.
     * <p>
     * This is analogous to {@link Ingredient#EMPTY} and friends for most usage
     */
    public static final BlockStateIngredient AIR = BlockStateIngredient.anyState(Blocks.AIR);

    /**
     * Special {@link BlockStateIngredient} used for matching <strong><i>any</i></strong> {@link BlockState}.
     * <p>
     * This always matches. This has few use cases
     */
    public static final BlockStateIngredient ANY = BlockStateIngredient.anyState(Blocks.AIR);

    public static final StreamCodec<RegistryFriendlyByteBuf, BlockStateIngredient> STREAM_CODEC = new StreamCodec<>() {
        public static final StreamCodec<RegistryFriendlyByteBuf, HolderSet<Block>> HOLDER_SET_STREAM_CODEC = ByteBufCodecs.holderSet(
                Registries.BLOCK);
        public static final StreamCodec<ByteBuf, List<StateMatcher>> STATE_MATCHER_LIST_STREAM_CODEC = StateMatcher.STREAM_CODEC.apply(
                ByteBufCodecs.list());

        @Override
        public void encode(final RegistryFriendlyByteBuf buffer, final BlockStateIngredient ingredient) {
            if (ingredient == AIR) {
                buffer.writeByte(-1);
                return;
            }
            if (ingredient == ANY) {
                buffer.writeByte(-2);
                return;
            }

            HOLDER_SET_STREAM_CODEC.encode(buffer, ingredient.getBlocks());
            STATE_MATCHER_LIST_STREAM_CODEC.encode(buffer, ingredient.getStateMatchers());
        }

        @Override
        public BlockStateIngredient decode(final RegistryFriendlyByteBuf buffer) {
            buffer.markReaderIndex();
            final var specialType = buffer.readByte();
            if (specialType == -1) return AIR;
            if (specialType == -2) return ANY;
            buffer.resetReaderIndex();

            return new BlockStateIngredient(HOLDER_SET_STREAM_CODEC.decode(buffer),
                    STATE_MATCHER_LIST_STREAM_CODEC.decode(buffer));
        }
    };

    /**
     * Map codec that permits {@code "state":<StateMatcher>} or {@code "states":[<StateMatcher>...]}
     */
    private static final MapCodec<List<StateMatcher>> STATE_MATCHER_LIST_MAP_CODEC = Codec.mapEither(
                    StateMatcher.CODEC.fieldOf("state"), StateMatcher.CODEC.listOf().optionalFieldOf("states", PROPERTIES_ANY))
            .xmap(either -> Either.unwrap(either.mapLeft(List::of)), stateMatchers -> {
                if (stateMatchers.size() == 1) return Either.left(stateMatchers.getFirst());
                return Either.right(stateMatchers);
            });

    public static final MapCodec<BlockStateIngredient> MAP_CODEC = RecordCodecBuilder.<BlockStateIngredient>mapCodec(
            instance -> instance.group(RegistryCodecs.homogeneousList(Registries.BLOCK)
                                    .fieldOf("values")
                                    .forGetter(BlockStateIngredient::getBlocks),
                            STATE_MATCHER_LIST_MAP_CODEC.forGetter(BlockStateIngredient::getStateMatchers))
                    .apply(instance, BlockStateIngredient::new)).validate(BlockStateIngredient::validate);

    public static final Codec<BlockStateIngredient> CODEC = MAP_CODEC.codec();

    @Getter(AccessLevel.PRIVATE)
    private final HolderSet<Block> blocks;
    /**
     * The state matchers
     */
    @Unmodifiable
    @Getter(AccessLevel.PRIVATE)
    private final List<StateMatcher> stateMatchers;
    /**
     * The matching states.
     * Use generated getter, {@link #getMatchingStates()}
     */
    @Getter(lazy = true)
    @EqualsAndHashCode.Exclude
    private final BlockState[] matchingStates = BlockStateIngredient.calculateMatchingStates(this.blocks,
            this.stateMatchers);

    /**
     * @param blocks The block
     *
     * @return A {@link BlockStateIngredient} which matches any of it's states
     */
    @SuppressWarnings("deprecation")
    @Contract(value = "_ -> new", pure = true)
    public static BlockStateIngredient anyState(final Block... blocks) {
        return new BlockStateIngredient(HolderSet.direct(Block::builtInRegistryHolder, Arrays.asList(blocks)),
                PROPERTIES_ANY);
    }

    /**
     * @param tag The tag
     *
     * @return A {@link BlockStateIngredient} which matches any of it's states
     */
    @SuppressWarnings("unused")
    @Contract(value = "_ -> new", pure = true)
    public static BlockStateIngredient anyState(final TagKey<Block> tag) {
        return new BlockStateIngredient(BuiltInRegistries.BLOCK.getOrCreateTag(tag), PROPERTIES_ANY);
    }

    /**
     * Like {@link #exactState(BlockState)} this matches the provided {@link BlockState} exactly <i><b>but</b></i> it will
     * ignore values of the provided properties
     * <p>
     * The following example code produces a {@link BlockStateIngredient} which ignores the {@link StairBlock#WATERLOGGED WATERLOGGED}
     * state, matching the others exactly.
     * <pre>{@code
     * final BlockState blockState = Blocks.STONE_STAIRS.defaultBlockState()
     *          // Values are set explicitly for demonstration, the result is identical to the default state
     *          .setValue(StairBlock.FACING, Direction.NORTH)
     *          .setValue(StairBlock.HALF, Half.BOTTOM)
     *          .setValue(StairBlock.SHAPE, StairsShape.STRAIGHT)
     *          .setValue(StairBlock.WATERLOGGED, false);
     *
     * final BlockStateIngredient ingredient = BlockStateIngredient.exactStateIgnoring(blockState, StairBlock.WATERLOGGED);
     * }</pre>
     * <p>
     * For more flexable {@link BlockStateIngredient}s use the builder
     *
     * @param blockState        The blockstate to exactly match
     * @param ignoredProperties The properties to ignore
     *
     * @return A {@link BlockStateIngredient} ignoring some properties and exactly matching the rest
     *
     * @see BlockStateIngredient#builder(Block...)
     * @see BlockStateIngredient#builder(Supplier...)
     * @see BlockStateIngredient#builder(TagKey)
     */
    @Contract(value = "_, _ -> new", pure = true)
    public static BlockStateIngredient exactStateIgnoring(final BlockState blockState,
            final Property<?>... ignoredProperties) {
        final var ignoredPropertiesSet = Arrays.stream(ignoredProperties)
                .collect(Collectors.toUnmodifiableSet());

        final var stateMatcherBuilder = StateMatcher.builder();
        blockState.getBlock().getStateDefinition().getProperties()
                .stream()
                .filter(Predicate.not(ignoredPropertiesSet::contains))
                .toList()
                .forEach(property -> {
                    //noinspection unchecked,rawtypes
                    stateMatcherBuilder.property(
                            PropertyMatcher.single((Property) property, (Comparable) blockState.getValue(property)));
                });

        return BlockStateIngredient.builder(blockState.getBlock())
                .state(stateMatcherBuilder)
                .build();
    }

    /**
     * @param blockState The blockstate to exactly match
     *
     * @return A {@link BlockStateIngredient} matching the exact blockstate passed in
     *
     * @see #builder(Block...)
     */
    @SuppressWarnings("unused")
    @Contract(value = "_ -> new", pure = true)
    public static BlockStateIngredient exactState(final BlockState blockState) {
        return BlockStateIngredient.exactStateIgnoring(blockState);
    }

    /**
     * @param blocks The blocks to create a builder for
     */
    @SafeVarargs
    @SuppressWarnings("deprecation")
    @Contract(value = "_ -> new", pure = true)
    public static BlockStateIngredientBuilder builder(final Supplier<? extends Block>... blocks) {
        return new BlockStateIngredientBuilder(
                HolderSet.direct(b -> b.get().builtInRegistryHolder(), Arrays.asList(blocks)));
    }

    /**
     * @param blocks The blocks to create a builder for
     */
    @SuppressWarnings("deprecation")
    @Contract(value = "_ -> new", pure = true)
    public static BlockStateIngredientBuilder builder(final Block... blocks) {
        return new BlockStateIngredientBuilder(HolderSet.direct(Block::builtInRegistryHolder, Arrays.asList(blocks)));
    }

    /**
     * @param tag The block tag to create a builder for
     */
    @Contract(value = "_ -> new", pure = true)
    public static BlockStateIngredientBuilder builder(final TagKey<Block> tag) {
        return new BlockStateIngredientBuilder(BuiltInRegistries.BLOCK.getOrCreateTag(tag));
    }

    /**
     * Stairs like blocks have multiple states which look visually the same, this helper will match the sister state
     * when applicable. Use addCommonProperties to match extra properties in addition to the stair state
     *
     * @param block               The block
     * @param direction           The direction
     * @param stairsShape         The stair shape
     * @param addCommonProperties A consumer for the common properties. See {@link StateMatcher#addProperty(PropertyMatcher)}
     *
     * @return The ingredient builder to support instances where adding additional states is required.
     */
    public static BlockStateIngredientBuilder stairs(final Block block, final Direction direction,
            final StairsShape stairsShape, final UnaryOperator<StateMatcher.StateMatcherBuilder> addCommonProperties) {
        if (stairsShape == StairsShape.STRAIGHT) {
            final var stateMatcherBuilder = StateMatcher.builder()
                    .property(PropertyMatcher.single(BlockStateProperties.HORIZONTAL_FACING, direction))
                    .property(PropertyMatcher.single(BlockStateProperties.STAIRS_SHAPE, StairsShape.STRAIGHT));
            return BlockStateIngredient.builder(block)
                    .state(addCommonProperties.apply(stateMatcherBuilder));
        }

        final var first = StateMatcher.builder()
                .property(PropertyMatcher.single(BlockStateProperties.HORIZONTAL_FACING, direction))
                .property(PropertyMatcher.single(BlockStateProperties.STAIRS_SHAPE, stairsShape));

        final var secondDirection = stairsShape == StairsShape.INNER_LEFT || stairsShape == StairsShape.OUTER_LEFT ? direction.getCounterClockWise() : direction.getClockWise();
        final var second = StateMatcher.builder()
                .property(PropertyMatcher.single(BlockStateProperties.HORIZONTAL_FACING, secondDirection))
                .property(PropertyMatcher.single(BlockStateProperties.STAIRS_SHAPE, switch (stairsShape) {
                    case INNER_LEFT -> StairsShape.INNER_RIGHT;
                    case INNER_RIGHT -> StairsShape.INNER_LEFT;
                    case OUTER_LEFT -> StairsShape.OUTER_RIGHT;
                    case OUTER_RIGHT -> StairsShape.OUTER_LEFT;
                    default -> throw new IllegalStateException("Unexpected value: " + stairsShape);
                }));

        return BlockStateIngredient.builder(block)
                .state(addCommonProperties.apply(first))
                .state(addCommonProperties.apply(second));
    }

    private static DataResult<BlockStateIngredient> validate(final BlockStateIngredient ingredient) {
        for (final var blockHolder : ingredient.blocks) {
            final var stateDefinition = blockHolder.value().getStateDefinition();

            for (final var stateMatcher : ingredient.stateMatchers) {
                final var optional = stateMatcher.checkProperties(stateDefinition);
                if (optional.isEmpty()) continue;
                return DataResult.error(
                        () -> MessageFormat.format("{0} lacks property {1}", stateDefinition, optional.get()));
            }
        }
        return DataResult.success(ingredient);
    }

    /**
     * Do not call, used by lombok to populate {@link #matchingStates}
     */
    private static BlockState[] calculateMatchingStates(final HolderSet<Block> blocks,
            final @Unmodifiable List<StateMatcher> stateMatchers) {
        return blocks.stream()
                .map(Holder::value)
                .flatMap(block -> {
                    final var stateDefinition = block.getStateDefinition();
                    return stateDefinition.getPossibleStates()
                            .stream()
                            .filter(blockState -> stateMatchers.isEmpty() || stateMatchers.stream()
                                    .anyMatch(stateMatcher -> stateMatcher.match(stateDefinition, blockState)));
                })
                .toArray(BlockState[]::new);
    }

    @Override
    public boolean test(final BlockState blockState) {
        // We are special, we match all BlockStates
        if (this == ANY) return true;
        // We are special, we match anything considered air
        if (this == AIR) return blockState.isAir();

        return Arrays.stream(this.getMatchingStates()).anyMatch(Predicate.isEqual(blockState));
    }

    @Override
    public String toString() {
        if (this == ANY) return "BlockStateIngredient(any)";
        if (this == AIR) return "BlockStateIngredient(air)";
        return MessageFormat.format("BlockStateIngredient({0}:{1})", this.blocks,
                this.stateMatchers.isEmpty() ? "[any]" : this.stateMatchers);
    }

    /**
     * A builder providing a flexable api for building {@link BlockStateIngredient}s.
     */
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class BlockStateIngredientBuilder {

        private final HolderSet<Block> values;
        private final ImmutableList.Builder<StateMatcher> stateMatchers = ImmutableList.builder();

        @CanIgnoreReturnValue
        @Contract("_ -> this")
        public BlockStateIngredientBuilder state(final PropertyMatcher... propertyMatchers) {
            return this.state(StateMatcher.of(propertyMatchers));
        }

        @CanIgnoreReturnValue
        @Contract("_ -> this")
        public BlockStateIngredientBuilder state(final StateMatcher.StateMatcherBuilder stateMatcherBuilder) {
            return this.state(stateMatcherBuilder.build());
        }

        @CanIgnoreReturnValue
        @Contract("_ -> this")
        public BlockStateIngredientBuilder state(final StateMatcher stateMatcher) {
            for (final var blockHolder : this.values) {
                final var stateDefinition = blockHolder.value().getStateDefinition();
                stateMatcher.checkProperties(stateDefinition).ifPresent(string -> {
                    throw new IllegalArgumentException(
                            MessageFormat.format("{0} lacks property {1}", stateDefinition, string));
                });
            }
            this.stateMatchers.add(stateMatcher);
            return this;
        }

        @Contract(value = "-> new", pure = true)
        public BlockStateIngredient build() {
            return new BlockStateIngredient(this.values, this.stateMatchers.build());
        }
    }
}