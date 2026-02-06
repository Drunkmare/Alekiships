package com.alekiponi.alekiships.common.recipe;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Arrays;
import org.jetbrains.annotations.Nullable;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.With;

/**
 * A friendly cached way to access blocks from a {@link LevelAccessor}.
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class PatternBlockStateCache {

    private static final Rotation[] ROTATIONS = Rotation.values();
    private final LoadingCache<BlockPos, WorldlyBlock>[] caches;

    /**
     * Creates a block state cache for efficient pattern
     *
     * @param levelAccessor The level accessor
     */
    public static PatternBlockStateCache create(final LevelAccessor levelAccessor) {
        final LoadingCache<BlockPos, WorldlyBlock> levelCache = CacheBuilder.newBuilder()
                .build(new CacheLoader<>() {
                    @Override
                    public WorldlyBlock load(final BlockPos blockPos) {
                        final var state = levelAccessor.getBlockState(blockPos);
                        final var blockEntity = levelAccessor.getBlockEntity(blockPos);
                        return new WorldlyBlock(state, blockEntity);
                    }
                });

        @SuppressWarnings("unchecked") final LoadingCache<BlockPos, WorldlyBlock>[] loadingCaches = Util.make(
                new LoadingCache[ROTATIONS.length], caches -> Arrays.stream(ROTATIONS)
                        .forEach(rotation -> caches[rotation.ordinal()] = rotateCache(levelCache, rotation)));

        return new PatternBlockStateCache(loadingCaches);
    }

    /**
     * Rotates the provided LoadingCache
     *
     * @param blockStateCache The state cache
     * @param rotation        The rotation, if {@link Rotation#NONE} returns the passed in cache
     *
     * @return A cache of BlockStates with the provided rotation applied.
     */
    private static LoadingCache<BlockPos, WorldlyBlock> rotateCache(
            final LoadingCache<BlockPos, WorldlyBlock> blockStateCache, final Rotation rotation) {
        if (rotation == Rotation.NONE) return blockStateCache;
        return CacheBuilder.newBuilder()
                .build(new CacheLoader<>() {
                    @Override
                    @SuppressWarnings("deprecation")
                    public WorldlyBlock load(final BlockPos blockPos) {
                        final var block = blockStateCache.getUnchecked(blockPos);
                        return block.withState(block.state().rotate(rotation));
                    }
                });
    }

    /**
     * Get a {@link BlockState} at the given position with the given mirror and rotation applied
     *
     * @param blockPos The block pos
     * @param rotation The rotation
     */
    public WorldlyBlock get(final BlockPos blockPos, final Rotation rotation) {
        return this.caches[rotation.ordinal()].getUnchecked(blockPos);
    }

    @With
    public record WorldlyBlock(BlockState state, @Nullable BlockEntity entity) {}
}