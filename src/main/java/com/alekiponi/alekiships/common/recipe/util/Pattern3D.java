package com.alekiponi.alekiships.common.recipe.util;

import com.google.common.collect.ImmutableList;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.CheckReturnValue;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import it.unimi.dsi.fastutil.chars.CharArraySet;
import it.unimi.dsi.fastutil.chars.CharSet;

import net.minecraft.core.Vec3i;
import net.minecraft.util.ExtraCodecs;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.IntFunction;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Range;
import org.jetbrains.annotations.Unmodifiable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * A 3D Pattern recipe style. Wraps a list of {@link Layer}s
 */
@ToString
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class Pattern3D {
    public static final Codec<Pattern3D> CODEC = Layer.CODEC.listOf().comapFlatMap(layers -> {
        if (layers.isEmpty()) {
            return DataResult.error(() -> "Invalid pattern: empty pattern not allowed");
        }

        final int width = layers.getFirst().width();
        final int depth = layers.getFirst().depth();

        for (final var layer : layers) {
            if (width != layer.width()) {
                return DataResult.error(
                        () -> "Invalid pattern: each layer must be the same width. Width of " + layer.width() + " does not match " + width);
            }
            if (depth != layer.depth()) {
                return DataResult.error(
                        () -> "Invalid pattern: each layer must be the same depth. Depth of " + layer.depth() + " does not match " + depth);
            }
        }

        return DataResult.success(new Pattern3D(layers));
    }, pattern -> pattern.layers);

    private final @Unmodifiable List<Layer> layers;

    @Contract(" -> new")
    public static Pattern3DBuilder builder() {return new Pattern3DBuilder();}

    /**
     * @param depth The packed depth
     * @param width The packed width
     * @param x     The x index (width)
     * @param y     The y index (height)
     * @param z     The z index (depth)
     *
     * @return The index into a packed 1D collection
     */
    @Contract(pure = true)
    public static int getPackedIndex(@Range(from = 0, to = Integer.MAX_VALUE) final int width,
            @Range(from = 0, to = Integer.MAX_VALUE) final int depth,
            @Range(from = 0, to = Integer.MAX_VALUE) final int x, @Range(from = 0, to = Integer.MAX_VALUE) final int y,
            @Range(from = 0, to = Integer.MAX_VALUE) final int z) {
        return (depth * width * y) + (width * z) + x;
    }

    /**
     * Unpack the pattern into a some sort of flattened 1D {@link List} using the provided key.
     * Typically {@link com.alekiponi.alekiships.util.PalettedList PaletedList} is a good fit as patterns tend to use
     * few keys and use them multiple times.
     *
     * @param <L>                  The list type. This list must be mutable, in particular {@link List#set(int, Object)}
     *                             must be callable.
     * @param listFactory          The factory function for constructing the list with the provided size.
     * @param key                  The key for this pattern
     * @param reservedSymbolMapper The reserved symbol mapper function for mapping reserved symbols. Vanilla for example
     *                             reserves the ' ' symbol and maps it to the empty ingredient
     *
     * @return The pattern as a packed 1D list
     *
     * @implNote The layer to list conversion happens in reverse order to make the encoding more sensible (IE in json the
     * first layer encoded is actually the top most layer)
     * @see #getPackedIndex(int, int, int, int, int)
     */
    public <L extends List<? super T>, T> DataResult<L> unpack(final IntFunction<L> listFactory,
            final Map<Character, T> key, final Function<Character, T> reservedSymbolMapper) {
        final int height = this.height();
        final int width = this.width();
        final int depth = this.depth();
        final var list = listFactory.apply(height * width * depth);
        final CharSet charset = new CharArraySet(key.keySet());

        // Layers are in reverse order (easier to manually encode patterns)
        for (int y = height - 1; y >= 0; y--) {
            final var layer = this.get(y);
            for (int x = 0; x < width; x++) {
                final String row = layer.get(x);
                for (int z = 0; z < depth; z++) {
                    final char symbol = row.charAt(z);
                    final var t = key.getOrDefault(symbol, reservedSymbolMapper.apply(symbol));
                    if (t == null) {
                        return DataResult.error(
                                () -> "Pattern references symbol '" + symbol + "' but it's not defined in the key");
                    }

                    charset.remove(symbol);
                    final var index = getPackedIndex(width, depth, x, height - 1 - y, z);
                    list.set(index, t);
                }
            }
        }

        if (!charset.isEmpty()) {
            return DataResult.error(() -> "Key defines symbols that aren't used in pattern: " + charset);
        }

        return DataResult.success(list);
    }

    @Contract(" -> new")
    public Vec3i size() {
        return new Vec3i(this.width(), this.height(), this.depth());
    }

    @Contract(pure = true)
    public Layer get(final int index) {
        return this.layers.get(index);
    }

    @Contract(pure = true)
    public @Range(from = 1, to = Integer.MAX_VALUE) int height() {
        return this.layers.size();
    }

    @Contract(pure = true)
    public @Range(from = 1, to = Integer.MAX_VALUE) int width() {
        return this.layers.getFirst().width();
    }

    @Contract(pure = true)
    public @Range(from = 1, to = Integer.MAX_VALUE) int depth() {
        return this.layers.getFirst().depth();
    }

    public static final class Pattern3DBuilder {
        private final ImmutableList.Builder<Layer> layers = ImmutableList.builder();
        private int width = -1, depth = -1;

        @CanIgnoreReturnValue
        @Contract("_ -> this")
        public Pattern3DBuilder layer(final Layer layer) {
            if (this.width == -1) {
                this.width = layer.width();
                this.depth = layer.depth();
                this.layers.add(layer);
                return this;
            }

            if (layer.width() != this.width) {
                throw new IllegalArgumentException(
                        "Layers must be the same width! Width of " + layer.width() + " doesn't match " + this.width);
            }
            if (layer.depth() != this.depth) {
                throw new IllegalArgumentException(
                        "Layers must be the same depth! Depth of " + layer.depth() + " doesn't match " + this.depth);
            }

            this.layers.add(layer);
            return this;
        }

        @CheckReturnValue
        @Contract(" -> new")
        public Pattern3D build() {
            final var layers = this.layers.build();
            if (layers.isEmpty()) throw new IllegalArgumentException("Pattern cannot be empty");
            return new Pattern3D(layers);
        }
    }

    /**
     * A layer of a 3D Pattern. These are composed of a list of strings. Each string is a row with each character being
     * a collum. Think {@link net.minecraft.world.item.crafting.ShapedRecipePattern vanilla shaped recipe patterns} but
     * flat on the ground
     */
    @ToString
    @EqualsAndHashCode
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Layer {
        public static final Codec<Layer> CODEC = ExtraCodecs.NON_EMPTY_STRING.listOf().comapFlatMap(rows -> {
            if (rows.isEmpty()) return DataResult.error(() -> "Invalid layer: empty layers not allowed");

            final int width = rows.getFirst().length();

            for (final String row : rows) {
                if (width != row.length()) {
                    return DataResult.error(
                            () -> "Invalid layer: each row must be the same width. Width of " + row.length() + " does not match " + width);
                }
            }

            return DataResult.success(new Layer(rows));
        }, layer -> layer.rows);

        private final @Unmodifiable List<String> rows;

        @Contract(" -> new")
        public static LayerBuilder builder() {return new LayerBuilder();}

        @Contract(pure = true)
        public @Range(from = 1, to = Integer.MAX_VALUE) int width() {
            return this.rows.size();
        }

        @Contract(pure = true)
        public @Range(from = 1, to = Integer.MAX_VALUE) int depth() {
            return this.rows.getFirst().length();
        }

        @Contract(pure = true)
        public String get(final int index) {
            return this.rows.get(index);
        }

        public static final class LayerBuilder {

            private final ImmutableList.Builder<String> rows = ImmutableList.builder();
            private int width = -1;

            @CanIgnoreReturnValue
            @Contract("_ -> this")
            public LayerBuilder row(final String row) {
                if (row.isEmpty()) throw new IllegalArgumentException("Cannot have empty rows");

                if (this.width == -1) {
                    this.width = row.length();
                    this.rows.add(row);
                    return this;
                }

                if (row.length() != this.width) {
                    throw new IllegalArgumentException(
                            "Rows must be the same width! Width of " + row.length() + " doesn't match " + this.width);
                }
                this.rows.add(row);
                return this;
            }

            @Contract(" -> new")
            public Layer build() {
                final var rows = this.rows.build();
                if (rows.isEmpty()) throw new IllegalArgumentException("Rows cannot be empty");
                return new Layer(rows);
            }
        }
    }
}