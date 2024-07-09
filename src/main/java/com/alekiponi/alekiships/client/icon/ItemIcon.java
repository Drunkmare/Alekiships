package com.alekiponi.alekiships.client.icon;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * @param item The item to render
 */
record ItemIcon(ItemStack item) implements Icon {

    public static final int ITEM_WIDTH = 16;
    public static final int ITEM_HEIGHT = 16;

    @Override
    public void render(final GuiGraphics graphics, final int x, final int y, final float partialTick) {
        graphics.renderFakeItem(this.item, x - ITEM_WIDTH, y - ITEM_HEIGHT);
    }

    @Override
    public int width() {
        return ITEM_WIDTH;
    }

    @Override
    public int height() {
        return ITEM_HEIGHT;
    }

    /**
     * @param font The font
     * @param item The item to render
     * @param text The custom text to display
     */
    record DecoratedItemIcon(Font font, ItemStack item, @Nullable String text) implements Icon {

        @Override
        public void render(final GuiGraphics graphics, final int x, final int y, final float partialTick) {
            graphics.renderFakeItem(this.item, x - ITEM_WIDTH, y - ITEM_HEIGHT);
            graphics.renderItemDecorations(this.font, this.item, x - ITEM_WIDTH, y - ITEM_HEIGHT, this.text);
        }

        @Override
        public int width() {
            return ITEM_WIDTH;
        }

        @Override
        public int height() {
            return ITEM_HEIGHT;
        }
    }
}
