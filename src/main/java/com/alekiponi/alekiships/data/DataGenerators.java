package com.alekiponi.alekiships.data;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.data.providers.AlekiShipsLootTableProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AlekiShips.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class DataGenerators {

    @SubscribeEvent
    public static void gatherData(final GatherDataEvent event) {
        final DataGenerator generator = event.getGenerator();
        final PackOutput packOutput = generator.getPackOutput();

        generator.addProvider(event.includeServer(), AlekiShipsLootTableProvider.create(packOutput));
    }
}