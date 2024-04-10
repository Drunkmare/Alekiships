package com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.vanilla;

import com.alekiponi.alekiships.common.entity.vehiclehelper.CompartmentType;
import com.alekiponi.alekiships.common.menu.AbstractFurnaceCompartmentMenu;
import com.alekiponi.alekiships.common.menu.SmokerCompartmentMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public class SmokerCompartmentEntity extends AbstractFurnaceCompartmentEntity {

    public SmokerCompartmentEntity(final CompartmentType<? extends SmokerCompartmentEntity> compartmentType,
            final Level level) {
        super(compartmentType, level, RecipeType.SMOKING);
    }

    public SmokerCompartmentEntity(final CompartmentType<? extends SmokerCompartmentEntity> compartmentType,
            final Level level, final ItemStack itemStack) {
        super(compartmentType, level, RecipeType.SMOKING, itemStack);
    }

    @Override
    protected void animateTick() {
        // TODO should spawn particles like the Smoker block does. I'm too stupid for it - Traister
    }

    @Override
    protected AbstractFurnaceCompartmentMenu createMenu(final int id, final Inventory playerInventory) {
        return new SmokerCompartmentMenu(id, playerInventory, this, this.dataAccess);
    }
}