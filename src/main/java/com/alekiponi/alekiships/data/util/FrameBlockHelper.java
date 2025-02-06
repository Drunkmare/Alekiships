package com.alekiponi.alekiships.data.util;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.common.block.AngledWoodenBoatFrameBlock;
import com.alekiponi.alekiships.common.block.FlatWoodenBoatFrameBlock;

import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;

import java.text.MessageFormat;
import java.util.function.Function;
import java.util.stream.IntStream;

public final class FrameBlockHelper {
    /**
     * The location for our empty flat frame model
     */
    public static final ResourceLocation FLAT_FRAME = AlekiShips.location("block/watercraft_frame/flat/frame");
    /**
     * The location for our empty straight angled frame model
     */
    public static final ResourceLocation ANGLED_FRAME_STRAIGHT = AlekiShips.location(
            "block/watercraft_frame/angled/straight");
    /**
     * The location for our empty inner angled frame model
     */
    public static final ResourceLocation ANGLED_FRAME_INNER = AlekiShips.location(
            "block/watercraft_frame/angled/inner");
    /**
     * The location for our empty outer angled frame model
     */
    public static final ResourceLocation ANGLED_FRAME_OUTER = AlekiShips.location(
            "block/watercraft_frame/angled/outer");

    private static final String[] PROGRESS_STRINGS = {"first", "second", "third", "fourth"};

    /**
     * Helper for creating a loot table for processing frames
     *
     * @param frameBlock         The frame block
     * @param dropItem           The item this state drops
     * @param startValue         The first state for the drop
     * @param endValue           The final state for the drop
     * @param processingProperty The processing property
     */
    public static LootPool.Builder createProcessedFrameTable(final Block frameBlock, final Item dropItem,
            final int startValue, final int endValue, final IntegerProperty processingProperty) {
        final var contentsPool = LootPool.lootPool();

        for (int propertyValue = startValue, itemCount = 1; propertyValue <= endValue; propertyValue++, itemCount++) {
            final var entry = LootItem.lootTableItem(dropItem);
            if (itemCount > 1) entry.apply(SetItemCountFunction.setCount(ConstantValue.exactly(itemCount)));

            contentsPool.add(entry.when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(frameBlock)
                    .setProperties(StatePropertiesPredicate.Builder.properties()
                            .hasProperty(processingProperty, propertyValue))));
        }

