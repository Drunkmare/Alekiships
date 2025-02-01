package com.alekiponi.alekiships.common.entity.vehicle;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.common.AlekiShipsRegistries;
import com.alekiponi.alekiships.common.entity.ConstructionSloopInputs;

import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public final class ConstructionSloopVariants {

    public static final ResourceKey<ConstructionSloopVariant> OAK = createKey("oak");
    public static final ResourceKey<ConstructionSloopVariant> SPRUCE = createKey("spruce");
    public static final ResourceKey<ConstructionSloopVariant> BIRCH = createKey("birch");
    public static final ResourceKey<ConstructionSloopVariant> ACACIA = createKey("acacia");
    public static final ResourceKey<ConstructionSloopVariant> CHERRY = createKey("cherry");
    public static final ResourceKey<ConstructionSloopVariant> JUNGLE = createKey("jungle");
    public static final ResourceKey<ConstructionSloopVariant> DARK_OAK = createKey("dark_oak");
    public static final ResourceKey<ConstructionSloopVariant> CRIMSON = createKey("crimson");
    public static final ResourceKey<ConstructionSloopVariant> WARPED = createKey("warped");
    public static final ResourceKey<ConstructionSloopVariant> MANGROVE = createKey("mangrove");
    public static final ResourceKey<ConstructionSloopVariant> BAMBOO = createKey("bamboo");
    public static final ResourceKey<ConstructionSloopVariant> DEFAULT = OAK;

    private static ResourceKey<ConstructionSloopVariant> createKey(final String name) {
        return ResourceKey.create(AlekiShipsRegistries.CONSTRUCTION_SLOOP_VARIANT, AlekiShips.location(name));
    }

    private static ResourceLocation texture(final String wood) {
        return AlekiShips.location("entity/watercraft/sloop_construction/" + wood);
    }

    public static void bootstrapOverworld(final BootstrapContext<ConstructionSloopVariant> context) {
        final var constructionInputLookup = context.lookup(AlekiShipsRegistries.CONSTRUCTION_SLOOP_INPUT);

        context.register(OAK, ConstructionSloopVariant.builder()
                .texture(texture("oak"))
                .constructionInput(constructionInputLookup.getOrThrow(ConstructionSloopInputs.OAK))
                .build());

        context.register(SPRUCE, ConstructionSloopVariant.builder()
                .texture(texture("spruce"))
                .constructionInput(constructionInputLookup.getOrThrow(ConstructionSloopInputs.SPRUCE))
                .build());

        context.register(BIRCH, ConstructionSloopVariant.builder()
                .texture(texture("birch"))
                .constructionInput(constructionInputLookup.getOrThrow(ConstructionSloopInputs.BIRCH))
                .build());

        context.register(ACACIA, ConstructionSloopVariant.builder()
                .texture(texture("acacia"))
                .constructionInput(constructionInputLookup.getOrThrow(ConstructionSloopInputs.ACACIA))
                .build());

        context.register(CHERRY, ConstructionSloopVariant.builder()
                .texture(texture("cherry"))
                .constructionInput(constructionInputLookup.getOrThrow(ConstructionSloopInputs.CHERRY))
                .build());

        context.register(JUNGLE, ConstructionSloopVariant.builder()
                .texture(texture("jungle"))
                .constructionInput(constructionInputLookup.getOrThrow(ConstructionSloopInputs.JUNGLE))
                .build());

        context.register(DARK_OAK, ConstructionSloopVariant.builder()
                .texture(texture("dark_oak"))
                .constructionInput(constructionInputLookup.getOrThrow(ConstructionSloopInputs.DARK_OAK))
                .build());

        context.register(MANGROVE, ConstructionSloopVariant.builder()
                .texture(texture("mangrove"))
                .constructionInput(constructionInputLookup.getOrThrow(ConstructionSloopInputs.MANGROVE))
                .build());

        context.register(BAMBOO, ConstructionSloopVariant.builder()
                .texture(texture("bamboo"))
                .constructionInput(constructionInputLookup.getOrThrow(ConstructionSloopInputs.BAMBOO))
                .build());
    }

    public static void bootstrapNether(final BootstrapContext<ConstructionSloopVariant> context) {
        final var constructionInputLookup = context.lookup(AlekiShipsRegistries.CONSTRUCTION_SLOOP_INPUT);

        context.register(CRIMSON, ConstructionSloopVariant.builder()
                .texture(texture("crimson"))
                .constructionInput(constructionInputLookup.getOrThrow(ConstructionSloopInputs.CRIMSON))
                .build());

        context.register(WARPED, ConstructionSloopVariant.builder()
                .texture(texture("warped"))
                .constructionInput(constructionInputLookup.getOrThrow(ConstructionSloopInputs.WARPED))
                .build());
    }
}