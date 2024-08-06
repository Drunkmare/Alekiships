package com.alekiponi.alekiships.data.providers;

import com.alekiponi.alekiships.data.advancements.AlekiShipsAdvancementGenerator;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeAdvancementProvider;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class AlekiShipsAdvancementsProvider {

    public static ForgeAdvancementProvider create(final PackOutput packOutput,
            final CompletableFuture<HolderLookup.Provider> registries, final ExistingFileHelper existingFileHelper) {
        return new ForgeAdvancementProvider(packOutput, registries, existingFileHelper,
                List.of(new AlekiShipsAdvancementGenerator()));
    }
}