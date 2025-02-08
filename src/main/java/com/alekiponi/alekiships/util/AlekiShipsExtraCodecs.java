package com.alekiponi.alekiships.util;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;

import java.text.MessageFormat;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class AlekiShipsExtraCodecs {

    public static final Splitter.MapSplitter PROPERTY_VALUE_SPLITTER = Splitter.on(',').withKeyValueSeparator('=');

    /**
     * A codec for the more common {@link net.minecraft.commands.arguments.blocks.BlockStateParser} blockstate syntax (used in commands)
     * Unnamed properties use the default
     */
    public static final Codec<BlockState> BLOCK_STATE_CODEC = ExtraCodecs.NON_EMPTY_STRING.comapFlatMap(
            AlekiShipsExtraCodecs::parseBlockState, blockState -> {
                final var block = blockState.getBlock();
                final var defaultState = block.defaultBlockState();
                if (blockState == defaultState) return BuiltInRegistries.BLOCK.getKey(block).toString();

                final var stringBuilder = new StringBuilder();
                stringBuilder.append(BuiltInRegistries.BLOCK.getKey(block));
                final var values = blockState.getValues();
                if (!values.isEmpty()) {
                    stringBuilder.append('[');
                    stringBuilder.append(values.entrySet()
                            .stream()
                            .filter(entry -> {
                                final var key = entry.getKey();
                                return blockState.getValue(key) != defaultState.getValue(key);
                            })
                            .map(entry -> {
                                @SuppressWarnings("rawtypes") final Property property = entry.getKey();
                                @SuppressWarnings("unchecked") final var valueName = property.getName(entry.getValue());
                                return property.getName() + "=" + valueName;
                            })
                            .collect(Collectors.joining(",")));
                    stringBuilder.append(']');
                }

                return stringBuilder.toString();
            });

    public static DataResult<BlockState> parseBlockState(final String string) {
        final var propertiesStart = string.indexOf('[');
        final var propertiesEnd = string.indexOf(']');
        // No encoded properties, use default blockstate
        if (propertiesStart == -1) {
            // Ensure there isn't a closing bracket
            if (propertiesEnd != -1) return DataResult.error(() -> "Missing opening '['");

            return parseBlock(string).map(Block::defaultBlockState);
        }
        // No closing bracket
        if (propertiesEnd == -1) return DataResult.error(() -> "Missing closing ']'");
        // Closing bracket is not the last character
        if (propertiesEnd != string.length() - 1) {
            return DataResult.error(() -> "No characters allowed after closing ']'");
        }

        final var blockResult = parseBlock(string.substring(0, propertiesStart));
        if (blockResult.isError()) return DataResult.error(((DataResult.Error<?>) blockResult)::message);

        final Block block = blockResult.getOrThrow();
        return parsePropertyValuesMap(string.substring(propertiesStart + 1, propertiesEnd),
                block.getStateDefinition()).map(map -> {
            var blockState = block.defaultBlockState();
            for (final var entry : map.entrySet()) {
                //noinspection unchecked,rawtypes
                blockState = blockState.setValue((Property) entry.getKey(), (Comparable) entry.getValue());
            }
            return blockState;
        });
    }

    public static DataResult<Block> parseBlock(final String registryName) {
        return ResourceLocation.read(registryName)
                .flatMap(id -> BuiltInRegistries.BLOCK.getOptional(id)
                        .map(DataResult::success).orElseGet(() -> DataResult.error(() -> "Unknown block " + id)));
    }

    public static DataResult<Map<Property<?>, Comparable<?>>> parsePropertyValuesMap(final String properties,
            final StateDefinition<?, ?> stateDefinition) {
        final Map<String, String> stringPropertyValuesMap;
        try { // Splitting can fail
            stringPropertyValuesMap = PROPERTY_VALUE_SPLITTER.split(properties);
        } catch (final IllegalArgumentException e) {
            return DataResult.error(() -> MessageFormat.format(
                    "Failure during parsing of properties. Reported cause: {0}. Ensure properties are formated like [property_a=value,property_b=value]",
                    e.getMessage()));
        }

        final var builder = ImmutableMap.<Property<?>, Comparable<?>>builderWithExpectedSize(
                stringPropertyValuesMap.size());

        for (final var propertyValuePair : stringPropertyValuesMap.entrySet()) {
            final var propertyName = propertyValuePair.getKey();
            final var property = stateDefinition.getProperty(propertyName);
            if (property == null) {
                return DataResult.error(() -> MessageFormat.format("Unknown property: ''{0}''", propertyName));
            }

            final var valueName = propertyValuePair.getValue();
            final var propertyValue = property.getValue(valueName);
            if (propertyValue.isEmpty()) {
                return DataResult.error(
                        () -> MessageFormat.format("Unknown value: ''{0}'' for property: ''{1}'' {2}", valueName,
                                propertyName, property.getPossibleValues()));
            }
            builder.put(property, propertyValue.get());
        }

        return DataResult.success(builder.build());
    }

    /**
     * @param codec  The codec
     * @param ops    The ops
     * @param input  The input
     * @param setter The setter for the decoding result
     * @param <T>    The type to decode
     * @param <I>    The type to decode from
     */
    public static <T, I> void load(final Codec<T> codec, final DynamicOps<I> ops, final I input,
            final Consumer<T> setter) {
        codec.parse(ops, input).ifSuccess(setter);
    }

    /**
     * @param codec  The codec
     * @param ops    The ops
     * @param input  The input value
     * @param setter The setter for the encoding result
     * @param <T>    The type to encode
     * @param <I>    The type to encode to
     */
    public static <T, I> void save(final Codec<T> codec, final DynamicOps<I> ops, final T input,
            final Consumer<I> setter) {
        codec.encodeStart(ops, input).ifSuccess(setter);
    }

    /**
     * Helper allowing easier map codec mapping as vanilla doesn't provide these helpers
     *
     * @param codec The map codec
     * @param to    The function to transform to {@code <S>}
     * @param from  The function to transform to {@code <A>}
     * @param <S>   The output codec type
     * @param <A>   The input codec type
     */
    public static <S, A> MapCodec<S> flatComapMap(final MapCodec<A> codec, final Function<? super A, ? extends S> to,
            final Function<? super S, ? extends DataResult<? extends A>> from) {
        return MapCodec.of(codec.flatComap(from), codec.map(to), () -> codec + "[flatComapMapped]");
    }
}