from mcresources import ResourceManager

import constants


def generate(manager: ResourceManager):
    # Tags with all wood types
    for wood in constants.TFC_WOODS.keys():
        manager.block_tag("canoe_component_blocks", f"wood/canoe_component_block/{wood}")
        manager.block_tag("can_make_canoe_unrestricted", f"tfc:wood/stripped_log/{wood}")
        manager.block_tag("wooden_watercraft_frames", f"wood/watercraft_frame_angled/{wood}",
                          f"wood/watercraft_frame_flat/{wood}")
        manager.entity_tag("sloops", f"sloop/{wood}")
        manager.entity_tag("dugout_canoes", f"dugout_canoe/{wood}")
        manager.entity_tag("rowboats", f"rowboat/{wood}")

    # Vehicle helpers such as our collision entities
    manager.entity_tag("vehicle_helpers", "vehicle_cleat", "vehicle_part_boat", "vehicle_switch_windlass",
                       "vehicle_switch_sail", "vehicle_collider", "vehicle_mast")

    # Compartment Entities
    manager.entity_tag("compartments", "compartment_barrel", "compartment_blast_furnace",
                       "compartment_cartography_table", "compartment_chest", "compartment_empty",
                       "compartment_ender_chest", "compartment_furnace", "compartment_grindstone", "compartment_loom",
                       "compartment_shulker_box", "compartment_smithing_table", "compartment_smoker",
                       "compartment_stonecutter", "compartment_crafting_table")

    # Tag that allows things to go into compartments
    manager.item_tag("can_place_in_compartments", "minecraft:barrel", "minecraft:chest", "minecraft:ender_chest",
                     "#alekiships:shulker_boxes", "minecraft:furnace", "minecraft:blast_furnace", "minecraft:smoker",
                     "#alekiships:crafting_tables", "minecraft:stonecutter", "minecraft:cartography_table",
                     "minecraft:smithing_table", "minecraft:grindstone", "minecraft:loom")

    manager.item_tag("crafting_tables", "minecraft:crafting_table")

    manager.item_tag("shulker_boxes", "minecraft:shulker_box",
                     *[f"minecraft:{color}_shulker_box" for color in constants.COLORS])

    # Vanilla mining tags
    manager.block_tag("minecraft:mineable/axe", "watercraft_frame_angled", "watercraft_frame_flat",
                      "#alekiships:canoe_component_blocks", "#alekiships:wooden_watercraft_frames")
    manager.block_tag("minecraft:mineable/pickaxe", "oarlock")

    # TFC tags
    manager.block_tag("tfc:mineable_with_blunt_tool", "#alekiships:canoe_component_blocks")
    manager.item_tag("tfc:usable_on_tool_rack", "canoe_paddle", "kayak_paddle", "oar", "kayak", "nav_clock", "sextant",
                     "barometer")

    # Carryon blacklist tags (as of writing carryon has a bug which means these are ignored)
    manager.block_tag("carryon:block_blacklist", "#alekiships:canoe_component_blocks")
    manager.entity_tag("carryon:entity_blacklist", "cannonball", "kayak",
                       "#alekiships:dugout_canoes", "#alekiships:sloops", "#alekiships:rowboats",
                       "#alekiships:vehicle_helpers", "#alekiships:compartments",
                       *[f"sloop_construction/{wood}" for wood in constants.TFC_WOODS.keys()])
