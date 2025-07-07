package com.alekiponi.alekiships.client.render.entity.vehicle.vehiclehelper;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import com.alekiponi.alekiships.common.entity.compartment.AbstractCompartmentEntity;
import com.alekiponi.alekiships.common.entity.vehicle.AbstractVehicle;
import com.alekiponi.alekiships.common.entity.vehiclehelper.VehiclePart;

import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;

public abstract class CompartmentRenderer<CompartmentType extends AbstractCompartmentEntity> extends EntityRenderer<CompartmentType> {

    public CompartmentRenderer(final EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0;
    }

    @Override
    public void render(final CompartmentType compartmentEntity, final float entityYaw, final float partialTicks,
            final PoseStack poseStack, final MultiBufferSource bufferSource, int packedLight) {
        AbstractVehicle vehicle = compartmentEntity.getTrueVehicle();
        if (vehicle != null) {
            packedLight = Math.max(packedLight, LightTexture.pack(compartmentEntity.getCompartmentBlockLight(),
                    getSkyLightLevel(compartmentEntity, compartmentEntity.blockPosition())));
        }

        super.render(compartmentEntity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);

        final float rotation;
        if (compartmentEntity.getTrueVehicle() != null && compartmentEntity.getVehicle() instanceof VehiclePart vehiclePart && compartmentEntity.tickCount < 2) {
            rotation = compartmentEntity.getTrueVehicle().getYRot() + vehiclePart.getCompartmentRotation();
        } else {
            rotation = entityYaw;
        }

        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(180 - rotation));

        float renderSize = 0.6875f;
        if (compartmentEntity.isPassenger() && compartmentEntity.getTrueVehicle() != null) {
            renderSize = compartmentEntity.getTrueVehicle().renderSizeForCompartments();
        }

        poseStack.scale(renderSize, renderSize, renderSize);

        poseStack.translate(-0.5F, 0, -0.5F);

        this.renderCompartmentContents(compartmentEntity, partialTicks, poseStack, bufferSource, packedLight);

        poseStack.popPose();
    }

    @Override
    protected boolean shouldShowName(final CompartmentType entity) {
        return false;
    }

    /**
     * Render the compartment contents. This is pre-scaled, rotated and translated for ease of use
     */
    protected abstract void renderCompartmentContents(final CompartmentType compartmentEntity, final float partialTicks,
            final PoseStack poseStack, final MultiBufferSource bufferSource, final int packedLight);

    @Override
    public ResourceLocation getTextureLocation(final CompartmentType compartmentEntity) {
        return MissingTextureAtlasSprite.getLocation();
    }
}