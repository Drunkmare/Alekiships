package com.alekiponi.alekiships.data.worldgen;

import com.alekiponi.alekiships.AlekiShips;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;

import java.util.List;

public final class AlekiShipsStructureSets {

    public static final ResourceKey<StructureSet> UNFINISHED_ROWBOAT = key("unfinished_rowboat");
    public static final ResourceKey<StructureSet> UNFINISHED_SLOOP = key("unfinished_sloop");

    private static ResourceKey<StructureSet> key(final String id) {
        return ResourceKey.create(Registries.STRUCTURE_SET, AlekiShips.location(id));
    }

    public static void bootstrap(final BootstrapContext<StructureSet> context) {
        final var structures = context.lookup(Registries.STRUCTURE);
        final var biomes = context.lookup(Registries.BIOME);

        context.register(UNFINISHED_ROWBOAT, new StructureSet(
                List.of(StructureSet.entry(structures.getOrThrow(AlekiShipsStructures.UNFINISHED_ROWBOAT))),
                new RandomSpreadStructurePlacement(22, 10, RandomSpreadType.LINEAR, 1642136474)));

        context.register(UNFINISHED_SLOOP, new StructureSet(
                List.of(StructureSet.entry(structures.getOrThrow(AlekiShipsStructures.UNFINISHED_SLOOP))),
                new RandomSpreadStructurePlacement(24, 10, RandomSpreadType.LINEAR, 1644568644)));
    }
}