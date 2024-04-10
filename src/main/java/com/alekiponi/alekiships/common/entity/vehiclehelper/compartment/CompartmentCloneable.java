package com.alekiponi.alekiships.common.entity.vehiclehelper.compartment;

import com.alekiponi.alekiships.network.ServerBoundPickCompartmentPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;

/**
 * This interface allows compartment entities to be cloneable via ctrl + middle click similar to block entities
 */
public interface CompartmentCloneable {

    /**
     * Saves the compartment contents to an {@link ItemStack}. This tag is stored under {@value BlockItem#BLOCK_ENTITY_TAG}
     * as that's what vanilla does for block entity cloning.
     *
     * @apiNote This method is called on the client (when in creative) and on the server via {@link ServerBoundPickCompartmentPacket}
     */
    CompoundTag saveForItemStack();
}