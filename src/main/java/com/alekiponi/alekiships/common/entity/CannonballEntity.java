package com.alekiponi.alekiships.common.entity;

import com.alekiponi.alekiships.common.entity.vehicle.AbstractAlekiBoatEntity;
import com.alekiponi.alekiships.common.entity.vehicle.AbstractVehicle;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import net.neoforged.neoforge.event.EventHooks;

public class CannonballEntity extends Projectile {

    public static final ExplosionDamageCalculator CANNONBALL_EXPLOSION_CALCULATOR = new ExplosionDamageCalculator() {

        @Override
        public float getKnockbackMultiplier(final Entity entity) {
            return switch (entity) {
                case final LivingEntity livingEntity -> {
                    if (!(livingEntity instanceof final Player player)) yield super.getKnockbackMultiplier(entity);

                    for (final var touchedEntity : player.level()
                            .getEntities(player, player.getBoundingBox().inflate(0, 0.1, 0),
                                    EntitySelector.CAN_BE_COLLIDED_WITH)) {
                        if (!(touchedEntity instanceof AbstractVehicle vehicle)) continue;
                        if (!vehicle.collectPlayersToTakeWith().contains(player)) continue;

                        yield 0;
                    }
                    yield super.getKnockbackMultiplier(entity);
                }
                case CannonEntity ignored -> 0;
                default -> super.getKnockbackMultiplier(entity);
            };
        }

        @Override
        public float getEntityDamageAmount(final Explosion explosion, final Entity entity) {
            return switch (entity) {
                case AbstractAlekiBoatEntity ignored -> 100;
                case Boat ignored -> 10_000;
                // TODO this seems stupid but should be the same behavior as in the previous custom Explosion class
                //  so don't blame me -Traister
                case Player ignored -> 1;
                default -> super.getEntityDamageAmount(explosion, entity);
            };
        }
    };

    public CannonballEntity(final EntityType<? extends CannonballEntity> entityType, final Level level) {
        super(entityType, level);
    }

    public CannonballEntity(final double x, final double y, final double z, final Level level) {
        super(AlekiShipsEntities.CANNONBALL_ENTITY.get(), level);
        this.moveTo(x, y, z, this.getYRot(), this.getXRot());
        this.reapplyPosition();
    }

    /**
     * Construct a cannonball in an already fired state from a {@link CannonEntity}
     *
     * @param cannonEntity The cannon entity this cannonball is shot from
     */
    public CannonballEntity(final CannonEntity cannonEntity) {
        this(cannonEntity.getX(), cannonEntity.getY(), cannonEntity.getZ(), cannonEntity.level());
        this.shoot(cannonEntity);
    }

    @Override
    protected void defineSynchedData(final SynchedEntityData.Builder builder) {
        // We define no custom synced data
    }

    @Override
    protected void onHit(final HitResult hitResult) {
        super.onHit(hitResult);
        //this.level().explode(this, this.getX(), this.getY(), this.getZ(), (float) Math.min(this.getDeltaMovement().length() * 2,4), true, Level.ExplosionInteraction.MOB);
        this.explode((float) Math.min(this.getDeltaMovement().length() * 2, 4));
        this.discard();
    }

    /**
     * Shoot the cannonball from the passed in {@link CannonEntity}
     *
     * @param cannonEntity The cannon entity to shoot from
     */
    public void shoot(final CannonEntity cannonEntity) {
        this.setOwner(cannonEntity);
        this.shootFromRotation(cannonEntity, cannonEntity.getXRot(), cannonEntity.getYRot(), 0, 6, 0);

        if (cannonEntity.isPassenger()) {
            this.setDeltaMovement(this.getDeltaMovement().add(cannonEntity.getRootVehicle().getDeltaMovement()));
        }
    }

    @Override
    public void tick() {
        final Entity owner = this.getOwner();
        //noinspection deprecation with no alternative :|
        if (owner != null && owner.isRemoved() || !this.level().hasChunkAt(this.blockPosition())) {
            if (!this.level().isClientSide) {
                this.discard();
                return;
            }
        }

        super.tick();

        {
            final HitResult hitResult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
            if (hitResult.getType() != HitResult.Type.MISS && !EventHooks.onProjectileImpact(this, hitResult)) {
                this.onHit(hitResult);
            }
        }

        this.checkInsideBlocks();
        final Vec3 movement = this.getDeltaMovement();
        final double nextX = this.getX() + movement.x;
        final double nextY = this.getY() + movement.y;
        final double nextZ = this.getZ() + movement.z;
        ProjectileUtil.rotateTowardsMovement(this, 0.2F);

        final float inertia;
        if (this.isInWater()) {
            for (int i = 0; i < 4; ++i) {
                this.level().addParticle(ParticleTypes.BUBBLE, nextX - movement.x * 0.25, nextY - movement.y * 0.25,
                        nextZ - movement.z * 0.25, movement.x, movement.y, movement.z);
            }

            inertia = 0.8F;
        } else {
            inertia = 0.98F;
        }

        this.setDeltaMovement(movement.add(0, -0.1, 0).scale(inertia));
        this.level().addParticle(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, nextX, nextY + 0.5, nextZ, 0, 0, 0);
        this.setPos(nextX, nextY, nextZ);
    }

    protected void explode(final float radius) {
        if (this.level().isClientSide()) return;

        this.level()
                .explode(this, Explosion.getDefaultDamageSource(this.level(), this), CANNONBALL_EXPLOSION_CALCULATOR,
                        this.getX(), this.getY(0.0625D), this.getZ(), radius, false, Level.ExplosionInteraction.TNT);
    }

    @Override
    protected boolean canHitEntity(final Entity target) {
        // stop cannons from being able to hit the boat they're fired from
        if (target.getRootVehicle() instanceof AbstractVehicle && this.getOwner() != null) {
            if (target.getRootVehicle().is(this.getOwner().getRootVehicle())) {
                return false;
            }
        }

        if (!target.canBeHitByProjectile()) return false;

        return !this.isPassengerOfSameVehicle(target) && !target.noPhysics;
    }

    @Override
    public AABB getBoundingBoxForCulling() {
        float bbRadius = 2;
        final Vec3 startingPoint = new Vec3(this.getX() - bbRadius, this.getY() - bbRadius, this.getZ() - bbRadius);
        final Vec3 endingPoint = new Vec3(this.getX() + bbRadius, this.getY() + bbRadius, this.getZ() + bbRadius);
        return new AABB(startingPoint, endingPoint);
    }

    @Override
    public void recreateFromPacket(final ClientboundAddEntityPacket addEntityPacket) {
        super.recreateFromPacket(addEntityPacket);
        // For some reason vanilla doesn't use the packets delta movement even though it's passed through the network
        this.setDeltaMovement(addEntityPacket.getXa(), addEntityPacket.getYa(), addEntityPacket.getZa());
    }
}