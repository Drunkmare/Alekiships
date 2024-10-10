package com.alekiponi.alekiships.mixins.accessors;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(Explosion.class)
public interface ExplosionAccessor {

    @Accessor
    Level getLevel();

    @Accessor("x")
    double x();

    @Accessor("y")
    double y();

    @Accessor("z")
    double z();

    @Accessor
    @Nullable Entity getSource();

    @Accessor
    float getRadius();

    @Accessor
    ExplosionDamageCalculator getDamageCalculator();

    @Accessor
    ObjectArrayList<BlockPos> getToBlow();

    @Accessor
    Map<Player, Vec3> getHitPlayers();
}