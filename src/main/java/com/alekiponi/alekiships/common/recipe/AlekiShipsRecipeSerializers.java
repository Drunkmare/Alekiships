package com.alekiponi.alekiships.common.recipe;

import com.mojang.serialization.MapCodec;

import com.alekiponi.alekiships.AlekiShips;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class AlekiShipsRecipeSerializers {

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(
            Registries.RECIPE_SERIALIZER, AlekiShips.MOD_ID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<EntityMultiblockRecipe>> ENTITY_MULTIBLOCK_RECIPE = register(
            "entity_multiblock_recipe", EntityMultiblockRecipe.CODEC, EntityMultiblockRecipe.STREAM_CODEC);

    @SuppressWarnings("SameParameterValue")
    private static <R extends Recipe<?>> DeferredHolder<RecipeSerializer<?>, RecipeSerializer<R>> register(
            final String name, final MapCodec<R> codec, final StreamCodec<RegistryFriendlyByteBuf, R> streamCodec) {
        return RECIPE_SERIALIZERS.register(name, () -> new RecipeSerializer<>() {
            @Override
            public MapCodec<R> codec() {
                return codec;
            }

            @Override
            public StreamCodec<RegistryFriendlyByteBuf, R> streamCodec() {
                return streamCodec;
            }
        });
    }
}