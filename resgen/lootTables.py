from mcresources import loot_tables, ResourceManager
from mcresources.type_definitions import Json

import constants

SLOOP_LOGS = 34

FRAME_CAPACITY = 4

SLOOP_FRAMES = 24

ROWBOAT_FRAMES = 6

DESTRUCTION_MODIFIER = 0.2


def boat_drops(rm: ResourceManager):
    for wood in constants.WOODS:
        log = f"minecraft:stripped_{wood}_log"
        if wood == "bamboo":
            log = "minecraft:stripped_bamboo_block"

        if wood == "crimson":
            log = "minecraft:stripped_crimson_hyphae"

        if wood == "warped":
            log = "minecraft:stripped_warped_hyphae"

        # TOTO Drop all the resources used to construct a rowboat?
        rm.entity_loot(f"rowboat/{wood}", {"name": "alekiships:oarlock",
                                           "functions": loot_tables.set_count(0, 2)},
                       {"name": f"minecraft:{wood}_planks",
                        "functions": loot_tables.set_count(FRAME_CAPACITY * ROWBOAT_FRAMES * DESTRUCTION_MODIFIER,
                                                           FRAME_CAPACITY * ROWBOAT_FRAMES)})
        rm.entity_loot(f"sloop/{wood}",
                       {"name": "alekiships:cleat",
                        "functions": loot_tables.set_count(1, 3)},
                       {"name": "minecraft:lead",
                        "functions": loot_tables.set_count(2, 7)},
                       {"name": "alekiships:anchor",
                        "functions": loot_tables.set_count(0, 1)},
                       {"name": log,
                        "functions": loot_tables.set_count(0, 20)},
                       {"name": f"minecraft:{wood}_planks",
                        "functions": loot_tables.set_count(0, 15)},
                       {"name": f"minecraft:{wood}_fence",
                        "functions": loot_tables.set_count(0, 10)},
                       {"name": f"minecraft:{wood}_planks",
                        "functions": loot_tables.set_count(FRAME_CAPACITY * SLOOP_FRAMES * DESTRUCTION_MODIFIER,
                                                           FRAME_CAPACITY * SLOOP_FRAMES)})

def boat_frame_flat(wood: str) -> list[Json]:
    return [{"name": "alekiships:watercraft_frame_flat"},
            [
                # Planks
                {"name": f"minecraft:{wood}_planks",
                 "conditions": [loot_tables.block_state_property(
                     f"alekiships:wood/watercraft_frame/flat/{wood}[frame_processed=0]")]},

                {"name": f"minecraft:{wood}_planks", "functions": loot_tables.set_count(2),
                 "conditions": [loot_tables.block_state_property(
                     f"alekiships:wood/watercraft_frame/flat/{wood}[frame_processed=1]")]},

                {"name": f"minecraft:{wood}_planks", "functions": loot_tables.set_count(3),
                 "conditions": [loot_tables.block_state_property(
                     f"alekiships:wood/watercraft_frame/flat/{wood}[frame_processed=2]")]},

                {"name": f"minecraft:{wood}_planks", "functions": loot_tables.set_count(4),
                 "conditions": [loot_tables.block_state_property(
                     f"alekiships:wood/watercraft_frame/flat/{wood}[frame_processed=3]")
                 ]}
            ]]


def boat_frame(wood: str) -> list[Json]:
    return [{"name": "alekiships:watercraft_frame_angled"},
            [
                # Planks
                {"name": f"minecraft:{wood}_planks",
                 "conditions": [loot_tables.block_state_property(
                     f"alekiships:wood/watercraft_frame/angled/{wood}[frame_processed=0]")]},

                {"name": f"minecraft:{wood}_planks", "functions": loot_tables.set_count(2),
                 "conditions": [loot_tables.block_state_property(
                     f"alekiships:wood/watercraft_frame/angled/{wood}[frame_processed=1]")]},

                {"name": f"minecraft:{wood}_planks", "functions": loot_tables.set_count(3),
                 "conditions": [loot_tables.block_state_property(
                     f"alekiships:wood/watercraft_frame/angled/{wood}[frame_processed=2]")]},

                {"name": f"minecraft:{wood}_planks", "functions": loot_tables.set_count(4),
                 "conditions": [loot_tables.block_state_property(
                     f"alekiships:wood/watercraft_frame/angled/{wood}[frame_processed=3]")]}
            ]]


