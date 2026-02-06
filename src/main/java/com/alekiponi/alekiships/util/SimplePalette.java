package com.alekiponi.alekiships.util;

import io.netty.buffer.ByteBuf;

import net.minecraft.core.IdMapper;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import org.jetbrains.annotations.Range;
import lombok.ToString;

/**
 * A simple palette of objects.
 */
@ToString
public class SimplePalette<T> extends IdMapper<T> {

    public SimplePalette() {
        this(16);
    }

    public SimplePalette(final int expectedSize) {
        super(expectedSize);
    }

    @SuppressWarnings("unused")
    public static <B extends ByteBuf, T> StreamCodec.CodecOperation<B, T, SimplePalette<T>> palette() {
        return palette(Integer.MAX_VALUE);
    }

    /**
     * @param maxSize The max size to encode/decode
     */
    public static <B extends ByteBuf, T> StreamCodec.CodecOperation<B, T, SimplePalette<T>> palette(
            @Range(from = 0, to = Integer.MAX_VALUE) final int maxSize) {
        return codec -> SimplePalette.streamCodec(codec, maxSize);
    }

    /**
     * @param codec   The object codec
     * @param maxSize The max size to encode/decode
     */
    public static <B extends ByteBuf, T> StreamCodec<B, SimplePalette<T>> streamCodec(
            final StreamCodec<? super B, T> codec, @Range(from = 0, to = Integer.MAX_VALUE) final int maxSize) {
        assert maxSize > 0 : "'maxSize' cannot be negative";
        return new StreamCodec<>() {
            @Override
            public SimplePalette<T> decode(final B buffer) {
                final int size = ByteBufCodecs.readCount(buffer, maxSize);
                final var palette = new SimplePalette<T>(Math.min(size, ByteBufCodecs.MAX_INITIAL_COLLECTION_SIZE));

                for (int i = 0; i < size; i++) {
                    palette.add(codec.decode(buffer));
                }

                return palette;
            }

            @Override
            public void encode(final B buffer, final SimplePalette<T> palette) {
                ByteBufCodecs.writeCount(buffer, palette.size(), maxSize);

                for (final var t : palette) {
                    codec.encode(buffer, t);
                }
            }
        };
    }

    /**
     * @param value An object to get an id for
     *
     * @return The id for the passed object
     *
     * @implNote Will automatically call {@link #addMapping(Object, int)} if no mapping is present
     */
    public @Range(from = 0, to = Integer.MAX_VALUE) int idFor(final T value) {
        int id = this.getId(value);
        if (id == -1) {
            id = this.nextId;
            this.add(value);
        }

        return id;
    }

    /**
     * {@inheritDoc}
     *
     * @return the integer ID used to identify the given object. {@value #DEFAULT} if the value is not in the pallet
     *
     * @see #idFor(Object)
     */
    @Override
    public @Range(from = DEFAULT, to = Integer.MAX_VALUE) int getId(final T value) {
        return super.getId(value);
    }

    /**
     * Clears the pallet
     */
    public void clear() {
        this.tToId.clear();
        this.idToT.clear();
        this.nextId = 0;
    }
}