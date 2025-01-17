package com.alekiponi.alekiships.data;

import weather2.Weather;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.common.AlekiShipsDataMaps;
import com.alekiponi.alekiships.common.AlekiShipsRegistries;
import com.alekiponi.alekiships.common.block.AlekiShipsBlocks;
import com.alekiponi.alekiships.common.entity.ConstructionSloopInputs;
import com.alekiponi.alekiships.common.entity.EntityInput;
import com.alekiponi.alekiships.common.entity.vehicle.ConstructionSloopVariants;
import com.alekiponi.alekiships.common.entity.vehicle.RowboatVariants;
import com.alekiponi.alekiships.common.entity.vehicle.SloopVariants;
import com.alekiponi.alekiships.common.sounds.AlekiShipsJukeboxSongs;
import com.alekiponi.alekiships.data.providers.*;
import com.alekiponi.alekiships.data.providers.models.AlekiShipsBlockStateProvider;
import com.alekiponi.alekiships.data.providers.models.AlekiShipsItemModelProvider;
import com.alekiponi.alekiships.data.providers.tags.*;
import com.alekiponi.alekiships.data.util.DataMapBuilderExtensions;
import com.alekiponi.alekiships.util.BoatFrame;
import com.alekiponi.alekiships.util.BoatMaterials;
import com.alekiponi.alekiships.util.FrameMaterial;
import com.alekiponi.alekiships.util.NetherWood;

