package com.alekiponi.alekiships.client.render.flywheel.compartment;

import com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.vanilla.EnderChestCompartmentEntity;
import com.jozufozu.flywheel.api.visualization.VisualizationContext;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.resources.model.Material;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EnderChestCompartmentVisual extends ChestCompartmentVisual<EnderChestCompartmentEntity> {

    public EnderChestCompartmentVisual(final VisualizationContext ctx, final EnderChestCompartmentEntity entity) {
        super(ctx, entity);
    }

    @Override
    protected Material getMaterial(final EnderChestCompartmentEntity compartmentEntity) {
        return Sheets.ENDER_CHEST_LOCATION;
    }
}