        return contentsPool;
    }

    /**
     * Helper to get a models Y rotation for angled boat frames.
     */
    public static int angledBoatFrameYRot(final StairsShape shape, final Direction facing) {
        return switch (shape) {
            case INNER_RIGHT, STRAIGHT -> ((int) facing.toYRot());
            case OUTER_LEFT -> (int) facing.toYRot() + 90;
            case OUTER_RIGHT -> (int) facing.toYRot() + 180;
            case INNER_LEFT -> (int) facing.toYRot() + 270;
        } % 360;
    }

    /**
     * @return The state mapper for our simple angled boat frames
     */
    public static Function<BlockState, ConfiguredModel[]> angledBoatFrameStateMapper(final ModelFile straight,
            final ModelFile inner, final ModelFile outer) {
        return blockState -> {
            final StairsShape shape = blockState.getValue(StairBlock.SHAPE);
            final int yRot = angledBoatFrameYRot(shape, blockState.getValue(StairBlock.FACING));

            return ConfiguredModel.builder().modelFile(
                            shape == StairsShape.STRAIGHT ? straight : shape == StairsShape.INNER_LEFT || shape == StairsShape.INNER_RIGHT ? inner : outer)
                    .rotationY(yRot)/*.uvLock(yRot != 0)*/.build();
        };
    }

    /**
     * Helper for generating models for {@link FlatWoodenBoatFrameBlock}s
     *
     * @param blockStateProvider The datagen provider
     * @param flatFrameBaseModel The base flat frame model. See {@link #FLAT_FRAME}
     * @param plankTexture       The texture for the planks
     * @param flatFrameBlock     The block instance
     * @param woodName           The name of the wood (for categorization purposes, this <i>could</i> be any non-empty string)
     */
    public static void woodenBoatFrameFlat(final BlockStateProvider blockStateProvider,
            final ModelFile flatFrameBaseModel, final ResourceLocation plankTexture,
            final FlatWoodenBoatFrameBlock flatFrameBlock, final String woodName) {
        final var multipartBuilder = blockStateProvider.getMultipartBuilder(flatFrameBlock).part()
                .modelFile(flatFrameBaseModel).addModel().end();

        IntStream.rangeClosed(0, FlatWoodenBoatFrameBlock.FULLY_PROCESSED).forEach(progress -> {
            final var plankModel = blockStateProvider.models().withExistingParent(
                    MessageFormat.format("block/wood/watercraft_frame/flat/{0}/{1}", woodName,
                            PROGRESS_STRINGS[progress]), AlekiShips.location(
                            MessageFormat.format("block/watercraft_frame/flat/template/{0}",
                                    PROGRESS_STRINGS[progress]))).texture("plank", plankTexture);

            multipartBuilder.part().modelFile(plankModel).addModel().condition(FlatWoodenBoatFrameBlock.FRAME_PROCESSED,
                    IntStream.rangeClosed(progress, FlatWoodenBoatFrameBlock.FULLY_PROCESSED).boxed()
                            .toArray(Integer[]::new));
        });
    }

    /**
     * Helper for generating models for {@link AngledWoodenBoatFrameBlock}s
     *
     * @param blockStateProvider The datagen provider
     * @param straight           The base straight angled frame model. See {@link #ANGLED_FRAME_STRAIGHT}
     * @param inner              The base inner angled frame model. See {@link #ANGLED_FRAME_INNER}
     * @param outer              The base outer angled frame model. See {@link #ANGLED_FRAME_OUTER}
     * @param plankTexture       The texture for the planks
     * @param angledFrameBlock   The block instance
     * @param woodName           The name of the wood (for categorization purposes, this <i>could</i> be any non-empty string)
     */
    public static void woodenBoatFrameAngled(final BlockStateProvider blockStateProvider, final ModelFile straight,
            final ModelFile inner, final ModelFile outer, final ResourceLocation plankTexture,
            final AngledWoodenBoatFrameBlock angledFrameBlock, final String woodName) {
        AngledWoodenBoatFrameBlock.FACING.getPossibleValues().forEach(facing -> {
            AngledWoodenBoatFrameBlock.SHAPE.getPossibleValues().forEach(shape -> {
                final var multipartBuilder = blockStateProvider.getMultipartBuilder(angledFrameBlock);

                final int yRot = angledBoatFrameYRot(shape, facing);

                multipartBuilder.part().modelFile(
                                shape == StairsShape.STRAIGHT ? straight : shape == StairsShape.INNER_LEFT || shape == StairsShape.INNER_RIGHT ? inner : outer)
                        .rotationY(yRot)/*.uvLock(yRot != 0)*/.addModel()
                        .condition(AngledWoodenBoatFrameBlock.FACING, facing)
                        .condition(AngledWoodenBoatFrameBlock.SHAPE, shape);

                IntStream.rangeClosed(0, AngledWoodenBoatFrameBlock.FULLY_PROCESSED).forEach(progress -> {
                    final String modelShape = shape == StairsShape.STRAIGHT ? "straight" : shape == StairsShape.INNER_LEFT || shape == StairsShape.INNER_RIGHT ? "inner" : "outer";
                    final var plankModel = blockStateProvider.models().withExistingParent(
                            MessageFormat.format("block/wood/watercraft_frame/angled/{0}/{1}/{2}", woodName, modelShape,
                                    PROGRESS_STRINGS[progress]), AlekiShips.location(
                                    MessageFormat.format("block/watercraft_frame/angled/template/{0}/{1}", modelShape,
                                            PROGRESS_STRINGS[progress]))).texture("plank", plankTexture);

                    multipartBuilder.part().modelFile(plankModel).rotationY(yRot).uvLock(yRot != 0).addModel()
                            .condition(AngledWoodenBoatFrameBlock.FACING, facing)
                            .condition(AngledWoodenBoatFrameBlock.SHAPE, shape)
                            .condition(AngledWoodenBoatFrameBlock.FRAME_PROCESSED,
                                    IntStream.rangeClosed(progress, AngledWoodenBoatFrameBlock.FULLY_PROCESSED).boxed()
                                            .toArray(Integer[]::new));
                });
            });
        });
    }
}