from mcresources import ResourceManager

import blockStates
import constants


def generate(rm: ResourceManager):
    for wood in constants.WOODS:
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
                                *blockStates.getWoodFrameFlatMultipart(wood))

        rm.blockstate_multipart(f"wood/watercraft_frame/angled/{wood}",
                                *blockStates.getWoodFrameMultipart(wood))

    # Basic frame
    rm.blockstate("watercraft_frame_angled", variants=blockStates.angledWaterCraftFrame)

    # Need to manually make the model
    rm.item_model("watercraft_frame_angled", parent="alekiships:block/watercraft_frame/angled/straight",
                  no_textures=True)

    # Basic flat frame
    rm.blockstate("watercraft_frame_flat", "alekiships:block/watercraft_frame/flat/frame")

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
    })
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
    })
    rm.item_model("cleat")

    # Items with generated models
    rm.item("cannon").with_item_model()
    rm.item("cannonball").with_item_model()
    rm.item("anchor").with_item_model()
    rm.item("sloop_icon_only").with_item_model()
    rm.item("rowboat_icon_only").with_item_model()
    rm.item("oar").with_item_model()
