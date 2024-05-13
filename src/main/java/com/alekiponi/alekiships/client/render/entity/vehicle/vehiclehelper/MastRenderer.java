package com.alekiponi.alekiships.client.render.entity.vehicle.vehiclehelper;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.client.model.entity.AnchorEntityModel;
import com.alekiponi.alekiships.client.render.util.AlekiShipsRenderHelper;
import com.alekiponi.alekiships.common.entity.vehicle.AbstractAlekiBoatEntity;
import com.alekiponi.alekiships.common.entity.vehiclehelper.AnchorEntity;
import com.alekiponi.alekiships.common.entity.vehiclehelper.MastEntity;
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

public class MastRenderer extends EntityRenderer<MastEntity> {

    public MastRenderer(final EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(final MastEntity mast, final float entityYaw, final float partialTicks,
                       final PoseStack poseStack, final MultiBufferSource bufferSource, final int packedLight) {
        if (!(mast.getTrueVehicle() instanceof AbstractAlekiBoatEntity)) return;

        AbstractAlekiBoatEntity ship = (AbstractAlekiBoatEntity) mast.getTrueVehicle();

        final float rotation = ship.getWindLocalRotation();

        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(rotation));
        poseStack.translate(0, 0, 0f);

        super.render(mast, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(final MastEntity pEntity) {
        return null;
    }
}