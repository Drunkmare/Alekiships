from mcresources import ResourceManager

import lootTables


def generate(rm: ResourceManager):
    lootTables.boat_drops(rm)
    lootTables.structures(rm)
