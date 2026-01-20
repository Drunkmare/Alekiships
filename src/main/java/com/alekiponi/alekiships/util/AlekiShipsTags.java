package com.alekiponi.alekiships.util;

import com.alekiponi.alekiships.AlekiShips;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.material.Fluid;

public final class AlekiShipsTags {


    public static final class Blocks {
        public static final TagKey<Block> PLANTS_THAT_GET_MOWED = create("plants_that_get_mowed");

        public static final TagKey<Block> WOODEN_WATERCRAFT_FRAMES = create("wooden_watercraft_frames");

        private static TagKey<Block> create(final String id) {
            return TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(AlekiShips.MOD_ID, id));
        }
    }

    public static final class Items {
        /**
         * Tag for items which are consumed for the sloop icebreaker upgrade
         */
        public static final TagKey<Item> ICEBREAKER_UPGRADES = create("icebreaker_upgrades");

        public static TagKey<Item> create(final String id) {
            return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(AlekiShips.MOD_ID, id));
        }
    }

    public static final class Entities {
        /**
         * Vehicle helpers such as our collision entities
         */
        public static final TagKey<EntityType<?>> VEHICLE_HELPERS = create("vehicle_helpers");
        /**
         * All compartments
         */
        public static final TagKey<EntityType<?>> COMPARTMENTS = create("compartments");

        public static TagKey<EntityType<?>> create(final String id) {
            return TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(AlekiShips.MOD_ID, id));
        }
    }

    public static final class Fluids {
        /**
         * The tag containing all fluids we consider "paint removers"
         */
        public static final TagKey<Fluid> PAINT_REMOVER = create("paint_remover");

        public static TagKey<Fluid> create(final String id) {
            return TagKey.create(Registries.FLUID, ResourceLocation.fromNamespaceAndPath(AlekiShips.MOD_ID, id));
        }
    }

    public static final class Structures {

        public static final TagKey<Structure> UNFINISHED_SLOOP = create("unfinished_sloop");

        public static final TagKey<Structure> UNFINISHED_ROWBOAT = create("unfinished_rowboat");

        public static TagKey<Structure> create(final String id) {
            return TagKey.create(Registries.STRUCTURE, ResourceLocation.fromNamespaceAndPath(AlekiShips.MOD_ID, id));
        }
    }

    public static final class Biomes {
        public static final TagKey<Biome> HAS_UNFINISHED_ROWBOAT_STRUCTURE = create("has_structure/unfinished_rowboat");
        public static final TagKey<Biome> HAS_UNFINISHED_SLOOP_STRUCTURE = create("has_structure/unfinished_sloop");

        private static TagKey<Biome> create(final String id) {
            return TagKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(AlekiShips.MOD_ID, id));
        }
    }
}