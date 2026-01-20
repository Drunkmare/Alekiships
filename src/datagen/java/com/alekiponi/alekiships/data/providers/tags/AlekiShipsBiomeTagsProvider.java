package com.alekiponi.alekiships.data.providers.tags;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.util.AlekiShipsTags;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.minecraft.tags.BiomeTags;

import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;
import org.jetbrains.annotations.Nullable;

public class AlekiShipsBiomeTagsProvider extends BiomeTagsProvider {
    public AlekiShipsBiomeTagsProvider(final PackOutput output, final CompletableFuture<HolderLookup.Provider> provider,
            @Nullable final ExistingFileHelper existingFileHelper) {
        super(output, provider, AlekiShips.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(final HolderLookup.Provider provider) {
        this.tag(AlekiShipsTags.Biomes.HAS_UNFINISHED_ROWBOAT_STRUCTURE)
                .addTag(BiomeTags.IS_BEACH)
                .addTag(BiomeTags.IS_RIVER);
        this.tag(AlekiShipsTags.Biomes.HAS_UNFINISHED_SLOOP_STRUCTURE).addTag(BiomeTags.IS_BEACH);
    }
}