COLORS = ["white",
          "orange",
          "magenta",
          "light_blue",
          "yellow",
          "lime",
          "pink",
          "gray",
          "light_gray",
          "cyan",
          "purple",
          "blue",
          "brown",
          "green",
          "red",
          "black"]

WOODS = ["oak", "spruce", "birch", "acacia", "cherry", "jungle", "dark_oak", "crimson", "warped", "mangrove", "bamboo"]


def normalize(s: str) -> str:
    """
    Takes a string like dark_oak and converts it to Dark Oak.
    Yes this method is horribly named I'm having trouble coming up with a good one :|
    """
    return ' '.join([word.capitalize() for word in s.split('_')])


DEFAULT_LANG = {

    "item.alekiships.testitem": "They're waiting for you, Mr. Freeman. With the Test Item.",

    # Entities
    **{f"entity.alekiships.rowboat.{wood}": f"{normalize(wood)} Rowboat" for wood in WOODS},
    **{f"entity.alekiships.sloop.{wood}": f"{normalize(wood)} Sloop" for wood in WOODS},
    **{f"entity.alekiships.sloop_construction.{wood}": f"{normalize(wood)} Sloop" for wood in WOODS},

    "entity.alekiships.kayak": "Kayak",

    "entity.alekiships.vehicle_part": "Vehicle Part",
    "entity.alekiships.vehicle_cleat": "Cleat",
    "entity.alekiships.vehicle_switch_windlass": "Windlass",
    "entity.alekiships.vehicle_switch_sail": "Sail",
    "entity.alekiships.vehicle_collider": "Vehicle",
    "entity.alekiships.vehicle_part_boat": "Boat Part",
    "entity.alekiships.vehicle_mast": "Mast",

    "entity.alekiships.cannonball": "Cannonball",
    "entity.alekiships.cannon": "Cannon",

    # Compartments
    "entity.alekiships.compartment_empty": "Empty Compartment",
    "entity.alekiships.compartment_barrel": "Barrel Compartment",
    "entity.alekiships.compartment_chest": "Chest Compartment",
    "entity.alekiships.compartment_ender_chest": "Ender Chest Compartment",
    "entity.alekiships.compartment_shulker_box": "Shulker Box Compartment",
    "entity.alekiships.compartment_furnace": "Furnace Compartment",
    "entity.alekiships.compartment_blast_furnace": "Blast Furnace Compartment",
    "entity.alekiships.compartment_smoker": "Smoker Compartment",
    "entity.alekiships.compartment_brewing_stand": "Brewing Stand Compartment",
    "entity.alekiships.compartment_crafting_table": "Workbench Compartment",
    "entity.alekiships.compartment_stonecutter": "Stonecutter Compartment",
    "entity.alekiships.compartment_cartography_table": "Cartography Table Compartment",
    "entity.alekiships.compartment_smithing_table": "Smithing Table Compartment",
    "entity.alekiships.compartment_grindstone": "Grindstone Compartment",
    "entity.alekiships.compartment_loom": "Loom Compartment",

    "block.alekiships.boat_frame": "Shipwright's Scaffolding",

    "block.alekiships.thatch_roofing": "Thatch Roofing",

    "itemGroup.alekiships": "aleki's Nifty Ships",
    "creativetab.alekiships_tab": "aleki's Nifty Ships",
    "creativetab.watercraft_tab": "Watercraft",
    "creativetab.navigation_tab": "Navigation",

    "press_button": "Press",
    "eject_passengers": "to eject",
    "restless_passenger": "This passenger is restless.",

    "alekiships.advancements.kayak_paddle.title": "Double Trouble",
    "alekiships.advancements.kayak_paddle.description": "Craft a Kayak Paddle",
    "alekiships.advancements.canoe.title": "Burnout Paradise",
    "alekiships.advancements.canoe.description": "Attempt to light a canoe hull",
    "alekiships.advancements.oar.title": "Oaring my Paddleboat",
    "alekiships.advancements.oar.description": "Craft an Oar",
    "alekiships.advancements.kayak.title": "I'll carry you home tonight",
    "alekiships.advancements.kayak.description": "Craft a Kayak",
    "alekiships.advancements.nav_clock.title": "Get your time from the Admiral",
    "alekiships.advancements.nav_clock.description": "Craft a Navigator's Timepiece",
    "alekiships.advancements.sextant.title": "The Stars Will Aid",
    "alekiships.advancements.sextant.description": "Craft a Sextant",
    "alekiships.advancements.barometer.title": "Surf and/or Turf",
    "alekiships.advancements.barometer.description": "Craft a Barometer",
    "alekiships.advancements.oarlock.title": "The Montlake Cut",
    "alekiships.advancements.oarlock.description": "Smith an Oarlock"
}
