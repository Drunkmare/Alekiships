package com.alekiponi.alekiships.data.providers;

import com.alekiponi.alekiships.common.block.AlekiShipsBlocks;
import com.alekiponi.alekiships.common.item.AlekiShipsItems;
import com.alekiponi.alekiships.data.recipes.ShapedRecipeBuilder;
import com.alekiponi.alekiships.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.Tags;

import java.util.function.Consumer;

public class AlekiShipsRecipeProvider extends RecipeProvider {

    public AlekiShipsRecipeProvider(final PackOutput packOutput) {
        super(packOutput);
    }

    @Override
    protected void buildRecipes(final Consumer<FinishedRecipe> writer) {
        ShapedRecipeBuilder.shaped(AlekiShipsBlocks.BOAT_FRAME_ANGLED.get(), 5).pattern("  S", " SS", "SS ")
                .define('S', Items.SCAFFOLDING).unlockedBy("has_scaffolding", has(Items.SCAFFOLDING)).save(writer);

        ShapedRecipeBuilder.shaped(AlekiShipsBlocks.BOAT_FRAME_FLAT.get(), 3).pattern("SSS")
                .define('S', Items.SCAFFOLDING).unlockedBy("has_scaffolding", has(Items.SCAFFOLDING)).save(writer);

        ShapedRecipeBuilder.shaped(AlekiShipsItems.OAR.get()).pattern("  S", " S ", "L  ")
                .define('S', Tags.Items.RODS_WOODEN).define('L', ItemTags.WOODEN_SLABS)
                .unlockedBy("has_stick", has(Items.STICK)).unlockedBy("has_wooden_slab", has(ItemTags.WOODEN_SLABS))
                .save(writer);

        ShapedRecipeBuilder.shaped(AlekiShipsItems.CANNON.get()).pattern("BBB", "LL ", "R R")
                .define('B', Items.IRON_BLOCK).define('L', ItemTags.WOODEN_SLABS).define('R', Items.IRON_NUGGET)
                .unlockedBy("has_iron_block", has(Items.IRON_BLOCK))
                .unlockedBy("has_wooden_slab", has(ItemTags.WOODEN_SLABS))
                .unlockedBy("has_iron_nugget", has(Items.IRON_NUGGET)).save(writer);

        ShapedRecipeBuilder.shaped(AlekiShipsItems.ANCHOR.get()).pattern("NIN", " I ", "IBI")
                .define('N', Items.IRON_NUGGET).define('I', Items.IRON_INGOT).define('B', Items.IRON_BLOCK)
                .unlockedBy("has_iron_nugget", has(Items.IRON_NUGGET))
                .unlockedBy("has_iron_ingot", has(Items.IRON_INGOT)).unlockedBy("has_iron_block", has(Items.IRON_BLOCK))
                .save(writer);

        ShapedRecipeBuilder.shaped(AlekiShipsBlocks.CLEAT.get()).pattern("III", "N N").define('N', Items.IRON_NUGGET)
                .define('I', Items.IRON_INGOT).unlockedBy("has_iron_nugget", has(Items.IRON_NUGGET))
                .unlockedBy("has_iron_ingot", has(Items.IRON_INGOT)).save(writer);

        ShapedRecipeBuilder.shaped(AlekiShipsBlocks.OARLOCK.get()).pattern(" I ", "III").define('I', Items.IRON_INGOT)
                .unlockedBy("has_iron_ingot", has(Items.IRON_INGOT)).save(writer);

        ShapelessRecipeBuilder.shapeless(AlekiShipsItems.CANNONBALL.get()).requires(Items.IRON_INGOT)
                .requires(Items.PAPER).requires(Items.GUNPOWDER).unlockedBy("has_iron_ingot", has(Items.IRON_INGOT))
                .unlockedBy("has_paper", has(Items.PAPER)).unlockedBy("has_gunpowder", has(Items.GUNPOWDER))
                .save(writer);
    }
}