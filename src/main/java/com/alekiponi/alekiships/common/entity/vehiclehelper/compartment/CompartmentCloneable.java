package com.alekiponi.alekiships.common.entity.vehiclehelper.compartment;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BlockItem;

/**
 * This interface allows compartment entities to be cloneable via ctrl + middle click similar to block entities
 */
public interface CompartmentCloneable {

    /**
     * Saves the compartment contents to an ItemStack. This tag is stored under {@link BlockItem#BLOCK_ENTITY_TAG}
     * as that's what vanilla does for block entity cloning.
     */
    CompoundTag saveForItemStack();

}