package com.alekiponi.alekiships.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.alekiponi.alekiships.common.recipe.ingredient.block.BlockStateIngredient;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.StateDefinition;

import java.text.MessageFormat;
import java.util.Collection;

public final class BlockStateIngredientTests {

    @Test
    @DisplayName("Exact state helper should match a single block state exactly")
    public void exactMatch() {
        final var blockState = Blocks.STONE_STAIRS.defaultBlockState();
        final var exactIngredient = BlockStateIngredient.exactState(blockState);
        Assertions.assertTrue(exactIngredient.test(blockState),
                () -> MessageFormat.format("Exact ingredient ({0}) does not match the state it was created for ({1})",
                        exactIngredient, blockState));
        final var failState = blockState.cycle(StairBlock.FACING);
        Assertions.assertFalse(exactIngredient.test(failState),
                () -> MessageFormat.format("Exact ingredient ({0}) matched a state it was not created for ({1})",
                        exactIngredient, failState));
    }

    @Test
    @DisplayName("Any state helper should match all states of the block")
    public void looseMatch() {
        final var looseIngredient = BlockStateIngredient.anyState(Blocks.STONE_STAIRS);
        for (final var blockState : Blocks.STONE_STAIRS.getStateDefinition().getPossibleStates()) {
            Assertions.assertTrue(looseIngredient.test(blockState),
                    () -> MessageFormat.format("Loose ingredient ({0}) did not match ({1})", looseIngredient,
                            blockState));
        }
    }

    @Test
    @DisplayName("Special air ingredient should match anything that counts as air")
    public void matchAir() {
        final var airIngredient = BlockStateIngredient.AIR;
        Assertions.assertAll("Special Air ingredient",
                () -> Assertions.assertTrue(airIngredient.test(Blocks.AIR.defaultBlockState()), "Did not match air"),
                () -> Assertions.assertTrue(airIngredient.test(Blocks.VOID_AIR.defaultBlockState()),
                        "Did not match void air"),
                () -> Assertions.assertTrue(airIngredient.test(Blocks.CAVE_AIR.defaultBlockState()),
                        "Did not match cave air"));
    }

    @Test
    @Tag("slow")
    @DisplayName("Special any ingredient should match absolutely any state")
    public void matchAny() {
        Assertions.assertAll(BuiltInRegistries.BLOCK.stream()
                .map(Block::getStateDefinition)
                .map(StateDefinition::getPossibleStates)
                .flatMap(Collection::stream)
                .map(blockState -> () -> Assertions.assertTrue(BlockStateIngredient.ANY.test(blockState),
                        () -> MessageFormat.format("Did not match {0}", blockState))));
    }
}