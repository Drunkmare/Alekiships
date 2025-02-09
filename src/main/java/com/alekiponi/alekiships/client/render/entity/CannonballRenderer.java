package com.alekiponi.alekiships.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.client.model.entity.CannonballEntityModel;
import com.alekiponi.alekiships.common.entity.CannonballEntity;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class CannonballRenderer extends EntityRenderer<CannonballEntity> {

    private static final ResourceLocation CANNONBALL = ResourceLocation.fromNamespaceAndPath(AlekiShips.MOD_ID,
            "textures/entity/cannonball.png");

    private final CannonballEntityModel<CannonballEntity> model = new CannonballEntityModel<>();

    public CannonballRenderer(final EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(final CannonballEntity entity, final float entityYaw, final float partialTicks,
            final PoseStack poseStack, final MultiBufferSource bufferSource, final int packedLight) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(entity.getYRot()));
        poseStack.translate(0, -1.4f, 0);
        this.model.setupAnim(entity, 0, 0, 0, 0, 0);
        VertexConsumer vertexconsumer = bufferSource.getBuffer(this.model.renderType(this.getTextureLocation(entity)));
        this.model.renderToBuffer(poseStack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(final CannonballEntity entity) {
        return CANNONBALL;
    }
}