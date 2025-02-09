package com.alekiponi.alekiships.data.recipes;


import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.NonNullList;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;

import java.util.*;
import org.jetbrains.annotations.Nullable;

/**
 * An enhanced crafting recipe builder allowing folder names. Also contains all factory functions for the related builders
 * like {@link #shaped(String, CraftingBookCategory, ItemLike, int)} and {@link #shapeless(String, CraftingBookCategory, ItemLike, int)}
 */
@SuppressWarnings({"UnusedReturnValue", "unused"})
public abstract class CraftingRecipeBuilder<B extends CraftingRecipeBuilder<B>> implements RecipeBuilder {

    protected final String folderName;
    protected final CraftingBookCategory craftingBookCategory;
    protected final Advancement.Builder advancement = Advancement.Builder.recipeAdvancement();
    protected final ItemStack result;
    private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();
    @Nullable
    protected String group;
    protected boolean damageInputs;

    protected CraftingRecipeBuilder(final String folderName, final CraftingBookCategory craftingBookCategory,
            final ItemStack result) {
        this.folderName = folderName;
        this.craftingBookCategory = craftingBookCategory;
        this.result = result;
    }

    public static ShapelessCraftingRecipeBuilder shapeless(final ItemLike result) {
        return shapeless("crafting", result);
    }

    public static ShapelessCraftingRecipeBuilder shapeless(final ItemLike result, final int count) {
        return shapeless("crafting", CraftingBookCategory.MISC, result, count);
    }

    public static ShapelessCraftingRecipeBuilder shapeless(final String folderName, final ItemLike result) {
        return shapeless(folderName, CraftingBookCategory.MISC, result, 1);
    }

    public static ShapelessCraftingRecipeBuilder shapeless(final String folderName,
            final CraftingBookCategory craftingBookCategory, final ItemLike result, final int count) {
        return new ShapelessCraftingRecipeBuilder(folderName, craftingBookCategory, result, count);
    }

    public static ShapedCraftingRecipeBuilder shaped(final ItemLike result) {
        return shaped("crafting", result);
    }

    public static ShapedCraftingRecipeBuilder shaped(final ItemLike result, final int count) {
        return shaped("crafting", CraftingBookCategory.MISC, result, count);
    }

    public static ShapedCraftingRecipeBuilder shaped(final String folderName, final ItemLike result) {
        return shaped(folderName, CraftingBookCategory.MISC, result, 1);
    }

    public static ShapedCraftingRecipeBuilder shaped(final String folderName,
            final CraftingBookCategory craftingBookCategory, final ItemLike result, final int count) {
        return new ShapedCraftingRecipeBuilder(folderName, craftingBookCategory, result, count);
    }

    @Override
    public B unlockedBy(final String criterionName, final Criterion<?> criterion) {
        criteria.put(criterionName, criterion);
        return self();
    }

    @Override
    public B group(@Nullable final String groupName) {
        group = groupName;
        return self();
    }

    @Override
    public Item getResult() {
        return result.getItem();
    }

