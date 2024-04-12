package com.alekiponi.alekiships.common.block;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

/**
 * This interface describes a block which is a boat frame and has processing states such as
 * {@link AngledWoodenBoatFrameBlock} or {@link FlatWoodenBoatFrameBlock}.
 */
public interface ProcessedBoatFrame extends BoatFrame {

    /**
     * @return {@code true} when not a {@link ProcessedBoatFrame} otherwise if the processing property value equals
     * the processing limit
     */
    static boolean isFullyProcessed(final BlockState blockState) {
        if (!(blockState.getBlock() instanceof ProcessedBoatFrame frame)) return true;

        return blockState.getValue(frame.getProcessingProperty()) == frame.getProcessingLimit();
    }

    /**
     * @return The IntegerProperty this block uses to store its processing states.
     */
    IntegerProperty getProcessingProperty();

    /**
     * @return The integer value that represents a fully processed BoatFrame
     */
    int getProcessingLimit();
}