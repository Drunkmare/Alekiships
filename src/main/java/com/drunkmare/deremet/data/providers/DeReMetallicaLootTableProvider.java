package com.drunkmare.deremet.data.providers;

import com.drunkmare.deremet.data.loot.DeReMetallicaBlockLootTables;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.List;
import java.util.Set;

public final class DeReMetallicaLootTableProvider {

    public static LootTableProvider create(final PackOutput packOutput) {
        return new LootTableProvider(packOutput, Set.of(),
                List.of(new LootTableProvider.SubProviderEntry(DeReMetallicaBlockLootTables::new,
                        LootContextParamSets.BLOCK)));
    }
}
