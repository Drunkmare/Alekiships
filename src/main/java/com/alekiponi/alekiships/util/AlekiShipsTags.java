package com.alekiponi.alekiships.util;

import com.alekiponi.alekiships.AlekiShips;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class AlekiShipsTags {


    public static class Blocks {
        public static final TagKey<Block> PLANTS_THAT_GET_MOWED = create("plants_that_get_mowed");

        private static TagKey<Block> create(final String id) {
            return TagKey.create(Registries.BLOCK, new ResourceLocation(AlekiShips.MOD_ID, id));
        }
    }

    public static class Items {
        public static final TagKey<Item> PLANKS = create("planks");
        public static final TagKey<Item> CRAFTING_TABLES = create("crafting_tables");
        public static final TagKey<Item> SHULKER_BOXES = create("shulker_boxes");
        public static final TagKey<Item> CAN_PLACE_IN_COMPARTMENTS = create("can_place_in_compartments");
        public static final TagKey<Item> PLANKS_THAT_MAKE_SHIPS = create("planks_that_make_ships");

        public static TagKey<Item> create(final String id) {
            return TagKey.create(Registries.ITEM, new ResourceLocation(AlekiShips.MOD_ID, id));
        }
    }
}