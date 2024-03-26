package com.alekiponi.alekiships.common.entity.vehiclehelper.compartment;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.common.entity.vehiclehelper.CompartmentType;
import com.alekiponi.alekiships.util.AlekiShipsTags;
import net.minecraft.SharedConstants;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

/**
 * This is a light class for compartments which are dumb blocks. It automatically sets the display blockstate if the
 * given {@link ItemStack} is a {@link BlockItem} as well as uses blockstate to get and play the break sound
 */
public class BlockCompartmentEntity extends AbstractCompartmentEntity implements BlockCompartment {

    private static final EntityDataAccessor<BlockState> DATA_ID_DISPLAY_BLOCK = SynchedEntityData.defineId(
            BlockCompartmentEntity.class, EntityDataSerializers.BLOCK_STATE);

    public BlockCompartmentEntity(final CompartmentType<? extends BlockCompartmentEntity> compartmentType,
            final Level level) {
        super(compartmentType, level);
    }

    public BlockCompartmentEntity(final CompartmentType<? extends BlockCompartmentEntity> compartmentType,
            final Level level, final ItemStack itemStack) {
        super(compartmentType, level);
        if (!(itemStack.getItem() instanceof BlockItem blockItem)) {
            if (SharedConstants.IS_RUNNING_IN_IDE) {
                AlekiShips.LOGGER.warn("Inspect {} a dev likely broke the tag. Otherwise {} is at fault",
                        AlekiShipsTags.Items.CAN_PLACE_IN_COMPARTMENTS, compartmentType);
            }
            AlekiShips.LOGGER.debug("A block compartment was created using a stack of {} but it's not a BlockItem",
                    itemStack);
            AlekiShips.LOGGER.debug("Please inspect {} for any items that don't have a block form",
                    AlekiShipsTags.Items.CAN_PLACE_IN_COMPARTMENTS);
            return;
        }

        this.setDisplayBlockState(blockItem.getBlock().defaultBlockState());
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_ID_DISPLAY_BLOCK, Blocks.AIR.defaultBlockState());
    }

    @Override
    protected void addAdditionalSaveData(final CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.put(HELD_BLOCK_KEY, NbtUtils.writeBlockState(this.getDisplayBlockState()));
    }

    @Override
    protected void readAdditionalSaveData(final CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.setDisplayBlockState(NbtUtils.readBlockState(this.level().holderLookup(Registries.BLOCK),
                compoundTag.getCompound(HELD_BLOCK_KEY)));
    }

    @Override
    protected void playHurtSound(final DamageSource damageSource) {
        this.playHitSound();
    }

    @Override
    public void remove(final RemovalReason removalReason) {
        if (!this.level().isClientSide() && removalReason.shouldDestroy()) {
            this.playBreakSound();
        }

        super.remove(removalReason);
    }

    @Override
    public ItemStack getDropStack() {
        return new ItemStack(this.getDisplayBlockState().getBlock());
    }

    @Nullable
    @Override
    public ItemStack getPickResult() {
        return new ItemStack(this.getDisplayBlockState().getBlock());
    }

    @Override
    protected void onPlaced() {
        this.playPlaceSound();
    }

    @Override
    public BlockState getDisplayBlockState() {
        return this.entityData.get(DATA_ID_DISPLAY_BLOCK);
    }

    @Override
    public void setDisplayBlockState(final BlockState blockState) {
        this.entityData.set(DATA_ID_DISPLAY_BLOCK, blockState);
    }
}