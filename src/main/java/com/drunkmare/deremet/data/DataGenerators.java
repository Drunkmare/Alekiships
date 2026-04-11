package com.drunkmare.deremet.data;

import com.drunkmare.deremet.DeReMetallica;
import com.drunkmare.deremet.data.loot.DeReMetallicaBlockLootTables;
import com.drunkmare.deremet.data.providers.DeReMetallicaLanguageProvider;
import com.drunkmare.deremet.data.providers.DeReMetallicaLootTableProvider;
import com.drunkmare.deremet.data.providers.models.DeReMetallicaBlockStateProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DeReMetallica.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class DataGenerators {

    @SubscribeEvent
    public static void gatherData(final GatherDataEvent event) {
        final DataGenerator generator = event.getGenerator();
        final PackOutput packOutput = generator.getPackOutput();
        final ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        generator.addProvider(event.includeServer(), DeReMetallicaLootTableProvider.create(packOutput));

        generator.addProvider(event.includeClient(), new DeReMetallicaLanguageProvider(packOutput));
        generator.addProvider(event.includeClient(),
                new DeReMetallicaBlockStateProvider(packOutput, existingFileHelper));
    }
}