    @Override
    public void save(final RecipeOutput recipeOutput, final ResourceLocation recipeId) {
        ensureValid(recipeId);
        Advancement.Builder builder = recipeOutput.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(recipeId))
                .rewards(AdvancementRewards.Builder.recipe(recipeId)).requirements(AdvancementRequirements.Strategy.OR);
        criteria.forEach(builder::addCriterion);
        recipeOutput.accept(recipeId, createRecipe(),
                builder.build(recipeId.withPrefix("recipes/" + folderName + "/")));
    }

    @Override
    public void save(final RecipeOutput recipeOutput) {
        save(recipeOutput, RecipeBuilder.getDefaultRecipeId(getResult()).withPrefix(folderName + "/"));
    }

    /**
     * Makes sure that this recipe is valid and obtainable.
     */
    protected void ensureValid(final ResourceLocation recipeId) {
        if (criteria.isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + recipeId);
        }
    }

    protected abstract B self();

    protected abstract Recipe<?> createRecipe();

    public static final class ShapedCraftingRecipeBuilder extends CraftingRecipeBuilder<ShapedCraftingRecipeBuilder> {

        private final List<String> rows = Lists.newArrayList();
        private final Map<Character, Ingredient> key = Maps.newLinkedHashMap();
        private boolean showNotification = true;

        private ShapedCraftingRecipeBuilder(final String folderName, final CraftingBookCategory craftingBookCategory,
                final ItemLike result, final int count) {
            super(folderName, craftingBookCategory, new ItemStack(result, count));
        }

        /**
         * Adds a tag key to the recipe pattern.
         */
        public ShapedCraftingRecipeBuilder define(final Character symbol, final TagKey<Item> tag) {
            return define(symbol, Ingredient.of(tag));
        }

        /**
         * Adds an item key to the recipe pattern.
         */
        public ShapedCraftingRecipeBuilder define(final Character symbol, final ItemLike item) {
            return define(symbol, Ingredient.of(item));
        }

        /**
         * Adds an ingredient key to the recipe pattern.
         */
        public ShapedCraftingRecipeBuilder define(final Character symbol, final Ingredient ingredient) {
            if (key.containsKey(symbol)) {
                throw new IllegalArgumentException("Symbol '" + symbol + "' is already defined!");
            }

            if (symbol == ' ') {
                throw new IllegalArgumentException("Symbol ' ' (whitespace) is reserved and cannot be defined");
            }

            key.put(symbol, ingredient);
            return self();
        }

        /**
         * Adds a new row to the pattern for this recipe.
         */
        public ShapedCraftingRecipeBuilder pattern(final String pattern) {
            if (!rows.isEmpty() && pattern.length() != rows.getFirst().length()) {
                throw new IllegalArgumentException("Pattern must be the same width on every line!");
            }

            rows.add(pattern);
            return self();
        }

        /**
         * Adds multiple rows to the pattern for this recipe.
         */
        public ShapedCraftingRecipeBuilder pattern(final String... pattern) {
            Arrays.stream(pattern).forEach(this::pattern);
            return self();
        }

        public ShapedCraftingRecipeBuilder showNotification(final boolean showNotification) {
            this.showNotification = showNotification;
            return self();
        }

        @Override
        protected void ensureValid(final ResourceLocation recipeId) {
            super.ensureValid(recipeId);

            if (rows.isEmpty()) {
                throw new IllegalStateException("No pattern is defined for shaped recipe " + recipeId + "!");
            }

            final Set<Character> set = Sets.newHashSet(key.keySet());
            set.remove(' ');

            for (final String pattern : rows) {
                for (int i = 0; i < pattern.length(); ++i) {
                    final char symbol = pattern.charAt(i);
                    if (!key.containsKey(symbol) && symbol != ' ') {
                        throw new IllegalStateException(
                                "Pattern in recipe " + recipeId + " uses undefined symbol '" + symbol + "'");
                    }

                    set.remove(symbol);
                }
            }

            if (!set.isEmpty()) {
                throw new IllegalStateException(
                        "Ingredients are defined but not used in pattern for recipe " + recipeId);
            }

            if (rows.size() == 1 && rows.getFirst().length() == 1) {
                throw new IllegalStateException(
                        "Shaped recipe " + recipeId + " only takes in a single item - it should be a shapeless recipe instead");
            }
        }

        @Override
        protected ShapedCraftingRecipeBuilder self() {
            return this;
        }

        @Override
        protected ShapedRecipe createRecipe() {
            return new ShapedRecipe(group == null ? "" : group, craftingBookCategory, ShapedRecipePattern.of(key, rows),
                    result, showNotification);
        }
    }

    public static final class ShapelessCraftingRecipeBuilder extends CraftingRecipeBuilder<ShapelessCraftingRecipeBuilder> {

        private final NonNullList<Ingredient> ingredients = NonNullList.create();

        private ShapelessCraftingRecipeBuilder(final String folderName, final CraftingBookCategory craftingBookCategory,
                final ItemLike result, final int count) {
            super(folderName, craftingBookCategory, new ItemStack(result, count));
        }

        /**
         * Adds an ingredient that can be any item in the given tag.
         */
        public ShapelessCraftingRecipeBuilder requires(final TagKey<Item> tag) {
            return requires(Ingredient.of(tag));
        }

        /**
         * Adds an ingredient of the given item.
         */
        public ShapelessCraftingRecipeBuilder requires(final ItemLike item) {
            return requires(Ingredient.of(item));
        }

        /**
         * Adds the given item as an ingredient multiple times.
         */
        public ShapelessCraftingRecipeBuilder requires(final ItemLike item, final int quantity) {
            for (int i = 0; i < quantity; ++i) requires(item);
            return this;
        }

        /**
         * Adds an ingredient.
         */
        public ShapelessCraftingRecipeBuilder requires(final Ingredient ingredient) {
            ingredients.add(ingredient);
            return this;
        }

        /**
         * Adds an ingredient multiple times.
         */
        public ShapelessCraftingRecipeBuilder requires(final Ingredient ingredient, final int quantity) {
            for (int i = 0; i < quantity; ++i) requires(ingredient);

            return this;
        }

        @Override
        protected void ensureValid(final ResourceLocation recipeId) {
            super.ensureValid(recipeId);
            if (ingredients.isEmpty()) throw new IllegalStateException("Recipe must have at least 1 ingredient");
        }

        @Override
        protected ShapelessCraftingRecipeBuilder self() {
            return this;
        }

        @Override
        protected ShapelessRecipe createRecipe() {
            return new ShapelessRecipe(group == null ? "" : group, craftingBookCategory, result, ingredients);
        }
    }
}