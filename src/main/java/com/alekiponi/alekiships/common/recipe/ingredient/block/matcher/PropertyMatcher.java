package com.alekiponi.alekiships.common.recipe.ingredient.block.matcher;

import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;

import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import lombok.*;

/**
 * A property matcher
 */
@ToString
@EqualsAndHashCode
@Getter(AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class PropertyMatcher {

    private static final Interner<PropertyMatcher> VALUES = Interners.newWeakInterner();

    /**
     * Encoded as a map. {@literal {"<property name a>": <value matcher>, "<property name b>": <value matcher>}}
     */
    public static final Codec<List<PropertyMatcher>> LIST_FROM_MAP_CODEC = Codec.unboundedMap(
                    ExtraCodecs.NON_EMPTY_STRING, ValueMatcher.CODEC)
            .xmap(matcherMap -> matcherMap.entrySet()
                    .stream()
                    .map(matcherEntry -> PropertyMatcher.create(matcherEntry.getKey(), matcherEntry.getValue()))
                    .toList(), propertyMatchers -> propertyMatchers.stream()
                    .collect(Collectors.toMap(PropertyMatcher::getName, PropertyMatcher::getValueMatcher)));

    public static final StreamCodec<ByteBuf, PropertyMatcher> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, PropertyMatcher::getName, ValueMatcher.STREAM_CODEC,
            PropertyMatcher::getValueMatcher, PropertyMatcher::create);
    /**
     * The property name
     */
    private final String name;
    /**
     * The value matcher for the property
     */
    private final ValueMatcher valueMatcher;

    /**
     * Helper for interning our {@link PropertyMatcher} instances
     *
     * @param name         The property name
     * @param valueMatcher The value matcher for the property
     */
    private static PropertyMatcher create(final String name, final ValueMatcher valueMatcher) {
        return VALUES.intern(new PropertyMatcher(name, valueMatcher));
    }

    /**
     * @param property The property to match
     * @param value    The property value to match
     *
     * @return A {@link PropertyMatcher} for the property value
     *
     * @see PropertyMatcher#ranged(Property, Comparable, Comparable)
     * @see PropertyMatcher#min(Property, Comparable)
     * @see PropertyMatcher#max(Property, Comparable)
     * @see PropertyMatcher#list(Property, Comparable[])
     */
    @Contract(value = "_, _ -> new", pure = true)
    public static <T extends Comparable<T>, V extends T> PropertyMatcher single(final Property<T> property,
            final V value) {
        return PropertyMatcher.create(property.getName(), new ExactMatcher(property.getName(value)));
    }

    /**
     * @param property The property to match
     * @param min      The minimum property value
     * @param max      The maximum property value
     *
     * @return A {@link PropertyMatcher} for a range of property values
     *
     * @see PropertyMatcher#single(Property, Comparable)
     * @see PropertyMatcher#min(Property, Comparable)
     * @see PropertyMatcher#max(Property, Comparable)
     * @see PropertyMatcher#list(Property, Comparable[])
     */
    @SuppressWarnings("unused")
    @Contract(value = "_, _, _ -> new", pure = true)
    public static <T extends Comparable<T>, V extends T> PropertyMatcher ranged(final Property<T> property, V min,
            V max) {
        if (min.compareTo(max) == 0) {
            throw new IllegalArgumentException(MessageFormat.format("Min: {0} Max: {1} are the same value", min, max));
        }

        // Args in the wrong order
        if (min.compareTo(max) > 0) {
            final V temp = min;
            min = max;
            max = temp;
        }
        assert min.compareTo(max) < 0;

        return PropertyMatcher.create(property.getName(),
                new RangedMatcher(Optional.of(property.getName(min)), Optional.of(property.getName(max))));
    }

    /**
     * @param property The property to match
     * @param min      The minimum property value
     *
     * @return A {@link PropertyMatcher} which will match when the property value is at least {@code min} or larger
     *
     * @see PropertyMatcher#single(Property, Comparable)
     * @see PropertyMatcher#ranged(Property, Comparable, Comparable)
     * @see PropertyMatcher#max(Property, Comparable)
     * @see PropertyMatcher#list(Property, Comparable[])
     */
    @Contract(value = "_, _ -> new", pure = true)
    public static <T extends Comparable<T>, V extends T> PropertyMatcher min(final Property<T> property, final V min) {
        return PropertyMatcher.create(property.getName(),
                new RangedMatcher(Optional.of(property.getName(min)), Optional.empty()));
    }

    /**
     * @param property The property to match
     * @param max      The maximum property value
     *
     * @return A {@link PropertyMatcher} which will match when the property value is at most equal to {@code max} or smaller
     *
     * @see PropertyMatcher#single(Property, Comparable)
     * @see PropertyMatcher#ranged(Property, Comparable, Comparable)
     * @see PropertyMatcher#min(Property, Comparable)
     * @see PropertyMatcher#list(Property, Comparable[])
     */
    @Contract(value = "_, _ -> new", pure = true)
    public static <T extends Comparable<T>, V extends T> PropertyMatcher max(final Property<T> property, final V max) {
        return PropertyMatcher.create(property.getName(),
                new RangedMatcher(Optional.empty(), Optional.of(property.getName(max))));
    }

    /**
     * @param property The property to match
     * @param values   The property values to match
     *
     * @return A {@link PropertyMatcher} for a list of property values
     *
     * @see PropertyMatcher#single(Property, Comparable)
     * @see PropertyMatcher#ranged(Property, Comparable, Comparable)
     * @see PropertyMatcher#min(Property, Comparable)
     * @see PropertyMatcher#max(Property, Comparable)
     */
    @SafeVarargs
    @Contract(value = "_, _ -> new", pure = true)
    public static <T extends Comparable<T>, V extends T> PropertyMatcher list(final Property<T> property,
            final V... values) {
        if (values.length < 1) throw new IllegalArgumentException("Must pass at least 1 value");
        return PropertyMatcher.create(property.getName(), new ListMatcher(Arrays.stream(values)
                .map(property::getName)
                .toList()));
    }

    /**
     * @param stateDefinition The State Definition used to grab the {@link Property} this matcher represents
     * @param stateHolder     The State Holder to match against
     *
     * @return If the provided state holder matches
     */
    @CheckReturnValue
    public <S extends StateHolder<?, S>> boolean match(final StateDefinition<?, S> stateDefinition,
            final S stateHolder) {
        final Property<?> property = stateDefinition.getProperty(this.name);
        return property != null && this.valueMatcher.match(property, stateHolder);
    }

    /**
     * Checks if the provided {@link StateDefinition} is applicable to this {@link PropertyMatcher}.
     *
     * @param stateDefinition The State Definition
     *
     * @return The property name if the provided State Definition lacks the property
     */
    @ApiStatus.Internal
    @CheckReturnValue
    public Optional<String> checkProperty(final StateDefinition<?, ?> stateDefinition) {
        final var property = stateDefinition.getProperty(this.name);
        return property != null ? Optional.empty() : Optional.of(this.name);
    }

    /**
     * A property value matcher
     */
    private sealed interface ValueMatcher {

        Codec<ValueMatcher> CODEC = Codec.xor(ExactMatcher.CODEC, Codec.xor(ListMatcher.CODEC, RangedMatcher.CODEC))
                .xmap(ValueMatcher::merge, ValueMatcher::split);

        StreamCodec<ByteBuf, ValueMatcher> STREAM_CODEC = ByteBufCodecs.either(ExactMatcher.STREAM_CODEC,
                        ByteBufCodecs.either(ListMatcher.STREAM_CODEC, RangedMatcher.STREAM_CODEC))
                .map(ValueMatcher::merge, ValueMatcher::split);


        private static ValueMatcher merge(final Either<ExactMatcher, Either<ListMatcher, RangedMatcher>> valueMatcher) {
            return valueMatcher.map(Function.identity(), Either::unwrap);
        }

        private static Either<ExactMatcher, Either<ListMatcher, RangedMatcher>> split(final ValueMatcher valueMatcher) {
            return switch (valueMatcher) {
                case ExactMatcher exactMatcher -> Either.left(exactMatcher);
                case ListMatcher listMatcher -> Either.right(Either.left(listMatcher));
                case RangedMatcher rangedMatcher -> Either.right(Either.right(rangedMatcher));
            };
        }

        /**
         * If the state holder
         *
         * @param <T>         The value type for the property
         * @param property    The property to check
         * @param stateHolder The state holder
         *
         * @return If the property value in the state holder matches this value matcher
         */
        @CheckReturnValue
        <T extends Comparable<T>> boolean match(Property<T> property, StateHolder<?, ?> stateHolder);
    }

    /**
     * A value matcher matching an exact value
     *
     * @param value The property value to match
     */
    record ExactMatcher(String value) implements ValueMatcher {

        public static final Codec<ExactMatcher> CODEC = ExtraCodecs.NON_EMPTY_STRING.xmap(ExactMatcher::new,
                ExactMatcher::value);

        public static final StreamCodec<ByteBuf, ExactMatcher> STREAM_CODEC = ByteBufCodecs.STRING_UTF8.map(
                ExactMatcher::new, ExactMatcher::value);

        @Override
        public <T extends Comparable<T>> boolean match(final Property<T> property,
                final StateHolder<?, ?> stateHolder) {
            final var t = stateHolder.getValue(property);
            final var optional = property.getValue(this.value);
            return optional.isPresent() && t.compareTo(optional.get()) == 0;
        }
    }

    /**
     * A value matcher for a list of values.
     *
     * @param values The list of property values
     */
    record ListMatcher(List<String> values) implements ValueMatcher {

        public static final Codec<ListMatcher> CODEC = ExtraCodecs.nonEmptyList(ExtraCodecs.NON_EMPTY_STRING.listOf())
                .xmap(ListMatcher::new, ListMatcher::values);

        public static final StreamCodec<ByteBuf, ListMatcher> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()), ListMatcher::values, ListMatcher::new);

        @Override
        public <T extends Comparable<T>> boolean match(final Property<T> property,
                final StateHolder<?, ?> stateHolder) {
            final var propertyValue = stateHolder.getValue(property);

            return this.values.stream()
                    .map(property::getValue)
                    .flatMap(Optional::stream).anyMatch(t -> propertyValue.compareTo(t) == 0);
        }
    }

    /**
     * A value matcher for a range of values.
     *
     * @param minValue The optional minimum property value
     * @param maxValue The optional maximum property value
     */
    record RangedMatcher(Optional<String> minValue, Optional<String> maxValue) implements ValueMatcher {

        public static final Codec<RangedMatcher> CODEC = RecordCodecBuilder.<RangedMatcher>create(
                matcherInstance -> matcherInstance.group(
                                ExtraCodecs.NON_EMPTY_STRING.optionalFieldOf("min").forGetter(RangedMatcher::minValue),
                                ExtraCodecs.NON_EMPTY_STRING.optionalFieldOf("max").forGetter(RangedMatcher::maxValue))
                        .apply(matcherInstance, RangedMatcher::new)).validate(RangedMatcher::validate);

        public static final StreamCodec<ByteBuf, RangedMatcher> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8), RangedMatcher::minValue,
                ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8), RangedMatcher::maxValue, RangedMatcher::new);

        private static DataResult<RangedMatcher> validate(final RangedMatcher rangedMatcher) {
            // Neither field is present
            if (rangedMatcher.minValue.or(rangedMatcher::maxValue).isEmpty()) {
                return DataResult.error(() -> "Requires at least one of [min, max]");
            }

            return DataResult.success(rangedMatcher);
        }

        @Override
        public <T extends Comparable<T>> boolean match(final Property<T> property,
                final StateHolder<?, ?> stateHolder) {
            final var propertyValue = stateHolder.getValue(property);
            if (this.minValue.isPresent()) {
                final var value = property.getValue(this.minValue.get());
                if (value.isEmpty() || propertyValue.compareTo(value.get()) < 0) {
                    return false;
                }
            }

            if (this.maxValue.isPresent()) {
                final var value = property.getValue(this.maxValue.get());
                return value.isPresent() && propertyValue.compareTo(value.get()) <= 0;
            }

            return true;
        }
    }
}