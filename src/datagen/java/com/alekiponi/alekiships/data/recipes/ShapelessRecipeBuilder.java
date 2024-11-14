package com.alekiponi.alekiships.data.recipes;

import com.google.common.collect.Lists;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.CraftingRecipeBuilder;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

/**
 * Custom builder for vanillas shapeless recipes. Vanillas doesn't let us directly control the folder name which prevents
 * us from outputting crafting recipes under "recipes/crafting/"
 */
public class ShapelessRecipeBuilder extends CraftingRecipeBuilder implements RecipeBuilder {
    private final String folderName;
    private final CraftingBookCategory craftingBookCategory;
    private final Item result;
    private final int count;
    private final List<Ingredient> ingredients = Lists.newArrayList();
    private final Advancement.Builder advancement = Advancement.Builder.recipeAdvancement();
    @Nullable
    private String group;

    public ShapelessRecipeBuilder(final CraftingBookCategory craftingBookCategory, final String folderName,
            final ItemLike result, final int count) {
        this.craftingBookCategory = craftingBookCategory;
        this.folderName = folderName;
        this.result = result.asItem();
        this.count = count;
    }


    public static ShapelessRecipeBuilder shapeless(final ItemLike result) {
        return shapeless("crafting", result);
    }

    public static ShapelessRecipeBuilder shapeless(final ItemLike result, final int count) {
        return shapeless("crafting", result, count);
    }

    public static ShapelessRecipeBuilder shapeless(final String folderName, final ItemLike result) {
        return shapeless(folderName, result, 1);
    }

    public static ShapelessRecipeBuilder shapeless(final String folderName, final ItemLike result, final int count) {
        return new ShapelessRecipeBuilder(CraftingBookCategory.MISC, folderName, result, count);
    }

    /**
     * Adds an ingredient that can be any item in the given tag.
     */
    public ShapelessRecipeBuilder requires(TagKey<Item> pTag) {
        return this.requires(Ingredient.of(pTag));
    }

    /**
     * Adds an ingredient of the given item.
     */
    public ShapelessRecipeBuilder requires(ItemLike pItem) {
        return this.requires(pItem, 1);
    }

    /**
     * Adds the given ingredient multiple times.
     */
    public ShapelessRecipeBuilder requires(ItemLike pItem, int pQuantity) {
        for (int i = 0; i < pQuantity; ++i) {
            this.requires(Ingredient.of(pItem));
        }

        return this;
    }

    /**
     * Adds an ingredient.
     */
    public ShapelessRecipeBuilder requires(final Ingredient ingredient) {
        return this.requires(ingredient, 1);
    }

    /**
     * Adds an ingredient multiple times.
     */
    public ShapelessRecipeBuilder requires(final Ingredient ingredient, final int quantity) {
        for (int i = 0; i < quantity; ++i) {
            this.ingredients.add(ingredient);
        }

        return this;
    }

    @Override
    public ShapelessRecipeBuilder unlockedBy(final String criterionName,
            final CriterionTriggerInstance criterionTrigger) {
        this.advancement.addCriterion(criterionName, criterionTrigger);
        return this;
    }

    @Override
    public ShapelessRecipeBuilder group(@Nullable final String groupName) {
        this.group = groupName;
        return this;
    }

    @Override
    public Item getResult() {
        return this.result;
    }

    @Override
    public void save(final Consumer<FinishedRecipe> finishedRecipeConsumer) {
        this.save(finishedRecipeConsumer,
                RecipeBuilder.getDefaultRecipeId(this.getResult()).withPrefix(this.folderName + "/"));
    }

    @Override
    public void save(final Consumer<FinishedRecipe> finishedRecipeConsumer, final ResourceLocation recipeId) {
        this.ensureValid(recipeId);
        this.advancement.parent(ROOT_RECIPE_ADVANCEMENT)
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(recipeId))
                .rewards(AdvancementRewards.Builder.recipe(recipeId)).requirements(RequirementsStrategy.OR);
        finishedRecipeConsumer.accept(
                new net.minecraft.data.recipes.ShapelessRecipeBuilder.Result(recipeId, this.result, this.count,
                        this.group == null ? "" : this.group, this.craftingBookCategory, this.ingredients,
                        this.advancement, recipeId.withPrefix("recipes/" + this.folderName + "/")));
    }

    /**
     * Makes sure that this recipe is valid and obtainable.
     */
    private void ensureValid(final ResourceLocation recipeId) {
        if (this.advancement.getCriteria().isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + recipeId);
        }
    }
}