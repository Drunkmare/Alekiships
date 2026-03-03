package com.alekiponi.alekiships.common.recipe;

import com.alekiponi.alekiships.util.CommonHelper;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import lombok.*;
import lombok.experimental.Accessors;

public sealed interface MultiblockPatternMatchResult extends Comparable<MultiblockPatternMatchResult> {
    @Contract("_, _, _, _ -> new")
    static MultiblockPatternMatchResult success(final BlockPos multiblockRoot, final Rotation rotation,
            final int matchedCount, final MultiblockPattern multiblockPattern) {
        return new SuccessResult(multiblockRoot, rotation, matchedCount, multiblockPattern);
    }

    @Contract("_ -> new")
    static MultiblockPatternMatchResult fail(final int matchedCount) {
        return new FailResult(matchedCount);
    }

    /**
     * Position the passed in entity to the center of the multiblock
     *
     * @param entity The entity to position
     */
    void positionEntity(Entity entity);

    void assemble(LevelAccessor levelAccessor, Runnable runnable);

    /**
     * @return If this match result was a failure
     */
    @CheckReturnValue
    boolean failed();

    /**
     * @return If this match result was a success
     */
    @CheckReturnValue
    boolean success();

    int matchedCount();

    @ToString
    @EqualsAndHashCode
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    final class SuccessResult implements MultiblockPatternMatchResult {
        public final BlockPos multiblockRoot;
        public final Rotation rotation;
        @Getter
        @Accessors(fluent = true)
        public final int matchedCount;
        public final MultiblockPattern multiblockPattern;

        @Override
        public void positionEntity(final Entity entity) {
            if (this.failed()) return;
            final int facingAngle = this.rotation.rotate(90, 360);
            final var offset = CommonHelper.rotate(this.multiblockPattern.getBounds(), this.rotation);
            final var multiblockCenter = Vec3.atLowerCornerWithOffset(this.multiblockRoot, (offset.getX() + 1) / 2D,
                    offset.getY() / 2D, (offset.getZ() + 1) / 2D);
            entity.moveTo(multiblockCenter, facingAngle, 0);
        }

        @Override
        public void assemble(final LevelAccessor levelAccessor, final Runnable runnable) {
            final List<BlockPos> matchedBlocks = new ArrayList<>();
            for (final var patternElementInfo : this.multiblockPattern.shape()) {
                final var blockPos = this.multiblockRoot.offset(
                        CommonHelper.rotate(patternElementInfo.position(), this.rotation));
                final var blockInWorld = new PatternBlockStateCache.WorldlyBlock(
                        levelAccessor.getBlockState(blockPos).rotate(levelAccessor, blockPos, switch (this.rotation) {
                            case NONE, CLOCKWISE_180 -> this.rotation;
                            case CLOCKWISE_90, COUNTERCLOCKWISE_90 -> this.rotation.getRotated(Rotation.CLOCKWISE_180);
                        }), levelAccessor.getBlockEntity(blockPos));

                if (patternElementInfo.test(blockInWorld)) {
                    matchedBlocks.add(blockPos.immutable());
                }
            }
            matchedBlocks.forEach(blockPos -> {
                final var blockState = levelAccessor.getBlockState(blockPos);
                if (levelAccessor.setBlock(blockPos, blockState.getFluidState().createLegacyBlock(),
                        Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE)) {
                    levelAccessor.levelEvent(LevelEvent.PARTICLES_DESTROY_BLOCK, blockPos, Block.getId(blockState));
                }
            });
            runnable.run();
            matchedBlocks.forEach(blockPos -> levelAccessor.blockUpdated(blockPos, Blocks.AIR));
        }

        @Override
        public boolean failed() {
            return false;
        }

        @Override
        public boolean success() {
            return true;
        }

        @Override
        public int compareTo(final MultiblockPatternMatchResult o) {
            return switch (o) {
                case FailResult ignored -> 1;
                case SuccessResult successResult -> Integer.compare(this.matchedCount, successResult.matchedCount);
            };
        }
    }

    @ToString
    @EqualsAndHashCode
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    final class FailResult implements MultiblockPatternMatchResult {
        @Getter
        @Accessors(fluent = true)
        private final int matchedCount;

        @Override
        public void positionEntity(final Entity entity) {

        }

        @Override
        public void assemble(final LevelAccessor levelAccessor, final Runnable runnable) {

        }

        @Override
        public boolean failed() {
            return true;
        }

        @Override
        public boolean success() {
            return false;
        }

        @Override
        public int compareTo(final MultiblockPatternMatchResult o) {
            return switch (o) {
                case FailResult failResult -> Integer.compare(this.matchedCount, failResult.matchedCount);
                case SuccessResult ignored -> -1;
            };
        }
    }
}