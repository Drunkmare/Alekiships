package com.alekiponi.alekiships.client.icon;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.GuiSpriteManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;

/**
 * @param sprite The sprite location in {@link GuiSpriteManager#getSprite(ResourceLocation)}
 * @param width  The sprite width
 * @param height The sprite height
 */
record SpriteIcon(ResourceLocation sprite, int width, int height) implements Icon {

    @Override
    public void render(final GuiGraphics graphics, final int x, final int y, final float partialTick) {
        if (this.sprite == TextureManager.INTENTIONAL_MISSING_TEXTURE) return;
        graphics.blitSprite(this.sprite, x - this.width, y - this.height, this.width, this.height);
    }
}
