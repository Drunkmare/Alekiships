package com.alekiponi.alekiships.common.entity.compartment;

import com.alekiponi.alekiships.common.entity.AlekiShipsEntities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

/**
 * This is a light class for compartments which are dumb blocks. In most cases you'll want to simply implement
 * {@link BlockCompartment} instead of extending this class.
 */
public class BlockCompartmentEntity extends AbstractCompartmentEntity implements BlockCompartment {

    private static final EntityDataAccessor<BlockState> DATA_ID_DISPLAY_BLOCK = SynchedEntityData.defineId(
            BlockCompartmentEntity.class, EntityDataSerializers.BLOCK_STATE);

    public BlockCompartmentEntity(final EntityType<? extends BlockCompartmentEntity> entityType, final Level level) {
        super(entityType, level);
    }

    public BlockCompartmentEntity(final EntityType<? extends BlockCompartmentEntity> entityType, final Level level,
            final BlockState blockState) {
        super(entityType, level);
        this.setDisplayBlockState(blockState);
    }

    public static BlockCompartmentEntity create(final Level level, final BlockState blockState) {
        return new BlockCompartmentEntity(AlekiShipsEntities.BLOCK_COMPARTMENT_ENTITY.get(), level, blockState);
    }

    @Override
    protected void defineSynchedData(final SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_ID_DISPLAY_BLOCK, Blocks.AIR.defaultBlockState());
    }

    @Override
    protected void addAdditionalSaveData(final CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        BlockCompartment.saveBlockstate(this, compoundTag);
    }

    @Override
    protected void readAdditionalSaveData(final CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        BlockCompartment.readBlockstate(this, compoundTag);
    }

    @Override
    protected void onHurt(final DamageSource damageSource) {
        BlockCompartment.playHitSound(this);
    }

    @Override
    protected void onBreak() {
        BlockCompartment.playBreakSound(this);
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
        BlockCompartment.playPlaceSound(this);
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