package com.alekiponi.alekiships.data.providers.tags;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.common.block.AlekiShipsBlocks;
import com.alekiponi.alekiships.util.AlekiShipsTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class AlekiShipsBlockTagsProvider extends BlockTagsProvider {

    public AlekiShipsBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
            @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, AlekiShips.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(final HolderLookup.Provider provider) {
        {
            final var tag = this.tag(AlekiShipsTags.Blocks.WOODEN_WATERCRAFT_FRAMES);
            AlekiShipsBlocks.WOODEN_BOAT_FRAME_ANGLED.values().stream().map(RegistryObject::get).forEach(tag::add);
            AlekiShipsBlocks.WOODEN_BOAT_FRAME_FLAT.values().stream().map(RegistryObject::get).forEach(tag::add);
        }

        this.tag(AlekiShipsTags.Blocks.PLANTS_THAT_GET_MOWED).add(Blocks.KELP);

        //Ice types
        this.tag(AlekiShipsTags.Blocks.BREAKABLE_ICE).add(Blocks.ICE);
        this.tag(AlekiShipsTags.Blocks.BREAKABLE_ICE).add(Blocks.FROSTED_ICE);
        this.tag(AlekiShipsTags.Blocks.BREAKABLE_ICE).add(Blocks.PACKED_ICE);
        this.tag(AlekiShipsTags.Blocks.BREAKABLE_ICE).add(Blocks.BLUE_ICE);

        // Vanilla mining tags
        this.tag(BlockTags.MINEABLE_WITH_AXE).addTag(AlekiShipsTags.Blocks.WOODEN_WATERCRAFT_FRAMES);

        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(AlekiShipsBlocks.OARLOCK.get(), AlekiShipsBlocks.CLEAT.get());
    }
}