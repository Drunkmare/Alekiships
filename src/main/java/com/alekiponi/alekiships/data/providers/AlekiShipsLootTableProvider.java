package com.alekiponi.alekiships.data.providers;

import com.alekiponi.alekiships.data.loot.AlekiShipsBlockLootTables;
import com.alekiponi.alekiships.data.loot.AlekiShipsEntityLootTables;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.List;
import java.util.Set;

public final class AlekiShipsLootTableProvider {

    public static LootTableProvider create(final PackOutput packOutput) {
        return new LootTableProvider(packOutput, Set.of(),
                List.of(new LootTableProvider.SubProviderEntry(AlekiShipsEntityLootTables::new,
                                LootContextParamSets.ENTITY),
                        new LootTableProvider.SubProviderEntry(AlekiShipsBlockLootTables::new,
                                LootContextParamSets.BLOCK)));
    }
}