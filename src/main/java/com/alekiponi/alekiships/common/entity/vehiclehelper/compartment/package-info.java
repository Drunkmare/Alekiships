/**
 * The core api surface for Compartments.
 * Contains interfaces like:
 * <p>
 * {@link com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.BlockCompartment}
 * <p>
 * {@link com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.CompartmentCloneable}
 * <p>
 * {@link com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.SimpleBlockMenuCompartment}
 * <p>
 * {@link com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.LidCompartment}
 * <p>
 * As well as helpful abstractions such as
 * <p>
 * {@link com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.BlockCompartmentEntity}
 * <p>
 * {@link com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.ContainerCompartmentEntity}
 * <p>
 * {@link com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.SimpleBlockMenuCompartmentEntity}
 * <p>
 * All provided vanilla compartments are found under the sub-package
 * {@link com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.vanilla}. These can be useful if the modded
 * block you are writing a compartment for extends a vanilla block such as the Furnace. If it is simply a re-texture
 * you can simply register the pre-existing compartment type to your custom item.
 */
@ParametersAreNonnullByDefault @MethodsReturnNonnullByDefault @FieldsAreNonnullByDefault
package com.alekiponi.alekiships.common.entity.vehiclehelper.compartment;

import net.minecraft.FieldsAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;

import javax.annotation.ParametersAreNonnullByDefault;