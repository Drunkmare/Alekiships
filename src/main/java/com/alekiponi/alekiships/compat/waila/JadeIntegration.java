package com.alekiponi.alekiships.compat.waila;

import com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.BlockCompartmentEntity;
import com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.vanilla.AbstractFurnaceCompartmentEntity;
import com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.vanilla.BrewingStandCompartmentEntity;
import com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.vanilla.JukeboxCompartmentEntity;
import com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.vanilla.NoteBlockCompartmentEntity;
import com.alekiponi.alekiships.compat.waila.compartment.BlockCompartmentProvider;
import com.alekiponi.alekiships.compat.waila.compartment.FurnaceCompartmentProvider;
import com.alekiponi.alekiships.compat.waila.compartment.JukeboxCompartmentProvider;
import com.alekiponi.alekiships.compat.waila.compartment.NoteBlockCompartmentProvider;
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
    }

    @Override
    public void registerClient(final IWailaClientRegistration registry) {
        registry.registerEntityComponent(JukeboxCompartmentProvider.INSTANCE, JukeboxCompartmentEntity.class);
        registry.registerEntityComponent(NoteBlockCompartmentProvider.INSTANCE, NoteBlockCompartmentEntity.class);
        registry.registerEntityComponent(FurnaceCompartmentProvider.INSTANCE, AbstractFurnaceCompartmentEntity.class);

        // Block compartments
        registry.registerEntityComponent(BlockCompartmentProvider.INSTANCE, BlockCompartmentEntity.class);
        registry.registerEntityComponent(BlockCompartmentProvider.INSTANCE, AbstractFurnaceCompartmentEntity.class);
        registry.registerEntityComponent(BlockCompartmentProvider.INSTANCE, BrewingStandCompartmentEntity.class);
    }
}