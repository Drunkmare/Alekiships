package com.alekiponi.alekiships.common.block;

import com.alekiponi.alekiships.util.BoatMaterial;
import com.alekiponi.alekiships.util.CommonHelper;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraft.world.phys.BlockHitResult;

public class AngledWoodenBoatFrameBlock extends AngledBoatFrameBlock implements ProcessedBoatFrame {
    public static final IntegerProperty FRAME_PROCESSED = AlekiShipsBlockStateProperties.FRAME_PROCESSED;
    public static final int FULLY_PROCESSED = 3;

    public final BoatMaterial boatMaterial;

    public AngledWoodenBoatFrameBlock(final BoatMaterial boatMaterial, final Properties properties) {
        super(properties);
        this.registerDefaultState(
                this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(SHAPE, StairsShape.STRAIGHT)
                        .setValue(WATERLOGGED, false).setValue(FRAME_PROCESSED, 0));
        this.boatMaterial = boatMaterial;
    }

    public static void triggerDetection(Level level, BlockPos blockPos) {
        BlockPos search = blockPos.above();
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                BlockPos cur = search.relative(Direction.Axis.X, x).relative(Direction.Axis.Z, z);
                if (level.getBlockState(cur).getBlock() instanceof CleatBlock cleat) {
                    cleat.validateMultiblock(level, cur, level.getBlockState(cur));
                }
                if (level.getBlockState(cur).getBlock() instanceof OarlockBlock oarlock) {
                    oarlock.validateMultiblock(level, cur, level.getBlockState(cur));
                }
            }
        }
    }

    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(FRAME_PROCESSED));
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState blockState, Level level, BlockPos blockPos,
            Player player, InteractionHand hand, BlockHitResult hitResult) {

        final ItemStack heldStack = player.getItemInHand(hand);

        int processState = blockState.getValue(FRAME_PROCESSED);

        // Should we do plank stuff
        if (heldStack.is(this.boatMaterial.getDeckItem())) {
            // Must be [0,3)
            if (processState < FULLY_PROCESSED) {
                if (!player.getAbilities().instabuild) {
                    heldStack.shrink(1);
                }
                level.setBlockAndUpdate(blockPos, blockState.cycle(FRAME_PROCESSED));
                level.playSound(null, blockPos, SoundEvents.WOOD_PLACE, SoundSource.BLOCKS, 1.5F,
                        level.getRandom().nextFloat() * 0.1F + 0.9F);
                if (processState + 1 == FULLY_PROCESSED) {
                    triggerDetection(level, blockPos);
                }
                return ItemInteractionResult.SUCCESS;
            }
            return ItemInteractionResult.CONSUME;
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState blockState, Level level, BlockPos blockPos, Player player,
            BlockHitResult hitResult) {

        int processState = blockState.getValue(FRAME_PROCESSED);

        // Try extract
        if (processState <= FULLY_PROCESSED) {
            CommonHelper.giveItemToPlayer(player, new ItemStack(this.boatMaterial.getDeckItem()));
        }

        // Set ourselves back to our base
        if (processState == 0) {
            final BlockState newState = AlekiShipsBlocks.BOAT_FRAME_ANGLED.get().defaultBlockState()
                    .setValue(SHAPE, blockState.getValue(SHAPE)).setValue(FACING, blockState.getValue(FACING));

            level.setBlockAndUpdate(blockPos, newState);
            return InteractionResult.SUCCESS;
        }

        level.setBlockAndUpdate(blockPos, blockState.setValue(FRAME_PROCESSED, processState - 1));

        return InteractionResult.PASS;
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        return AlekiShipsBlocks.BOAT_FRAME_ANGLED.get().getCloneItemStack(level, pos, state);
    }

    @Override
    public IntegerProperty getProcessingProperty() {
        return FRAME_PROCESSED;
    }

    @Override
    public int getProcessingLimit() {
        return FULLY_PROCESSED;
    }

    @Override
    public BoatMaterial getBoatMaterial() {
        return this.boatMaterial;
    }
}