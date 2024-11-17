package com.alekiponi.alekiships.common.entity.vehiclehelper.compartment;

import com.alekiponi.alekiships.network.ServerboundPickCompartmentPacket;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * This interface allows compartment entities to be cloneable via ctrl + middle click similar to block entities
 */
public interface CompartmentCloneable {

    /**
     * Saves the compartment contents to an {@link ItemStack}. This tag is stored under {@value BlockItem#BLOCK_ENTITY_TAG}
     * as that's what vanilla does for block entity cloning.
     *
     * @apiNote This method is called on the client (when in creative) and on the server via {@link ServerboundPickCompartmentPacket}
     */
    CompoundTag saveForItemStack();

    /**
     * TODO this must actually be implemented on stuff. See {@link BlockEntity#collectComponents()} for how this is done
     */
    DataComponentMap collectComponents();
}