package com.alekiponi.alekiships.util;

import net.minecraft.resources.ResourceKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;

/**
 * Helper interface to consolidate {@link OverworldWood} and {@link NetherWood}
 *
 * @apiNote These objects are compared vai identity and do not have a value.
 */
public interface Wood extends StringRepresentable {

    /**
     * The plank item for this wood
     */
    Item getPlankItem();

    /**
     * @return The resource key for the frame material
     */
    ResourceKey<FrameMaterial> frameMaterialKey();
}