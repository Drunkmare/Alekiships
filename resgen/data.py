from mcresources import ResourceManager, loot_tables

import constants

SLOOP_LOGS = 34

FRAME_CAPACITY = 4

SLOOP_FRAMES = 24

ROWBOAT_FRAMES = 6


def generate(rm: ResourceManager):
    for wood in constants.WOODS:
        # TOTO Drop all the resources used to construct a rowboat?
        rm.entity_loot(f"rowboat/{wood}",
                       {"name": f"minecraft:{wood}_planks",
                        "functions": loot_tables.set_count(FRAME_CAPACITY * ROWBOAT_FRAMES)})
        # TODO Drop all the resources used to construct a sloop?
        rm.entity_loot(f"sloop/{wood}", "alekiships:anchor",
                       {"name": f"minecraft:{wood}_planks",
                        "functions": loot_tables.set_count(FRAME_CAPACITY * SLOOP_FRAMES)})
