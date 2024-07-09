package com.alekiponi.alekiships.client.render.icon;

import com.alekiponi.alekiships.client.icon.Icon;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

/**
 * A simple {@link IconRenderer} useful for when you only ever display one {@link Icon} at a time
 */
public abstract class SingleIconRenderer<E extends Entity> extends IconRenderer<E> {

    protected SingleIconRenderer(final IconRendererProvider.Context context) {
        super(context);
    }

    @Override
    public final void render(final E entity, final LocalPlayer player, final GuiGraphics graphics,
            final float partialTick) {
        for (final var hand : InteractionHand.values()) {
            final var icon = this.getCurrentIcon(entity, player.getItemInHand(hand), partialTick);
            if (icon == Icon.NONE) continue;
            IconRenderer.renderIcon(graphics, icon, partialTick);
            return;
        }
    }

    /**
     * @param entity    The entity
     * @param heldStack The held stack
     * @return The current {@link Icon}. Return {@link Icon#NONE} if you want to pass
     */
    protected abstract Icon getCurrentIcon(final E entity, final ItemStack heldStack, float partialTick);
}