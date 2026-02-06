package com.alekiponi.alekiships.common.recipe.ingredient.block.entity;

import com.mojang.serialization.MapCodec;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public interface BlockEntityIngredientSerializer<T extends BlockEntityIngredient> {

    MapCodec<T> codec();

    StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec();
}