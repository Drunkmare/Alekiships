package com.alekiponi.alekiships.mixins.minecraft;

import com.alekiponi.alekiships.common.entity.vehiclehelper.MastEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {


    @Shadow
    private Optional<BlockPos> lastClimbablePos;

    public LivingEntityMixin(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Inject(method = "onClimbable", at = @At(value = "HEAD"), cancellable = true)
    public void injectOnClimbableWhileClimbingMasts(CallbackInfoReturnable<Boolean> cir){
        if((LivingEntity)(Object)((this)) instanceof Player){

            List<Entity> masts = new ArrayList<Entity>(this.level()
                    .getEntities(this, this.getBoundingBox().inflate(0, 0, 0).move(0, 0, 0), EntitySelector.NO_SPECTATORS));

            masts.removeIf(entity -> !(entity instanceof MastEntity));

            Optional<BlockPos> climbable = Optional.of(this.blockPosition());

            if(!masts.isEmpty() && climbable.isPresent()){
                lastClimbablePos = climbable;
                cir.setReturnValue(true);
            }

        }

    }


}
