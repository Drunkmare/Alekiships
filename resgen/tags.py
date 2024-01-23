from mcresources import ResourceManager

import constants


def generate(manager: ResourceManager):
    # Tags with all wood types
    for wood in constants.TFC_WOODS.keys():
        manager.block_tag("canoe_component_blocks", f"wood/canoe_component_block/{wood}")
        manager.block_tag("can_make_canoe_unrestricted", f"tfc:wood/stripped_log/{wood}")
        manager.block_tag("wooden_watercraft_frames", f"wood/watercraft_frame_angled/{wood}",
                          f"wood/watercraft_frame_flat/{wood}")
        manager.entity_tag("alekiships:sloops", f"alekiships:sloop/{wood}")
        manager.entity_tag("alekiships:dugout_canoes", f"alekiships:dugout_canoe/{wood}")
        manager.entity_tag("alekiships:rowboats", f"alekiships:rowboat/{wood}")

    # Vehicle helpers such as our collision entities
    manager.entity_tag("vehicle_helpers", "alekiships:vehicle_cleat", "alekiships:vehicle_part_boat",
                       "alekiships:vehicle_switch_windlass", "alekiships:vehicle_switch_sail", "alekiships:vehicle_collider",
                       "alekiships:vehicle_mast")

    # Compartment Entities
    manager.entity_tag("alekiships:compartments", "alekiships:compartment_anvil", "alekiships:compartment_barrel",
                       "alekiships:compartment_blast_furnace", "alekiships:compartment_cartography_table",
                       "alekiships:compartment_chest", "alekiships:compartment_empty", "alekiships:compartment_ender_chest",
                       "alekiships:compartment_furnace", "alekiships:compartment_grindstone", "alekiships:compartment_loom",
                       "alekiships:compartment_shulker_box", "alekiships:compartment_smithing_table",
                       "alekiships:compartment_smoker", "alekiships:compartment_stonecutter",
                       "alekiships:compartment_tfcchest", "alekiships:compartment_workbench")

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
    manager.entity_tag("carryon:entity_blacklist", "alekiships:cannonball", "alekiships:kayak", "#alekiships:dugout_canoes",
                       "#alekiships:sloops", "#alekiships:rowboats", "#alekiships:vehicle_helpers", "#alekiships:compartments",
                       [f"alekiships:sloop_construction/{wood}" for wood in constants.TFC_WOODS.keys()])
