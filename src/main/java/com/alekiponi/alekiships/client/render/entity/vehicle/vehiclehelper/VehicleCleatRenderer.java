package com.alekiponi.alekiships.client.render.entity.vehicle.vehiclehelper;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.client.model.entity.CleatKnotEntityModel;
import com.alekiponi.alekiships.client.render.util.AlekiShipsRenderHelper;
import com.alekiponi.alekiships.common.entity.vehicle.AbstractVehicle;
import com.alekiponi.alekiships.common.entity.vehicle.RowboatEntity;
import com.alekiponi.alekiships.common.entity.vehiclehelper.CleatEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class VehicleCleatRenderer extends EntityRenderer<CleatEntity> {

    private static final ResourceLocation CLEAT_KNOT = new ResourceLocation(AlekiShips.MOD_ID,
            "textures/entity/cleat_knot.png");
    private final CleatKnotEntityModel<CleatEntity> model = new CleatKnotEntityModel<>();

    public VehicleCleatRenderer(final EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(final CleatEntity cleat, final float entityYaw, final float partialTicks,
                       final PoseStack poseStack, final MultiBufferSource bufferSource, int packedLight) {
        AbstractVehicle vehicle = cleat.getTrueVehicle();
        if (vehicle != null && LightTexture.block(packedLight) < vehicle.getCompartmentBlockLight()) {
            packedLight = LightTexture.pack(Math.max(0, vehicle.getCompartmentBlockLight() - 1), getSkyLightLevel(cleat, cleat.blockPosition()));
        }

        final Entity entity = cleat.getLeashHolder();
        if (entity == null) return;

        super.render(cleat, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
        final float rotation;
        
        if (cleat.getRootVehicle() instanceof AbstractVehicle trueVehicle) {
            rotation = trueVehicle.getYRot();
        } else {
            rotation = entityYaw;
        }


        if (vehicle != null) {
            poseStack.pushPose();
            poseStack.translate(0, 1.5f, 0);
            poseStack.mulPose(Axis.ZP.rotationDegrees(180));

            if (vehicle instanceof RowboatEntity) {
                poseStack.mulPose(Axis.YP.rotationDegrees(rotation));
            } else {
                poseStack.mulPose(Axis.YP.rotationDegrees(rotation + 90));
            }

            this.model.setupAnim(cleat, 0, 0, 0, 0, 0);
            final VertexConsumer vertexconsumer = bufferSource.getBuffer(
                    this.model.renderType(this.getTextureLocation(cleat)));

            this.model.renderToBuffer(poseStack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);

            if (vehicle.renderCleatKnotSides()) {
                model.getSides().render(poseStack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY);
            }

            poseStack.popPose();
            AlekiShipsRenderHelper.renderRope(cleat, partialTicks, poseStack, bufferSource, entity,
                    this.getBlockLightLevel(cleat, BlockPos.containing(cleat.getEyePosition(partialTicks))));
        }


    }

    @Override
    public ResourceLocation getTextureLocation(final CleatEntity cleat) {
        return CLEAT_KNOT;
    }
}