def structures(rm: ResourceManager):
    for wood in constants.WOODS:
        rm.loot(f"rowboat/{wood}",
                {"name": f"minecraft:{wood}_planks",
                 "functions": loot_tables.set_count(0, 16)},
                {"name": f"minecraft:{wood}_planks",
                 "functions": loot_tables.set_count(0, 16)},
                {"name": f"minecraft:{wood}_planks",
                 "functions": loot_tables.set_count(0, 16)},
                {"name": "alekiships:watercraft_frame_angled",
                 "functions": loot_tables.set_count(1, 3)},
                {"name": "alekiships:watercraft_frame_angled",
                 "functions": loot_tables.set_count(1, 3)},
                {"name": "alekiships:watercraft_frame_angled",
                 "functions": loot_tables.set_count(1)},
                {"name": "alekiships:oarlock",
                 "functions": loot_tables.set_count(0, 1)},
                {"name": "alekiships:oarlock",
                 "functions": loot_tables.set_count(0, 1)},
                {"name": "minecraft:scaffolding",
                 "functions": loot_tables.set_count(0, 5)},
                {"name": "minecraft:scaffolding",
                 "functions": loot_tables.set_count(0, 5)},
                {"name": "alekiships:oar",
                 "functions": loot_tables.set_count(0, 1)},
                {"name": "alekiships:oar",
                 "functions": loot_tables.set_count(0, 1)},
                {"name": "alekiships:lead",
                 "functions": loot_tables.set_count(0, 1)},
                path="structures",
                loot_type="chest"
                )
        rm.loot(f"sloop/{wood}",
                {"name": f"minecraft:{wood}_planks",
                 "functions": loot_tables.set_count(0, 32)},
                {"name": f"minecraft:{wood}_planks",
                 "functions": loot_tables.set_count(0, 32)},
                {"name": f"minecraft:{wood}_planks",
                 "functions": loot_tables.set_count(0, 32)},
                {"name": "alekiships:watercraft_frame_angled",
                 "functions": loot_tables.set_count(1, 3)},
                {"name": "alekiships:watercraft_frame_angled",
                 "functions": loot_tables.set_count(1, 3)},
                {"name": "alekiships:watercraft_frame_angled",
                 "functions": loot_tables.set_count(1)},
                {"name": "alekiships:watercraft_frame_flat",
                 "functions": loot_tables.set_count(1, 3)},
                {"name": "alekiships:watercraft_frame_flat",
                 "functions": loot_tables.set_count(1, 3)},
                {"name": "alekiships:watercraft_frame_flat",
                 "functions": loot_tables.set_count(1)},
                {"name": "alekiships:cleat",
                 "functions": loot_tables.set_count(0, 1)},
                {"name": "alekiships:cleat",
                 "functions": loot_tables.set_count(0, 1)},
                {"name": "alekiships:cleat",
                 "functions": loot_tables.set_count(0, 1)},
                {"name": "alekiships:cleat",
                 "functions": loot_tables.set_count(0, 1)},
                {"name": "minecraft:scaffolding",
                 "functions": loot_tables.set_count(0, 5)},
                {"name": "minecraft:scaffolding",
                 "functions": loot_tables.set_count(0, 5)},
                {"name": "minecraft:slime_ball",
                 "functions": loot_tables.set_count(8)},
                {"name": "minecraft:lead",
                 "functions": loot_tables.set_count(1)},
                path="structures",
                loot_type="chest"
                )
        rm.loot(f"sloop_hut/{wood}",
                {"name": "alekiships:cleat",
                 "functions": loot_tables.set_count(0, 1)},
                {"name": "alekiships:cleat",
                 "functions": loot_tables.set_count(0, 1)},
                {"name": "alekiships:anchor",
                 "functions": loot_tables.set_count(0, 1)},
                {"name": "minecraft:scaffolding",
                 "functions": loot_tables.set_count(0, 5)},
                {"name": "minecraft:scaffolding",
                 "functions": loot_tables.set_count(0, 5)},
                {"name": "minecraft:bread",
                 "functions": loot_tables.set_count(0, 3)},
                {"name": "minecraft:bread",
                 "functions": loot_tables.set_count(0, 5)},
                {"name": "alekiships:cannonball",
                 "functions": loot_tables.set_count(0, 3)},
                {"name": "alekiships:cannonball",
                 "functions": loot_tables.set_count(0, 3)},
                {"name": "minecraft:flint_and_steel",
                 "functions": loot_tables.set_count(0, 1)},
                {"name": f"minecraft:{wood}_planks",
                 "functions": loot_tables.set_count(0, 8)},
                path="structures",
                loot_type="chest"
                )
