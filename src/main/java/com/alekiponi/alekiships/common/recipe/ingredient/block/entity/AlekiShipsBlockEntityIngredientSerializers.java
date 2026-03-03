package com.alekiponi.alekiships.common.recipe.ingredient.block.entity;

import com.mojang.serialization.MapCodec;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.common.AlekiShipsRegistries;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class AlekiShipsBlockEntityIngredientSerializers {

    public static final DeferredRegister<BlockEntityIngredientSerializer<?>> BLOCK_ENTITY_INGREDIENT_SERIALIZERS = DeferredRegister.create(
            AlekiShipsRegistries.BLOCK_ENTITY_RESULT_SERIALIZER, AlekiShips.MOD_ID);

    public static final DeferredHolder<BlockEntityIngredientSerializer<?>, BlockEntityIngredientSerializer<FrameBlockEntityIngredient>> FRAME = register(
            "frame", FrameBlockEntityIngredient.CODEC, FrameBlockEntityIngredient.STREAM_CODEC);

    @SuppressWarnings("SameParameterValue")
    private static <R extends BlockEntityIngredient> DeferredHolder<BlockEntityIngredientSerializer<?>, BlockEntityIngredientSerializer<R>> register(
            final String name, final MapCodec<R> codec, final StreamCodec<RegistryFriendlyByteBuf, R> streamCodec) {
        return BLOCK_ENTITY_INGREDIENT_SERIALIZERS.register(name, () -> new BlockEntityIngredientSerializer<>() {
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