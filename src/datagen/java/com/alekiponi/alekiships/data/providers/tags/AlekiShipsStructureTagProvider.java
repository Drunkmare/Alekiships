package com.alekiponi.alekiships.data.providers.tags;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.util.AlekiShipsTags;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.StructureTagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.Structure;

import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;
import org.jetbrains.annotations.Nullable;

public class AlekiShipsStructureTagProvider extends StructureTagsProvider {

    public AlekiShipsStructureTagProvider(final PackOutput packOutput,
            final CompletableFuture<HolderLookup.Provider> lookupProvider,
            @Nullable final ExistingFileHelper existingFileHelper) {
        super(packOutput, lookupProvider, AlekiShips.MOD_ID, existingFileHelper);
    }

    private static ResourceKey<Structure> createKey(final String name) {
        return ResourceKey.create(Registries.STRUCTURE, ResourceLocation.fromNamespaceAndPath(AlekiShips.MOD_ID, name));
    }

    @Override
    protected void addTags(final HolderLookup.Provider provider) {
        this.tag(AlekiShipsTags.Structures.UNFINISHED_SLOOP)
                .add(createKey("unfinished_sloop_birch"), createKey("unfinished_sloop_cherry"),
                        createKey("unfinished_sloop_dark_oak"), createKey("unfinished_sloop_oak"),
                        createKey("unfinished_sloop_spruce"));

        this.tag(AlekiShipsTags.Structures.UNFINISHED_ROWBOAT)
                .add(createKey("unfinished_rowboat_birch"), createKey("unfinished_rowboat_cherry"),
                        createKey("unfinished_rowboat_dark_oak"), createKey("unfinished_rowboat_oak"),
                        createKey("unfinished_rowboat_spruce"));
    }
}