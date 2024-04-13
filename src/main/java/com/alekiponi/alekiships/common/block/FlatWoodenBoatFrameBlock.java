package com.alekiponi.alekiships.common.block;

import com.alekiponi.alekiships.util.AlekiShipsHelper;
import com.alekiponi.alekiships.util.BoatMaterial;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;

public class FlatWoodenBoatFrameBlock extends FlatBoatFrameBlock implements ProcessedBoatFrame {

    public static final IntegerProperty FRAME_PROCESSED = AlekiShipsBlockStateProperties.FRAME_PROCESSED;
    public static final int FULLY_PROCESSED = 3;
    public final BoatMaterial boatMaterial;

    public FlatWoodenBoatFrameBlock(final BoatMaterial boatMaterial, final Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(FRAME_PROCESSED, 0));
        this.boatMaterial = boatMaterial;
    }

    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(FRAME_PROCESSED));
    }

    @Override
    public InteractionResult use(final BlockState blockState, final Level level, final BlockPos blockPos,
            final Player player, final InteractionHand hand, final BlockHitResult hitResult) {

        if (hand != InteractionHand.MAIN_HAND) return InteractionResult.PASS;

        final ItemStack heldStack = player.getItemInHand(hand);

        final int processState = blockState.getValue(FRAME_PROCESSED);

        // Try extract
        if (heldStack.isEmpty() && !level.isClientSide) {
            // Extract an item
            if (processState <= FULLY_PROCESSED) {
                AlekiShipsHelper.giveItemToPlayer(player, new ItemStack(this.boatMaterial.getDeckItem()));
            }

            // Set ourselves back to our base
            if (processState == 0) {
                level.setBlockAndUpdate(blockPos, AlekiShipsBlocks.BOAT_FRAME_FLAT.get().defaultBlockState());
                return InteractionResult.SUCCESS;
            }

            level.setBlockAndUpdate(blockPos, blockState.setValue(FRAME_PROCESSED, processState - 1));

            return InteractionResult.SUCCESS;
        }

        // Should we do plank stuff
        if (heldStack.is(this.boatMaterial.getDeckItem())) {
            // Must be [0,3)
            if (processState < FULLY_PROCESSED) {
                if(!player.getAbilities().instabuild){
                    heldStack.shrink(1);
                }
                level.setBlockAndUpdate(blockPos, blockState.cycle(FRAME_PROCESSED));
                level.playSound(null, blockPos, SoundEvents.WOOD_PLACE, SoundSource.BLOCKS, 1.5F,
                        level.getRandom().nextFloat() * 0.1F + 0.9F);
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.CONSUME;
        }

        return InteractionResult.PASS;
    }

    @Override
    @SuppressWarnings("deprecation")
    public ItemStack getCloneItemStack(final BlockGetter blockGetter, final BlockPos blockPos,
            final BlockState blockState) {
        // We don't exist as an item so pass it the base version instead
        return AlekiShipsBlocks.BOAT_FRAME_FLAT.get().getCloneItemStack(blockGetter, blockPos, blockState);
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