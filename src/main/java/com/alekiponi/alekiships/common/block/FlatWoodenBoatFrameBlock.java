package com.alekiponi.alekiships.common.block;

import com.alekiponi.alekiships.common.AlekiShipsDataMaps;
import com.alekiponi.alekiships.common.block.entity.FrameBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.function.Supplier;
import org.jetbrains.annotations.Nullable;

public class FlatWoodenBoatFrameBlock extends FlatBoatFrameBlock implements EntityBlock {

    public static final IntegerProperty FRAME_PROCESSED = AlekiShipsBlockStateProperties.FRAME_PROCESSED;
    public static final int FULLY_PROCESSED = 3;

    private final Supplier<FlatBoatFrameBlock> emptyFrame;

    public FlatWoodenBoatFrameBlock(final Supplier<FlatBoatFrameBlock> emptyFrame, final Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(FRAME_PROCESSED, 0));
        this.emptyFrame = emptyFrame;
    }

    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(FRAME_PROCESSED));
    }

    @Override
    protected ItemInteractionResult useItemOn(final ItemStack heldStack, final BlockState blockState, final Level level,
            final BlockPos blockPos, final Player player, final InteractionHand hand, final BlockHitResult hitResult) {
        return FrameBlock.tryContinueFillFrame(heldStack, blockState, level, blockPos, player,
                AlekiShipsDataMaps.FLAT_BOAT_FRAME, FRAME_PROCESSED, FULLY_PROCESSED);
    }

    @Override
    protected InteractionResult useWithoutItem(final BlockState blockState, final Level level, final BlockPos blockPos,
            final Player player, final BlockHitResult hitResult) {
        return FrameBlock.tryExtractFrame(blockState, level, blockPos, player, this.emptyFrame.get(), FRAME_PROCESSED,
                FULLY_PROCESSED);
    }

    @Override
    public boolean onDestroyedByPlayer(final BlockState state, final Level level, final BlockPos pos,
            final Player player, final boolean willHarvest, final FluidState fluid) {
        return FrameBlock.onDestroy(state, level, pos, player, fluid);
    }

    @Override
    protected void onRemove(final BlockState prevState, final Level level, final BlockPos pos,
            final BlockState newState, final boolean movedByPiston) {
        FrameBlock.dropContents(prevState, level, pos, newState);
        super.onRemove(prevState, level, pos, newState, movedByPiston);
    }

    @Override
    public ItemStack getCloneItemStack(final BlockState state, final HitResult target, final LevelReader level,
            final BlockPos pos, final Player player) {
        return FrameBlock.getCloneStack(state, target, level, pos, player, this.emptyFrame.get());
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(final BlockPos pos, final BlockState state) {
        return new FrameBlockEntity(pos, state);
    }
}