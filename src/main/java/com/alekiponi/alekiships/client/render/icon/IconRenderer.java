package com.alekiponi.alekiships.client.render.icon;

import com.alekiponi.alekiships.client.icon.Icon;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;

public abstract class IconRenderer<E extends Entity> {

    public static final int OFFSET_X = -8;
    public static final int OFFSET_Y = 4;

    protected IconRenderer(@SuppressWarnings("unused") final IconRendererProvider.Context context) {
        // Future proofing
    }

    /**
     * @param icon        The icon to render
     * @param partialTick The partial tick
     */
    protected static void renderIcon(final GuiGraphics graphics, final Icon icon, final float partialTick) {
        IconRenderer.renderIcon(graphics, icon, partialTick, OFFSET_X, OFFSET_Y);
    }

    /**
     * @param icon        The icon to render
     * @param partialTick The partial tick
     * @param xOffset     The x offset
     * @param yOffset     The y offset
     */
    @SuppressWarnings("SameParameterValue")
    protected static void renderIcon(final GuiGraphics graphics, final Icon icon, final float partialTick,
            final int xOffset, final int yOffset) {
        IconRenderer.renderIcon(graphics, icon, partialTick, (graphics.guiWidth() / 2), (graphics.guiHeight() / 2),
                xOffset, yOffset);
    }

    /**
     * @param icon        The icon to render
     * @param partialTick The partial tick
     * @param x           The x position
     * @param y           The y position
     * @param xOffset     The x offset
     * @param yOffset     The y offset
     */
    protected static void renderIcon(final GuiGraphics graphics, final Icon icon, final float partialTick, final int x,
            final int y, final int xOffset, final int yOffset) {
        icon.render(graphics, x + xOffset, y + yOffset, partialTick);
    }

    /**
     * @param entity      The entity to render an icon for
     * @param player      The local player passed in for convince
     * @param graphics    The graphics helper
     * @param partialTick The partial tick
     */
    public abstract void render(final E entity, final LocalPlayer player, final GuiGraphics graphics,
            final float partialTick);

    /**
     * Called on the first render after "switching" entities. Use this to reset any semi persistent data such as
     * tracking time between frames to do some sort of animation.
     *
     * @param entity The entity to render an icon for
     * @param player The local player passed in for convince
     */
    public void resetRenderer(final E entity, final LocalPlayer player) {
    }
}