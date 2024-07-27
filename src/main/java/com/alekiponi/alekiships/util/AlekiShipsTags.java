package com.alekiponi.alekiships.util;

import com.alekiponi.alekiships.AlekiShips;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public final class AlekiShipsTags {


    public static final class Blocks {
        public static final TagKey<Block> PLANTS_THAT_GET_MOWED = create("plants_that_get_mowed");

        public static final TagKey<Block> WOODEN_WATERCRAFT_FRAMES = create("wooden_watercraft_frames");

        private static TagKey<Block> create(final String id) {
            return TagKey.create(Registries.BLOCK, new ResourceLocation(AlekiShips.MOD_ID, id));
        }
    }

    public static final class Items {
        /**
         * Tag for items that are for crafting table compartments
         */
        public static final TagKey<Item> CRAFTING_TABLES = create("crafting_tables");
        /**
         * Tag for items that are for shulker box compartments
         */
        public static final TagKey<Item> SHULKER_BOXES = create("shulker_boxes");
        /**
         * Tag that allows things to go into compartments
         */
        public static final TagKey<Item> CAN_PLACE_IN_COMPARTMENTS = create("can_place_in_compartments");
        /**
         * Tag for items which are consumed for the sloop icebreaker upgrade
         */
        public static final TagKey<Item> ICEBREAKER_UPGRADES = create("icebreaker_upgrades");

        public static TagKey<Item> create(final String id) {
            return TagKey.create(Registries.ITEM, new ResourceLocation(AlekiShips.MOD_ID, id));
        }
    }
}