package com.alekiponi.alekiships.mixins.minecraft;

import com.alekiponi.alekiships.common.entity.compartment.SimpleBlockMenuCompartment;
import com.alekiponi.alekiships.common.entity.compartment.vanilla.crafting.GrindstoneCompartmentEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.OptionalInt;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {

    protected PlayerMixin(EntityType<? extends LivingEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
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
    public abstract OptionalInt openMenu(@Nullable final MenuProvider pMenu);
}