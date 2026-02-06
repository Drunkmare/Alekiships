package com.alekiponi.alekiships.common.recipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.Contract;

public abstract class NoopRecipe implements Recipe<NoopRecipe.NoopInput> {

    @Override
    @Deprecated
    public boolean canCraftInDimensions(final int width, final int height) {
        return true;
    }

    @Override
    @Deprecated
    @Contract("_, _ -> fail")
    public boolean matches(final NoopInput input, final Level level) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    @Contract("_, _ -> fail")
    public ItemStack assemble(final NoopInput input, final HolderLookup.Provider registries) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    @Contract("_ -> fail")
    public NonNullList<ItemStack> getRemainingItems(final NoopInput input) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    public ItemStack getResultItem(final HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    @Override
    @Deprecated
    public boolean isSpecial() {
        return true;
    }

    public static class NoopInput implements RecipeInput {

        private NoopInput() {
        }

        @Override
        public ItemStack getItem(final int index) {
            return ItemStack.EMPTY;
        }

        @Override
        public int size() {
            return 0;
        }
    }
}