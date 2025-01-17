package com.alekiponi.alekiships.data.util;

import com.alekiponi.alekiships.common.block.AlekiShipsBlocks;
import com.alekiponi.alekiships.common.block.AngledBoatFrameBlock;
import com.alekiponi.alekiships.common.block.OarlockBlock;
import com.alekiponi.alekiships.common.recipe.EntityMultiblockRecipe;
import com.alekiponi.alekiships.common.recipe.MultiblockPattern;
import com.alekiponi.alekiships.common.recipe.ingredient.block.BlockStateIngredient;
import com.alekiponi.alekiships.common.recipe.ingredient.block.entity.FrameBlockEntityIngredient;
import com.alekiponi.alekiships.common.recipe.ingredient.block.matcher.PropertyMatcher;
import com.alekiponi.alekiships.common.recipe.ingredient.block.matcher.StateMatcher;
import com.alekiponi.alekiships.common.recipe.util.Pattern3D;
import com.alekiponi.alekiships.util.FrameMaterial;

import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.state.properties.StairsShape;

import java.util.function.UnaryOperator;
import org.jetbrains.annotations.Nullable;

/**
 * Helper things pertaining to {@link EntityMultiblockRecipe} datagen
 */
public final class EntityMultiblockHelper {

    /**
     * The rowboat pattern
     *
     * @param boatFrame              The boat frame
     * @param fullyProcessedProperty The fully processed property matcher
     * @param frameMaterial          The frame material
     */
    public static MultiblockPattern rowboatPattern(final AngledBoatFrameBlock boatFrame,
            final @Nullable PropertyMatcher fullyProcessedProperty, final Holder<FrameMaterial> frameMaterial) {
        final var addCommonProperties = fullyProcessedProperty == null ? UnaryOperator.<StateMatcher.StateMatcherBuilder>identity() : StateMatcher.addProperty(
                fullyProcessedProperty);

        final var frameIngredient = new FrameBlockEntityIngredient(frameMaterial);

        final var flat1 = BlockStateIngredient.stairs(boatFrame, Direction.WEST, StairsShape.INNER_RIGHT,
                addCommonProperties);
        final var flat2 = BlockStateIngredient.stairs(boatFrame, Direction.WEST, StairsShape.INNER_LEFT,
                addCommonProperties);
        final var flat3 = BlockStateIngredient.stairs(boatFrame, Direction.NORTH, StairsShape.STRAIGHT,
                addCommonProperties);
        final var flat4 = BlockStateIngredient.stairs(boatFrame, Direction.SOUTH, StairsShape.STRAIGHT,
                addCommonProperties);
        final var flat5 = BlockStateIngredient.stairs(boatFrame, Direction.EAST, StairsShape.INNER_LEFT,
                addCommonProperties);
        final var flat6 = BlockStateIngredient.stairs(boatFrame, Direction.EAST, StairsShape.INNER_RIGHT,
                addCommonProperties);

        return MultiblockPattern.builder()
                .layers(Pattern3D.Layer.builder()
                        .row("  ")
                        .row("LR")
                        .row("  "), Pattern3D.Layer.builder()
                        .row("12")
                        .row("34")
                        .row("56"))
                .define('L', BlockStateIngredient.builder(AlekiShipsBlocks.OARLOCK)
                        .state(PropertyMatcher.single(OarlockBlock.FACING, Direction.NORTH)))
                .define('R', BlockStateIngredient.builder(AlekiShipsBlocks.OARLOCK)
                        .state(PropertyMatcher.single(OarlockBlock.FACING, Direction.SOUTH)))
                .define('1', flat1, frameIngredient)
                .define('2', flat2, frameIngredient)
                .define('3', flat3, frameIngredient)
                .define('4', flat4, frameIngredient)
                .define('5', flat5, frameIngredient)
                .define('6', flat6, frameIngredient)
                .build();
    }
}