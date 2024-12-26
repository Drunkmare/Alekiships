package com.alekiponi.alekiships.data.providers.tags;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.util.AlekiShipsTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class AlekiShipsFluidTagsProvider extends FluidTagsProvider {

    public AlekiShipsFluidTagsProvider(final PackOutput output, final CompletableFuture<HolderLookup.Provider> provider,
            @Nullable final ExistingFileHelper existingFileHelper) {
        super(output, provider, AlekiShips.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(final HolderLookup.Provider provider) {
        this.tag(AlekiShipsTags.Fluids.PAINT_REMOVER).add(Fluids.WATER, Fluids.FLOWING_WATER);
    }
}