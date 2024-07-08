package com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.vanilla;

import com.alekiponi.alekiships.common.entity.vehiclehelper.CompartmentType;
import com.alekiponi.alekiships.common.menu.AbstractFurnaceCompartmentMenu;
import com.alekiponi.alekiships.common.menu.BlastFurnaceCompartmentMenu;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;

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
    protected void animateTick() {
        if (!this.getDisplayBlockState().getValue(AbstractFurnaceBlock.LIT)) return;

        final double xPos = this.getX();
        final double yPos = this.getY();
        final double zPos = this.getZ();
        if (this.random.nextDouble() < 0.1D) {
            this.level().playLocalSound(xPos, yPos, zPos, SoundEvents.FURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 1, 1,
                    false);
        }

        final double randomOffset = this.random.nextDouble() * 50 - 25;
        final double xOffset = Mth.sin((float) ((-this.getYRot() + randomOffset) * Mth.DEG_TO_RAD)) * 0.4;
        final double yOffset = this.random.nextDouble() * 6 / 16;
        final double zOffset = Mth.cos((float) ((-this.getYRot() + randomOffset) * Mth.DEG_TO_RAD)) * 0.4;
        this.level().addParticle(ParticleTypes.SMOKE, xPos + xOffset, yPos + yOffset, zPos + zOffset, 0, 0, 0);
    }

    @Override
    protected AbstractFurnaceCompartmentMenu createMenu(final int id, final Inventory playerInventory) {
        return new BlastFurnaceCompartmentMenu(id, playerInventory, this, this.dataAccess);
    }
}