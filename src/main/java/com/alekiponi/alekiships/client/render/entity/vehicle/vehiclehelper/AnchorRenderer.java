package com.alekiponi.alekiships.client.render.entity.vehicle.vehiclehelper;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.client.model.entity.AnchorEntityModel;
import com.alekiponi.alekiships.client.render.util.AlekiShipsRenderHelper;
import com.alekiponi.alekiships.common.entity.vehicle.AbstractAlekiBoatEntity;
import com.alekiponi.alekiships.common.entity.vehiclehelper.AnchorEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class AnchorRenderer extends EntityRenderer<AnchorEntity> {
    public static final ResourceLocation ANCHOR = ResourceLocation.fromNamespaceAndPath(AlekiShips.MOD_ID,
            "textures/entity/watercraft/anchor.png");
    private final AnchorEntityModel model;

    public AnchorRenderer(final EntityRendererProvider.Context context) {
        super(context);
        this.model = new AnchorEntityModel(context.bakeLayer(AnchorEntityModel.LAYER_LOCATION));
    }

    @Override
    public void render(final AnchorEntity anchor, final float entityYaw, final float partialTicks,
            final PoseStack poseStack, final MultiBufferSource bufferSource, final int packedLight) {
        if (anchor.getVehicle() == null) return;
        if (anchor.getVehicle().getVehicle() == null) return;
        if (anchor.getVehicle().getVehicle().getVehicle() == null) return;

        final float rotation = anchor.getVehicle().getVehicle()
                .getVehicle() instanceof AbstractAlekiBoatEntity trueVehicle ? trueVehicle.getYRot() : entityYaw;

        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(60 - rotation));
        poseStack.translate(0, 0, 1.4f);
        poseStack.mulPose(Axis.XP.rotationDegrees(270));

        this.model.setupAnim(anchor, 0, 0, 0, 0, 0);
        final VertexConsumer vertexconsumer = bufferSource.getBuffer(
                this.model.renderType(this.getTextureLocation(anchor)));
        this.model.renderToBuffer(poseStack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
        final Entity vehicle = anchor.getVehicle();
        assert vehicle != null;
        AlekiShipsRenderHelper.renderRope(anchor, partialTicks, poseStack, bufferSource, vehicle,
                this.getBlockLightLevel(anchor, BlockPos.containing(anchor.getEyePosition(partialTicks))));
        super.render(anchor, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(final AnchorEntity pEntity) {
        return ANCHOR;
    }
}