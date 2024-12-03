package com.alekiponi.alekiships.mixins.client;

import com.alekiponi.alekiships.common.entity.vehicle.AbstractVehicle;
import com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.EmptyCompartmentEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.entity.ArmorStandRenderer;
import net.minecraft.world.entity.decoration.ArmorStand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ArmorStandRenderer.class)
public abstract class ArmorStandRendererMixin {
    @Inject(method = "setupRotations(Lnet/minecraft/world/entity/decoration/ArmorStand;Lcom/mojang/blaze3d/vertex/PoseStack;FFFF)V", at = @At(value = "HEAD"), cancellable = true)
    public void injectTurnWithVehicle(final ArmorStand entity, final PoseStack poseStack, final float bob,
            final float yBodyRot, final float partialTick, final float scale, final CallbackInfo ci){
        if(entity.getVehicle() instanceof EmptyCompartmentEntity vehicle){
            poseStack.mulPose(Axis.YP.rotationDegrees(180-vehicle.getYRot()));
            ci.cancel();
        }
    }

}
