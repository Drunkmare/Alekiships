package com.alekiponi.alekiships.common.recipe.ingredient.block.matcher;

import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import io.netty.buffer.ByteBuf;

import com.alekiponi.alekiships.common.recipe.ingredient.block.BlockStateIngredient;

import net.minecraft.core.Direction;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.StairsShape;

import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Unmodifiable;
import lombok.Builder;
import lombok.Singular;

/**
 * A state matcher consisting of a list of properties to match
 *
 * @param propertyMatchers The property matchers to match against
 */
@Builder(toBuilder = true)
public record StateMatcher(@Singular("property") @Unmodifiable List<PropertyMatcher> propertyMatchers) {

    public static final Codec<StateMatcher> CODEC = PropertyMatcher.LIST_FROM_MAP_CODEC.xmap(StateMatcher::new,
                    StateMatcher::propertyMatchers)
            .validate(stateMatcher -> stateMatcher.propertyMatchers.isEmpty() ? DataResult.error(
                    () -> "State matchers must include at least one property matcher") : DataResult.success(
                    stateMatcher));

    public static final StreamCodec<ByteBuf, StateMatcher> STREAM_CODEC = PropertyMatcher.STREAM_CODEC.apply(
                    ByteBufCodecs.list())
            .map(StateMatcher::new, StateMatcher::propertyMatchers);

    private static final Interner<StateMatcher> VALUES = Interners.newWeakInterner();

    /**
     * @param propertyMatchers The property matchers
     */
    public static StateMatcher of(final PropertyMatcher... propertyMatchers) {
        if (propertyMatchers.length < 1) throw new IllegalArgumentException("Must pass at least 1 PropertyMatcher");
        return new StateMatcher(List.of(propertyMatchers));
    }

    /**
     * Helper for adding a {@link PropertyMatcher} to a {@link StateMatcherBuilder}. Useful for helpers like
     * {@link BlockStateIngredient#stairs(Block, Direction, StairsShape, UnaryOperator)}
     *
     * @param propertyMatcher The property matcher to add to the state matcher
     */
    public static UnaryOperator<StateMatcherBuilder> addProperty(final PropertyMatcher propertyMatcher) {
        return stateMatcherBuilder -> stateMatcherBuilder.property(propertyMatcher);
    }

    /**
     * @param stateDefinition The State Definition used to grab the {@link Property} this matcher represents
     * @param stateHolder     The State Holder to match against
     *
     * @return If the provided state holder matches all the property matchers
     */
    @Contract(pure = true)
    public <S extends StateHolder<?, S>> boolean match(final StateDefinition<?, S> stateDefinition,
            final S stateHolder) {
        return this.propertyMatchers.stream()
                .allMatch(propertyMatcher -> propertyMatcher.match(stateDefinition, stateHolder));
    }

    /**
     * Checks if the provided {@link StateDefinition} is applicable to this {@link StateMatcher}.
     *
     * @param stateDefinition The State Definition
     *
     * @return The property name if the provided State Definition lacks the property
     */
    @ApiStatus.Internal
    @CheckReturnValue
    public Optional<String> checkProperties(final StateDefinition<?, ?> stateDefinition) {
        for (final var propertyMatcher : this.propertyMatchers) {
            final var optional = propertyMatcher.checkProperty(stateDefinition);
            if (optional.isEmpty()) continue;

            return optional;
        }
        return Optional.empty();
    }
}