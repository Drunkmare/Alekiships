package com.alekiponi.alekiships.util;

import com.alekiponi.alekiships.common.entity.vehicle.AbstractVehicle;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

/**
 * This represents a unique boat material. This may be wood or another material
 *
 * @apiNote Implementing this on an enum is recommended as these objects are compared using identity
 */
public interface BoatMaterial extends StringRepresentable {

    /**
     * @return The Item instance that is used for building the deck of a sloop. This should also be the same instance
     * that maps to the frame blocks
     */
    default Item getDeckItem() {
        return this.getDeckBlock().getBlock().asItem();
    }

    /**
     * @return The Item instance that's used for the railing of sloops
     */
    Item getRailing();

    /**
     * @return The Item instance that's used for the log parts of sloop construction
     */
    Item getStrippedLog();

    /**
     * @return Whether this material withstands lava
     */
    boolean withstandsLava();

    BlockState getDeckBlock();

    /**
     * @param boatType The entity type that should be returned
     * @return An optional entity type for the passed in {@link BoatType}
     */
    Optional<EntityType<? extends AbstractVehicle>> getEntityType(final BoatType boatType);

    enum BoatType {
        ROWBOAT,
        SLOOP,
        CONSTRUCTION_SLOOP
    }
}