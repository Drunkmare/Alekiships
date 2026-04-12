package com.drunkmare.deremet.common.block;

import com.drunkmare.deremet.util.VanillaWood;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

/**
 * The unprocessed millstone frame — the starting block of the millstone crafting sequence.
 *
 * Right-clicking with a TFC Quern converts this into a {@link MillstoneProcessedFrameBlock},
 * consuming the quern. The cogwheel offset for the processed state is determined here
 * based on the block's XZ position (checkerboard pattern).
 */
public class MillstoneFrameBlock extends MachineFrameBlock {

    public MillstoneFrameBlock(final Properties properties) {
        super(properties);
    }

    @Override
    protected Item getConversionItem() {
        // Placing a TFC Quern onto the frame starts the millstone build process.
        return TFCBlocks.QUERN.get().asItem();
    }

    @Override
    protected BlockState createProcessedState(final BlockPos blockPos, final BlockState blockState) {
        // The cogwheel offset alternates in a checkerboard pattern so adjacent frames
        // mesh correctly when both are fully built into Create millstones.
        final boolean cogwheelOffset = (Math.floorMod(blockPos.getX() + blockPos.getZ(), 2)) == 0;

        // Always converts to the OAK variant — the frame itself has no wood type.
        // Wood planks are added as a later crafting step on the processed frame.
        return DeReMetallicaBlocks.PROCESSED_MILLSTONE_FRAME.get(VanillaWood.OAK).get()
                .defaultBlockState()
                .setValue(BlockStateProperties.WATERLOGGED, blockState.getValue(WATERLOGGED))
                .setValue(MillstoneProcessedFrameBlock.COGWHEEL_OFFSET, cogwheelOffset);
    }
}
