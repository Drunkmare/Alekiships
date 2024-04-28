from mcresources import ResourceManager


def generate(rm: ResourceManager):
    rm.crafting_shaped("crafting/watercraft_frame_angled", ["  S", " SS", "SS "], {"S": "minecraft:scaffolding"},
                       "alekiships:watercraft_frame_angled").with_advancement("alekiships:watercraft_frame_angled")

    rm.crafting_shaped("crafting/watercraft_frame_flat", ["SSS"], {"S": "minecraft:scaffolding"},
                       (6, "alekiships:watercraft_frame_flat")).with_advancement("alekiships:watercraft_frame_flat")

    rm.crafting_shaped("crafting/oar", ["  S", " S ", "L  "],
                       {"S": "#forge:rods/wooden", "L": "#minecraft:wooden_slabs"},
                       "alekiships:oar").with_advancement("alekiships:oar")

    rm.crafting_shaped("crafting/cannon", ["BBB", "LL ", "R R"],
                       {"B": "minecraft:iron_block", "L": "#minecraft:wooden_slabs", "R": "minecraft:iron_nugget"},
                       "alekiships:cannon").with_advancement("alekiships:cannon")

    rm.crafting_shaped("crafting/anchor", ["NIN", " I ", "IBI"],
                       {"N": "minecraft:iron_nugget", "I": "minecraft:iron_ingot", "B": "minecraft:iron_block"},
                       "alekiships:anchor").with_advancement("alekiships:anchor")

    rm.crafting_shaped("crafting/cleat", ["III", "N N"],
                       {"N": "minecraft:iron_nugget", "I": "minecraft:iron_ingot"},
                       "alekiships:cleat").with_advancement("alekiships:cleat")

    rm.crafting_shaped("crafting/oarlock", [" I ", "III"],
                       {"I": "minecraft:iron_ingot"},
                       "alekiships:oarlock").with_advancement("alekiships:oarlock")

    rm.crafting_shapeless("crafting/cannonball",
                          ["minecraft:iron_ingot", "minecraft:paper", "minecraft:gunpowder"],
                          "alekiships:cannonball").with_advancement("alekiships:cannonball")
