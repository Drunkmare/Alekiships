package com.alekiponi.alekiships.commands.server;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.common.recipe.AlekiShipsRecipeTypes;
import com.alekiponi.alekiships.common.recipe.EntityMultiblockRecipe;
import com.alekiponi.alekiships.common.recipe.ingredient.block.BlockStateIngredient;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.Rotation;

import net.neoforged.neoforge.server.command.EnumArgument;

import lombok.extern.slf4j.Slf4j;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

@Slf4j
public final class EntityMultiblockCommands {

    public static final SuggestionProvider<CommandSourceStack> ALL_ENTITY_MULTIBLOCKS = (commandContext, suggestionsBuilder) -> SharedSuggestionProvider.suggestResource(
            commandContext.getSource()
                    .getRecipeManager()
                    .getAllRecipesFor(AlekiShipsRecipeTypes.ENTITY_MULTIBLOCK_RECIPE.get()), suggestionsBuilder,
            RecipeHolder::id, recipeHolder -> () -> recipeHolder.id().toString());

    public static final String NOT_ENTITY_MULTIBLOCK_RECIPE = AlekiShips.MOD_ID + ".commands.place_entity_multiblock.not_entity_multiblock";
    public static final String NO_MATCHING_STATES = AlekiShips.MOD_ID + ".commands.place_entity_multiblock.no_matching_states";

    private static final Rotation[] ROTATIONS = Rotation.values();
    private static final String ENTITY_MULTIBLOCK = "entityMultiblock";

    private static final DynamicCommandExceptionType ERROR_NOT_ENTITYMULTIBLOCK = new DynamicCommandExceptionType(
            o -> Component.translatableEscape(NOT_ENTITY_MULTIBLOCK_RECIPE, o));

    private static final DynamicCommandExceptionType ERROR_NO_MATCHING_STATES = new DynamicCommandExceptionType(
            o -> Component.translatableEscape(NO_MATCHING_STATES, o));

    public static void register(final CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("entity_multiblock")
                .then(literal("place")
                        .then(argument(ENTITY_MULTIBLOCK, ResourceLocationArgument.id())
                                .suggests(ALL_ENTITY_MULTIBLOCKS)
                                .then(argument("rotation", EnumArgument.enumArgument(Rotation.class))
                                        .executes(context -> placeEntityMultiblock(context.getSource(),
                                                getEntityMultiblock(context, ENTITY_MULTIBLOCK),
                                                context.getSource().getPlayerOrException(),
                                                context.getArgument("rotation", Rotation.class), 0))
                                        .then(argument("stateVariant", IntegerArgumentType.integer(0)).executes(
                                                context -> placeEntityMultiblock(context.getSource(),
                                                        getEntityMultiblock(context, ENTITY_MULTIBLOCK),
                                                        context.getSource().getPlayerOrException(),
                                                        context.getArgument("rotation", Rotation.class),
                                                        IntegerArgumentType.getInteger(context, "stateVariant"))))))
                        .then(literal("all")
                                .then(argument(ENTITY_MULTIBLOCK, ResourceLocationArgument.id())
                                        .suggests(ALL_ENTITY_MULTIBLOCKS)
                                        .executes(context -> placeEntityMultiblocks(context.getSource(),
                                                getEntityMultiblock(context, ENTITY_MULTIBLOCK),
                                                context.getSource().getPlayerOrException()))))));
    }


    private static int placeEntityMultiblock(final CommandSourceStack source,
            final RecipeHolder<EntityMultiblockRecipe> recipeHolder, final Player player, final Rotation rotation,
            final int stateVariant) {
        final var level = source.getLevel();
        final var pattern = recipeHolder.value().getPattern();

        final var startPos = player.blockPosition().relative(player.getDirection(), 5);

        pattern.place(level, startPos, stateVariant, rotation);

        source.sendSuccess(() -> Component.literal("Placed " + recipeHolder), true);
        return Command.SINGLE_SUCCESS;
    }

    private static int placeEntityMultiblocks(final CommandSourceStack source,
            final RecipeHolder<EntityMultiblockRecipe> recipeHolder,
            final Player player) throws CommandSyntaxException {
        final var level = source.getLevel();
        final var pattern = recipeHolder.value().getPattern();

        final var startPos = player.blockPosition().relative(player.getDirection(), 5);

        final var uniqueIngredients = pattern.getUniqueIngredients();

        final var max = uniqueIngredients.stream()
                .map(BlockStateIngredient::getMatchingStates).mapToInt(blockStates -> blockStates.length)
                .max();

        if (max.isEmpty()) {
            throw ERROR_NO_MATCHING_STATES.create(recipeHolder);
        }

        for (final Rotation rotation : ROTATIONS) {
            for (int stateVariant = 0; stateVariant < max.getAsInt(); stateVariant++) {
                final int x = (switch (rotation) {
                    case NONE -> 0;
                    case CLOCKWISE_90 -> pattern.width();
                    case CLOCKWISE_180 -> pattern.width() + pattern.depth();
                    case COUNTERCLOCKWISE_90 -> pattern.width() + pattern.depth() + pattern.width();
                }) + rotation.ordinal();
                final int z = (switch (rotation) {
                    case NONE, CLOCKWISE_180 -> pattern.depth();
                    case CLOCKWISE_90, COUNTERCLOCKWISE_90 -> pattern.width();
                } * stateVariant) + stateVariant;
                final var blockPos = startPos.offset(x, 0, z);
                pattern.place(level, blockPos, stateVariant, rotation);
            }
        }

        source.sendSuccess(() -> Component.literal("Placed all " + max.getAsInt() + " variants of " + recipeHolder),
                true);
        return Command.SINGLE_SUCCESS;
    }

    @SuppressWarnings("unchecked")
    private static RecipeHolder<EntityMultiblockRecipe> getEntityMultiblock(
            final CommandContext<CommandSourceStack> context,
            @SuppressWarnings("SameParameterValue") final String entityMultiblock) throws CommandSyntaxException {
        final RecipeHolder<?> recipeHolder = ResourceLocationArgument.getRecipe(context, entityMultiblock);

        if (recipeHolder.value().getType() != AlekiShipsRecipeTypes.ENTITY_MULTIBLOCK_RECIPE.get()) {
            throw ERROR_NOT_ENTITYMULTIBLOCK.create(recipeHolder);
        }

        return (RecipeHolder<EntityMultiblockRecipe>) recipeHolder;
    }
}