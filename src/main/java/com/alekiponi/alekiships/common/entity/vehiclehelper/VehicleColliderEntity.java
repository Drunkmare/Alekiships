package com.alekiponi.alekiships.common.entity.vehiclehelper;

import com.alekiponi.alekiships.client.IngameOverlays;
import com.alekiponi.alekiships.common.entity.IHaveIcons;
import com.alekiponi.alekiships.common.entity.vehicle.AbstractAlekiBoatEntity;
import com.alekiponi.alekiships.common.entity.vehicle.AbstractVehicle;
import com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.AbstractCompartmentEntity;
import com.alekiponi.alekiships.util.AlekiShipsHelper;
import com.google.common.collect.ImmutableList;
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
import net.minecraft.world.level.EntityGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class VehicleColliderEntity extends Entity implements IHaveIcons {

    public VehicleColliderEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    protected static final EntityDataAccessor<Integer> DATA_ID_PLAYER_UUID = SynchedEntityData.defineId(
            VehicleColliderEntity.class, EntityDataSerializers.INT);

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

    public Vec3 getCollision(){
        return this.collide(this.getDeltaMovement());
    }

    @Override
    protected Vec3 collide(Vec3 pVec) {
        AABB aabb = this.getBoundingBox();
        List<VoxelShape> list = getEntityCollisions(this, aabb.expandTowards(pVec));
        Vec3 vec3 = pVec.lengthSqr() == 0.0D ? pVec : collideBoundingBox(this, pVec, aabb, this.level(), list);
        boolean flag = pVec.x != vec3.x;
        boolean flag1 = pVec.y != vec3.y;
        boolean flag2 = pVec.z != vec3.z;
        boolean flag3 = this.onGround() || flag1 && pVec.y < 0.0D;
        float stepHeight = getStepHeight();
        if (stepHeight > 0.0F && flag3 && (flag || flag2)) {
            Vec3 vec31 = collideBoundingBox(this, new Vec3(pVec.x, (double) stepHeight, pVec.z), aabb, this.level(), list);
            Vec3 vec32 = collideBoundingBox(this, new Vec3(0.0D, (double) stepHeight, 0.0D), aabb.expandTowards(pVec.x, 0.0D, pVec.z), this.level(), list);
            if (vec32.y < (double) stepHeight) {
                Vec3 vec33 = collideBoundingBox(this, new Vec3(pVec.x, 0.0D, pVec.z), aabb.move(vec32), this.level(), list).add(vec32);
                if (vec33.horizontalDistanceSqr() > vec31.horizontalDistanceSqr()) {
                    vec31 = vec33;
                }
            }

            if (vec31.horizontalDistanceSqr() > vec3.horizontalDistanceSqr()) {
                return vec31.add(collideBoundingBox(this, new Vec3(0.0D, -vec31.y + pVec.y, 0.0D), aabb.move(vec31), this.level(), list));
            }
        }

        return vec3;
    }


    private static List<VoxelShape> getEntityCollisions(@Nullable Entity collider, AABB pCollisionBox) {
        if(collider instanceof VehicleColliderEntity || collider instanceof AbstractVehicle){
            if (pCollisionBox.getSize() < 1.0E-7D) {
                return List.of();
            } else {
                Predicate<Entity> predicate = collider == null ? EntitySelector.CAN_BE_COLLIDED_WITH : EntitySelector.NO_SPECTATORS.and(collider::canCollideWith);
                List<Entity> list = collider.level().getEntities(collider, pCollisionBox.inflate(1.0E-7D), predicate);

                list.removeIf(entity -> entity.getRootVehicle().is(collider.getRootVehicle()));

                if (list.isEmpty()) {
                    return List.of();
                } else {
                    ImmutableList.Builder<VoxelShape> builder = ImmutableList.builderWithExpectedSize(list.size());

                    for(Entity entity : list) {
                        builder.add(Shapes.create(entity.getBoundingBox()));
                    }

                    return builder.build();
                }
            }
        }
        return List.of();
    }


    /*
    public Vec3 getDeltaMovement() {
        if(!(this.getRootVehicle() instanceof AbstractVehicle vehicle)){
            return super.getDeltaMovement();
        }

        Vec3 movement = vehicle.getDeltaMovement();
        double radius = (this.position().vectorTo(vehicle.position())).horizontalDistance();
        double deltaRotation = vehicle.getDeltaRotation();
        Vec3 angularDeltaMovement = Vec3.ZERO;
        movement = movement.add(angularDeltaMovement);
    }*/

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
