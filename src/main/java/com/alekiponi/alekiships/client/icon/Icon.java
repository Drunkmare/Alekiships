package com.alekiponi.alekiships.client.icon;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.client.render.icon.IconRenderer;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.GuiSpriteManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

/**
 * An icon. Primarily used for {@link IconRenderer}s.
 *
 * @see Icon#NONE
 * @see Icon.DefaultIcons
 * @see Icon#item(ItemLike)
 * @see Icon#item(ItemStack)
 * @see Icon#sprite(ResourceLocation, int, int)
 */
public interface Icon {
    /**
     * Our Noop icon object. This should be treated like {@link ItemStack#EMPTY} and friends
     */
    Icon NONE = new Icon() {
        @Override
        public void render(final GuiGraphics graphics, final int x, final int y, final float partialTick) {
        }

        @Override
        public int width() {
            return 0;
        }

        @Override
        public int height() {
            return 0;
        }
    };

    /**
     * @param sprite The sprite location in {@link GuiSpriteManager#getSprite(ResourceLocation)}
     * @param width  The sprite width
     * @param height The sprite height
     */
    static Icon sprite(final ResourceLocation sprite, final int width, final int height) {
        return new SpriteIcon(sprite, width, height);
    }

    /**
     * @param item The item to create an icon of
     *
     * @see Icon#item(ItemStack)
     */
    static Icon item(final ItemLike item) {
        return Icon.item(new ItemStack(item));
    }

    /**
     * @param itemStack The item to create an icon of. <strong>Mutations will modify the icon</strong>
     */
    static Icon item(final ItemStack itemStack) {
        return new ItemIcon(itemStack);
    }

    /**
     * @param font      The font used for rendering text
     * @param itemStack The Item Stack
     */
    static Icon itemWithDecorations(final Font font, final ItemStack itemStack) {
        return Icon.itemWithDecorations(font, itemStack, null);
    }

    /**
     * @param font      The font used for rendering text
     * @param itemStack The Item Stack
     * @param text      The custom text to display. Can be null.
     */
    static Icon itemWithDecorations(final Font font, final ItemStack itemStack, final @Nullable String text) {
        return new ItemIcon.DecoratedItemIcon(font, itemStack, text);
    }

    /**
     * @param icons The icons. At least one is required
     *
     * @return An icon consisting of a row of icons
     */
    static Icon row(final Icon... icons) {
        return Icon.row(3, icons);
    }

    /**
     * @param padding The padding in pixels between each icon
     * @param icons   The icons. At least one is required
     *
     * @return An icon consisting of a row of icons
     */
    static Icon row(final @Range(from = 0, to = Integer.MAX_VALUE) int padding, final Icon... icons) {
        assert icons.length > 0;
        if (icons.length == 1) return icons[0];

        Icon ret = icons[0];
        for (int i = 1; i < icons.length; i++) {
            ret = icons[i].offsetX(ret.width() + padding).merge(ret);
        }

        return ret;
    }

    /**
     * @param icons The icons. At least one is required
     *
     * @return An icon consisting of a column of icons
     */
    static Icon column(final Icon... icons) {
        return Icon.column(1, icons);
    }

    /**
     * @param padding The padding in pixels between each icon
     * @param icons   The icons. At least one is required
     *
     * @return An icon consisting of a column of icons
     */
    static Icon column(final @Range(from = 0, to = Integer.MAX_VALUE) int padding, final Icon... icons) {
        assert icons.length > 0;
        if (icons.length == 1) return icons[0];

        Icon ret = icons[0];
        for (int i = 1; i < icons.length; i++) {
            ret = icons[i].offsetY(ret.height() + padding).merge(ret);
        }

        return ret;
    }

    /**
     * Merge two icons together into a single icon.
     *
     * @param a Icon a
     * @param b Icon b
     *
     * @return An icon which renders the two parent icons with the width and height of their parents sum
     *
     * @see Icon#merge(Icon)
     */
    static Icon merge(final Icon a, final Icon b) {
        if (a == NONE) return b;
        if (b == NONE) return a;
        return new CompositeIcon(a, b);
    }

    /**
     * Renders the icon to the screen at the provided coordinates
     *
     * @param graphics    The {@link GuiGraphics} helper
     * @param x           The x position
     * @param y           The y position
     * @param partialTick The partial tick
     */
    void render(GuiGraphics graphics, int x, int y, float partialTick);

    /**
     * @return Icon width
     */
    int width();

    /**
     * @return Icon height
     */
    int height();

    /**
     * Offset where the icon will render
     *
     * @param xOffset The x offset
     * @param yOffset The y offset
     *
     * @return An icon which renders with the provided offsets
     *
     * @see Icon#offsetX(int)
     * @see Icon#offsetY(int)
     */
    default Icon offset(final int xOffset, final int yOffset) {
        if (this == NONE) return this;
        return new Icon() {
            @Override
            public void render(final GuiGraphics graphics, final int x, final int y, final float partialTick) {
                Icon.this.render(graphics, x + xOffset, y + yOffset, partialTick);
            }

            @Override
            public int width() {
                return Icon.this.width();
            }

            @Override
            public int height() {
                return Icon.this.height();
            }
        };
    }

    /**
     * @param offset The x offset
     *
     * @see Icon#offset(int, int)
     * @see Icon#offsetY(int)
     */
    @SuppressWarnings("unused")
    default Icon offsetX(final int offset) {
        return this.offset(offset, 0);
    }

    /**
     * @param offset The y offset
     *
     * @see Icon#offset(int, int)
     * @see Icon#offsetX(int)
     */
    @SuppressWarnings("unused")
    default Icon offsetY(final int offset) {
        return this.offset(0, offset);
    }

    /**
     * Merge the provided icon "onto" {@code this}
     *
     * @param other The other icon to merge
     *
     * @see Icon#merge(Icon, Icon)
     */
    default Icon merge(final Icon other) {
        return Icon.merge(this, other);
    }

    /**
     * The default icons provided by NiftyShips
     */
    final class DefaultIcons {
        private static final int WIDTH = 9, HEIGHT = 9;

        public static final Icon HELM = defaultSize(AlekiShips.location("icon/helm"));
        public static final Icon BLOCK = defaultSize(AlekiShips.location("icon/block"));
        public static final Icon SAIL = defaultSize(AlekiShips.location("icon/sail"));
        public static final Icon PADDLE = defaultSize(AlekiShips.location("icon/paddle"));
        public static final Icon SEAT = defaultSize(AlekiShips.location("icon/seat"));
        public static final Icon EJECT = defaultSize(AlekiShips.location("icon/eject"));
        public static final Icon LEAD = defaultSize(AlekiShips.location("icon/lead"));
        public static final Icon ARROW_UP = defaultSize(AlekiShips.location("icon/arrow_up"));
        public static final Icon ARROW_DOWN = defaultSize(AlekiShips.location("icon/arrow_down"));
        public static final Icon ANCHOR = defaultSize(AlekiShips.location("icon/anchor"));
        public static final Icon BRUSH = defaultSize(AlekiShips.location("icon/brush"));
        public static final Icon HAMMER = defaultSize(AlekiShips.location("icon/hammer"));

        private static Icon defaultSize(final ResourceLocation sprite) {
            return sprite(sprite, WIDTH, HEIGHT);
        }
    }
}