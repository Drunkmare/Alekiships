package com.alekiponi.alekiships.common.entity.compartment.vanilla;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.inventory.SmokerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class SmokerCompartmentEntity extends AbstractFurnaceCompartmentEntity {

    public SmokerCompartmentEntity(final EntityType<? extends SmokerCompartmentEntity> entityType, final Level level) {
        super(entityType, level, RecipeType.SMOKING);
        this.setDisplayBlockState(Blocks.SMOKER.defaultBlockState());
    }

    public SmokerCompartmentEntity(final EntityType<? extends SmokerCompartmentEntity> entityType, final Level level,
            final BlockState blockState) {
        this(entityType, level);
        this.setDisplayBlockState(blockState);
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
    protected int getBurnDuration(final ItemStack fuelStack) {
        return super.getBurnDuration(fuelStack) / 2;
    }

    @Override
    protected void saveBlockEntityData(final CompoundTag compoundTag) {
        BlockEntity.addEntityType(compoundTag, BlockEntityType.SMOKER);
        super.saveBlockEntityData(compoundTag);
    }

    @Override
    protected AbstractFurnaceMenu createMenu(final int id, final Inventory playerInventory) {
        return new SmokerMenu(id, playerInventory, this, this.dataAccess);
    }
}