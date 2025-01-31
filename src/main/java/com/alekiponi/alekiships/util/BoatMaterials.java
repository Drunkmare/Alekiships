package com.alekiponi.alekiships.util;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.common.AlekiShipsRegistries;

import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public final class BoatMaterials {
    public static final ResourceKey<DynamicBoatMaterial> OAK = createKey("oak");
    public static final ResourceKey<DynamicBoatMaterial> SPRUCE = createKey("spruce");
    public static final ResourceKey<DynamicBoatMaterial> BIRCH = createKey("birch");
    public static final ResourceKey<DynamicBoatMaterial> ACACIA = createKey("acacia");
    public static final ResourceKey<DynamicBoatMaterial> CHERRY = createKey("cherry");
    public static final ResourceKey<DynamicBoatMaterial> JUNGLE = createKey("jungle");
    public static final ResourceKey<DynamicBoatMaterial> DARK_OAK = createKey("dark_oak");
    public static final ResourceKey<DynamicBoatMaterial> CRIMSON = createKey("crimson");
    public static final ResourceKey<DynamicBoatMaterial> WARPED = createKey("warped");
    public static final ResourceKey<DynamicBoatMaterial> MANGROVE = createKey("mangrove");
    public static final ResourceKey<DynamicBoatMaterial> BAMBOO = createKey("bamboo");

    private static ResourceKey<DynamicBoatMaterial> createKey(final String name) {
        return ResourceKey.create(AlekiShipsRegistries.BOAT_MATERIAL, AlekiShips.location(name));
    }

    public static void bootstrapOverworld(final BootstrapContext<DynamicBoatMaterial> context) {
        context.register(OAK, createWaterMaterial("oak"));
        context.register(SPRUCE, createWaterMaterial("spruce"));
        context.register(BIRCH, createWaterMaterial("birch"));
        context.register(ACACIA, createWaterMaterial("acacia"));
        context.register(CHERRY, createWaterMaterial("cherry"));
        context.register(JUNGLE, createWaterMaterial("jungle"));
        context.register(DARK_OAK, createWaterMaterial("dark_oak"));
        context.register(MANGROVE, createWaterMaterial("mangrove"));
        context.register(BAMBOO, createWaterMaterial("bamboo"));
    }

    public static void bootstrapNether(final BootstrapContext<DynamicBoatMaterial> context) {
        context.register(CRIMSON, createLavaMaterial("crimson"));
        context.register(WARPED, createLavaMaterial("warped"));
    }

    private static DynamicBoatMaterial createWaterMaterial(final String name) {
        return material(name(AlekiShips.location(name))).build();
    }

    private static DynamicBoatMaterial createLavaMaterial(final String name) {
        return material(name(AlekiShips.location(name))).withstandsLava(true)
                .build();
    }

    private static DynamicBoatMaterial.DynamicBoatMaterialBuilder material(final Component name) {
        return DynamicBoatMaterial.builder().name(name);
    }

    private static MutableComponent name(final ResourceLocation registryName) {
        return Component.translatable(DynamicBoatMaterial.getDescriptionId(registryName));
    }
}