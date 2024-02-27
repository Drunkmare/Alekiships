package com.alekiponi.alekiships.mixins.minecraft;

import com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.AbstractCompartmentEntity;
import com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.SimpleBlockMenuCompartment;
import com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.vanilla.GrindstoneCompartmentEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.OptionalInt;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity{

    protected PlayerMixin(EntityType<? extends LivingEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Inject(method = "attack", at = @At("HEAD"), cancellable = true)
    public void injectAttackSoundCancellation(Entity pTarget, CallbackInfo ci){
        if(pTarget instanceof AbstractCompartmentEntity){
            if (!net.minecraftforge.common.ForgeHooks.onPlayerAttackTarget((Player)(Object)this, pTarget))
            {
                ci.cancel();
            }
            float damage = (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE);
            float f1 = 0;

            float f2 = 1.0f;
            damage *= 0.2F + f2 * f2 * 0.8F;
            f1 *= f2;
            if (damage > 0.0F || f1 > 0.0F) {
                boolean flag5 = pTarget.hurt(this.damageSources().playerAttack((Player)(Object)this), damage);
                if (flag5) {
                    this.setLastHurtMob(pTarget);
                    this.causeFoodExhaustion(0.1F);
                }
            }
            this.resetAttackStrengthTicker();
            ci.cancel();
        }
    }

    /**
     * @reason Injection to {@link Player#interactOn(Entity, InteractionHand)} so we can have simple compartments
     * like {@link GrindstoneCompartmentEntity} that will open in Spectator Mode as {@link MenuProvider} results in
     * using the entities name which causes us a number of issues. {@link MenuProvider} takes priority over our
     * interface if both are present and has no effect if the player isn't in Spectator Mode
     * @author Traister101
     */
    @Inject(method = "interactOn", at = @At(value = "RETURN", ordinal = 0))
    public void inject$interactOn(final Entity entityToInteractOn, final InteractionHand pHand,
            final CallbackInfoReturnable<InteractionResult> callbackInfo) {
        if (!(entityToInteractOn instanceof MenuProvider)) {
            if (entityToInteractOn instanceof SimpleBlockMenuCompartment compartment) {
                this.openMenu(compartment.getMenuProvider());
            }
        }
    }

    @Shadow
    public abstract void causeFoodExhaustion(float pExhaustion);

    @Shadow
    public abstract void resetAttackStrengthTicker();

    @Shadow
    public abstract OptionalInt openMenu(@Nullable final MenuProvider pMenu);
}