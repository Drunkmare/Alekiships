package com.alekiponi.alekiships.compat.waila;

import com.alekiponi.alekiships.common.entity.vehiclehelper.ConstructionEntity;
import com.alekiponi.alekiships.common.entity.compartment.BlockCompartmentEntity;
import com.alekiponi.alekiships.common.entity.compartment.vanilla.AbstractFurnaceCompartmentEntity;
import com.alekiponi.alekiships.common.entity.compartment.vanilla.BrewingStandCompartmentEntity;
import com.alekiponi.alekiships.common.entity.compartment.vanilla.JukeboxCompartmentEntity;
import com.alekiponi.alekiships.common.entity.compartment.vanilla.NoteBlockCompartmentEntity;
import com.alekiponi.alekiships.compat.waila.compartment.*;
import com.alekiponi.alekiships.compat.waila.compartment.vehicle.ConstructionEntityProvider;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class JadeIntegration implements IWailaPlugin {

    @Override
    public void register(final IWailaCommonRegistration registry) {
        registry.registerEntityDataProvider(JukeboxCompartmentProvider.INSTANCE, JukeboxCompartmentEntity.class);
        registry.registerEntityDataProvider(FurnaceCompartmentProvider.INSTANCE,
                AbstractFurnaceCompartmentEntity.class);
        registry.registerEntityDataProvider(BrewingStandCompartmentProvider.INSTANCE,
                BrewingStandCompartmentEntity.class);
    }

    @Override
    public void registerClient(final IWailaClientRegistration registry) {
        registry.registerEntityComponent(ConstructionEntityProvider.INSTANCE, ConstructionEntity.class);

        registry.registerEntityComponent(JukeboxCompartmentProvider.INSTANCE, JukeboxCompartmentEntity.class);
        registry.registerEntityComponent(NoteBlockCompartmentProvider.INSTANCE, NoteBlockCompartmentEntity.class);
        registry.registerEntityComponent(FurnaceCompartmentProvider.INSTANCE, AbstractFurnaceCompartmentEntity.class);
        registry.registerEntityComponent(BrewingStandCompartmentProvider.INSTANCE, BrewingStandCompartmentEntity.class);

        // Block compartments
        registry.registerEntityComponent(BlockCompartmentProvider.INSTANCE, BlockCompartmentEntity.class);
        registry.registerEntityComponent(BlockCompartmentProvider.INSTANCE, AbstractFurnaceCompartmentEntity.class);
        registry.registerEntityComponent(BlockCompartmentProvider.INSTANCE, BrewingStandCompartmentEntity.class);
    }
}