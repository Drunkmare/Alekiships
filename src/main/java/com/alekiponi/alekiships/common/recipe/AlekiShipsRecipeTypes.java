package com.alekiponi.alekiships.common.recipe;

import com.alekiponi.alekiships.AlekiShips;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;

import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class AlekiShipsRecipeTypes {

    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE,
            AlekiShips.MOD_ID);

    public static final DeferredHolder<RecipeType<?>, RecipeType<EntityMultiblockRecipe>> ENTITY_MULTIBLOCK_RECIPE = register(
            "entity_multiblock_recipe");

    @SuppressWarnings("SameParameterValue")
    private static <R extends Recipe<?>> DeferredHolder<RecipeType<?>, RecipeType<R>> register(final String name) {
        return RECIPE_TYPES.register(name, RecipeType::simple);
    }
}