package com.alekiponi.alekiships.client.render.icon.vehiclehelper;

import com.alekiponi.alekiships.client.icon.Icon;
import com.alekiponi.alekiships.client.render.icon.IconRendererProvider;
import com.alekiponi.alekiships.client.render.icon.SingleIconRenderer;
import com.alekiponi.alekiships.common.entity.vehicle.AbstractVehicle;
import com.alekiponi.alekiships.common.entity.vehiclehelper.SailSwitchEntity;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import net.neoforged.neoforge.common.Tags;

public class SailSwitchIconRenderer extends SingleIconRenderer<SailSwitchEntity> {

    private static final Icon SAIL_UP = Icon.column(Icon.DefaultIcons.SAIL, Icon.DefaultIcons.ARROW_UP);
    private static final Icon SAIL_DOWN = Icon.column(Icon.DefaultIcons.ARROW_DOWN, Icon.DefaultIcons.SAIL)
            .offsetY(-10);

    public SailSwitchIconRenderer(final IconRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected Icon getCurrentIcon(final SailSwitchEntity entity, final ItemStack heldStack, final float partialTick) {
        if (heldStack.is(Tags.Items.DYES) || heldStack.is(Items.WATER_BUCKET)) {
            // TODO don't show this when you're holding the same dye color that the sail already is
            return Icon.DefaultIcons.BRUSH;
        }

        if (entity.getRootVehicle() instanceof AbstractVehicle) {
            if (entity.getSwitched()) {
                return SAIL_UP;
            } else {
                return SAIL_DOWN;
            }
        }
        return Icon.NONE;
    }
}