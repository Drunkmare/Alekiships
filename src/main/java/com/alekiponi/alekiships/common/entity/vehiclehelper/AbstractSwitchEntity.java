package com.alekiponi.alekiships.common.entity.vehiclehelper;

import com.alekiponi.alekiships.network.ServerboundSwitchEntityPacket;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import net.neoforged.neoforge.network.PacketDistributor;

public abstract class AbstractSwitchEntity extends AbstractPassthroughHelper {

    protected static final EntityDataAccessor<Boolean> DATA_ID_SWITCH = SynchedEntityData.defineId(
            AbstractSwitchEntity.class, EntityDataSerializers.BOOLEAN);
    static String SWITCHED_KEY = "switched";

    public AbstractSwitchEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public InteractionResult interact(final Player player, final InteractionHand hand) {
        if (this.level().isClientSide()) {
            PacketDistributor.sendToServer(new ServerboundSwitchEntityPacket(!this.getSwitched(), this));
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    public boolean getSwitched() {
        return this.entityData.get(DATA_ID_SWITCH);
    }

    public void setSwitched(boolean switched) {
        this.entityData.set(DATA_ID_SWITCH, switched);
    }

    @Override
    public boolean isPickable() {
        return !this.isRemoved();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_ID_SWITCH, false);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
        this.setSwitched(pCompound.getBoolean(SWITCHED_KEY));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {
        pCompound.putBoolean(SWITCHED_KEY, this.getSwitched());
    }


}
