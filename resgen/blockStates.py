from mcresources.type_definitions import Json

angledWaterCraftFrame = {
    "facing=north,shape=straight": {
        "model": "alekiships:block/watercraft_frame/angled/straight",
        "y": 180
    },
    "facing=east,shape=straight": {
        "model": "alekiships:block/watercraft_frame/angled/straight",
        "y": 270
    },
    "facing=south,shape=straight": {
        "model": "alekiships:block/watercraft_frame/angled/straight",
    },
    "facing=west,shape=straight": {
        "model": "alekiships:block/watercraft_frame/angled/straight",
        "y": 90
    },
    "facing=north,shape=inner_left": {
        "model": "alekiships:block/watercraft_frame/angled/inner",
        "y": 90
    },
    "facing=east,shape=inner_left": {
        "model": "alekiships:block/watercraft_frame/angled/inner",
        "y": 180
    },
    "facing=south,shape=inner_left": {
        "model": "alekiships:block/watercraft_frame/angled/inner",
        "y": 270
    },
    "facing=west,shape=inner_left": {
        "model": "alekiships:block/watercraft_frame/angled/inner"
    },
    "facing=north,shape=outer_left": {
        "model": "alekiships:block/watercraft_frame/angled/outer",
        "y": 270
    },
    "facing=east,shape=outer_left": {
        "model": "alekiships:block/watercraft_frame/angled/outer"
    },
    "facing=south,shape=outer_left": {
        "model": "alekiships:block/watercraft_frame/angled/outer",
        "y": 90
    },
    "facing=west,shape=outer_left": {
        "model": "alekiships:block/watercraft_frame/angled/outer",
        "y": 180
    },
    "facing=north,shape=inner_right": {
        "model": "alekiships:block/watercraft_frame/angled/inner",
        "y": 180
    },
    "facing=east,shape=inner_right": {
        "model": "alekiships:block/watercraft_frame/angled/inner",
        "y": 270
    },
    "facing=south,shape=inner_right": {
        "model": "alekiships:block/watercraft_frame/angled/inner"
    },
    "facing=west,shape=inner_right": {
        "model": "alekiships:block/watercraft_frame/angled/inner",
        "y": 90
    },
    "facing=north,shape=outer_right": {
        "model": "alekiships:block/watercraft_frame/angled/outer"
    },
    "facing=east,shape=outer_right": {
        "model": "alekiships:block/watercraft_frame/angled/outer",
        "y": 90
    },
    "facing=south,shape=outer_right": {
        "model": "alekiships:block/watercraft_frame/angled/outer",
        "y": 180
    },
    "facing=west,shape=outer_right": {
        "model": "alekiships:block/watercraft_frame/angled/outer",
        "y": 270
    }}


def getWoodFrameFlatMultipart(wood: str) -> list[Json]:
    return [{"model": "alekiships:block/watercraft_frame/flat/frame"},
            *[({"frame_processed": processedStates},
               {
                   "model": f"alekiships:block/wood/watercraft_frame/flat/{wood}/{template}"})
              for template, processedStates in
              {"first": "0|1|2|3", "second": "1|2|3", "third": "2|3", "fourth": "3"}.items()]]


