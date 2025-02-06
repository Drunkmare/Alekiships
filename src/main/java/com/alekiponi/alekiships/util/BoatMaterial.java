package com.alekiponi.alekiships.util;

import com.alekiponi.alekiships.common.entity.vehicle.ConstructionSloopVariant;
import com.alekiponi.alekiships.common.entity.vehicle.RowboatVariant;

import net.minecraft.resources.ResourceKey;
import net.minecraft.util.StringRepresentable;

/**
 * This represents a unique boat material. This may be wood or another material
 *
 * @apiNote Implementing this on an enum is recommended as these objects are compared using identity
 * @deprecated Boat materials will be dynamic via datapack registry
 */
@Deprecated(forRemoval = true)
public interface BoatMaterial extends StringRepresentable {

    /**
     * @return Whether this material withstands lava
     */
    boolean withstandsLava();

    // TODO temporary helper while we migrate away from static boat materials
    ResourceKey<RowboatVariant> rowboatKey();

    // TODO temporary helper while we migrate away from static boat materials
    ResourceKey<ConstructionSloopVariant> sloopConstructionKey();
}