package com.alekiponi.alekiships.common.entity.vehicle;

import com.google.common.collect.Lists;

import com.alekiponi.alekiships.common.entity.AlekiShipsEntities;
import com.alekiponi.alekiships.common.entity.compartment.AbstractCompartmentEntity;
import com.alekiponi.alekiships.common.entity.compartment.EmptyCompartmentEntity;
import com.alekiponi.alekiships.common.entity.vehiclecapability.IAllowFallDamage;
import com.alekiponi.alekiships.common.entity.vehiclecapability.IHaveColliders;
import com.alekiponi.alekiships.common.entity.vehiclecapability.IHaveCompartments;
import com.alekiponi.alekiships.common.entity.vehiclehelper.ColliderEntity;
import com.alekiponi.alekiships.common.entity.vehiclehelper.VehiclePart;
import com.alekiponi.alekiships.network.ServerboundFlagVehicleForUpdatePacket;
import com.alekiponi.alekiships.util.CommonHelper;

import net.minecraft.BlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.WaterlilyBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;

import static com.alekiponi.alekiships.util.ClientHelper.tickTakeClientPlayersForARide;

public abstract class AbstractVehicle extends Entity implements IHaveColliders, IHaveCompartments {
    protected static final EntityDataAccessor<Integer> DATA_ID_HURT = SynchedEntityData.defineId(
            AbstractVehicle.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Integer> DATA_ID_HURTDIR = SynchedEntityData.defineId(
            AbstractVehicle.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Float> DATA_ID_DAMAGE = SynchedEntityData.defineId(
            AbstractVehicle.class, EntityDataSerializers.FLOAT);
    protected static final EntityDataAccessor<Float> DATA_ID_DELTA_ROTATION = SynchedEntityData.defineId(
            AbstractVehicle.class, EntityDataSerializers.FLOAT);
    protected static final EntityDataAccessor<Float> DATA_ID_ACCELERATION = SynchedEntityData.defineId(
            AbstractVehicle.class, EntityDataSerializers.FLOAT);
    private final LinkedList<Double> speedOverTime;
    protected AbstractCompartmentEntity.RidingPose[] ridingPoses;
    protected float invFriction;
    protected int lerpSteps;
    protected double lerpX;
    protected double lerpY;
    protected double lerpZ;
    protected double lerpYRot;
    protected double lerpXRot;
    protected double waterLevel;
    protected double lavaLevel;
    protected float landFriction;
    protected int blockLightLevel;

    @Nullable
    protected MediumStatus status;
    @Nullable
    protected MediumStatus oldStatus;
    protected double lastYd;

    private boolean hasAllParts = false;

    private boolean passengerUpdateFlag = false;

    public AbstractVehicle(final EntityType entityType, final Level level) {
        super(entityType, level);
        this.blocksBuilding = true;

        this.speedOverTime = new LinkedList<Double>();
        for (int i = 0; i < 5; i++) {
            this.speedOverTime.add(0.0);
        }
        ridingPoses = new AbstractCompartmentEntity.RidingPose[0];
    }

    public static boolean canVehicleCollide(final Entity vehicle, final Entity entity) {
        return (entity.canBeCollidedWith() || entity.isPushable()) && !vehicle.isPassengerOfSameVehicle(entity);
    }

    public abstract int getMaxPassengers();

    public AbstractCompartmentEntity.RidingPose[] getRidingPoses() {
        if (ridingPoses.length == 0) {
            AbstractCompartmentEntity.RidingPose[] poses = new AbstractCompartmentEntity.RidingPose[this.getMaxPassengers()];
            for (int i = 0; i < this.getMaxPassengers(); i++) {
                poses[i] = AbstractCompartmentEntity.RidingPose.STANDARD;
            }
            this.ridingPoses = poses;
        }
        return ridingPoses;
    }

    public abstract float renderSizeForCompartments();

    public boolean pilotCompartmentAcceptsNonPlayers() {
        return false;
    }

    public boolean renderCleatKnotSides() {
        return true;
    }

    public float[] getDefaultColliderDimensions() {
        return new float[]{1, 1};
    }

    public abstract float getPassengerSizeLimit();

    @Override
    public void tick() {
        if (!this.isFunctional()) {
            hasAllParts = false;
        }
        if (!hasAllParts && this.getPassengers().size() < this.getMaxPassengers() && this.isFunctional()) {
            addVehicleParts();
        } else if (this.isFunctional()) {
            hasAllParts = true;
        }
        if (!hasAllParts()) {
            return;
        }
        if (everyNthTickUnique(40)) {
            checkIfNeedsPassengerUpdate();
        }
        if (everyNthTickUnique(2)) {
            int light = 0;
            for (AbstractCompartmentEntity compartment : this.getCompartments()) {
                light = Math.max(compartment.getCompartmentBlockLight(), light);
            }
            blockLightLevel = Math.max(0, light - 1);
        }


        super.tick();
    }

    private void addVehicleParts() {
        final VehiclePart newPart = AlekiShipsEntities.VEHICLE_PART.get().create(this.level());
        newPart.setPos(this.getX(), this.getY(), this.getZ());
        this.level().addFreshEntity(newPart);
        newPart.startRiding(this);
    }

    public boolean hasAllParts() {
        return hasAllParts;
    }

    public void checkIfNeedsPassengerUpdate() {
        if (this.level().isClientSide()) {
            Player player = this.level().getNearestPlayer(this, 5 * 16);
            if (player != null) {
                if (this.distanceTo(player) < 4 * 16 && !this.hasAllHelpers()) {
                    PacketDistributor.sendToServer(new ServerboundFlagVehicleForUpdatePacket(true, this));
                }
            }
        }
    }

    public boolean isFlaggedForPassengerUpdate() {
        return passengerUpdateFlag;
    }

    public void setFlaggedForPassengerUpdate(boolean flag) {
        passengerUpdateFlag = flag;
    }

    public boolean hasAllHelpers() {
        for (Entity passenger : this.getPassengers()) {
            if (!passenger.isVehicle()) {
                return false;
            }
        }
        return true;
    }

    public boolean isFunctional() {
        return this.getDamage() <= this.getDamageThreshold();
    }

    @Override
    protected MovementEmission getMovementEmission() {
        return MovementEmission.EVENTS;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_ID_HURT, 0);
        builder.define(DATA_ID_HURTDIR, 0);
        builder.define(DATA_ID_DAMAGE, 0f);
        builder.define(DATA_ID_DELTA_ROTATION, 0f);
        builder.define(DATA_ID_ACCELERATION, 0f);
    }

    @Override
    public boolean canCollideWith(final Entity other) {
        return canVehicleCollide(this, other);
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public boolean isPushable() {
        return true;
    }

    @Override
    public Vec3 getRelativePortalPosition(final Direction.Axis axis, final BlockUtil.FoundRectangle portal) {
        return LivingEntity.resetForwardDirectionOfRelativePortalPosition(
                super.getRelativePortalPosition(axis, portal));
    }

    public boolean shouldShowName() {
        return this.isCustomNameVisible();
    }

    public double getPassengersRidingOffset() {
        return 0;
    }

    @Override
    public boolean hurt(final DamageSource damageSource, final float amount) {
        if (this.isInvulnerableTo(damageSource)) return false;

        if (this.level().isClientSide || this.isRemoved()) return true;

        this.setHurtDir(-this.getHurtDir());
        this.setHurtTime(10);
        this.setDamage(this.getDamage() + amount);
        this.markHurt();
        this.gameEvent(GameEvent.ENTITY_DAMAGE, damageSource.getEntity());
        final boolean instantKill = damageSource.getEntity() instanceof Player && ((Player) damageSource.getEntity()).getAbilities().instabuild && damageSource.is(
                DamageTypes.PLAYER_ATTACK);

        if (instantKill) {
            this.setDamage(this.getDamage() + this.getDeathDamageThreshold() / 4);
        }
        if (this.getDamage() > getDamageThreshold()) {
            for (Entity entity : this.getPassengers()) {
                entity.kill();
            }
            if (this.level().getGameRules()
                    .getBoolean(GameRules.RULE_DOENTITYDROPS) && this.getDamage() > this.getDeathDamageThreshold()) {
                if (this.level() instanceof ServerLevel serverLevel) {
                    this.dropAllDestructionLoot(damageSource, serverLevel);
                }
            }
        }

        return true;
    }


    @Override
    public void push(final Entity entity) {
        if (entity instanceof AbstractVehicle) {
            if (entity.getBoundingBox().minY < this.getBoundingBox().maxY) {
                super.push(entity);
            }
        } else if (entity.getBoundingBox().minY <= this.getBoundingBox().minY) {
            super.push(entity);
        }
    }

    @Deprecated
    public Item getDropItem() {
        return Items.AIR;
    }

    @Override
    public void animateHurt(final float pYaw) {
        this.setHurtDir(-this.getHurtDir());
        this.setHurtTime(10);
        this.setDamage(this.getDamage() * 11.0F);
    }

    @Override
    public boolean isPickable() {
        return !this.isRemoved();
    }

    @Override
    public Direction getMotionDirection() {
        return this.getDirection().getClockWise();
    }

    protected void tickTakeEntitiesForARide() {
        if (this.tickCount < 10) {
            return;
        }
        if (this instanceof AbstractAlekiBoatEntity && (this.status == MediumStatus.UNDER_WATER || this.status == MediumStatus.UNDER_FLOWING_WATER)) {
            return;
        }
        if (this.getDeltaMovement().length() > 0) {
            List<Entity> entitiesToTakeWith = this.collectEntitesToTakeWith();

            if (!entitiesToTakeWith.isEmpty()) {
                for (final Entity entity : entitiesToTakeWith) {
                    if (this.level().isClientSide()) {
                        tickTakeClientPlayersForARide(this, entity);
                    }
                    if (!(entity instanceof AbstractVehicle) && !entity.isPassenger() && !(entity instanceof Player)) {
                        entity.setDeltaMovement(
                                entity.getDeltaMovement().add(this.getDeltaMovement().multiply(0.45, 0, 0.45)));
                    }
                }
            }
        }
    }

    public List<Entity> collectEntitesToTakeWith() {
        List<Entity> entities = this.level()
                .getEntities(this, this.getBoundingBox().inflate(0, -this.getBoundingBox().getYsize() + 2, 0)
                        .move(0, this.getBoundingBox().getYsize(), 0), EntitySelector.pushableBy(this));

        for (ColliderEntity collider : this.getColliders()) {
            entities.addAll(this.level()
                    .getEntities(collider,
                            collider.getBoundingBox().inflate(0, -collider.getBoundingBox().getYsize() + 2, 0)
                                    .move(0, collider.getBoundingBox().getYsize(), 0),
                            EntitySelector.pushableBy(this)));
        }

        entities = entities.stream().distinct().collect(Collectors.toList());

        List<Entity> entitiesToTakeWith = new ArrayList<>();
        for (Entity entity : entities) {
            if (!entity.isPassenger() && !(entity instanceof AbstractVehicle)) {
                entitiesToTakeWith.add(entity);
            }
        }

        return entitiesToTakeWith;
    }

    protected void tickLerp() {

        if (this.isControlledByLocalInstance()) {
            this.lerpSteps = 0;
            this.syncPacketPositionCodec(this.getX(), this.getY(), this.getZ());
        }

        if (this.lerpSteps > 0) {
            double d0 = this.getX() + (this.lerpX - this.getX()) / (double) this.lerpSteps;
            double d1 = this.getY() + (this.lerpY - this.getY()) / (double) this.lerpSteps;
            double d2 = this.getZ() + (this.lerpZ - this.getZ()) / (double) this.lerpSteps;
            double d3 = Mth.wrapDegrees(this.lerpYRot - (double) this.getYRot());
            this.setYRot(this.getYRot() + (float) d3 / (float) this.lerpSteps);
            this.setXRot(this.getXRot() + (float) (this.lerpXRot - (double) this.getXRot()) / (float) this.lerpSteps);
            --this.lerpSteps;
            this.setPos(d0, d1, d2);
        }


    }

    public MediumStatus getStatus() {
        final MediumStatus underwater = this.isUnderwater();

        if (underwater != null) {
            this.waterLevel = this.getBoundingBox().maxY;
            return underwater;
        }

        if (this.checkInWater()) {
            return MediumStatus.IN_WATER;
        }

        final MediumStatus underlava = this.isUnderlava();

        if (underlava != null) {
            this.lavaLevel = this.getBoundingBox().maxY;
            return underlava;
        }

        if (this.checkInLava()) {
            return MediumStatus.IN_LAVA;
        }

        final float groundFriction = this.getGroundFriction();

        if (0 < groundFriction) {
            this.landFriction = groundFriction;
            return MediumStatus.ON_LAND;
        }

        return MediumStatus.IN_AIR;
    }


    public float getWaterLevelAbove() {
        final AABB boundingBox = this.getBoundingBox();
        final int minX = Mth.floor(boundingBox.minX);
        final int maxX = Mth.ceil(boundingBox.maxX);
        final int maxY = Mth.floor(boundingBox.maxY);
        final int l = Mth.ceil(boundingBox.maxY - this.lastYd);
        final int minZ = Mth.floor(boundingBox.minZ);
        final int maxZ = Mth.ceil(boundingBox.maxZ);
        final BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();

        topLoop:
        for (int k1 = maxY; k1 < l; ++k1) {
            float waterLevel = 0;

            for (int l1 = minX; l1 < maxX; ++l1) {
                for (int i2 = minZ; i2 < maxZ; ++i2) {
                    mutableBlockPos.set(l1, k1, i2);
                    final FluidState fluidstate = this.level().getFluidState(mutableBlockPos);
                    if (fluidstate.is(FluidTags.WATER)) {
                        waterLevel = Math.max(waterLevel, fluidstate.getHeight(this.level(), mutableBlockPos));
                    }

                    if (1 <= waterLevel) {
                        continue topLoop;
                    }
                }
            }

            if (1 > waterLevel) {
                return mutableBlockPos.getY() + waterLevel;
            }
        }

        return l + 1;
    }


    public float getGroundFriction() {
        AABB aabb = this.getBoundingBox();
        AABB aabb1 = new AABB(aabb.minX, aabb.minY - 0.001D, aabb.minZ, aabb.maxX, aabb.minY, aabb.maxZ);
        int i = Mth.floor(aabb1.minX) - 1;
        int j = Mth.ceil(aabb1.maxX) + 1;
        int k = Mth.floor(aabb1.minY) - 1;
        int l = Mth.ceil(aabb1.maxY) + 1;
        int i1 = Mth.floor(aabb1.minZ) - 1;
        int j1 = Mth.ceil(aabb1.maxZ) + 1;
        VoxelShape voxelshape = Shapes.create(aabb1);
        float f = 0.0F;
        int k1 = 0;
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

        for (int l1 = i; l1 < j; ++l1) {
            for (int i2 = i1; i2 < j1; ++i2) {
                int j2 = (l1 != i && l1 != j - 1 ? 0 : 1) + (i2 != i1 && i2 != j1 - 1 ? 0 : 1);
                if (j2 != 2) {
                    for (int k2 = k; k2 < l; ++k2) {
                        if (j2 <= 0 || k2 != k && k2 != l - 1) {
                            blockpos$mutableblockpos.set(l1, k2, i2);
                            BlockState blockstate = this.level().getBlockState(blockpos$mutableblockpos);
                            if (!(blockstate.getBlock() instanceof WaterlilyBlock) && Shapes.joinIsNotEmpty(
                                    blockstate.getCollisionShape(this.level(), blockpos$mutableblockpos)
                                            .move(l1, k2, i2), voxelshape, BooleanOp.AND)) {
                                f += blockstate.getFriction(this.level(), blockpos$mutableblockpos, this);
                                ++k1;
                            }
                        }
                    }
                }
            }
        }

        return f / k1;
    }

    protected boolean checkInWater() {
        AABB aabb = this.getBoundingBox();
        int i = Mth.floor(aabb.minX);
        int j = Mth.ceil(aabb.maxX);
        int k = Mth.floor(aabb.minY);
        int l = Mth.ceil(aabb.minY + 0.001D);
        int i1 = Mth.floor(aabb.minZ);
        int j1 = Mth.ceil(aabb.maxZ);
        boolean flag = false;
        this.waterLevel = -Double.MAX_VALUE;
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

        for (int k1 = i; k1 < j; ++k1) {
            for (int l1 = k; l1 < l; ++l1) {
                for (int i2 = i1; i2 < j1; ++i2) {
                    blockpos$mutableblockpos.set(k1, l1, i2);
                    FluidState fluidstate = this.level().getFluidState(blockpos$mutableblockpos);
                    if (fluidstate.is(FluidTags.WATER)) {
                        float f = (float) l1 + fluidstate.getHeight(this.level(), blockpos$mutableblockpos);
                        this.waterLevel = Math.max(f, this.waterLevel);
                        flag |= aabb.minY < (double) f;
                    }
                }
            }
        }

        return flag;
    }

    protected boolean checkInLava() {
        AABB aabb = this.getBoundingBox();
        int i = Mth.floor(aabb.minX);
        int j = Mth.ceil(aabb.maxX);
        int k = Mth.floor(aabb.minY);
        int l = Mth.ceil(aabb.minY + 0.001D);
        int i1 = Mth.floor(aabb.minZ);
        int j1 = Mth.ceil(aabb.maxZ);
        boolean flag = false;
        this.lavaLevel = -Double.MAX_VALUE;
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

        for (int k1 = i; k1 < j; ++k1) {
            for (int l1 = k; l1 < l; ++l1) {
                for (int i2 = i1; i2 < j1; ++i2) {
                    blockpos$mutableblockpos.set(k1, l1, i2);
                    FluidState fluidstate = this.level().getFluidState(blockpos$mutableblockpos);
                    if (fluidstate.is(FluidTags.LAVA)) {
                        float f = (float) l1 + fluidstate.getHeight(this.level(), blockpos$mutableblockpos);
                        this.lavaLevel = Math.max(f, this.lavaLevel);
                        flag |= aabb.minY < (double) f;
                    }
                }
            }
        }

        return flag;
    }

    @Nullable
    protected MediumStatus isUnderlava() {
        final AABB aabb = this.getBoundingBox();
        final double d0 = aabb.maxY + 0.001D;
        final int i = Mth.floor(aabb.minX);
        final int j = Mth.ceil(aabb.maxX);
        final int k = Mth.floor(aabb.maxY);
        final int l = Mth.ceil(d0);
        final int i1 = Mth.floor(aabb.minZ);
        final int j1 = Mth.ceil(aabb.maxZ);
        boolean isUnderLava = false;
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();

        for (int k1 = i; k1 < j; ++k1) {
            for (int l1 = k; l1 < l; ++l1) {
                for (int i2 = i1; i2 < j1; ++i2) {
                    mutableBlockPos.set(k1, l1, i2);
                    final FluidState fluidstate = this.level().getFluidState(mutableBlockPos);
                    if (fluidstate.is(FluidTags.LAVA) &&
                            d0 < mutableBlockPos.getY() + fluidstate.getHeight(this.level(), mutableBlockPos)) {
                        if (!fluidstate.isSource()) {
                            return MediumStatus.UNDER_FLOWING_LAVA;
                        }

                        isUnderLava = true;
                    }
                }
            }
        }

        return isUnderLava ? MediumStatus.UNDER_LAVA : null;
    }

    /*
    @Nullable
    public Entity getNewHelperForPart(AbstractVehiclePart part) {
        int i = 0;
        for (AbstractVehiclePart part1 : this.collectVehicleParts()) {
            if (part1.is(part) && !part.isVehicle()) {
                return getNewHelperForIndex(i);
            }
            i++;
        }
        return null;
    }

    @Nullable
    public Entity getNewHelperForIndex(int index) {

        return null;
    }*/

    @Nullable
    protected MediumStatus isUnderwater() {
        final AABB aabb = this.getBoundingBox();
        final double d0 = aabb.maxY + 0.001D;
        final int i = Mth.floor(aabb.minX);
        final int j = Mth.ceil(aabb.maxX);
        final int k = Mth.floor(aabb.maxY);
        final int l = Mth.ceil(d0);
        final int i1 = Mth.floor(aabb.minZ);
        final int j1 = Mth.ceil(aabb.maxZ);
        boolean isUnderwater = false;
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();

        for (int k1 = i; k1 < j; ++k1) {
            for (int l1 = k; l1 < l; ++l1) {
                for (int i2 = i1; i2 < j1; ++i2) {
                    mutableBlockPos.set(k1, l1, i2);
                    final FluidState fluidstate = this.level().getFluidState(mutableBlockPos);
                    if (fluidstate.is(FluidTags.WATER) &&
                            d0 < mutableBlockPos.getY() + fluidstate.getHeight(this.level(), mutableBlockPos)) {
                        if (!fluidstate.isSource()) {
                            return MediumStatus.UNDER_FLOWING_WATER;
                        }

                        isUnderwater = true;
                    }
                }
            }
        }

        return isUnderwater ? MediumStatus.UNDER_WATER : null;
    }

    public final List<Entity> collectLivingPassengers() {
        final List<Entity> truePassengers = Lists.newArrayList();

        for (final Entity vehiclePart : this.getPassengers()) {
            if (vehiclePart.isVehicle() && vehiclePart.getFirstPassenger() instanceof AbstractCompartmentEntity compartmentEntity) {
                if (compartmentEntity.isVehicle()) {
                    if (compartmentEntity.getFirstPassenger() instanceof LivingEntity) {
                        truePassengers.add(compartmentEntity.getFirstPassenger());
                    }
                }
            }
        }
        return truePassengers;
    }

    public final List<Player> collectPlayerPassengers() {
        final List<Entity> truePassengers = collectLivingPassengers();

        ArrayList<Player> players = new ArrayList<Player>();

        for (Entity entity : truePassengers) {
            if (entity instanceof Player player) {
                players.add(player);
            }
        }

        return players;
    }

    public final List<Player> collectPlayersToTakeWith() {
        final List<Entity> truePassengers = collectEntitesToTakeWith();

        ArrayList<Player> players = new ArrayList<Player>();

        for (Entity entity : truePassengers) {
            if (entity instanceof Player player) {
                players.add(player);
            }
        }

        return players;
    }

    public final List<AbstractCompartmentEntity> collectCompartments() {
        final List<AbstractCompartmentEntity> compartments = Lists.newArrayList();

        for (final Entity vehiclePart : this.getPassengers()) {
            if (vehiclePart.isVehicle() && vehiclePart.getFirstPassenger() instanceof AbstractCompartmentEntity compartmentEntity) {
                compartments.add(compartmentEntity);
            }
        }
        return compartments;
    }

    public final List<VehiclePart> collectVehicleParts() {
        final List<VehiclePart> vehicleParts = Lists.newArrayList();

        for (final Entity vehiclePart : this.getPassengers()) {
            vehicleParts.add((VehiclePart) vehiclePart);
        }
        return vehicleParts;
    }

    public final List<Entity> collectVehicleHelpers() {
        final List<Entity> helpers = Lists.newArrayList();

        for (final VehiclePart part : this.collectVehicleParts()) {
            if (part.isVehicle()) {
                helpers.add(part.getFirstPassenger());
            }
        }
        return helpers;
    }

    public final int countVehicleHelpers() {
        int count = 0;

        for (final VehiclePart part : this.collectVehicleParts()) {
            if (part.isVehicle()) {
                count++;
            }
        }
        return count;
    }

    @Override
    protected void positionRider(final Entity passenger, final MoveFunction moveFunction) {
        if (this.hasPassenger(passenger)) {
            if (!(passenger instanceof VehiclePart)) {
                passenger.stopRiding();
            }

            final Vec3 position = positionRiderByIndex(this.getPassengers().indexOf(passenger));
            final Vec3 vec3 = this.positionLocally(position);
            moveFunction.accept(passenger, this.getX() + vec3.x, this.getY() + vec3.y, this.getZ() + vec3.z);
            passenger.setPos(this.getX() + vec3.x, this.getY() + vec3.y, this.getZ() + vec3.z);
            passenger.setYRot(this.getYRot());
        }
    }

    protected Vec3 positionRiderByIndex(int index) {
        float localX = 0.0F;
        float localZ = 0.0F;
        float localY = (float) ((this.isRemoved() ? (double) 0.01F : this.getPassengersRidingOffset()));
        switch (index) {
            case 0 -> {
                localX = 0.3f;
                localZ = 0.0f;
                localY += 0.0f;
            }
            case 1 -> {
                localX = -0.7f;
                localZ = 0.0f;
                localY += 0.0f;
            }
        }
        return new Vec3(localX, localY, localZ);
    }

    protected Vec3 positionLocally(float localX, float localY, float localZ) {
        return (new Vec3(localX, localY, localZ)).yRot(
                -this.getYRot() * ((float) Math.PI / 180F) - ((float) Math.PI / 2F));
    }

    protected Vec3 positionLocally(Vec3 vec) {
        return (positionLocally((float) vec.x, (float) vec.y, (float) vec.z));
    }

    protected void clampRotation(final Entity entity) {
        entity.setYBodyRot(this.getYRot());
        float f = Mth.wrapDegrees(entity.getYRot() - this.getYRot());
        float f1 = Mth.clamp(f, -105.0F, 105.0F);
        entity.yRotO += f1 - f;
        entity.setYRot(entity.getYRot() + f1 - f);
        entity.setYHeadRot(entity.getYRot());
    }

    @Override
    protected void checkFallDamage(final double fallDistance, final boolean onGround, final BlockState blockState,
            final BlockPos blockPos) {
        if (this instanceof IAllowFallDamage) {
            this.lastYd = this.getDeltaMovement().y;
            if (this.isPassenger()) return;

            if (!onGround) {
                if (!this.level().getFluidState(this.blockPosition().below()).is(FluidTags.WATER) && fallDistance < 0) {
                    this.fallDistance -= (float) fallDistance;
                }
                return;
            }

            if (this.fallDistance > 3) {

                if (this.status != MediumStatus.ON_LAND) {
                    this.resetFallDistance();
                    return;
                }

                this.causeFallDamage(this.fallDistance, 1, this.damageSources().fall());
                if (!this.level().isClientSide && !this.isRemoved()) {
                    for (Entity passenger : this.collectLivingPassengers()) {
                        passenger.causeFallDamage(this.fallDistance, 1, this.damageSources().fall());
                    }
                    for (Entity passenger : this.getPassengers()) {
                        if (passenger.isVehicle()) {
                            passenger.getFirstPassenger().kill();
                        }
                    }
                    this.kill();
                    if (this.level().getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                        if (this.level() instanceof ServerLevel serverLevel) {
                            this.dropAllDestructionLoot(this.damageSources().fall(), serverLevel);
                        }
                    }
                }
            }
            this.resetFallDistance();
        }
    }

    public float getDamage() {
        return this.entityData.get(DATA_ID_DAMAGE);
    }

    public void setDamage(final float damage) {
        this.entityData.set(DATA_ID_DAMAGE, damage);
    }

    public int getHurtTime() {
        return this.entityData.get(DATA_ID_HURT);
    }

    public void setHurtTime(final int hurtTime) {
        this.entityData.set(DATA_ID_HURT, hurtTime);
    }

    public int getHurtDir() {
        return this.entityData.get(DATA_ID_HURTDIR);
    }

    public void setHurtDir(final int hurtDirection) {
        this.entityData.set(DATA_ID_HURTDIR, hurtDirection);
    }

    public abstract float getDamageThreshold();

    public abstract float getDeathDamageThreshold();

    public abstract float getDamageRecovery();

    public float getDeltaRotation() {
        return this.entityData.get(DATA_ID_DELTA_ROTATION);
    }

    public void setDeltaRotation(float deltaRotation) {
        this.entityData.set(DATA_ID_DELTA_ROTATION, deltaRotation);
    }

    public float getAcceleration() {
        return this.entityData.get(DATA_ID_ACCELERATION);
    }

    public void setAcceleration(float acceleration) {
        this.entityData.set(DATA_ID_ACCELERATION, Mth.clamp(acceleration, -1, 1));
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
        this.setDeltaRotation(pCompound.getFloat("deltaRotation"));
        this.setHurtDir(pCompound.getInt("hurtDir"));
        this.setDamage(pCompound.getFloat("damage"));
        this.setHurtTime(pCompound.getInt("hurtTime"));
    }


    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {
        pCompound.putFloat("deltaRotation", this.getDeltaRotation());
        pCompound.putInt("hurtDir", this.getHurtDir());
        pCompound.putFloat("damage", this.getDamage());
        pCompound.putInt("hurtTime", this.getHurtTime());
    }

    @Override
    protected boolean canAddPassenger(final Entity passenger) {
        if (this.getDamage() > this.getDamageThreshold()) {
            return false;
        }
        return this.getPassengers()
                .size() < this.getMaxPassengers() && !this.isRemoved() && passenger instanceof VehiclePart;
    }

    @Nullable
    @Override
    public LivingEntity getControllingPassenger() {
        Entity entity = this.getPilotVehiclePartAsEntity();
        if (entity instanceof VehiclePart vehiclePart) {
            entity = vehiclePart.getFirstPassenger();
            if (entity instanceof EmptyCompartmentEntity emptyCompartmentEntity) {
                entity = emptyCompartmentEntity.getControllingPassenger();
            }
        }

        final LivingEntity livingentity1;
        if (entity instanceof LivingEntity livingentity) {
            livingentity1 = livingentity;
        } else {
            livingentity1 = null;
        }

        return livingentity1;
    }

    @Nullable
    public EmptyCompartmentEntity getPilotCompartment() {
        final Entity vehiclePart = this.getPilotVehiclePartAsEntity();

        if (!(vehiclePart instanceof VehiclePart) || !vehiclePart.isVehicle()) {
            return null;
        }

        if (!(vehiclePart.getFirstPassenger() instanceof EmptyCompartmentEntity emptyCompartmentEntity)) {
            return null;
        }

        return emptyCompartmentEntity;
    }

    @Nullable
    public Entity getPilotVehiclePartAsEntity() {
        if (this.isVehicle() && this.getPassengers().size() == this.getMaxPassengers()) {
            return this.getPassengers().get(0);
        }
        return null;
    }

    @Override
    public boolean isUnderWater() {
        return this.status == MediumStatus.UNDER_WATER || this.status == MediumStatus.UNDER_FLOWING_WATER;
    }

    public double getSmoothSpeedMS() {
        if (this.everyNthTickUnique(3)) {
            double speed = this.getDeltaMovement().length() * 20;
            this.speedOverTime.poll();
            this.speedOverTime.offer(speed);
        }
        double average = 0;
        for (double d : speedOverTime) {
            average += d;
        }
        average = average / speedOverTime.size();
        return average;
    }

    public boolean everyNthTickUnique(int n) {
        return CommonHelper.everyNthTickUnique(this.getId(), this.tickCount, n);
    }

    @Override
    protected void addPassenger(Entity passenger) {
        if (passenger instanceof VehiclePart) {
            super.addPassenger(passenger);
        }
    }

    @Override
    public ItemStack getPickResult() {
        return new ItemStack(this.getDropItem());
    }

    /**
     * Drops all destruction loot when the vehicle is destroyed
     *
     * @param damageSource The cause of the Destruction
     */
    protected void dropAllDestructionLoot(final DamageSource damageSource, final ServerLevel serverLevel) {
        this.dropFromLootTable(serverLevel, damageSource);
        this.dropCustomDestructionLoot(damageSource);
    }

    /**
     * Drop extra items that might not always drop such as the Oars in the {@link RowboatEntity}
     *
     * @param damageSource The cause of the Destruction
     */
    protected void dropCustomDestructionLoot(final DamageSource damageSource) {
    }

    /**
     * Drops the loot in the vehicle loot table. Allows for data-packable vehicle loot most helpful
     * with vehicles that should drop multiple things such as the {@link SloopEntity}
     */
    protected void dropFromLootTable(final ServerLevel serverLevel, final DamageSource damageSource) {
        ResourceKey<LootTable> resourcekey = this.getLootTable();
        LootTable loottable = this.level().getServer().reloadableRegistries().getLootTable(resourcekey);

        final LootParams.Builder lootBuilder = (new LootParams.Builder(serverLevel)).withParameter(
                        LootContextParams.THIS_ENTITY, this).withParameter(LootContextParams.ORIGIN, this.position())
                .withParameter(LootContextParams.DAMAGE_SOURCE, damageSource)
                .withOptionalParameter(LootContextParams.ATTACKING_ENTITY, damageSource.getEntity())
                .withOptionalParameter(LootContextParams.DIRECT_ATTACKING_ENTITY, damageSource.getDirectEntity());

        final LootParams lootparams = lootBuilder.create(LootContextParamSets.ENTITY);
        loottable.getRandomItems(lootparams, this.getLootTableSeed(), this::spawnAtLocation);
    }

    @Override
    public AABB getBoundingBoxForCulling() {
        float bbRadius = this.getBbWidth() * 3 + 1;
        Vec3 startingPoint = new Vec3(this.getX() - bbRadius, this.getY() - bbRadius, this.getZ() - bbRadius);
        Vec3 endingPoint = new Vec3(this.getX() + bbRadius, this.getY() + bbRadius, this.getZ() + bbRadius);
        return new AABB(startingPoint, endingPoint);
    }

    @NotNull
    @Override
    protected Vec3 collide(Vec3 pVec) {
        // TODO make this work by applying the angular velocity to each collider and by making collision affect deltaRotation
        Vec3 originalCollision = super.collide(pVec);
        /*
        if(originalCollision.horizontalDistance() == 0){
            return originalCollision;
        }
        ArrayList<Vec3> addedCollisions = new ArrayList<>();
        for(VehicleCollisionEntity collider : this.getColliders()){
            // apply angular velocity to pVec
            // apply collision to deltaRotation

            AABB aabb = collider.getBoundingBox();
            List<VoxelShape> list = collider.level().getEntityCollisions(collider, aabb.expandTowards(pVec));
            Vec3 vec3 = pVec.lengthSqr() == 0.0D ? pVec : collideBoundingBox(collider, pVec, aabb, this.level(), list);

            addedCollisions.add(vec3);
        }

        // resolve collisions
        //addedCollisions.add(originalCollision);
        Vec3 finalCollision = new Vec3(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
        for(Vec3 collision : addedCollisions){
            if(collision.horizontalDistance() < finalCollision.horizontalDistance()){
                finalCollision = collision;
            }
        }*/
        return originalCollision;
    }

    public int getCompartmentBlockLight() {
        return blockLightLevel;
    }

    @Override
    public void onAboveBubbleCol(boolean pDownwards) {
    }

    @Override
    public void onInsideBubbleColumn(boolean pDownwards) {
    }

    /**
     * @return The loot table that should be used
     */
    public ResourceKey<LootTable> getLootTable() {
        return this.getType().getDefaultLootTable();
    }

    /**
     * @return The seed to be used for the loot
     */
    public long getLootTableSeed() {
        return 0;
    }

    public enum MediumStatus {
        IN_WATER,
        UNDER_WATER,
        UNDER_FLOWING_WATER,
        ON_LAND,
        IN_AIR,
        IN_LAVA,
        UNDER_LAVA,
        UNDER_FLOWING_LAVA
    }
}
