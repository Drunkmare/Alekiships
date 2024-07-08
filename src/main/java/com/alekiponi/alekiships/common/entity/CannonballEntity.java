package com.alekiponi.alekiships.common.entity;

import com.alekiponi.alekiships.common.entity.vehicle.AbstractVehicle;
import com.alekiponi.alekiships.util.CannonballExplosion;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;

public class CannonballEntity extends Projectile {

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
    protected void defineSynchedData() {
        // We define no custom synced data
    }

    @Override
    protected void onHit(final HitResult hitResult) {
        super.onHit(hitResult);
        //this.level().explode(this, this.getX(), this.getY(), this.getZ(), (float) Math.min(this.getDeltaMovement().length() * 2,4), true, Level.ExplosionInteraction.MOB);
        this.explode((float) Math.min(this.getDeltaMovement().length() * 2,4));
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
            if (hitResult.getType() != HitResult.Type.MISS && !ForgeEventFactory.onProjectileImpact(this, hitResult)) {
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
        } else inertia = 0.98F;

        this.setDeltaMovement(movement.add(0, -0.1, 0).scale(inertia));
        this.level().addParticle(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, nextX, nextY + 0.5, nextZ, 0, 0, 0);
        this.setPos(nextX, nextY, nextZ);
    }

    protected void explode(final float radius) {
        final Level level = this.level();

        if (level.isClientSide()) return;

        final Explosion.BlockInteraction blockInteraction = level.getGameRules().getBoolean(
                GameRules.RULE_TNT_EXPLOSION_DROP_DECAY) ? Explosion.BlockInteraction.DESTROY_WITH_DECAY : Explosion.BlockInteraction.DESTROY;
        final CannonballExplosion explosion = new CannonballExplosion(level, this, null, null, this.getX(),
                this.getY(0.0625D), this.getZ(), radius, false, blockInteraction);

        if (ForgeEventFactory.onExplosionStart(level, explosion)) return;

        explosion.explode();
        explosion.finalizeExplosion(true);

        if (!(level instanceof ServerLevel serverLevel)) return;

        if (!explosion.interactsWithBlocks()) {
            explosion.clearToBlow();
        }

        final double x = this.getX();
        final double y = this.getY();
        final double z = this.getZ();
        // We have to manually send a packet to all players in range
        for (final ServerPlayer serverPlayer : serverLevel.players()) {
            if (serverPlayer.distanceToSqr(x, y, z) < 4096) {
                serverPlayer.connection.send(new ClientboundExplodePacket(x, y, z, radius, explosion.getToBlow(),
                        explosion.getHitPlayers().get(serverPlayer)));
            }
        }
    }

    @Override
    protected boolean canHitEntity(final Entity target) {
        // stop cannons from being able to hit the boat they're fired from
        if (target.getRootVehicle() instanceof AbstractVehicle && this.getOwner() != null) {
            if(target.getRootVehicle().is(this.getOwner().getRootVehicle())){
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