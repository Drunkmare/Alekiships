package com.alekiponi.alekiships.client.render.entity.vehicle.vehiclehelper;

import com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.AbstractCompartmentEntity;
import com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.BlockCompartment;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class LightableBlockCompartmentRenderer<Compartment extends AbstractCompartmentEntity & BlockCompartment> extends BlockCompartmentRenderer<Compartment> {

    public LightableBlockCompartmentRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderCompartmentContents(final Compartment compartmentEntity, final float partialTicks,
                                             final PoseStack poseStack, final MultiBufferSource bufferSource, int packedLight) {
        if (compartmentEntity.getDisplayBlockState().getValue(BlockStateProperties.LIT)) {
            packedLight = LightTexture.FULL_BLOCK;
        }

        super.renderCompartmentContents(compartmentEntity, partialTicks, poseStack, bufferSource, packedLight);
    }
}
