package com.alekiponi.alekiships.common.entity.vehiclehelper.compartment;

public interface LightEmittingCompartment {

    /**
     * @return If this compartment is emitting light
     */
    boolean isEmitting();

    /**
     * @return The light emitted by this compartment
     * @apiNote Normal ranges are between 0 and 15
     */
    int getLightEmission();
}