def getWoodFrameMultipart(wood: str) -> list[Json]:
    # Only the frames
    json = [
        ({"facing": "north", "shape": "straight"},
         {"model": "alekiships:block/watercraft_frame/angled/straight", "y": 180}),
        ({"facing": "east", "shape": "straight"},
         {"model": "alekiships:block/watercraft_frame/angled/straight", "y": 270}),
        ({"facing": "south", "shape": "straight"},
         {"model": "alekiships:block/watercraft_frame/angled/straight"}),
        ({"facing": "west", "shape": "straight"},
         {"model": "alekiships:block/watercraft_frame/angled/straight", "y": 90}),
        ({"facing": "north", "shape": "inner_left"},
         {"model": "alekiships:block/watercraft_frame/angled/inner", "y": 90}),
        ({"facing": "east", "shape": "inner_left"},
         {"model": "alekiships:block/watercraft_frame/angled/inner", "y": 180}),
        ({"facing": "south", "shape": "inner_left"},
         {"model": "alekiships:block/watercraft_frame/angled/inner", "y": 270}),
        ({"facing": "west", "shape": "inner_left"},
         {"model": "alekiships:block/watercraft_frame/angled/inner"}),
        ({"facing": "north", "shape": "outer_left"},
         {"model": "alekiships:block/watercraft_frame/angled/outer", "y": 270}),
        ({"facing": "east", "shape": "outer_left"},
         {"model": "alekiships:block/watercraft_frame/angled/outer"}),
        ({"facing": "south", "shape": "outer_left"},
         {"model": "alekiships:block/watercraft_frame/angled/outer", "y": 90}),
        ({"facing": "west", "shape": "outer_left"},
         {"model": "alekiships:block/watercraft_frame/angled/outer", "y": 180}),
        ({"facing": "north", "shape": "inner_right"},
         {"model": "alekiships:block/watercraft_frame/angled/inner", "y": 180}),
        ({"facing": "east", "shape": "inner_right"},
         {"model": "alekiships:block/watercraft_frame/angled/inner", "y": 270}),
        ({"facing": "south", "shape": "inner_right"},
         {"model": "alekiships:block/watercraft_frame/angled/inner"}),
        ({"facing": "west", "shape": "inner_right"},
         {"model": "alekiships:block/watercraft_frame/angled/inner", "y": 90}),
        ({"facing": "north", "shape": "outer_right"},
         {"model": "alekiships:block/watercraft_frame/angled/outer"}),
        ({"facing": "east", "shape": "outer_right"},
         {"model": "alekiships:block/watercraft_frame/angled/outer", "y": 90}),
        ({"facing": "south", "shape": "outer_right"},
         {"model": "alekiships:block/watercraft_frame/angled/outer", "y": 180}),
        ({"facing": "west", "shape": "outer_right"},
         {"model": "alekiships:block/watercraft_frame/angled/outer", "y": 270})
    ]

    plankTemplateStates = {"first": "0|1|2|3", "second": "1|2|3", "third": "2|3", "fourth": "3"}

    # Straight shape planks
    for template, processedStates in plankTemplateStates.items():
        # Wood progress states
        json += [
            ({"facing": "north", "shape": "straight", "frame_processed": processedStates},
             {"model": f"alekiships:block/wood/watercraft_frame/angled/{wood}/straight/{template}",
              "uvlock": True,
              "y": 180}),
            ({"facing": "east", "shape": "straight", "frame_processed": processedStates},
             {"model": f"alekiships:block/wood/watercraft_frame/angled/{wood}/straight/{template}",
              "uvlock": True,
              "y": 270}),
            ({"facing": "south", "shape": "straight", "frame_processed": processedStates},
             {"model": f"alekiships:block/wood/watercraft_frame/angled/{wood}/straight/{template}"}),
            ({"facing": "west", "shape": "straight", "frame_processed": processedStates},
             {"model": f"alekiships:block/wood/watercraft_frame/angled/{wood}/straight/{template}",
              "uvlock": True,
              "y": 90})
        ]

    inner = {"left": [90, 180, 270, None], "right": [180, 270, None, 90]}
    outer = {"left": [270, None, 90, 180], "right": [None, 90, 180, 270]}

    # Planks for inner and outer
    for shape, rotations in {"inner": inner, "outer": outer}.items():
        for side, rotation in rotations.items():
            for template, processedStates in plankTemplateStates.items():
                json += [
                    ({"facing": "north", "shape": f"{shape}_{side}", "frame_processed": processedStates},
                     {"model": f"alekiships:block/wood/watercraft_frame/angled/{wood}/{shape}/{template}",
                      "uvlock": True,
                      "y": rotation[0]}),
                    ({"facing": "east", "shape": f"{shape}_{side}", "frame_processed": processedStates},
                     {"model": f"alekiships:block/wood/watercraft_frame/angled/{wood}/{shape}/{template}",
                      "uvlock": True,
                      "y": rotation[1]}),
                    ({"facing": "south", "shape": f"{shape}_{side}", "frame_processed": processedStates},
                     {"model": f"alekiships:block/wood/watercraft_frame/angled/{wood}/{shape}/{template}",
                      "uvlock": True,
                      "y": rotation[2]}),
                    ({"facing": "west", "shape": f"{shape}_{side}", "frame_processed": processedStates},
                     {"model": f"alekiships:block/wood/watercraft_frame/angled/{wood}/{shape}/{template}",
                      "uvlock": True,
                      "y": rotation[3]})
                ]
    return json
