from mcresources import ResourceManager

import constants


def generate(manager: ResourceManager):
    manager.tag("unfinished_sloop", "worldgen/structure", "alekiships:unfinished_sloop_birch",
                "alekiships:unfinished_sloop_cherry"
                , "alekiships:unfinished_sloop_dark_oak", "alekiships:unfinished_sloop_oak",
                "alekiships:unfinished_sloop_spruce")

    manager.tag("unfinished_rowboat", "worldgen/structure", "alekiships:unfinished_rowboat_birch",
                "alekiships:unfinished_rowboat_cherry"
                , "alekiships:unfinished_rowboat_dark_oak", "alekiships:unfinished_rowboat_oak",
                "alekiships:unfinished_rowboat_spruce")
