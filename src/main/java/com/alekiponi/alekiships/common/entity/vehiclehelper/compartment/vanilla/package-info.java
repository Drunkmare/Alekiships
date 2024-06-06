/**
 * Everything in this package and any subpackage is related to vanilla compartments. This code is unlikely to change
 * outside of bug fixes however in contrast to what's provided in
 * {@link com.alekiponi.alekiships.common.entity.vehiclehelper.compartment} breaking changes may occur without warning
 * or documentation. Certain compartment types such as
 * {@link com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.vanilla.ChestCompartmentEntity} or
 * {@link com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.vanilla.AbstractFurnaceCompartmentEntity}
 * (and provided subclasses) are expected to be much more commonly used and can therefore be considered stable. Any
 * public facing changes should be present in change logs.
 */
@ParametersAreNonnullByDefault @MethodsReturnNonnullByDefault @FieldsAreNonnullByDefault
package com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.vanilla;

import net.minecraft.FieldsAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;

import javax.annotation.ParametersAreNonnullByDefault;