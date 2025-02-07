package com.alekiponi.alekiships.common.entity.compartment;

import com.alekiponi.alekiships.client.render.entity.vehicle.vehiclehelper.ChestCompartmentRenderer;

/**
 * Required by {@link ChestCompartmentRenderer} for animating the chest model
 */
public interface LidCompartment {

    /**
     * The amount this compartment is open represented as a value from 0 to 1
     *
     * @param partialTicks The partial ticks
     * @return How open this compartment is
     */
    float getOpenNess(final float partialTicks);
}