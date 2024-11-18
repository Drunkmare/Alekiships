package com.alekiponi.alekiships.client.render.entity.vehicle;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.client.model.entity.TestSailingShipEntityModel;
import com.alekiponi.alekiships.common.entity.vehicle.TestSailingShipEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class TestSailingShipRenderer extends EntityRenderer<TestSailingShipEntity> {

    private static final ResourceLocation TEST_TEXTURE = ResourceLocation.fromNamespaceAndPath(AlekiShips.MOD_ID,
            "textures/entity/watercraft/test.png");

    private final TestSailingShipEntityModel<TestSailingShipEntity> model = new TestSailingShipEntityModel<>();

    public TestSailingShipRenderer(final EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(final TestSailingShipEntity entity, final float entityYaw, final float partialTicks,
            final PoseStack poseStack, final MultiBufferSource bufferSource, final int packedLight) {

        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(entity.getYRot() + 180));
        poseStack.mulPose(Axis.ZP.rotationDegrees(180));
        poseStack.translate(0, -1.5f, 0);

        this.model.setupAnim(entity, 0, 0, 0, 0, 0);
        VertexConsumer vertexconsumer = bufferSource.getBuffer(this.model.renderType(this.getTextureLocation(entity)));
        this.model.renderToBuffer(poseStack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(final TestSailingShipEntity entity) {
        return TEST_TEXTURE;
    }
}