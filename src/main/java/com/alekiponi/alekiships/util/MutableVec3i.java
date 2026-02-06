package com.alekiponi.alekiships.util;

import com.google.common.collect.AbstractIterator;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.CheckReturnValue;

import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.Rotation;

import javax.annotation.Nullable;
import org.jetbrains.annotations.Contract;

/**
 * Similar to {@link net.minecraft.core.BlockPos.MutableBlockPos MutableBlockPos} but for
 */
public final class MutableVec3i extends Vec3i {

    public MutableVec3i() {
        this(0, 0, 0);
    }

    public MutableVec3i(final int x, final int y, final int z) {
        super(x, y, z);
    }

    public static MutableVec3i of(final int x, final int y, final int z) {
        return new MutableVec3i(x, y, z);
    }

    /**
     * Creates an {@link Iterable} that returns all positions eclosed between the two corners
     *
     * @param first  The first corner
     * @param second The second corner
     *
     * @implNote Unlike {@link #betweenClosed(int, int, int, int, int, int)} there is no enforced ordering
     */
    public static Iterable<MutableVec3i> betweenClosed(final Vec3i first, final Vec3i second) {
        return betweenClosed(Math.min(first.getX(), second.getX()), Math.min(first.getY(), second.getY()),
                Math.min(first.getZ(), second.getZ()), Math.max(first.getX(), second.getX()),
                Math.max(first.getY(), second.getY()), Math.max(first.getZ(), second.getZ()));
    }

    /**
     * Creates an Iterable that returns all positions in the box specified by the given corners. <strong>Coordinates
     * must be in order</strong>. e.g. x1 <= x2.
     * <p>
     * This method uses {@link MutableVec3i} instead of regular {@link Vec3i}, which grants better performance. However,
     * the resulting {@link Vec3i} instances can only be used inside the iteration loop (as otherwise the value will change),
     * unless {@link #immutable()} is called. This method is ideal for searching large areas and only storing a few locations.
     *
     * @implNote The {@link MutableVec3i} is safe to mutate in the loop body, any modification is overridden the next loop.
     * @see #betweenClosed(Vec3i, Vec3i)
     */
    public static Iterable<MutableVec3i> betweenClosed(final int x1, final int y1, final int z1, final int x2,
            final int y2, final int z2) {
        assert x1 <= x2 : "Contract Invalidation";
        assert y1 <= y2 : "Contract Invalidation";
        assert z1 <= z2 : "Contract Invalidation";

        final int maxX = x2 - x1 + 1;
        final int maxY = y2 - y1 + 1;
        final int maxZ = z2 - z1 + 1;
        final int end = maxX * maxY * maxZ;
        return () -> new AbstractIterator<>() {
            private final MutableVec3i cursor = new MutableVec3i();
            private int index;

            @Override
            protected @Nullable MutableVec3i computeNext() {
                if (this.index == end) return this.endOfData();

                final int xIncrement = this.index % maxX;
                final int n = this.index / maxX;
                final int yIncrement = n % maxY;
                final int zIncrement = n / maxY;
                this.index++;
                return this.cursor.set(x1 + xIncrement, y1 + yIncrement, z1 + zIncrement);
            }
        };
    }

    @Override
    @CanIgnoreReturnValue
    @Contract(value = "_ -> this", mutates = "this")
    public MutableVec3i setX(final int x) {
        super.setX(x);
        return this;
    }

    @Override
    @CanIgnoreReturnValue
    @Contract(value = "_ -> this", mutates = "this")
    public MutableVec3i setY(final int y) {
        super.setY(y);
        return this;
    }

    @Override
    @CanIgnoreReturnValue
    @Contract(value = "_ -> this", mutates = "this")
    public MutableVec3i setZ(final int z) {
        super.setZ(z);
        return this;
    }

    @CanIgnoreReturnValue
    @Contract(value = "_, _, _ -> this", mutates = "this")
    public MutableVec3i set(final int x, final int y, final int z) {
        this.setX(x);
        this.setY(y);
        this.setZ(z);
        return this;
    }

    /**
     * @param rotation The rotation
     *
     * @return A new rotated {@link Vec3i}
     */
    @CheckReturnValue
    @Contract(value = "_ -> new")
    public Vec3i rotate(final Rotation rotation) {
        return rotation == Rotation.NONE ? this.immutable() : CommonHelper.rotate(this, rotation);
    }

    @CheckReturnValue
    @Contract(value = " -> new")
    public Vec3i immutable() {
        return new Vec3i(this.getX(), this.getY(), this.getZ());
    }
}