package com.alekiponi.alekiships.mixins.accessors;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.world.item.crafting.*;

import java.util.Collection;

@Mixin(RecipeManager.class)
public interface RecipeManagerAccessor {

    @Invoker("byType")
    <I extends RecipeInput, T extends Recipe<I>> Collection<RecipeHolder<T>> invoke$byType(RecipeType<T> type);
}