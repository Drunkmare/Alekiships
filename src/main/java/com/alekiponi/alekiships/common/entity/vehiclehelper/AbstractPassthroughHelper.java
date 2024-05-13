package com.alekiponi.alekiships.common.entity.vehiclehelper;

import com.alekiponi.alekiships.client.IngameOverlays;
import com.alekiponi.alekiships.common.entity.IHaveIcons;
import com.alekiponi.alekiships.common.entity.vehicle.AbstractVehicle;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.ArrayList;

public abstract class AbstractPassthroughHelper extends AbstractHelper implements IHaveIcons {
    public AbstractPassthroughHelper(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public boolean hurt(final DamageSource damageSource, final float amount) {
        if(!(damageSource.getEntity() instanceof Player)){
            return false;
        }

        if (this.getRootVehicle() instanceof AbstractVehicle vehicle) {
            return vehicle.hurt(damageSource, amount);
        }

        return true;
    }

    @Override
    public boolean isInvulnerableTo(final DamageSource damageSource) {
        if (damageSource.is(DamageTypeTags.IS_EXPLOSION)) {
            return true;
        }
        return super.isInvulnerableTo(damageSource);
    }

    @Override
    public boolean isPickable() {
        return !this.isRemoved();
    }

    @Override
    public InteractionResult interact(final Player player, final InteractionHand hand) {
        return this.getRootVehicle().interact(player, hand);
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
