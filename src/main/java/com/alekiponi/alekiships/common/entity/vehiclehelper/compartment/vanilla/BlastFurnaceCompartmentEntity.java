package com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.vanilla;

import com.alekiponi.alekiships.common.entity.vehiclehelper.CompartmentType;
import com.alekiponi.alekiships.common.menu.AbstractFurnaceCompartmentMenu;
import com.alekiponi.alekiships.common.menu.BlastFurnaceCompartmentMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public class BlastFurnaceCompartmentEntity extends AbstractFurnaceCompartmentEntity {

    public BlastFurnaceCompartmentEntity(final CompartmentType<? extends BlastFurnaceCompartmentEntity> compartmentType,
            final Level level) {
        super(compartmentType, level, RecipeType.BLASTING);
    }

    public BlastFurnaceCompartmentEntity(final CompartmentType<? extends BlastFurnaceCompartmentEntity> compartmentType,
            final Level level, final ItemStack itemStack) {
        super(compartmentType, level, RecipeType.BLASTING, itemStack);
    }

    @Override
    protected AbstractFurnaceCompartmentMenu createMenu(final int id, final Inventory playerInventory) {
        return new BlastFurnaceCompartmentMenu(id, playerInventory, this, this.dataAccess);
    }
}