package com.alekiponi.alekiships.data.providers;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.common.AlekiShipsRegistries;
import com.alekiponi.alekiships.common.block.AlekiShipsBlocks;
import com.alekiponi.alekiships.common.block.AngledWoodenBoatFrameBlock;
import com.alekiponi.alekiships.common.item.AlekiShipsItems;
import com.alekiponi.alekiships.common.recipe.EntityMultiblockRecipe;
import com.alekiponi.alekiships.common.recipe.entity.ConstructionSloopResult;
import com.alekiponi.alekiships.common.recipe.entity.RowboatResult;
import com.alekiponi.alekiships.common.recipe.ingredient.block.matcher.PropertyMatcher;
import com.alekiponi.alekiships.data.recipes.CraftingRecipeBuilder;
import com.alekiponi.alekiships.data.util.EntityMultiblockHelper;
import com.alekiponi.alekiships.util.OverworldWood;
import com.alekiponi.alekiships.util.Wood;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;

import net.neoforged.neoforge.common.Tags;

import java.util.concurrent.CompletableFuture;

public class AlekiShipsRecipeProvider extends RecipeProvider {

    public AlekiShipsRecipeProvider(final PackOutput packOutput,
            final CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider);
    }

    protected static <T extends Wood> void createRowboatRecipes(final RecipeOutput recipeOutput,
            final HolderLookup.Provider holderLookup, final T[] woodType) {
        final var fullyProcessedMatcher = PropertyMatcher.single(AngledWoodenBoatFrameBlock.FRAME_PROCESSED,
                AngledWoodenBoatFrameBlock.FULLY_PROCESSED);

        final var rowboatTypes = holderLookup.lookupOrThrow(AlekiShipsRegistries.ROWBOAT_VARIANT);
        final var frameMaterials = holderLookup.lookupOrThrow(AlekiShipsRegistries.FRAME_MATERIAL);
        for (final var wood : woodType) {
            recipeOutput.accept(AlekiShips.location("entity_multiblock/rowboat/" + wood.getSerializedName()),
                    EntityMultiblockRecipe.builder()
                            .pattern(EntityMultiblockHelper.rowboatPattern(
                                    AlekiShipsBlocks.WOODEN_BOAT_FRAME_ANGLED.get(), fullyProcessedMatcher,
                                    frameMaterials.getOrThrow(wood.frameMaterialKey())))
                            .entityResult(RowboatResult.of(rowboatTypes.getOrThrow(wood.rowboatKey())))
                            .build(), null);
        }
    }

    protected static <T extends Wood> void createSloopRecipes(final RecipeOutput recipeOutput,
            final HolderLookup.Provider holderLookup, final T[] woodType) {
        final var fullyProcessedMatcher = PropertyMatcher.single(AngledWoodenBoatFrameBlock.FRAME_PROCESSED,
                AngledWoodenBoatFrameBlock.FULLY_PROCESSED);

        final var constructionSloopTypes = holderLookup.lookupOrThrow(AlekiShipsRegistries.CONSTRUCTION_SLOOP_VARIANT);
        final var frameMaterials = holderLookup.lookupOrThrow(AlekiShipsRegistries.FRAME_MATERIAL);
        for (final var wood : woodType) {
            recipeOutput.accept(AlekiShips.location("entity_multiblock/construction_sloop/" + wood.getSerializedName()),
                    EntityMultiblockRecipe.builder()
                            .pattern(
                                    EntityMultiblockHelper.sloopPattern(AlekiShipsBlocks.WOODEN_BOAT_FRAME_ANGLED.get(),
                                            AlekiShipsBlocks.WOODEN_BOAT_FRAME_FLAT.get(), fullyProcessedMatcher,
                                            frameMaterials.getOrThrow(wood.frameMaterialKey())))
                            .entityResult(ConstructionSloopResult.of(
                                    constructionSloopTypes.getOrThrow(wood.sloopConstructionKey())))
                            .build(), null);
        }
    }

    @Override
    protected void buildRecipes(final RecipeOutput recipeOutput) {
        CraftingRecipeBuilder.shaped(AlekiShipsBlocks.BOAT_FRAME_ANGLED.get(), 5)
                .pattern("  S", " SS", "SS ")
                .define('S', Items.SCAFFOLDING)
                .unlockedBy("has_scaffolding", has(Items.SCAFFOLDING))
                .save(recipeOutput);

        CraftingRecipeBuilder.shaped(AlekiShipsBlocks.BOAT_FRAME_FLAT.get(), 3)
                .pattern("SSS")
                .define('S', Items.SCAFFOLDING)
                .unlockedBy("has_scaffolding", has(Items.SCAFFOLDING))
                .save(recipeOutput);

        CraftingRecipeBuilder.shaped(AlekiShipsItems.OAR.get())
                .pattern("  S", " S ", "L  ")
                .define('S', Tags.Items.RODS_WOODEN)
                .define('L', ItemTags.WOODEN_SLABS)
                .unlockedBy("has_stick", has(Items.STICK))
                .unlockedBy("has_wooden_slab", has(ItemTags.WOODEN_SLABS))
                .save(recipeOutput);

        CraftingRecipeBuilder.shaped(AlekiShipsItems.CANNON.get())
                .pattern("BBB", "LL ", "R R")
                .define('B', Items.IRON_BLOCK)
                .define('L', ItemTags.WOODEN_SLABS)
                .define('R', Items.IRON_NUGGET)
                .unlockedBy("has_iron_block", has(Items.IRON_BLOCK))
                .unlockedBy("has_wooden_slab", has(ItemTags.WOODEN_SLABS))
                .unlockedBy("has_iron_nugget", has(Items.IRON_NUGGET))
                .save(recipeOutput);

        CraftingRecipeBuilder.shaped(AlekiShipsItems.ANCHOR.get())
                .pattern("NIN", " I ", "IBI")
                .define('N', Items.IRON_NUGGET)
                .define('I', Items.IRON_INGOT)
                .define('B', Items.IRON_BLOCK)
                .unlockedBy("has_iron_nugget", has(Items.IRON_NUGGET))
                .unlockedBy("has_iron_ingot", has(Items.IRON_INGOT))
                .unlockedBy("has_iron_block", has(Items.IRON_BLOCK))
                .save(recipeOutput);

        CraftingRecipeBuilder.shaped(AlekiShipsBlocks.CLEAT.get())
                .pattern("III", "N N")
                .define('N', Items.IRON_NUGGET)
                .define('I', Items.IRON_INGOT)
                .unlockedBy("has_iron_nugget", has(Items.IRON_NUGGET))
                .unlockedBy("has_iron_ingot", has(Items.IRON_INGOT))
                .save(recipeOutput);

        CraftingRecipeBuilder.shaped(AlekiShipsBlocks.OARLOCK.get())
                .pattern(" I ", "III")
                .define('I', Items.IRON_INGOT)
                .unlockedBy("has_iron_ingot", has(Items.IRON_INGOT))
                .save(recipeOutput);

        CraftingRecipeBuilder.shapeless(AlekiShipsItems.CANNONBALL.get())
                .requires(Items.IRON_INGOT)
                .requires(Items.PAPER)
                .requires(Items.GUNPOWDER)
                .unlockedBy("has_iron_ingot", has(Items.IRON_INGOT))
                .unlockedBy("has_paper", has(Items.PAPER))
                .unlockedBy("has_gunpowder", has(Items.GUNPOWDER))
                .save(recipeOutput);
    }

    @Override
    protected void buildRecipes(final RecipeOutput recipeOutput, final HolderLookup.Provider holderLookup) {
        super.buildRecipes(recipeOutput, holderLookup);

        createRowboatRecipes(recipeOutput, holderLookup, OverworldWood.values());
        createSloopRecipes(recipeOutput, holderLookup, OverworldWood.values());
    }
}