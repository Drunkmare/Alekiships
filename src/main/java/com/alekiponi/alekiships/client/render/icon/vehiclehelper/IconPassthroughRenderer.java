package com.alekiponi.alekiships.client.render.icon.vehiclehelper;

import com.alekiponi.alekiships.client.render.icon.IconRenderDispatcher;
import com.alekiponi.alekiships.client.render.icon.IconRenderer;
import com.alekiponi.alekiships.client.render.icon.IconRendererProvider;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;

public class IconPassthroughRenderer<E extends Entity> extends IconRenderer<E> {

    private final IconRenderDispatcher iconRenderDispatcher;

    public IconPassthroughRenderer(final IconRendererProvider.Context context) {
        super(context);
        this.iconRenderDispatcher = context.iconRenderDispatcher();
    }

    @Override
    public void render(final E entity, final LocalPlayer player, final GuiGraphics graphics, final float partialTick) {
        final Entity rootVehicle = entity.getRootVehicle();

        if (entity.is(rootVehicle)) return;

        final var iconRenderer = this.iconRenderDispatcher.getIconRenderer(rootVehicle);

        if (iconRenderer == null) return;

        iconRenderer.render(rootVehicle, player, graphics, partialTick);
    }
}