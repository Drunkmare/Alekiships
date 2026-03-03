package com.alekiponi.alekiships.data.worldgen;

import com.mojang.datafixers.util.Pair;

import com.alekiponi.alekiships.AlekiShips;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

public final class AlekiShipsTemplatePools {

    public static final ResourceKey<StructureTemplatePool> UNFINISHED_ROWBOAT = key("unfinished_rowboat");
    public static final ResourceKey<StructureTemplatePool> UNFINISHED_SLOOP = key("unfinished_sloop");

    private static ResourceKey<StructureTemplatePool> key(final String id) {
        return ResourceKey.create(Registries.TEMPLATE_POOL, AlekiShips.location(id));
    }

    public static void bootstrap(final BootstrapContext<StructureTemplatePool> context) {
        final var templatePools = context.lookup(Registries.TEMPLATE_POOL);
        final var empty = templatePools.getOrThrow(Pools.EMPTY);

        final var unfinishedRowboat = AlekiShips.location("unfinished_rowboat");
        context.register(UNFINISHED_ROWBOAT, new StructureTemplatePool(empty,
                StructureVariant.createTemplates(TemplateFactory.usingBaseName(unfinishedRowboat)),
                StructureTemplatePool.Projection.RIGID));

        final var unfinishedSloop = AlekiShips.location("unfinished_sloop");
        context.register(UNFINISHED_SLOOP, new StructureTemplatePool(empty,
                StructureVariant.createTemplates(TemplateFactory.usingBaseName(unfinishedSloop)),
                StructureTemplatePool.Projection.RIGID));

        Arrays.stream(StructureVariant.values())
                .forEach(variant -> {
                    final var templateFactory = TemplateFactory.usingBaseName(
                            AlekiShips.location("unfinished_sloop_hut"));
                    context.register(key("unfinished_sloop_hut/" + variant.name), new StructureTemplatePool(empty,
                            List.of(templateFactory.apply(variant.name, variant.weight)),
                            StructureTemplatePool.Projection.RIGID));
                });
    }

    private static Pair<Function<StructureTemplatePool.Projection, ? extends StructurePoolElement>, Integer> createTemplate(
            final String id, final int weight) {
        return Pair.of(StructurePoolElement.single(id), weight);
    }

    @AllArgsConstructor
    @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
    private enum StructureVariant {
        BIRCH("birch", 50),
        CHERRY("cherry", 1),
        DARK_OAK("dark_oak", 50),
        OAK("oak", 50),
        SPRUCE("spruce", 50);

        String name;
        int weight;

        private static List<Pair<Function<StructureTemplatePool.Projection, ? extends StructurePoolElement>, Integer>> createTemplates(
                final TemplateFactory templateFactory) {
            return Arrays.stream(StructureVariant.values())
                    .map(structureVariant -> templateFactory.apply(structureVariant.name, structureVariant.weight))
                    .toList();
        }
    }

    interface TemplateFactory extends BiFunction<String, Integer, Pair<Function<StructureTemplatePool.Projection, ? extends StructurePoolElement>, Integer>> {
        static TemplateFactory usingBaseName(final ResourceLocation baseName) {
            return (name, weight) -> createTemplate(baseName.withSuffix("/" + name).toString(), weight);
        }
    }
}