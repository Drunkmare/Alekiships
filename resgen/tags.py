from mcresources import ResourceManager

import constants


def generate(manager: ResourceManager):
    # Tags with all wood types
    for wood in constants.WOODS:
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

    manager.tag("unfinished_sloop", "worldgen/structure", "alekiships:unfinished_sloop_birch",
                "alekiships:unfinished_sloop_cherry"
                , "alekiships:unfinished_sloop_dark_oak", "alekiships:unfinished_sloop_oak",
                "alekiships:unfinished_sloop_spruce")

    manager.tag("unfinished_rowboat", "worldgen/structure", "alekiships:unfinished_rowboat_birch",
                "alekiships:unfinished_rowboat_cherry"
                , "alekiships:unfinished_rowboat_dark_oak", "alekiships:unfinished_rowboat_oak",
                "alekiships:unfinished_rowboat_spruce")

    # Carryon blacklist tags (as of writing carryon has a bug which means these are ignored)
    manager.entity_tag("carryon:entity_blacklist", "cannonball", "#alekiships:sloops",
                       "#alekiships:rowboats", "#alekiships:vehicle_helpers", "#alekiships:compartments",
                       *[f"sloop_construction/{wood}" for wood in constants.WOODS])
