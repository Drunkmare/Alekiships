package com.alekiponi.alekiships.data;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.data.providers.AlekiShipsAdvancementsProvider;
import com.alekiponi.alekiships.data.providers.AlekiShipsLanguageProvider;
import com.alekiponi.alekiships.data.providers.AlekiShipsLootTableProvider;
import com.alekiponi.alekiships.data.providers.AlekiShipsRecipeProvider;
import com.alekiponi.alekiships.data.providers.models.AlekiShipsBlockStateProvider;
import com.alekiponi.alekiships.data.providers.models.AlekiShipsItemModelProvider;
import com.alekiponi.alekiships.data.providers.tags.AlekiShipsBlockTagsProvider;
import com.alekiponi.alekiships.data.providers.tags.AlekiShipsEntityTypeTagsProvider;
import com.alekiponi.alekiships.data.providers.tags.AlekiShipsItemTagsProvider;
import com.alekiponi.alekiships.data.providers.tags.AlekiShipsStructureTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = AlekiShips.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public final class DataGenerators {

    @SubscribeEvent
    public static void gatherData(final GatherDataEvent event) {
        final DataGenerator generator = event.getGenerator();
        final PackOutput packOutput = generator.getPackOutput();
        final CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        final ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        final AlekiShipsBlockTagsProvider blockTags = new AlekiShipsBlockTagsProvider(packOutput, lookupProvider,
                existingFileHelper);
        generator.addProvider(event.includeServer(), blockTags);
        generator.addProvider(event.includeServer(),
                new AlekiShipsItemTagsProvider(packOutput, lookupProvider, blockTags.contentsGetter(),
                        existingFileHelper));
        generator.addProvider(event.includeServer(),
                new AlekiShipsEntityTypeTagsProvider(packOutput, lookupProvider, existingFileHelper));
        generator.addProvider(event.includeServer(),
                new AlekiShipsStructureTagProvider(packOutput, lookupProvider, existingFileHelper));
        generator.addProvider(event.includeServer(), new AlekiShipsRecipeProvider(packOutput, lookupProvider));
        generator.addProvider(event.includeServer(), AlekiShipsLootTableProvider.create(packOutput, lookupProvider));
        generator.addProvider(event.includeServer(),
                AlekiShipsAdvancementsProvider.create(packOutput, lookupProvider, existingFileHelper));

        generator.addProvider(event.includeClient(), new AlekiShipsLanguageProvider(packOutput));
        generator.addProvider(event.includeClient(), new AlekiShipsItemModelProvider(packOutput, existingFileHelper));
        generator.addProvider(event.includeClient(), new AlekiShipsBlockStateProvider(packOutput, existingFileHelper));
    }
}