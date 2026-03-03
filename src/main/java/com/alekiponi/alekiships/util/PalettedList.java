package com.alekiponi.alekiships.util;

import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.ints.IntArrayList;

import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.AbstractList;
import java.util.Objects;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Range;
import org.jetbrains.annotations.UnknownNullability;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * A list which is backed in part by a palette. Good for when duplicate elements are needed in a specified order such as
 * shaped recipe patterns or our {@link com.alekiponi.alekiships.common.recipe.MultiblockPattern MultiblockPattern}
 */
@Getter(AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PalettedList<T> extends AbstractList<T> {

    private static final StreamCodec<ByteBuf, IntArrayList> ELEMENTS_STREAM_CODEC = ByteBufCodecs.collection(
            IntArrayList::new, ByteBufCodecs.VAR_INT);

    private final SimplePalette<T> palette;
    private final IntArrayList elements;

    @SuppressWarnings("unused")
    public PalettedList() {
        this(0);
    }

    public PalettedList(final int initialCapacity) {
        this(new SimplePalette<>(), new IntArrayList(initialCapacity));
    }

    /**
     * @param size         The size
     * @param defaultValue The default value to populate the list with
     */
    @Contract("_, _ -> new")
    public static <T> PalettedList<T> withSize(@Range(from = 0, to = Integer.MAX_VALUE) final int size,
            final T defaultValue) {
        assert size > 0;
        Objects.requireNonNull(defaultValue);
        final var palette = new SimplePalette<T>();
        palette.add(defaultValue);
        return new PalettedList<>(palette, IntArrayList.wrap(new int[size]));
    }

    /**
     * @param size The size
     */
    @Contract("_ -> new")
    @SuppressWarnings("unused")
    public static <T> PalettedList<T> withSize(@Range(from = 0, to = Integer.MAX_VALUE) final int size) {
        assert size > 0;
        return new PalettedList<>(new SimplePalette<>(), IntArrayList.wrap(new int[size]));
    }

    @Contract(pure = true)
    public static <B extends ByteBuf, T> StreamCodec.CodecOperation<B, T, PalettedList<T>> palettedList() {
        return PalettedList.palettedList(Integer.MAX_VALUE);
    }

    /**
     * @param maxSize The max size to encode/decode
     */
    @Contract(pure = true)
    public static <B extends ByteBuf, T> StreamCodec.CodecOperation<B, T, PalettedList<T>> palettedList(
            @Range(from = 0, to = Integer.MAX_VALUE) final int maxSize) {
        return codec -> PalettedList.streamCodec(codec, maxSize);
    }

    /**
     * @param codec   The element codec
     * @param maxSize The max size to encode/decode
     */
    @Contract(value = "_, _ -> new", pure = true)
    public static <B extends ByteBuf, T> StreamCodec<B, PalettedList<T>> streamCodec(
            final StreamCodec<? super B, T> codec, @Range(from = 0, to = Integer.MAX_VALUE) final int maxSize) {
        return StreamCodec.composite(SimplePalette.streamCodec(codec, maxSize), PalettedList::getPalette,
                ELEMENTS_STREAM_CODEC, PalettedList::getElements, PalettedList::new);
    }

    @Override
    @UnknownNullability
    public T set(@Range(from = 0, to = Integer.MAX_VALUE) final int index, final T element) {
        return this.palette.byId(this.elements.set(index, this.palette.idFor(element)));
    }

    @Override
    @UnknownNullability
    @Contract(pure = true)
    public T get(@Range(from = 0, to = Integer.MAX_VALUE) final int index) {
        return this.palette.byId(this.elements.getInt(index));
    }

    @Override
    @Contract(mutates = "this")
    public void add(@Range(from = 0, to = Integer.MAX_VALUE) final int index, final T element) {
        this.elements.add(index, this.palette.idFor(element));
    }

    @Override
    @UnknownNullability
    @Contract(mutates = "this")
    public T remove(@Range(from = 0, to = Integer.MAX_VALUE) final int index) {
        return this.palette.byId(this.elements.removeInt(index));
    }

    @Override
    @Contract(mutates = "this")
    public void clear() {
        this.palette.clear();
        this.elements.clear();
    }

    @Override
    @Contract(pure = true)
    @Range(from = 0, to = Integer.MAX_VALUE)
    public int size() {
        return this.elements.size();
    }
}