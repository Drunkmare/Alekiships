package com.alekiponi.alekiships.common.entity.vehiclehelper;

import com.alekiponi.alekiships.client.IngameOverlays;
import com.alekiponi.alekiships.common.entity.IHaveIcons;
import com.alekiponi.alekiships.common.entity.vehicle.AbstractAlekiBoatEntity;
import com.alekiponi.alekiships.common.entity.vehicle.AbstractVehicle;
import com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.AbstractCompartmentEntity;
import com.alekiponi.alekiships.util.AlekiShipsHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.ArrayList;

public class VehicleCollisionEntity extends AbstractInvisibleHelper implements IHaveIcons {

    public VehicleCollisionEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    protected static final EntityDataAccessor<Integer> DATA_ID_PLAYER_UUID = SynchedEntityData.defineId(
            VehicleCollisionEntity.class, EntityDataSerializers.INT);

    @Override
    protected void defineSynchedData() {

    }

    @Override
    public void tick() {
        AlekiShipsHelper.tickHopPlayersOnboard(this);

        if (!this.isPassenger()) {
            this.kill();
        } else if (tickCount < 10) {
            this.refreshDimensions();
        }

        super.tick();
    }

    @Override
    public boolean canCollideWith(final Entity other) {
        return canVehicleCollide(this, other);
    }

    @Override
    public boolean hurt(final DamageSource damageSource, final float amount) {
        if (this.getRootVehicle() instanceof AbstractVehicle vehicle) {
            return vehicle.hurt(damageSource, amount);
        }

        return true;
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    public static boolean canVehicleCollide(final Entity vehicle, final Entity entity) {
        if (entity instanceof AbstractAlekiBoatEntity || entity instanceof AbstractCompartmentEntity) {
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
    public InteractionResult interact(final Player player, final InteractionHand hand) {
        return this.getRootVehicle().interact(player, hand);
    }

    @Override
    public boolean isPickable() {
        return !this.isRemoved();
    }

    @Override
    public EntityDimensions getDimensions(Pose pPose) {
        if (this.getRootVehicle() instanceof AbstractVehicle vehicle) {
            return new EntityDimensions(vehicle.getDefaultColliderDimensions()[0], vehicle.getDefaultColliderDimensions()[1], false);
        }
        return super.getDimensions(pPose);
    }

    @Override
    public ArrayList<IngameOverlays.IconState> getIconStates(Player player) {
        if (this.getRootVehicle() instanceof AbstractVehicle vehicle) {
            return vehicle.getIconStates(player);
        }

        return new ArrayList<IngameOverlays.IconState>();
    }

    @Override
    public Component getName() {
        if (this.getRootVehicle() instanceof AbstractVehicle vehicle) {
            return vehicle.getName();
        } else {
            return super.getName();
        }
    }
}
