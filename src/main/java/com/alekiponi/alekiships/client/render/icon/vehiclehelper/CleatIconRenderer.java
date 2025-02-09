package com.alekiponi.alekiships.client.render.icon.vehiclehelper;

import com.alekiponi.alekiships.client.icon.Icon;
import com.alekiponi.alekiships.client.render.icon.IconRendererProvider;
import com.alekiponi.alekiships.client.render.icon.SingleIconRenderer;
import com.alekiponi.alekiships.common.entity.vehicle.AbstractVehicle;
import com.alekiponi.alekiships.common.entity.vehiclehelper.CleatEntity;

import net.minecraft.world.item.ItemStack;

public class CleatIconRenderer extends SingleIconRenderer<CleatEntity> {

    public CleatIconRenderer(final IconRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected Icon getCurrentIcon(final CleatEntity entity, final ItemStack heldStack, final float partialTick) {
        if (entity.isPassenger() && !entity.isLeashed() && entity.getRootVehicle() instanceof AbstractVehicle) {
            return Icon.DefaultIcons.LEAD;
        }

        return Icon.NONE;
    }
}