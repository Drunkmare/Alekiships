package com.alekiponi.alekiships.client.render.entity.vehicle.vehiclehelper;

import com.alekiponi.alekiships.common.entity.compartment.vanilla.EnderChestCompartmentEntity;

import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.resources.model.Material;

public class EnderChestCompartmentRenderer extends ChestCompartmentRenderer<EnderChestCompartmentEntity> {

    public EnderChestCompartmentRenderer(final EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected Material getMaterial(final EnderChestCompartmentEntity compartmentEntity) {
        return Sheets.ENDER_CHEST_LOCATION;
    }
}