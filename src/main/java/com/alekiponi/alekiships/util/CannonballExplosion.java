package com.alekiponi.alekiships.util;

import com.alekiponi.alekiships.common.entity.vehicle.AbstractAlekiBoatEntity;
import com.google.common.collect.Sets;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class CannonballExplosion extends Explosion {
    @SuppressWarnings("unused")
    public CannonballExplosion(final Level pLevel, @Nullable final Entity pSource, final double pToBlowX,
            final double pToBlowY, final double pToBlowZ, final float pRadius, final List<BlockPos> pPositions) {
        super(pLevel, pSource, pToBlowX, pToBlowY, pToBlowZ, pRadius, pPositions);
    }

    @SuppressWarnings("unused")
    public CannonballExplosion(final Level pLevel, @Nullable final Entity pSource, final double pToBlowX,
            final double pToBlowY, final double pToBlowZ, final float pRadius, final boolean pFire,
            final BlockInteraction pBlockInteraction, final List<BlockPos> pPositions) {
        super(pLevel, pSource, pToBlowX, pToBlowY, pToBlowZ, pRadius, pFire, pBlockInteraction, pPositions);
    }

    @SuppressWarnings("unused")
    public CannonballExplosion(final Level pLevel, @Nullable final Entity pSource, final double pToBlowX,
            final double pToBlowY, final double pToBlowZ, final float pRadius, final boolean pFire,
            final BlockInteraction pBlockInteraction) {
        super(pLevel, pSource, pToBlowX, pToBlowY, pToBlowZ, pRadius, pFire, pBlockInteraction);
    }

    public CannonballExplosion(final Level pLevel, @Nullable final Entity pSource,
            @Nullable final DamageSource pDamageSource, @Nullable final ExplosionDamageCalculator pDamageCalculator,
            final double pToBlowX, final double pToBlowY, final double pToBlowZ, final float pRadius,
            final boolean pFire, final BlockInteraction pBlockInteraction) {
        super(pLevel, pSource, pDamageSource, pDamageCalculator, pToBlowX, pToBlowY, pToBlowZ, pRadius, pFire,
                pBlockInteraction);
    }

    @Override
    public void explode() {
        // This method is primarily copied as is from vanilla. Anything we add or change should be commented

        this.level.gameEvent(this.source, GameEvent.EXPLODE, this.getPosition());
        final Set<BlockPos> set = Sets.newHashSet();

        for (int j = 0; j < 16; ++j) {
            for (int k = 0; k < 16; ++k) {
                for (int l = 0; l < 16; ++l) {
                    if (j == 0 || j == 15 || k == 0 || k == 15 || l == 0 || l == 15) {
                        double d0 = (float) j / 15 * 2 - 1;
                        double d1 = (float) k / 15 * 2 - 1;
                        double d2 = (float) l / 15 * 2 - 1;
                        double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
                        d0 /= d3;
                        d1 /= d3;
                        d2 /= d3;
                        float f = this.radius * (0.7F + this.level.random.nextFloat() * 0.6F);
                        double d4 = this.x;
                        double d6 = this.y;
                        double d8 = this.z;

                        for (; f > 0; f -= 0.22500001F) {
                            final BlockPos blockpos = BlockPos.containing(d4, d6, d8);
                            final BlockState blockstate = this.level.getBlockState(blockpos);
                            final FluidState fluidstate = this.level.getFluidState(blockpos);
                            if (!this.level.isInWorldBounds(blockpos)) {
                                break;
                            }

                            final Optional<Float> optional = this.damageCalculator.getBlockExplosionResistance(this,
                                    this.level, blockpos, blockstate, fluidstate);
                            if (optional.isPresent()) {
                                f -= (float) ((optional.get() + 0.3) * 0.3);
                            }

                            if (f > 0 && this.damageCalculator.shouldBlockExplode(this, this.level, blockpos,
                                    blockstate, f)) {
                                set.add(blockpos);
                            }

                            d4 += d0 * 0.3;
                            d6 += d1 * 0.3;
                            d8 += d2 * 0.3;
                        }
                    }
                }
            }
        }

        this.toBlow.addAll(set);
        final double diameter = this.radius * 2;

        final List<Entity> list;
        {
            final int k1 = Mth.floor(this.x - diameter - 1);
            final int l1 = Mth.floor(this.x + diameter + 1);
            final int i2 = Mth.floor(this.y - diameter - 1);
            final int i1 = Mth.floor(this.y + diameter + 1);
            final int j2 = Mth.floor(this.z - diameter - 1);
            final int j1 = Mth.floor(this.z + diameter + 1);
            list = this.level.getEntities(this.source, new AABB(k1, i2, j2, l1, i1, j1));
        }

        ForgeEventFactory.onExplosionDetonate(this.level, this, list, diameter);

        for (final Entity entity : list) {
            if (entity.ignoreExplosion()) continue;

            final double d12 = Math.sqrt(entity.distanceToSqr(this.getPosition())) / diameter;
            if (d12 > 1) continue;

            double distanceX = entity.getX() - this.x;
            double distanceY = (entity instanceof PrimedTnt ? entity.getY() : entity.getEyeY()) - this.y;
            double distanceZ = entity.getZ() - this.z;
            final double d13 = Math.sqrt(distanceX * distanceX + distanceY * distanceY + distanceZ * distanceZ);

            if (d13 == 0) continue;

            distanceX /= d13;
            distanceY /= d13;
            distanceZ /= d13;
            final double seenPercent = getSeenPercent(this.getPosition(), entity);
            final double damage = (1 - d12) * seenPercent;


            // Check for our Boats
            if (entity instanceof AbstractAlekiBoatEntity) {
                entity.hurt(this.getDamageSource(), 100);
                // Check for Vanilla Boats (also boat mods Aleki no like lol)
            } else if (entity instanceof Boat) {
                entity.hurt(this.getDamageSource(), 1000);
            } else {
                // What vanilla normally does
                entity.hurt(this.getDamageSource(), (float) ((damage * damage + damage) / 2 * 7 * diameter + 1));
            }

            final double d11;
            if (entity instanceof final LivingEntity livingentity) {
                d11 = ProtectionEnchantment.getExplosionKnockbackAfterDampener(livingentity, damage);
            } else {
                d11 = damage;
            }

            distanceX *= d11;
            distanceY *= d11;
            distanceZ *= d11;
            final Vec3 vec31 = new Vec3(distanceX, distanceY, distanceZ);
            entity.setDeltaMovement(entity.getDeltaMovement().add(vec31));
            if (entity instanceof final Player player) {
                if (!player.isSpectator() && (!player.isCreative() || !player.getAbilities().flying)) {
                    this.hitPlayers.put(player, vec31);
                }
            }
        }
    }
}