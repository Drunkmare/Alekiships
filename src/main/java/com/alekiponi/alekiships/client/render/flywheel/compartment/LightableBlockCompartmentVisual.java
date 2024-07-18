package com.alekiponi.alekiships.client.render.flywheel.compartment;

import com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.AbstractCompartmentEntity;
import com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.BlockCompartment;
import com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.LightEmittingCompartment;
import com.jozufozu.flywheel.api.visualization.VisualizationContext;
import com.jozufozu.flywheel.lib.instance.TransformedInstance;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LightableBlockCompartmentVisual<Compartment extends AbstractCompartmentEntity & BlockCompartment & LightEmittingCompartment> extends BlockCompartmentVisual<Compartment> {

    public LightableBlockCompartmentVisual(final VisualizationContext ctx, final Compartment entity) {
        super(ctx, entity);
    }

    @Override
    protected void updateBlockModel(final TransformedInstance model, final BlockState blockState,
            final PoseStack poseStack, final float partialTick) {
        if (this.entity.isEmitting()) {
            model.light(LightTexture.pack(this.entity.getLightEmission(), 0));
        }
        model.setTransform(poseStack).setChanged();
    }
}