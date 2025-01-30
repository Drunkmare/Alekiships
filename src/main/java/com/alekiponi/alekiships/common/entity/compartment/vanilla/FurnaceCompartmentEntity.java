package com.alekiponi.alekiships.common.entity.compartment.vanilla;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.inventory.FurnaceMenu;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class FurnaceCompartmentEntity extends AbstractFurnaceCompartmentEntity {

    public FurnaceCompartmentEntity(final EntityType<? extends FurnaceCompartmentEntity> compartmentType,
            final Level level) {
        super(compartmentType, level, RecipeType.SMELTING);
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

        // TODO I'm too stupid for this. This needs to be a horizontal line that the particles are randomly distributed on
        //  IE it should look like how the vanilla furnace particles do

        this.level().addParticle(ParticleTypes.SMOKE, xPos + xOffset, yPos + yOffset, zPos + zOffset, 0, 0, 0);
        this.level().addParticle(ParticleTypes.FLAME, xPos + xOffset, yPos + yOffset, zPos + zOffset, 0, 0, 0);
    }

    @Override
    protected void saveBlockEntityData(final CompoundTag compoundTag) {
        BlockEntity.addEntityType(compoundTag, BlockEntityType.FURNACE);
        super.saveBlockEntityData(compoundTag);
    }

    @Override
    protected AbstractFurnaceMenu createMenu(final int id, final Inventory playerInventory) {
        return new FurnaceMenu(id, playerInventory, this, this.dataAccess);
    }
}