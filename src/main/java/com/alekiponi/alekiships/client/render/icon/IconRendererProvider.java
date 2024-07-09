package com.alekiponi.alekiships.client.render.icon;

import net.minecraft.client.gui.Font;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.Entity;

@FunctionalInterface
public interface IconRendererProvider<E extends Entity> {

    IconRenderer<E> create(Context context);

    record Context(IconRenderDispatcher iconRenderDispatcher, ResourceManager resourceManager, Font font) {
    }
}