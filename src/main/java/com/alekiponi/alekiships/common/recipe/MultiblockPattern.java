package com.alekiponi.alekiships.common.recipe;

import com.google.common.collect.AbstractIterator;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;

import com.alekiponi.alekiships.common.recipe.ingredient.block.BlockStateIngredient;
import com.alekiponi.alekiships.common.recipe.ingredient.block.entity.BlockEntityIngredient;
import com.alekiponi.alekiships.common.recipe.util.Pattern3D;
import com.alekiponi.alekiships.util.AlekiShipsExtraCodecs;
import com.alekiponi.alekiships.util.CommonHelper;
import com.alekiponi.alekiships.util.MutableVec3i;
import com.alekiponi.alekiships.util.PalettedList;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.CommonLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

import net.neoforged.neoforge.common.util.NeoForgeExtraCodecs;

import java.text.MessageFormat;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.jetbrains.annotations.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * A pattern of multiple blocks
 */
@Slf4j
@ToString
@EqualsAndHashCode
public final class MultiblockPattern {

    public static final MapCodec<MultiblockPattern> MAP_CODEC = Data.MAP_CODEC.flatXmap(MultiblockPattern::unpack,
            multiblockPattern -> multiblockPattern.data.map(DataResult::success)
                    .orElseGet(() -> DataResult.error(() -> "Unable to encode pattern")));

    private static final Rotation[] ROTATIONS = Rotation.values();

