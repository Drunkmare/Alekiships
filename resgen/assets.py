from mcresources import ResourceManager

import blockStates
import constants
import lootTables


def generate(rm: ResourceManager):
    for wood in constants.WOODS:
        wood_name = ' '.join([word.capitalize() for word in wood.split('_')])

        for progress in ["first", "second", "third", "fourth"]:
            # Slab frame models
            rm.block_model(f"wood/watercraft_frame/flat/{wood}/{progress}",
                           {"plank": f"minecraft:block/{wood}_planks"},
                           f"alekiships:block/watercraft_frame/flat/template/{progress}")

            for shape in ["straight", "inner", "outer"]:
                rm.block_model(f"wood/watercraft_frame_angled/{wood}/{shape}/{progress}",
                               {"plank": f"minecraft:block/{wood}_planks"},
                               f"alekiships:block/watercraft_frame_angled/template/{shape}/{progress}")

        rm.blockstate_multipart(f"wood/watercraft_frame_flat/{wood}",
                                *blockStates.getWoodFrameFlatMultipart(wood)).with_lang(
            f"{wood_name} Flat Shipwright's Scaffolding").with_block_loot(
            *lootTables.boat_frame_flat(wood))

        rm.blockstate_multipart(f"wood/watercraft_frame_angled/{wood}",
                                *blockStates.getWoodFrameMultipart(wood)).with_lang(
            f"{wood_name} Sloped Shipwright's Scaffolding").with_block_loot(
            *lootTables.boat_frame(wood))

    # Basic frame
    rm.blockstate("watercraft_frame_angled", variants=blockStates.angledWaterCraftFrame).with_lang(
        "Sloped Shipwright's Scaffolding").with_block_loot("alekiships:watercraft_frame_angled")

    # Need to manually make the model
    rm.item_model("watercraft_frame_angled", parent="alekiships:block/watercraft_frame_angled/straight",
                  no_textures=True)

    # Basic flat frame
    rm.blockstate("watercraft_frame_flat", "alekiships:block/watercraft_frame/flat/frame").with_lang(
        "Flat Shipwright's Scaffolding").with_block_loot("alekiships:watercraft_frame_flat")

    # Need to manually make the model
    rm.item_model("watercraft_frame_flat", parent="alekiships:block/watercraft_frame/flat/frame",
                  no_textures=True)

    rm.blockstate("oarlock", variants={
        "facing=east": {
            "model": "alekiships:block/oarlock",
            "y": 90
        },
        "facing=north": {
            "model": "alekiships:block/oarlock"
        },
        "facing=south": {
            "model": "alekiships:block/oarlock",
            "y": 180
        },
        "facing=west": {
            "model": "alekiships:block/oarlock",
            "y": 270
        }
    }).with_lang("Oarlock").with_block_loot("alekiships:oarlock")
    rm.item_model("oarlock")

    rm.blockstate("cleat", variants={
        "facing=east": {
            "model": "alekiships:block/cleat",
            "y": 90
        },
        "facing=north": {
            "model": "alekiships:block/cleat"
        },
        "facing=south": {
            "model": "alekiships:block/cleat",
            "y": 180
        },
        "facing=west": {
            "model": "alekiships:block/cleat",
            "y": 270
        }
    }).with_lang("Cleat").with_block_loot("alekiships:cleat")
    rm.item_model("cleat")

    # Items with generated models
    rm.item("unfinished_barometer").with_item_model().with_lang("Unfinished Barometer")
    rm.item("unfinished_nav_clock").with_item_model().with_lang("Unfinished Navigator's Timepiece")
    rm.item("unfinished_sextant").with_item_model().with_lang("Unfinished Sextant")
    rm.item("cannon").with_item_model().with_lang("Cannon")
    rm.item("cannonball").with_item_model().with_lang("Cannonball")
    rm.item("cannon_barrel").with_item_model().with_lang("Cannon Barrel")
    rm.item("anchor").with_item_model().with_lang("Anchor")
    rm.item("small_triangular_sail").with_item_model().with_lang("Small Sail")
    rm.item("medium_triangular_sail").with_item_model().with_lang("Medium Sail")
    rm.item("large_triangular_sail").with_item_model().with_lang("Large Sail")
    rm.item("rope_coil").with_item_model().with_lang("Jute Rope")

    rm.item("sloop_icon_only").with_item_model().with_lang("ICON ONLY")
    rm.item("canoe_icon_only").with_item_model().with_lang("ICON ONLY")
    rm.item("canoe_with_paddle_icon_only").with_item_model().with_lang("ICON ONLY")
    rm.item("kayak_with_paddle_icon_only").with_item_model().with_lang("ICON ONLY")
    rm.item("rowboat_icon_only").with_item_model().with_lang("ICON ONLY")
