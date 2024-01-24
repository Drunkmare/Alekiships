from typing import Optional, Union

from mcresources import ResourceManager, utils, RecipeContext
from mcresources.type_definitions import ResourceIdentifier, Json

import constants


def generate(rm: ResourceManager):


    rm.crafting_shaped("crafting/watercraft_frame_angled", ["  S", " SS", "SS "], {"S": "minecraft:scaffolding"},
                       ("alekiships:watercraft_frame_angled")).with_advancement("alekiships:watercraft_frame_angled")

    rm.crafting_shaped("crafting/watercraft_frame_angled_3", ["S  ", "SS ", " SS"], {"S": "minecraft:scaffolding"},
                       ("alekiships:watercraft_frame_angled")).with_advancement("alekiships:watercraft_frame_angled")

    rm.crafting_shaped("crafting/watercraft_frame_flat", ["SSS"], {"S": "minecraft:scaffolding"},
                       (6, "alekiships:watercraft_frame_flat")).with_advancement("alekiships:watercraft_frame_flat")

    rm.crafting_shaped("crafting/oar", ["  S", " S ", "L  "], {"S": "#forge:rods/wooden", "L": "#minecraft:wooden_slabs"},
                       "alekiships:oar").with_advancement("alekiships:oar")

    rm.crafting_shaped("crafting/cannon", ["BBB", "LL ", "R R"], {"B": "minecraft:iron_block",
                                                                  "L": "#minecraft:wooden_slabs",
                                                                  "R": "minecraft:iron_nugget"},
                       "alekiships:cannon").with_advancement("alekiships:cannon")

    rm.crafting_shaped("crafting/small_triangular_sail", ["WSS", "WWS", "WWW"],
                       {"W": "minecraft:white_wool", "S": "#forge:string"},
                       "alekiships:small_triangular_sail").with_advancement("alekiships:small_triangular_sail")

    rm.crafting_shaped("crafting/medium_triangular_sail", ["S  ", "WS ", "WWS"],
                       {"W": "alekiships:small_triangular_sail", "S": "#forge:string"},
                       "alekiships:medium_triangular_sail").with_advancement("alekiships:medium_triangular_sail")

    rm.crafting_shapeless("crafting/cannonball",
                          ["minecraft:iron_ingot", "minecraft:paper", "minecraft:gunpowder",],
                          "alekiships:cannonball").with_advancement("alekiships:cannonball")

def fluid_stack(data_in: Json) -> Json:
    """
    Copied from tfc data gen
    """
    if isinstance(data_in, dict):
        return data_in
    fluid, tag, amount, _ = utils.parse_item_stack(data_in, False)
    assert not tag, 'fluid_stack() cannot be a tag'
    return {
        'fluid': fluid,
        'amount': amount
    }


def item_stack_provider(
        data_in: Json = None,
        # Possible Modifiers
        copy_input: bool = False,
        copy_heat: bool = False,
        copy_food: bool = False,  # copies both decay and traits
        copy_oldest_food: bool = False,  # copies only decay, from all inputs (uses crafting container)
        reset_food: bool = False,  # rest_food modifier - used for newly created food from non-food
        add_glass: bool = False,  # glassworking specific
        add_powder: bool = False,  # glassworking specific
        add_heat: float = None,
        add_trait: str = None,  # applies a food trait and adjusts decay accordingly
        remove_trait: str = None,  # removes a food trait and adjusts decay accordingly
        empty_bowl: bool = False,  # replaces a soup with its bowl
        copy_forging: bool = False,
        add_bait_to_rod: bool = False,  # adds bait to the rod, uses crafting container
        dye_color: str = None,  # applies a dye color to leather dye-able armor
        meal: Json = None  # makes a meal from input specified in json
) -> Json:
    """
    Copied from tfc data gen
    """
    if isinstance(data_in, dict):
        return data_in
    stack = utils.item_stack(data_in) if data_in is not None else None
    modifiers = [k for k, v in (
        # Ordering is important here
        # First, modifiers that replace the entire stack (copy input style)
        # Then, modifiers that only mutate an existing stack
        ('tfc:empty_bowl', empty_bowl),
        ('tfc:copy_input', copy_input),
        ('tfc:copy_heat', copy_heat),
        ('tfc:copy_food', copy_food),
        ('tfc:copy_oldest_food', copy_oldest_food),
        ('tfc:reset_food', reset_food),
        ('tfc:copy_forging_bonus', copy_forging),
        ('tfc:add_bait_to_rod', add_bait_to_rod),
        ('tfc:add_glass', add_glass),
        ('tfc:add_powder', add_powder),
        ({'type': 'tfc:add_heat', 'temperature': add_heat}, add_heat is not None),
        ({'type': 'tfc:add_trait', 'trait': add_trait}, add_trait is not None),
        ({'type': 'tfc:remove_trait', 'trait': remove_trait}, remove_trait is not None),
        ({'type': 'tfc:dye_leather', 'color': dye_color}, dye_color is not None),
        ({'type': 'tfc:meal', **(meal if meal is not None else {})}, meal is not None),
    ) if v]
    if modifiers:
        return {
            'stack': stack,
            'modifiers': modifiers
        }
    return stack

def disableRecipe(name_parts: ResourceIdentifier):
    rm.recipe(name_parts, None, {}, conditions="forge:false")