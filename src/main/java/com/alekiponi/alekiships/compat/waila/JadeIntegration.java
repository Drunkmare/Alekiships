package com.alekiponi.alekiships.compat.waila;

import com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.vanilla.JukeboxCompartmentEntity;
import com.alekiponi.alekiships.compat.waila.compartment.JukeboxCompartmentProvider;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class JadeIntegration implements IWailaPlugin {

    @Override
    public void register(final IWailaCommonRegistration registry) {
        registry.registerEntityDataProvider(JukeboxCompartmentProvider.INSTANCE, JukeboxCompartmentEntity.class);
    }

    @Override
    public void registerClient(final IWailaClientRegistration registry) {
        registry.registerEntityComponent(JukeboxCompartmentProvider.INSTANCE, JukeboxCompartmentEntity.class);
    }
}