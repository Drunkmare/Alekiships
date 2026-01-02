package com.alekiponi.alekiships.compat.jei.category;

import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;

import net.minecraft.network.chat.Component;

import org.jetbrains.annotations.Nullable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PROTECTED)
public abstract class JEIRecipeCategory<T> implements IRecipeCategory<T> {

    RecipeType<T> recipeType;
    IDrawable background;
    @Nullable IDrawable icon;
    Component title;
}