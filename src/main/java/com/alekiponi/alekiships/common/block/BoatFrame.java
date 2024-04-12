package com.alekiponi.alekiships.common.block;

import com.alekiponi.alekiships.util.BoatMaterial;

/**
 * This interface describes a block which is a boat frame.
 * Typically, boat frames will have processing states in which case you should implement {@link ProcessedBoatFrame}
 */
public interface BoatFrame {
    /**
     * @return The boat material of this block
     */
    BoatMaterial getBoatMaterial();
}