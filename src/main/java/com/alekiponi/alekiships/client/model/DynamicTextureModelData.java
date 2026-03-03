package com.alekiponi.alekiships.client.model;

import net.minecraft.resources.ResourceLocation;

import net.neoforged.neoforge.client.model.data.ModelProperty;

/**
 * Model data for {@link DynamicTextureModel}
 *
 * @param texture
 */
public record DynamicTextureModelData(ResourceLocation texture) {
    public static final ModelProperty<DynamicTextureModelData> PROPERTY = new ModelProperty<>();
}