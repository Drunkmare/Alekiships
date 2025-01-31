package com.alekiponi.alekiships.util;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

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
     * The plank block for this wood
     */
    Block getPlankBlock();
}