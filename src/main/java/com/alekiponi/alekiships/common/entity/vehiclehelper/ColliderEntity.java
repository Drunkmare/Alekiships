package com.alekiponi.alekiships.common.entity.vehiclehelper;

import com.alekiponi.alekiships.util.ClientHelper;
import com.alekiponi.alekiships.common.entity.IHaveIcons;
import com.alekiponi.alekiships.common.entity.vehicle.AbstractVehicle;
import com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.AbstractCompartmentEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;

public class ColliderEntity extends AbstractPassthroughHelper implements IHaveIcons {

    public ColliderEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    protected void defineSynchedData() {

    }

    @Override
    public void tick() {
        if (tickCount < 20) {
            this.refreshDimensions();
        }
        super.tick();
        if(this.level().isClientSide()){
            ClientHelper.tickHopPlayersOnboard(this);
        }

    }

    @Override
    public boolean canCollideWith(final Entity other) {
        return canVehicleCollide(this, other);
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    public static boolean canVehicleCollide(final Entity vehicle, final Entity entity) {
        if (entity instanceof AbstractVehicle || entity instanceof AbstractCompartmentEntity) {
            return false;
        }

        return (entity.canBeCollidedWith() || entity.isPushable()) && !vehicle.isPassengerOfSameVehicle(entity);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {

    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> pKey) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {

    }

    @Override
    public EntityDimensions getDimensions(Pose pPose) {
        if (this.getRootVehicle() instanceof AbstractVehicle vehicle) {
            return new EntityDimensions(vehicle.getDefaultColliderDimensions()[0], vehicle.getDefaultColliderDimensions()[1], false);
        }
        return super.getDimensions(pPose);
    }
}
