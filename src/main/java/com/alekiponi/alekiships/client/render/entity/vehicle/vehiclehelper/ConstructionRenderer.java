package com.alekiponi.alekiships.client.render.entity.vehicle.vehiclehelper;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.client.model.entity.ConstructionEntityModel;
import com.alekiponi.alekiships.common.entity.vehiclehelper.ConstructionEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class ConstructionRenderer extends EntityRenderer<ConstructionEntity> {

    private static final ResourceLocation HAMMER = ResourceLocation.fromNamespaceAndPath(AlekiShips.MOD_ID,
            "textures/entity/watercraft/construction.png");

    private final ConstructionEntityModel model;

    public ConstructionRenderer(final EntityRendererProvider.Context context) {
        super(context);
        this.model = new ConstructionEntityModel(context.bakeLayer(ConstructionEntityModel.LAYER_LOCATION));
    }

    @Override
    public void render(final ConstructionEntity entity, final float entityYaw, final float partialTicks,
            final PoseStack poseStack, final MultiBufferSource bufferSource, final int packedLight) {
        if (entity.getVehicle() == null) return;
        if (entity.getVehicle().getVehicle() == null) return;

        {
            final Minecraft mc = Minecraft.getInstance();
            assert mc.player != null;
            if (mc.player.distanceTo(entity.getRootVehicle()) > 5) return;
        }

        poseStack.pushPose();
        poseStack.scale(1, 1, 1);
        poseStack.translate(0, 1.75f, 0);

        poseStack.mulPose(Axis.YP.rotation(entity.getSpin(partialTicks)));
        poseStack.mulPose(Axis.ZP.rotation((float) Math.toRadians(180)));

        final VertexConsumer vertexconsumer = bufferSource.getBuffer(
                RenderType.entityCutout(this.getTextureLocation(entity)));
        this.model.renderToBuffer(poseStack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(final ConstructionEntity entity) {
        return HAMMER;
    }
}