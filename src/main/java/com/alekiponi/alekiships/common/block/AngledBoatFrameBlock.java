package com.alekiponi.alekiships.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;
import java.util.IdentityHashMap;

public class AngledBoatFrameBlock extends SquaredAngleBlock {

    private static final IdentityHashMap<Item, AngledBoatFrameBlock> ANGLED_FRAMES = new IdentityHashMap<>();

    public AngledBoatFrameBlock(final Properties properties) {
        super(properties);
    }

    /**
     * Registers a mapping of the passed in {@link Item} instance and the passed in {@link AngledBoatFrameBlock}
     * A given {@link Item} instance may only map to one {@link AngledBoatFrameBlock} instance but multiple
     * {@link Item}s can map to the same {@link AngledBoatFrameBlock}.
     */
    public static void registerFrame(final Item item, final AngledBoatFrameBlock frameBlock) {
        ANGLED_FRAMES.put(item, frameBlock);
    }

    @Nullable
    public static AngledBoatFrameBlock getFrame(final Item item) {
        return ANGLED_FRAMES.get(item);
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(final BlockState blockState, final Level level, final BlockPos blockPos,
            final Player player, final InteractionHand hand, final BlockHitResult hitResult) {

        final ItemStack heldStack = player.getItemInHand(hand);

        final SquaredAngleBlock frameBlock = getFrame(heldStack.getItem());

        if (frameBlock != null) {
            final BlockState newBlockState = frameBlock.defaultBlockState().setValue(SHAPE, blockState.getValue(SHAPE))
                    .setValue(FACING, blockState.getValue(FACING))
                    .setValue(WATERLOGGED, blockState.getValue(WATERLOGGED));

            level.setBlockAndUpdate(blockPos, newBlockState);

            if (!player.getAbilities().instabuild) {
                heldStack.shrink(1);
            }

            final SoundType soundType = newBlockState.getSoundType(level, blockPos, player);

            level.playSound(player, blockPos, soundType.getPlaceSound(), SoundSource.BLOCKS,
                    (soundType.getVolume() + 1) / 2, soundType.getPitch() * 0.8F);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }
}