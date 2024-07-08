package com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.vanilla;

import com.alekiponi.alekiships.common.entity.vehiclehelper.CompartmentType;
import com.alekiponi.alekiships.common.menu.AbstractFurnaceCompartmentMenu;
import com.alekiponi.alekiships.common.menu.SmokerCompartmentMenu;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;

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
        if (!this.getDisplayBlockState().getValue(AbstractFurnaceBlock.LIT)) return;

        final double xPos = this.getX();
        final double yPos = this.getY();
        final double zPos = this.getZ();
        if (this.random.nextDouble() < 0.1D) {
            this.level().playLocalSound(xPos, yPos, zPos, SoundEvents.FURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 1, 1,
                    false);
        }

        final double randomOffset = this.random.nextDouble() * 0.01 - 0.005;

        this.level().addParticle(ParticleTypes.SMOKE, xPos, yPos + 0.4, zPos, randomOffset, 0.02, randomOffset);
    }

    @Override
    protected AbstractFurnaceCompartmentMenu createMenu(final int id, final Inventory playerInventory) {
        return new SmokerCompartmentMenu(id, playerInventory, this, this.dataAccess);
    }
}