import net.minecraft.DetectedVersion;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.metadata.PackMetadataGenerator;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.OverlayMetadataSection;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.util.InclusiveRange;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;
import net.neoforged.neoforge.common.conditions.WithConditions;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.GeneratingOverlayMetadataSection;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = AlekiShips.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public final class DataGenerators {

    @SubscribeEvent
    public static void gatherData(final GatherDataEvent event) {
        final DataGenerator generator = event.getGenerator();
        final PackOutput packOutput = generator.getPackOutput();

        generator.addProvider(true, new PackMetadataGenerator(packOutput))
                .add(PackMetadataSection.TYPE,
                        new PackMetadataSection(Component.literal("Aleki's Nifty Ships mod Resources/Data"), -1))
                .add(GeneratingOverlayMetadataSection.NEOFORGE_TYPE, new GeneratingOverlayMetadataSection(
                        List.of(new WithConditions<>(new OverlayMetadataSection.OverlayEntry(
                                new InclusiveRange<>(DetectedVersion.BUILT_IN.getPackVersion(PackType.SERVER_DATA),
                                        DetectedVersion.BUILT_IN.getPackVersion(PackType.SERVER_DATA)), Weather.MODID),
                                new ModLoadedCondition(Weather.MODID)))));

        final CompletableFuture<HolderLookup.Provider> lookupProvider = generator.addProvider(event.includeServer(),
                new DatapackBuiltinEntriesProvider(packOutput, event.getLookupProvider(), datapackEntries(),
                        Set.of(AlekiShips.MOD_ID))).getRegistryProvider();

        final ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        final AlekiShipsBlockTagsProvider blockTags = new AlekiShipsBlockTagsProvider(packOutput, lookupProvider,
                existingFileHelper);
        generator.addProvider(event.includeServer(), blockTags);
        generator.addProvider(event.includeServer(),
                new AlekiShipsItemTagsProvider(packOutput, lookupProvider, blockTags.contentsGetter(),
                        existingFileHelper));
        generator.addProvider(event.includeServer(),
                new AlekiShipsEntityTypeTagsProvider(packOutput, lookupProvider, existingFileHelper));
        generator.addProvider(event.includeServer(),
                new AlekiShipsFluidTagsProvider(packOutput, lookupProvider, existingFileHelper));
        generator.addProvider(event.includeServer(),
                new AlekiShipsStructureTagProvider(packOutput, lookupProvider, existingFileHelper));
        generator.addProvider(event.includeServer(), new AlekiShipsRecipeProvider(packOutput, lookupProvider));
        generator.addProvider(event.includeServer(), AlekiShipsLootTableProvider.create(packOutput, lookupProvider));
        generator.addProvider(event.includeServer(),
                AlekiShipsAdvancementsProvider.create(packOutput, lookupProvider, existingFileHelper));
        generator.addProvider(event.includeServer(), new AlekiShipsDataMapProvider(packOutput, lookupProvider));

        generator.addProvider(event.includeClient(), new AlekiShipsLanguageProvider(packOutput));
        generator.addProvider(event.includeClient(), new AlekiShipsItemModelProvider(packOutput, existingFileHelper));
        generator.addProvider(event.includeClient(), new AlekiShipsBlockStateProvider(packOutput, existingFileHelper));
        generator.addProvider(event.includeClient(),
                new AlekiShipsSoundDefinitionsProvider(packOutput, existingFileHelper));

        final var netherWoodsPack = generator.getBuiltinDatapack(event.includeServer(), AlekiShips.MOD_ID,
                "nether_woods");
        gatherNetherData(netherWoodsPack, lookupProvider);
    }

    private static void gatherNetherData(final DataGenerator.PackGenerator netherWoodsPack,
            final CompletableFuture<HolderLookup.Provider> builtInRegistries) {
        netherWoodsPack.addProvider(PackMetadataGenerator::new)
                .add(PackMetadataSection.TYPE,
                        new PackMetadataSection(Component.translatable(AlekiShips.NETHER_WOOD_PACK_KEY),
                                DetectedVersion.BUILT_IN.getPackVersion(PackType.SERVER_DATA), Optional.empty()));

        final var netherRegistries = netherWoodsPack.addProvider(
                        output -> new DatapackBuiltinEntriesProvider(output, builtInRegistries,
                                new RegistrySetBuilder().add(AlekiShipsRegistries.CONSTRUCTION_SLOOP_INPUT,
                                                ConstructionSloopInputs::bootstrapNether)
                                        .add(AlekiShipsRegistries.BOAT_MATERIAL, BoatMaterials::bootstrapNether)
                                        .add(AlekiShipsRegistries.FRAME_MATERIAL, FrameMaterial::bootstrapNether)
                                        .add(AlekiShipsRegistries.ROWBOAT_VARIANT, RowboatVariants::bootstrapNether)
                                        .add(AlekiShipsRegistries.SLOOP_VARIANT, SloopVariants::bootstrapNether)
                                        .add(AlekiShipsRegistries.CONSTRUCTION_SLOOP_VARIANT,
                                                ConstructionSloopVariants::bootstrapNether), Set.of(AlekiShips.MOD_ID)))
                .getRegistryProvider();
        netherWoodsPack.addProvider(output -> new DataMapProvider(output, netherRegistries) {
            @Override
            protected void gather(final HolderLookup.Provider provider) {
                final var angledFrame = this.builder(AlekiShipsDataMaps.ANGLED_BOAT_FRAME);
                final var flatFrame = this.builder(AlekiShipsDataMaps.FLAT_BOAT_FRAME);
                final var frameMaterials = provider.lookupOrThrow(AlekiShipsRegistries.FRAME_MATERIAL);
                for (final var wood : NetherWood.values()) {
                    final var material = frameMaterials.getOrThrow(wood.frameMaterialKey());
                    DataMapBuilderExtensions.add(angledFrame, wood.getPlankItem(),
                            new BoatFrame(AlekiShipsBlocks.WOODEN_BOAT_FRAME_ANGLED.get(), material));
                    DataMapBuilderExtensions.add(flatFrame, wood.getPlankItem(),
                            new BoatFrame(AlekiShipsBlocks.WOODEN_BOAT_FRAME_FLAT.get(), material));
                }
            }
        });
        netherWoodsPack.addProvider(output -> new AlekiShipsRecipeProvider(output, netherRegistries) {
            @Override
            protected void buildRecipes(final RecipeOutput recipeOutput, final HolderLookup.Provider holderLookup) {
                createRowboatRecipes(recipeOutput, holderLookup, NetherWood.values());
            }
        });
    }

    private static RegistrySetBuilder datapackEntries() {
        return new RegistrySetBuilder().add(Registries.JUKEBOX_SONG, AlekiShipsJukeboxSongs::bootstrap)
                .add(AlekiShipsRegistries.ENTITY_INPUT, EntityInput::bootstrap)
                .add(AlekiShipsRegistries.CONSTRUCTION_SLOOP_INPUT, ConstructionSloopInputs::bootstrapOverworld)
                .add(AlekiShipsRegistries.BOAT_MATERIAL, BoatMaterials::bootstrapOverworld)
                .add(AlekiShipsRegistries.FRAME_MATERIAL, FrameMaterial::bootstrapOverworld)
                .add(AlekiShipsRegistries.ROWBOAT_VARIANT, RowboatVariants::bootstrapOverworld)
                .add(AlekiShipsRegistries.SLOOP_VARIANT, SloopVariants::bootstrapOverworld)
                .add(AlekiShipsRegistries.CONSTRUCTION_SLOOP_VARIANT, ConstructionSloopVariants::bootstrapOverworld);
    }
}