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


def langify(s: str) -> str:
    """
    Takes a string like dark_oak and converts it to Dark Oak.
    Yes this method is horribly named I'm having trouble coming up with a good one :|
    """
    return ' '.join([word.capitalize() for word in s.split('_')])


DEFAULT_LANG = {
    # Entities
    **{f"entity.alekiships.rowboat.{wood}": f"{langify(wood)} Rowboat" for wood in WOODS},
    **{f"entity.alekiships.sloop.{wood}": f"{langify(wood)} Sloop" for wood in WOODS},
    **{f"entity.alekiships.sloop_construction.{wood}": f"{langify(wood)} Sloop" for wood in WOODS},

    "entity.alekiships.kayak": "Kayak",

    "entity.alekiships.vehicle_part": "Vehicle Part",
    "entity.alekiships.vehicle_cleat": "Cleat",
    "entity.alekiships.vehicle_switch_windlass": "Windlass",
    "entity.alekiships.vehicle_switch_sail": "Sail",
    "entity.alekiships.vehicle_collider": "Vehicle",
    "entity.alekiships.vehicle_part_boat": "Boat Part",
    "entity.alekiships.vehicle_mast": "Mast",
    "entity.alekiships.vehicle_anchor": "Anchor",

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
    "entity.alekiships.compartment_note_block": "Note Block Compartment",
    "entity.alekiships.compartment_jukebox": "Jukebox Compartment",

    "block.alekiships.boat_frame": "Shipwright's Scaffolding",

    "block.alekiships.thatch_roofing": "Thatch Roofing",

    "itemGroup.alekiships": "aleki's Nifty Ships",
    "creativetab.alekiships_tab": "aleki's Nifty Ships",

    "press_button": "Press",
    "eject_passengers": "to eject",

    "alekiships.advancements.oar.title": "Oaring my Paddleboat",
    "alekiships.advancements.oar.description": "Craft an Oar",
    "alekiships.advancements.oarlock.title": "The Montlake Cut",
    "alekiships.advancements.oarlock.description": "Smith an Oarlock",

    "alekiships.advancements.cannon.title": "Incoming Cannon Event",
    "alekiships.advancements.cannon.description": "Craft a cannon",

    "alekiships.advancements.full_broadside.title": "Master and Commander",
    "alekiships.advancements.full_broadside.description": "Fire a full broadside",

    "alekiships.advancements.sloop_completed.title": "Yesler's Wharf",
    "alekiships.advancements.sloop_completed.description": "Build a Sloop",

    "alekiships.advancements.rowboat_completed.title": "The Montlake Cut",
    "alekiships.advancements.rowboat_completed.description": "Build a Rowboat",

    "alekiships.advancements.ride_barrel.title": "Lost at Sea",
    "alekiships.advancements.ride_barrel.description": "Ride a barrel",

    "alekiships.advancements.armor_stand_on_boat.title": "Wilson!!!!!!!",
    "alekiships.advancements.armor_stand_on_boat.description": "Place an inanimate companion on a boat",

    "alekiships.advancements.dye_ship_black.title": "The best pirate I've ever seen",
    "alekiships.advancements.dye_ship_black.description": "Name a black ship The Black Pearl",

}
