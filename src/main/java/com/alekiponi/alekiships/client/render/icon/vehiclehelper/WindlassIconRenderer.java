package com.alekiponi.alekiships.client.render.icon.vehiclehelper;

import com.alekiponi.alekiships.client.icon.Icon;
import com.alekiponi.alekiships.client.render.icon.IconRendererProvider;
import com.alekiponi.alekiships.client.render.icon.SingleIconRenderer;
import com.alekiponi.alekiships.common.entity.vehicle.AbstractVehicle;
import com.alekiponi.alekiships.common.entity.vehiclehelper.WindlassSwitchEntity;

import net.minecraft.world.item.ItemStack;

public class WindlassIconRenderer extends SingleIconRenderer<WindlassSwitchEntity> {

    private static final Icon ANCHOR_UP = Icon.column(Icon.DefaultIcons.ANCHOR, Icon.DefaultIcons.ARROW_UP);
    private static final Icon ANCHOR_DOWN = Icon.column(Icon.DefaultIcons.ARROW_DOWN, Icon.DefaultIcons.ANCHOR)
            .offsetY(-10);

    public WindlassIconRenderer(final IconRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected Icon getCurrentIcon(final WindlassSwitchEntity entity, final ItemStack heldStack,
            final float partialTick) {
        if (entity.getRootVehicle() instanceof AbstractVehicle) {
            if (entity.getSwitched()) {
                return ANCHOR_UP;
            } else {
                return ANCHOR_DOWN;
            }
        }
        return Icon.NONE;
    }
}