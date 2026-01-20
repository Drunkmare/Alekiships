package com.alekiponi.alekiships.data.worldgen;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.util.AlekiShipsTags;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.heightproviders.ConstantHeight;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;

public final class AlekiShipsStructures {

    public static final ResourceKey<Structure> UNFINISHED_ROWBOAT = key("unfinished_rowboat");
    public static final ResourceKey<Structure> UNFINISHED_SLOOP = key("unfinished_sloop");

    private static ResourceKey<Structure> key(final String id) {
        return ResourceKey.create(Registries.STRUCTURE, AlekiShips.location(id));
    }

    public static void bootstrap(final BootstrapContext<Structure> context) {
        final var biomes = context.lookup(Registries.BIOME);
        final var structureTemplates = context.lookup(Registries.TEMPLATE_POOL);

        context.register(UNFINISHED_ROWBOAT, new JigsawStructure(new Structure.StructureSettings.Builder(
                biomes.getOrThrow(AlekiShipsTags.Biomes.HAS_UNFINISHED_ROWBOAT_STRUCTURE)).terrainAdapation(
                        TerrainAdjustment.BEARD_THIN).generationStep(GenerationStep.Decoration.SURFACE_STRUCTURES)
                .build(), structureTemplates.getOrThrow(AlekiShipsTemplatePools.UNFINISHED_ROWBOAT), 1,
                ConstantHeight.ZERO, false, Heightmap.Types.WORLD_SURFACE_WG));

        context.register(UNFINISHED_SLOOP, new JigsawStructure(new Structure.StructureSettings.Builder(
                biomes.getOrThrow(AlekiShipsTags.Biomes.HAS_UNFINISHED_SLOOP_STRUCTURE)).terrainAdapation(
                        TerrainAdjustment.BEARD_THIN).generationStep(GenerationStep.Decoration.SURFACE_STRUCTURES)
                .build(), structureTemplates.getOrThrow(AlekiShipsTemplatePools.UNFINISHED_SLOOP), 2,
                ConstantHeight.ZERO, false, Heightmap.Types.WORLD_SURFACE_WG));
    }
}