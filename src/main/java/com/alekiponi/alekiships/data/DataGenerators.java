package com.alekiponi.alekiships.data;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.data.providers.AlekiShipsLootTableProvider;
import com.alekiponi.alekiships.data.providers.tags.AlekiShipsBlockTagsProvider;
import com.alekiponi.alekiships.data.providers.tags.AlekiShipsItemTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber(modid = AlekiShips.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
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
        generator.addProvider(event.includeServer(), AlekiShipsLootTableProvider.create(packOutput));
    }
}