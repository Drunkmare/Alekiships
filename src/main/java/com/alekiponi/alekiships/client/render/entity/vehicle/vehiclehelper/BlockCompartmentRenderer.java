package com.alekiponi.alekiships.client.render.entity.vehicle.vehiclehelper;

import com.alekiponi.alekiships.common.entity.vehicle.AbstractVehicle;
import com.alekiponi.alekiships.common.entity.compartment.AbstractCompartmentEntity;
import com.alekiponi.alekiships.common.entity.compartment.BlockCompartment;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;

/**
 * Generic unspecialized renderer for compartments that render normal blocks such as a furnace or barrel
 */
public class BlockCompartmentRenderer<Compartment extends AbstractCompartmentEntity & BlockCompartment> extends CompartmentRenderer<Compartment> {
    protected final BlockRenderDispatcher blockRenderer;

    public BlockCompartmentRenderer(final EntityRendererProvider.Context context) {
        super(context);

        this.blockRenderer = context.getBlockRenderDispatcher();
    }

    @Override
    protected void renderCompartmentContents(final Compartment compartmentEntity, final float partialTicks,
                                             final PoseStack poseStack, final MultiBufferSource bufferSource, int packedLight) {
        AbstractVehicle vehicle = compartmentEntity.getTrueVehicle();
        if (LightTexture.block(packedLight) < compartmentEntity.getCompartmentBlockLight()) {
            packedLight = LightTexture.pack(compartmentEntity.getCompartmentBlockLight(), getSkyLightLevel(compartmentEntity, compartmentEntity.blockPosition()));
        }
        if (vehicle != null && LightTexture.block(packedLight) < vehicle.getCompartmentBlockLight()) {
            packedLight = LightTexture.pack(Math.max(0, vehicle.getCompartmentBlockLight() - 1), getSkyLightLevel(compartmentEntity, compartmentEntity.blockPosition()));
        }

        //noinspection deprecation
        this.blockRenderer.renderSingleBlock(compartmentEntity.getDisplayBlockState(), poseStack, bufferSource,
                packedLight, OverlayTexture.NO_OVERLAY);
    }
}