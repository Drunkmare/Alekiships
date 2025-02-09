package com.alekiponi.alekiships.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.client.model.entity.CannonEntityModel;
import com.alekiponi.alekiships.common.entity.CannonEntity;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class CannonRenderer extends EntityRenderer<CannonEntity> {

    private static final ResourceLocation CANNON = ResourceLocation.fromNamespaceAndPath(AlekiShips.MOD_ID,
            "textures/entity/cannon.png");

    private final CannonEntityModel<CannonEntity> model = new CannonEntityModel<>();

    public CannonRenderer(final EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(final CannonEntity entity, final float entityYaw, final float partialTicks,
            final PoseStack poseStack, final MultiBufferSource bufferSource, final int packedLight) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(180 - entity.getYRot()));
        poseStack.mulPose(Axis.ZP.rotationDegrees(180));
        poseStack.translate(0, -1.5f, 0);
        this.model.setupAnim(entity, 0, 0, 0, 0, 0);
        VertexConsumer vertexconsumer = bufferSource.getBuffer(this.model.renderType(this.getTextureLocation(entity)));
        this.model.renderToBuffer(poseStack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(final CannonEntity entity) {
        return CANNON;
    }
}