package com.alekiponi.alekiships.client.icon;

import net.minecraft.client.gui.GuiGraphics;

/**
 * @param a The icon 'a'
 * @param b The icon 'b'
 */
record CompositeIcon(Icon a, Icon b) implements Icon {

    @Override
    public void render(final GuiGraphics graphics, final int x, final int y, final float partialTick) {
        this.a.render(graphics, x, y, partialTick);
        this.b.render(graphics, x, y, partialTick);
    }

    @Override
    public int width() {
        return this.a.width() + this.b.width();
    }

    @Override
    public int height() {
        return this.a.height() + this.b.height();
    }
}
