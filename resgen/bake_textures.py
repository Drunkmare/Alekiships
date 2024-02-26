import os

from PIL import Image

import constants


def main():
    # Move into the watercraft folder to make the paths shorter
    path = "../src/main/resources/assets/alekiships/textures/entity/watercraft"

    for wood, base_texture in zip(constants.WOODS,
                                  [Image.open(f"textures/rowboat/{wood}.png") for wood in constants.WOODS]):
        # Ensure path exists
        os.makedirs(f"{path}/rowboat/{wood}", exist_ok=True)
        # Convert and save the non paint texture
        base_texture.convert("P").save(f"{path}/rowboat/{wood}/normal.png", optimize=True)

        overlay_colors(base_texture, "textures/rowboat/paint", f"{path}/rowboat/{wood}")


def overlay_colors(base_texture: Image.Image, color_overlay_path: str, output_path: str):
    for color, overlay in zip(constants.COLORS,
                              [Image.open(f"{color_overlay_path}/{color}.png") for color in constants.COLORS]):
        # Composite the overlay, convert and save
        Image.alpha_composite(base_texture,
                              overlay).convert("P").save(f"{f"{output_path}/{color}"}.png")


if __name__ == '__main__':
    main()
