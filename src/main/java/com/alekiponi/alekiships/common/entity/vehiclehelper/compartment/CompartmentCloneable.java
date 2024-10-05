package com.alekiponi.alekiships.common.entity.vehiclehelper.compartment;

import com.alekiponi.alekiships.common.entity.vehiclehelper.CompartmentType;
import com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.vanilla.JukeboxCompartmentEntity;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.world.item.ItemStack;

/**
 * This interface allows compartment entities to be cloneable via ctrl + middle click similar to block entities
 */
public interface CompartmentCloneable {

    /**
     * The needed initialization for {@link ContainerCompartmentEntity}s. Exposed as a post-initialization step
     * to avoid confusing issues caused by invoking {@link #applyComponentsFromItemStack(ItemStack)} before fields in a child
     * class are initialized.
     *
     * @param compartment The compartment entity
     * @param itemStack   The {@link ItemStack} used to initialize the compartment
     */
    static <E extends AbstractCompartmentEntity & CompartmentCloneable> CompartmentType.InitializationResult initialize(
            final E compartment, final ItemStack itemStack) {
        compartment.applyComponentsFromItemStack(itemStack);
        return CompartmentType.InitializationResult.success();
    }

    /**
     * Called from {@link CompartmentCloneable#initialize(AbstractCompartmentEntity, ItemStack)} to load values from
     * the stacks components
     */
    void applyComponentsFromItemStack(final ItemStack itemStack);

    /**
     * Collect the cloned {@link ItemStack} components. In most cases this will be implemented for you by
     * {@link ContainerCompartmentEntity#collectComponents()} though {@link JukeboxCompartmentEntity#collectComponents()}
     * is a good example for very strict implementation.
     */
    DataComponentMap collectComponents();
}