    private static final StreamCodec<ByteBuf, Vec3i> PATTERN_SIZE_STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, Vec3i::getX, ByteBufCodecs.VAR_INT, Vec3i::getY, ByteBufCodecs.VAR_INT, Vec3i::getZ,
            Vec3i::new);

    public static final StreamCodec<RegistryFriendlyByteBuf, MultiblockPattern> STREAM_CODEC = StreamCodec.composite(
            PATTERN_SIZE_STREAM_CODEC, MultiblockPattern::getSize,
            PatternElement.STREAM_CODEC.apply(PalettedList.palettedList()),
            multiblockPattern -> multiblockPattern.pattern, MultiblockPattern::new);

    /**
     * The size of the multiblock pattern
     */
    @Getter
    public final Vec3i size;
    /**
     * Flattened list of our block pattern, indexed with {@link Pattern3D#getPackedIndex(int, int, int, int, int)}
     *
     * @see #getIngredient(int, int, int)
     */
    @Unmodifiable
    @ToString.Exclude
    private final PalettedList<PatternElement> pattern;
    @Unmodifiable
    @ToString.Exclude
    private final Set<Block> uniqueBlocks;
    /**
     * Keep our unpacked data around to facilitate datagen
     */
    @ToString.Exclude
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private final Optional<Data> data;

    private MultiblockPattern(final Vec3i size, final PalettedList<PatternElement> pattern) {
        this(size, pattern, Optional.empty());
    }

    private MultiblockPattern(final Vec3i size, final PalettedList<PatternElement> pattern,
            @SuppressWarnings("OptionalUsedAsFieldOrParameterType") final Optional<Data> data) {
        this.size = size;
        this.pattern = pattern;
        this.uniqueBlocks = pattern.stream()
                .map(PatternElement::blockIngredient)
                .map(BlockStateIngredient::getMatchingStates)
                .flatMap(Arrays::stream)
                .map(BlockBehaviour.BlockStateBase::getBlock)
                .collect(Collectors.toSet());
        this.data = data;
    }

    public static MultiblockPattern of(final Map<Character, PatternElement> key, final Pattern3D pattern) {
        return unpack(new Data(key, pattern)).getOrThrow();
    }

    private static DataResult<MultiblockPattern> unpack(final Data data) {
        return data.pattern.unpack(size -> PalettedList.withSize(size, new PatternElement(BlockStateIngredient.AIR)),
                        data.key, symbol -> switch (symbol) {
                            case ' ' -> new PatternElement(BlockStateIngredient.AIR);
                            case '_' -> new PatternElement(BlockStateIngredient.ANY);
                            default -> null;
                        })
                .map(states -> new MultiblockPattern(data.pattern.size(), states, Optional.of(data)));
    }

    public static MultiblockPatternBuilder builder() {return new MultiblockPatternBuilder();}

    public void place(final CommonLevelAccessor level, final BlockPos placePos, final int stateVariant,
            final Rotation rotation) {
        final var adjustedPlacePos = placePos.offset(CommonHelper.rotateWithPivot(Vec3i.ZERO, rotation, this.getSize()))
                .offset(switch (rotation) {
                    case NONE -> 0;
                    case CLOCKWISE_90, CLOCKWISE_180 -> -(this.width() + 1);
                    case COUNTERCLOCKWISE_90 -> -(this.depth() - 1);
                }, 0, switch (rotation) {
                    case NONE -> 0;
                    case CLOCKWISE_90 -> this.depth() - 1;
                    case CLOCKWISE_180, COUNTERCLOCKWISE_90 -> -(this.depth() + 1);
                });
        for (final var elementInfo : this.shape()) {
            final var matchingStates = elementInfo.element().blockIngredient().getMatchingStates();
            @SuppressWarnings("deprecation") final var blockState = matchingStates[stateVariant % matchingStates.length].rotate(rotation);

            final var blockPos = adjustedPlacePos.offset(CommonHelper.rotate(elementInfo.position(), rotation));

            level.setBlock(blockPos, blockState, Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE);
            elementInfo.element().entityIngredient().ifPresent(entityIngredient -> {
                final var blockEntity = level.getBlockEntity(blockPos);
                if (blockEntity != null) entityIngredient.initialize(blockEntity);
            });
        }
    }

    public MultiblockPatternMatchResult matches(final BlockPos startPos, final PatternBlockStateCache cache) {
        // Quit early if the starting block isn't part of the pattern at all
        if (!this.uniqueBlocks.contains(cache.get(startPos, Rotation.NONE)
                .state().getBlock())) {
            return MultiblockPatternMatchResult.fail(0);
        }

        final var bounds = this.getBounds();
        var bestMatch = MultiblockPatternMatchResult.fail(0);
        for (final var rotation : ROTATIONS) {
            final var offset = CommonHelper.rotate(bounds, rotation);
            final var boundsStart = startPos.offset(offset.getX(), bounds.getY(), offset.getZ());
            final var boundsEnd = startPos.offset(-offset.getX(), -bounds.getY(), -offset.getZ());

            for (final var multiblockRoot : BlockPos.betweenClosed(boundsStart, boundsEnd)) {
                final var matchResult = this.matches(multiblockRoot, rotation, cache);
                if (matchResult.matchType == MatchResult.Type.SUCCESS) {
                    log.debug("Found a successful match with match total of {}", matchResult.matchedCount);
                    return MultiblockPatternMatchResult.success(multiblockRoot, rotation, matchResult.matchedCount,
                            this);
                }

                if (bestMatch.matchedCount() < matchResult.matchedCount) {
                    bestMatch = MultiblockPatternMatchResult.fail(matchResult.matchedCount);
                }
            }
        }

        return bestMatch;
    }

    private MatchResult matches(final BlockPos multiblockRoot, final Rotation rotation,
            final PatternBlockStateCache cache) {
        int totalMatched = 0;
        for (final var elementInfo : this.shape()) {
            final var blockPos = multiblockRoot.offset(CommonHelper.rotate(elementInfo.position, rotation));
            final var worldlyBlock = cache.get(blockPos, switch (rotation) {
                case NONE, CLOCKWISE_180 -> rotation;
                case COUNTERCLOCKWISE_90 -> Rotation.CLOCKWISE_90;
                case CLOCKWISE_90 -> Rotation.COUNTERCLOCKWISE_90;
            });
            if (!elementInfo.test(worldlyBlock)) {
                return MatchResult.fail(totalMatched);
            }
            totalMatched++;
        }

        return MatchResult.success(totalMatched);
    }

    /**
     * @param patternPosition The position in the pattern
     *
     * @return The element at the specified pattern position
     */
    public PatternElement getIngredient(final Vec3i patternPosition) {
        return this.getIngredient(patternPosition.getX(), patternPosition.getY(), patternPosition.getZ());
    }

    /**
     * @param x The x position
     * @param y The y position
     * @param z The z position
     *
     * @return The element at the specified pattern position
     *
     * @see #getIngredient(Vec3i)
     */
    public PatternElement getIngredient(@Range(from = 0, to = Integer.MAX_VALUE) final int x,
            @Range(from = 0, to = Integer.MAX_VALUE) final int y,
            @Range(from = 0, to = Integer.MAX_VALUE) final int z) {
        final @Range(from = 0, to = Integer.MAX_VALUE) int width = this.width();
        final @Range(from = 0, to = Integer.MAX_VALUE) int depth = this.depth();
        return this.pattern.get(Pattern3D.getPackedIndex(width, depth, x, y, z));
    }

    /**
     * @return The set of unique {@link BlockStateIngredient}s this pattern contains.
     */
    @Contract(value = " -> new", pure = true)
    public Set<BlockStateIngredient> getUniqueIngredients() {
        return this.pattern.stream()
                .map(PatternElement::blockIngredient)
                .collect(Collectors.toSet());
    }

    /**
     * Helper for iterating over the patterns shape.
     *
     * @implNote Each {@link MultiblockPatternElementInfo#position} is a shared {@link MutableVec3i} and is mutated
     * every iteration
     */
    @Contract(pure = true)
    public Iterable<MultiblockPatternElementInfo> shape() {
        return () -> new AbstractIterator<>() {
            private final Iterator<MutableVec3i> iter = MutableVec3i.betweenClosed(Vec3i.ZERO,
                    MultiblockPattern.this.getBounds()).iterator();

            @Override
            protected @Nullable MultiblockPattern.MultiblockPatternElementInfo computeNext() {
                if (!this.iter.hasNext()) return this.endOfData();

                final var next = this.iter.next();
                return new MultiblockPatternElementInfo(next, MultiblockPattern.this.getIngredient(next));
            }
        };
    }

    /**
     * The width of the pattern
     */
    @CheckReturnValue
    @Range(from = 0, to = Integer.MAX_VALUE)
    public int width() {return this.size.getX();}

    /**
     * The height of the pattern
     */
    @CheckReturnValue
    @Range(from = 0, to = Integer.MAX_VALUE)
    public int height() {return this.size.getY();}

    /**
     * The depth of the pattern
     */
    @CheckReturnValue
    @Range(from = 0, to = Integer.MAX_VALUE)
    public int depth() {return this.size.getZ();}

    public Vec3i getBounds() {return this.size.offset(-1, -1, -1);}

    /**
     * An element in a multiblock pattern
     *
     * @param position The local position of the pattern element. {@link Vec3i#ZERO} is the bottom left corner
     * @param element  The element of the pattern
     */
    public record MultiblockPatternElementInfo(Vec3i position, PatternElement element) {
        public boolean test(final PatternBlockStateCache.WorldlyBlock worldlyBlock) {
            return this.element.test(worldlyBlock);
        }
    }

    public record PatternElement(BlockStateIngredient blockIngredient,
            Optional<BlockEntityIngredient> entityIngredient) implements Predicate<PatternBlockStateCache.WorldlyBlock> {

        public static final Codec<PatternElement> FLAT_BLOCK_STATE_INGREDIENT_CODEC = BlockStateIngredient.CODEC.flatComapMap(
                PatternElement::new, patternElement -> {
                    if (patternElement.entityIngredient.isPresent()) {
                        return DataResult.error(
                                () -> "This codec cannot encode PatternElements with an EntityIngredient");
                    }
                    return DataResult.success(patternElement.blockIngredient);
                });

        public static final Codec<PatternElement> CODEC = NeoForgeExtraCodecs.withAlternative(
                FLAT_BLOCK_STATE_INGREDIENT_CODEC, RecordCodecBuilder.create(instance -> instance.group(
                        BlockStateIngredient.CODEC.fieldOf("block").forGetter(PatternElement::blockIngredient),
                        BlockEntityIngredient.CODEC.optionalFieldOf("entity")
                                .forGetter(PatternElement::entityIngredient)).apply(instance, PatternElement::new)));

        public static final StreamCodec<RegistryFriendlyByteBuf, PatternElement> STREAM_CODEC = StreamCodec.composite(
                BlockStateIngredient.STREAM_CODEC, PatternElement::blockIngredient,
                ByteBufCodecs.optional(BlockEntityIngredient.STREAM_CODEC), PatternElement::entityIngredient,
                PatternElement::new);

        public PatternElement(final BlockStateIngredient blockIngredient,
                final BlockEntityIngredient entityIngredient) {
            this(blockIngredient, Optional.of(entityIngredient));
        }

        public PatternElement(final BlockStateIngredient blockIngredient) {
            this(blockIngredient, Optional.empty());
        }

        @Override
        public boolean test(final PatternBlockStateCache.WorldlyBlock worldlyBlock) {
            return this.blockIngredient.test(worldlyBlock.state()) && this.entityIngredient.map(
                    entityIngredient -> entityIngredient.test(worldlyBlock.entity())).orElse(true);
        }
    }

    private record MatchResult(Type matchType, int matchedCount) {

        @Contract("_ -> new")
        static MatchResult fail(final int matchedCount) {
            return new MatchResult(Type.FAIL, matchedCount);
        }

        @Contract("_ -> new")
        static MatchResult success(final int matchedCount) {
            return new MatchResult(Type.SUCCESS, matchedCount);
        }

        private enum Type {
            FAIL,
            SUCCESS
        }
    }

    private record Data(@Unmodifiable Map<Character, PatternElement> key, Pattern3D pattern) {
        public static final MapCodec<Data> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                        ExtraCodecs.strictUnboundedMap(AlekiShipsExtraCodecs.reservedSymbols(' ', '_'), PatternElement.CODEC)
                                .fieldOf("key")
                                .forGetter(Data::key), Pattern3D.CODEC.fieldOf("pattern").forGetter(Data::pattern))
                .apply(instance, Data::new));
    }

    public static class MultiblockPatternBuilder {
        private final Map<Character, PatternElement> key = new LinkedHashMap<>();
        private final Pattern3D.Pattern3DBuilder pattern = Pattern3D.builder();

        @CanIgnoreReturnValue
        @Contract("_ -> this")
        public MultiblockPatternBuilder layers(final Pattern3D.Layer.LayerBuilder... layers) {
            return this.layers(Arrays.stream(layers)
                    .map(Pattern3D.Layer.LayerBuilder::build)
                    .toArray(Pattern3D.Layer[]::new));
        }

        @CanIgnoreReturnValue
        @Contract("_ -> this")
        public MultiblockPatternBuilder layers(final Pattern3D.Layer... layers) {
            Arrays.stream(layers)
                    .forEach(this.pattern::layer);
            return this;
        }

        @CanIgnoreReturnValue
        @Contract("_, _ -> this")
        public MultiblockPatternBuilder define(final Character symbol, final Block block) {
            return this.define(symbol, BlockStateIngredient.anyState(block));
        }

        @CanIgnoreReturnValue
        @Contract("_, _ -> this")
        public MultiblockPatternBuilder define(final Character symbol,
                final BlockStateIngredient.BlockStateIngredientBuilder ingredientBuilder) {
            return this.define(symbol, ingredientBuilder.build());
        }

        @CanIgnoreReturnValue
        @Contract("_, _, _ -> this")
        public MultiblockPatternBuilder define(final Character symbol,
                final BlockStateIngredient.BlockStateIngredientBuilder ingredientBuilder,
                final BlockEntityIngredient blockEntityIngredient) {
            return this.define(symbol, ingredientBuilder.build(), blockEntityIngredient);
        }

        @CanIgnoreReturnValue
        @Contract("_, _, _ -> this")
        public MultiblockPatternBuilder define(final Character symbol, final BlockStateIngredient ingredient,
                final BlockEntityIngredient blockEntityIngredient) {
            return this.define(symbol, new PatternElement(ingredient, blockEntityIngredient));
        }

        @CanIgnoreReturnValue
        @Contract("_, _ -> this")
        public MultiblockPatternBuilder define(final Character symbol, final BlockStateIngredient ingredient) {
            if (ingredient.equals(BlockStateIngredient.AIR)) {
                throw new IllegalArgumentException(MessageFormat.format(
                        "Ingredient {0} is implicitly defined. Refer to it with '' '' (whitespace)", ingredient));
            }

            if (ingredient.equals(BlockStateIngredient.ANY)) {
                throw new IllegalArgumentException(MessageFormat.format(
                        "Ingredient {0} is implicitly defined. Refer to it with ''_'' (underscore)", ingredient));
            }

            return this.define(symbol, new PatternElement(ingredient));
        }

        @CanIgnoreReturnValue
        @Contract("_, _ -> this")
        public MultiblockPatternBuilder define(final Character symbol, final PatternElement patternElement) {
            if (this.key.containsKey(symbol)) {
                throw new IllegalArgumentException("Symbol '" + symbol + "' is already defined!");
            }

            if (symbol == ' ' || symbol == '_') {
                throw new IllegalArgumentException(
                        "Symbol '" + symbol + " ' (whitespace) is reserved and cannot be defined");
            }

            this.key.put(symbol, patternElement);
            return this;
        }

        public MultiblockPattern build() {
            return MultiblockPattern.of(this.key, this.pattern.build());
        }
    }
}