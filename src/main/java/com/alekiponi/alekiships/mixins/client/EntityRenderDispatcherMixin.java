package com.alekiponi.alekiships.mixins.client;

import com.alekiponi.alekiships.common.entity.OBBEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.alekiponi.alekiships.client.render.util.AlekiShipsRenderHelper.renderPolygonalHitbox;

@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRenderDispatcherMixin {

    @Inject(method = "renderHitbox", at = @At(value = "HEAD"), cancellable = true)
    private static void injectSwitchHitboxRendering(PoseStack pPoseStack, VertexConsumer pBuffer, Entity pEntity, float pPartialTicks, CallbackInfo ci){
        if(pEntity instanceof OBBEntity obbEntity){
            renderPolygonalHitbox(pPoseStack, pBuffer, obbEntity, pPartialTicks);
            ci.cancel();
        }
    }

}
