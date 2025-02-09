package com.alekiponi.alekiships.data.providers;

import com.alekiponi.alekiships.data.loot.AlekiShipsBlockLootTables;
import com.alekiponi.alekiships.data.loot.AlekiShipsEntityLootTables;
import com.alekiponi.alekiships.data.loot.AlekiShipsStructureLoot;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public final class AlekiShipsLootTableProvider {

    public static LootTableProvider create(final PackOutput packOutput,
            final CompletableFuture<HolderLookup.Provider> lookupProvider) {
        return new LootTableProvider(packOutput, Set.of(),
                List.of(new LootTableProvider.SubProviderEntry(AlekiShipsEntityLootTables::new,
                                LootContextParamSets.ENTITY),
                        new LootTableProvider.SubProviderEntry(AlekiShipsBlockLootTables::new,
                                LootContextParamSets.BLOCK),
                        new LootTableProvider.SubProviderEntry(AlekiShipsStructureLoot::new,
                                LootContextParamSets.CHEST)), lookupProvider);
    }
}