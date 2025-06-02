package com.alekiponi.alekiships.client.render.icon.compartment;

import com.alekiponi.alekiships.client.icon.Icon;
import com.alekiponi.alekiships.client.render.icon.IconRendererProvider;
import com.alekiponi.alekiships.client.render.icon.SingleIconRenderer;
import com.alekiponi.alekiships.common.compartment.CompartmentPlaceable;
import com.alekiponi.alekiships.common.entity.compartment.EmptyCompartmentEntity;
import com.alekiponi.alekiships.common.item.AlekiShipsItems;

import net.minecraft.world.item.ItemStack;

public class EmptyCompartmentIconRenderer extends SingleIconRenderer<EmptyCompartmentEntity> {

    public EmptyCompartmentIconRenderer(final IconRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected Icon getCurrentIcon(final EmptyCompartmentEntity entity, final ItemStack heldStack,
            final float partialTick) {
        if (entity.getTrueVehicle() == null || entity.isVehicle()) return Icon.NONE;

        if (entity.getTrueVehicle().getPilotCompartment() != null && entity.getTrueVehicle()
                .getPilotCompartment()
                .is(entity)) {

            if (entity.getTrueVehicle().pilotCompartmentAcceptsNonPlayers() && CompartmentPlaceable.fromStack(heldStack)
                    .isPresent()) {
                return Icon.row(Icon.DefaultIcons.BLOCK, Icon.DefaultIcons.HELM);
            }

            return Icon.DefaultIcons.HELM;
        }

        if (CompartmentPlaceable.fromStack(heldStack).isPresent() || heldStack.is(AlekiShipsItems.CANNON.get())) {
            return Icon.DefaultIcons.BLOCK;
        }

        if (!entity.canAddOnlyBLocks()) {
            return Icon.DefaultIcons.SEAT;
        }

        return Icon.NONE;
    }
}