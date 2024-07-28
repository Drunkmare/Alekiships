from mcresources import ResourceManager

import blockStates
import constants


def generate(rm: ResourceManager):
    for wood in constants.WOODS:
        wood_name = ' '.join([word.capitalize() for word in wood.split('_')])

        for progress in ["first", "second", "third", "fourth"]:
            # Slab frame models
            rm.block_model(f"wood/watercraft_frame/flat/{wood}/{progress}",
                           {"plank": f"minecraft:block/{wood}_planks"},
                           f"alekiships:block/watercraft_frame/flat/template/{progress}")

            for shape in ["straight", "inner", "outer"]:
                rm.block_model(f"wood/watercraft_frame/angled/{wood}/{shape}/{progress}",
                               {"plank": f"minecraft:block/{wood}_planks"},
                               f"alekiships:block/watercraft_frame/angled/template/{shape}/{progress}")

        rm.blockstate_multipart(f"wood/watercraft_frame/flat/{wood}",
                                *blockStates.getWoodFrameFlatMultipart(wood)).with_lang(
            f"{wood_name} Flat Shipwright's Scaffolding")

        rm.blockstate_multipart(f"wood/watercraft_frame/angled/{wood}",
                                *blockStates.getWoodFrameMultipart(wood)).with_lang(
            f"{wood_name} Sloped Shipwright's Scaffolding")

    # Basic frame
    rm.blockstate("watercraft_frame_angled", variants=blockStates.angledWaterCraftFrame).with_lang(
        "Sloped Shipwright's Scaffolding")

    # Need to manually make the model
    rm.item_model("watercraft_frame_angled", parent="alekiships:block/watercraft_frame/angled/straight",
                  no_textures=True)

    # Basic flat frame
    rm.blockstate("watercraft_frame_flat", "alekiships:block/watercraft_frame/flat/frame").with_lang(
        "Flat Shipwright's Scaffolding")

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
    }).with_lang("Oarlock")
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
    }).with_lang("Cleat")
    rm.item_model("cleat")

    # Items with generated models
    rm.item("cannon").with_item_model().with_lang("Cannon")
    rm.item("cannonball").with_item_model().with_lang("Cannonball")
    rm.item("anchor").with_item_model().with_lang("Anchor")
    rm.item("sloop_icon_only").with_item_model().with_lang("Sloop (ICON ONLY)")
    rm.item("rowboat_icon_only").with_item_model().with_lang("Rowboat (ICON ONLY)")
    rm.item("oar").with_item_model().with_lang("Oar")
