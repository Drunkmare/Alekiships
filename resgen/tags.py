from mcresources import ResourceManager

import constants


def generate(manager: ResourceManager):
    # Tags with all wood types
    for wood in constants.WOODS:
        manager.block_tag("wooden_watercraft_frames", f"wood/watercraft_frame/angled/{wood}",
                          f"wood/watercraft_frame/flat/{wood}")
        manager.entity_tag("sloops", f"sloop/{wood}")
        manager.entity_tag("rowboats", f"rowboat/{wood}")

    # Vehicle helpers such as our collision entities
    manager.entity_tag("vehicle_helpers", "vehicle_cleat", "vehicle_part", "vehicle_switch_windlass",
                       "vehicle_switch_sail", "vehicle_collider", "vehicle_mast")

    # Compartment Entities
    manager.entity_tag("compartments", "compartment_barrel", "compartment_blast_furnace",
                       "compartment_cartography_table", "compartment_chest", "compartment_empty",
                       "compartment_ender_chest", "compartment_furnace", "compartment_grindstone", "compartment_loom",
                       "compartment_shulker_box", "compartment_smithing_table", "compartment_smoker",
                       "compartment_stonecutter", "compartment_crafting_table", "compartment_brewing_stand",
                       "compartment_note_block", "compartment_jukebox")

    # Tag that allows things to go into compartments
    manager.item_tag("can_place_in_compartments", "minecraft:barrel", "minecraft:chest", "minecraft:ender_chest",
                     "#alekiships:shulker_boxes", "minecraft:furnace", "minecraft:blast_furnace", "minecraft:smoker",
                     "#alekiships:crafting_tables", "minecraft:stonecutter", "minecraft:cartography_table",
                     "minecraft:smithing_table", "minecraft:grindstone", "minecraft:loom", "minecraft:brewing_stand",
                     "minecraft:note_block", "minecraft:jukebox")

    manager.item_tag("crafting_tables", "minecraft:crafting_table")

    manager.item_tag("overworld_planks_that_make_ships", "minecraft:oak_planks", "minecraft:spruce_planks",
                     "minecraft:birch_planks", "minecraft:dark_oak_planks", "minecraft:jungle_planks",
                     "minecraft:cherry_planks", "minecraft:mangrove_planks", "minecraft:acacia_planks")

    manager.item_tag("nether_planks_that_make_ships", "minecraft:crimson_planks", "minecraft:warped_planks")

    manager.item_tag("overworld_planks_that_make_bamboo_ships", "minecraft:bamboo_planks")

    manager.item_tag("icebreaker_upgrades", "minecraft:iron_block")

    manager.item_tag("shulker_boxes", "minecraft:shulker_box",
                     *[f"minecraft:{color}_shulker_box" for color in constants.COLORS])

    manager.tag("unfinished_sloop", "worldgen/structure", "alekiships:unfinished_sloop_birch",
                "alekiships:unfinished_sloop_cherry"
                , "alekiships:unfinished_sloop_dark_oak", "alekiships:unfinished_sloop_oak",
                "alekiships:unfinished_sloop_spruce")

    manager.tag("unfinished_rowboat", "worldgen/structure", "alekiships:unfinished_rowboat_birch",
                "alekiships:unfinished_rowboat_cherry"
                , "alekiships:unfinished_rowboat_dark_oak", "alekiships:unfinished_rowboat_oak",
                "alekiships:unfinished_rowboat_spruce")

    # Vanilla mining tags
    manager.block_tag("minecraft:mineable/axe", "watercraft_frame_angled", "watercraft_frame_flat",
                      "#alekiships:wooden_watercraft_frames")
    manager.block_tag("minecraft:mineable/pickaxe", "oarlock", "cleat")

    # Carryon blacklist tags (as of writing carryon has a bug which means these are ignored)
    manager.entity_tag("carryon:entity_blacklist", "cannonball", "#alekiships:sloops",
                       "#alekiships:rowboats", "#alekiships:vehicle_helpers", "#alekiships:compartments",
                       *[f"sloop_construction/{wood}" for wood in constants.WOODS])
