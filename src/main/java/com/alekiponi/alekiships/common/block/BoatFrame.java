package com.alekiponi.alekiships.common.block;

import com.alekiponi.alekiships.util.BoatMaterial;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * This interface describes a block which is a boat frame.
 * Typically, boat frames will have processing states in which case you should implement {@link ProcessedBoatFrame}
 */
public interface BoatFrame {

    /**
     * Grabs a boat material from a blockstate if possible
     * @param blockState The blockstate
     * @return The blockstates boat material or null
     */
    @Nullable
    static BoatMaterial fromBlockstate(final BlockState blockState) {
        return blockState.getBlock() instanceof BoatFrame boatFrame ? boatFrame.getBoatMaterial() : null;
    }

    /**
     * @return The boat material of this block
     */
    BoatMaterial getBoatMaterial();
}