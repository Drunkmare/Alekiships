package com.alekiponi.alekiships.common.entity.vehiclehelper.compartment;

import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.level.block.CraftingTableBlock;

/**
 * This interface allows some of our compartment entities to behave more like vanilla blocks like {@link CraftingTableBlock}
 * which can be opened in spectator mode. It also is to easily avoid the problem of {@link Entity#getDisplayName()}
 * being awkward with these kinds of Menus (especially {@link GrindstoneMenu})
 *
 * @implNote If the implementing class also has {@link MenuProvider} the magic won't happen!
 */
public interface SimpleBlockMenuCompartment {

    /**
     * The returned {@link MenuProvider} is used to open a menu when the player interacts
     * with an entity in spectator mode. The entity must not implement {@link MenuProvider} as that
     * will take priority.
     */
    MenuProvider getMenuProvider();
}