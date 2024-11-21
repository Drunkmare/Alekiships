package com.alekiponi.alekiships.client.resources;

import com.alekiponi.alekiships.client.render.texture.PaintedTexture;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.item.DyeColor;
import org.slf4j.Logger;

/**
 * Generates painted textures at runtime from a base texture and a directory of textures with the names of vanilla dyes.
 */
public class PaintedTextureGenerator implements ResourceManagerReloadListener {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final FileToIdConverter TEXTURE_ID_CONVERTER = new FileToIdConverter("textures", ".png");
    /**
     * If somebody has added extra dye to the vanilla enum.
     * We care about this because missing textures are expected <i>and</i> not our problem.
     */
    private static final boolean EXTRA_DYE = DyeColor.values().length > 16;

    private final ResourceLocation baseTexture;
    private final ResourceLocation paintDirectory;

    /**
     * @param baseTexture    The resource location for the base texture which gets painted.
     *                       For example "alekiships:entity/watercraft/rowboat/oak"
     * @param paintDirectory A resource location for the paint texture directory.
     *                       For example "alekiships:entity/watercraft/rowboat/paint".
     *                       The paint textures should be named the color they are for. See
     *                       {@link DyeColor#getSerializedName()}
     */
    public PaintedTextureGenerator(final ResourceLocation baseTexture, final ResourceLocation paintDirectory) {
        this.baseTexture = baseTexture;
        this.paintDirectory = paintDirectory;
    }

    @Override
    public void onResourceManagerReload(final ResourceManager resourceManager) {
        final ResourceLocation baseTexturePath = TEXTURE_ID_CONVERTER.idToFile(this.baseTexture);

        final ResourceLocation basePath = this.baseTexture.withPrefix("textures/");

        if (EXTRA_DYE) {
            LOGGER.warn("A mod added extra dyes. Don't be surprised if some textures don't work!");
        }

        for (final DyeColor dyeColor : DyeColor.values()) {
            final ResourceLocation paintTexturePath = TEXTURE_ID_CONVERTER.idToFile(
                    this.paintDirectory.withSuffix("/" + dyeColor.getSerializedName()));

            final ResourceLocation outPath = basePath.withSuffix("/" + dyeColor.getSerializedName());

            if (resourceManager.getResource(outPath).isPresent()) {
                LOGGER.debug("Skipping {} as it's already present in the resource pack", outPath);
                continue;
            }

            Minecraft.getInstance().getTextureManager()
                    .register(outPath, new PaintedTexture(baseTexturePath, paintTexturePath));
        }
        Minecraft.getInstance().getTextureManager().register(basePath, new SimpleTexture(baseTexturePath));
    }
}