package com.alekiponi.alekiships.common.entity;

import com.alekiponi.alekiships.util.CannonballExplosion;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;

public class CannonballEntity extends AbstractHurtingProjectile {

    public CannonballEntity(final EntityType<? extends CannonballEntity> entityType, final Level level) {
        super(entityType, level);
    }

    public CannonballEntity(final double x, final double y, final double z, final double offsetX, final double offsetY,
            final double offsetZ, final Level level) {
        super(AlekiShipsEntities.CANNONBALL_ENTITY.get(), x, y, z, offsetX, offsetY, offsetZ, level);
    }

    @Override
    protected void onHit(final HitResult hitResult) {
        super.onHit(hitResult);
        this.explode((float) (this.getDeltaMovement().length() * 2));
        this.discard();
    }

    protected void explode(final float radius) {
        final Level level = this.level();

        final Explosion.BlockInteraction blockInteraction = level.getGameRules().getBoolean(
                GameRules.RULE_TNT_EXPLOSION_DROP_DECAY) ? Explosion.BlockInteraction.DESTROY_WITH_DECAY : Explosion.BlockInteraction.DESTROY;
        final CannonballExplosion explosion = new CannonballExplosion(level, this, null, null, this.getX(),
                this.getY(0.0625D), this.getZ(), radius, false, blockInteraction);

        if (ForgeEventFactory.onExplosionStart(level, explosion)) return;

        explosion.explode();
        explosion.finalizeExplosion(true);
    }

    @Override
    protected boolean shouldBurn() {
        return false;
    }

    @Override
    protected float getInertia() {
        return 0.98F;
    }

    @Override
    protected ParticleOptions getTrailParticle() {
        return ParticleTypes.CAMPFIRE_SIGNAL_SMOKE;
    }

    @Override
    protected boolean canHitEntity(final Entity target) {
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
}