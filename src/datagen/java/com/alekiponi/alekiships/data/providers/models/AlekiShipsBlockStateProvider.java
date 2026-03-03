package com.alekiponi.alekiships.data.providers.models;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.common.block.*;
import com.alekiponi.alekiships.data.util.FrameBlockHelper;

import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.StairsShape;

import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.text.MessageFormat;
import java.util.function.Function;
import java.util.stream.IntStream;

public class AlekiShipsBlockStateProvider extends BlockStateProvider {

    private static final String[] PROGRESS_STRINGS = {"first", "second", "third", "fourth"};

    public AlekiShipsBlockStateProvider(final PackOutput output, final ExistingFileHelper existingFileHelper) {
        super(output, AlekiShips.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        final var frameFlat = this.models().getExistingFile(FrameBlockHelper.FLAT_FRAME);

        this.woodenBoatFrameFlat(AlekiShipsBlocks.WOODEN_BOAT_FRAME_FLAT.get(), frameFlat);

        final ModelFile.ExistingModelFile angledFrameStraight = this.models()
                .getExistingFile(FrameBlockHelper.ANGLED_FRAME_STRAIGHT);
        final ModelFile.ExistingModelFile angledFrameInner = this.models()
                .getExistingFile(FrameBlockHelper.ANGLED_FRAME_INNER);
        final ModelFile.ExistingModelFile angledFrameOuter = this.models()
                .getExistingFile(FrameBlockHelper.ANGLED_FRAME_OUTER);

        this.woodenBoatFrameAngled(AlekiShipsBlocks.WOODEN_BOAT_FRAME_ANGLED.get(), angledFrameStraight,
                angledFrameInner, angledFrameOuter);
        this.angledBoatFrame(AlekiShipsBlocks.BOAT_FRAME_ANGLED.get(), angledFrameStraight, angledFrameInner,
                angledFrameOuter);
        this.simpleBlockItem(AlekiShipsBlocks.BOAT_FRAME_ANGLED.get(), angledFrameStraight);

        this.simpleBlockWithItem(AlekiShipsBlocks.BOAT_FRAME_FLAT.get(), frameFlat);

        this.horizontalBlock(AlekiShipsBlocks.OARLOCK.get(),
                this.models().getExistingFile(this.modLoc("block/oarlock")), 180, OarlockBlock.WATERLOGGED);

        this.itemModels().basicItem(AlekiShipsBlocks.OARLOCK.get().asItem());

        this.horizontalBlock(AlekiShipsBlocks.CLEAT.get(), this.models().getExistingFile(this.modLoc("block/cleat")),
                180, CleatBlock.WATERLOGGED);

        this.itemModels().basicItem(AlekiShipsBlocks.CLEAT.get().asItem());
    }

    public void angledBoatFrame(final Block block, final ModelFile straight, final ModelFile inner,
            final ModelFile outer) {
        this.getVariantBuilder(block)
                .forAllStatesExcept(FrameBlockHelper.angledBoatFrameStateMapper(straight, inner, outer),
                        AngledBoatFrameBlock.WATERLOGGED);
    }

    public void horizontalBlock(final Block block, final ModelFile model, final int angleOffset,
            final Property<?>... ignoredProperties) {
        horizontalBlock(block, $ -> model, angleOffset, ignoredProperties);
    }

    public void horizontalBlock(final Block block, final Function<BlockState, ModelFile> modelFunc,
            final int angleOffset, final Property<?>... ignoredProperties) {
        this.getVariantBuilder(block).forAllStatesExcept(blockState -> {
            final int yRot = ((int) blockState.getValue(BlockStateProperties.HORIZONTAL_FACING)
                    .toYRot() + angleOffset) % 360;
            return ConfiguredModel.builder()
                    .modelFile(modelFunc.apply(blockState))
                    .rotationY(yRot)
                    .uvLock(yRot != 0)
                    .build();
        }, ignoredProperties);
    }

    private void woodenBoatFrameFlat(final FlatWoodenBoatFrameBlock boatFrame, final ModelFile frameFlat) {
        final var multipartBuilder = this.getMultipartBuilder(boatFrame).part().modelFile(frameFlat).addModel().end();
        IntStream.rangeClosed(0, FlatWoodenBoatFrameBlock.FULLY_PROCESSED)
                .forEach(progress -> multipartBuilder.part()
                        .modelFile(this.models()
                                .getExistingFile(AlekiShips.location(
                                        MessageFormat.format("block/watercraft_frame/wooden/flat/{0}",
                                                PROGRESS_STRINGS[progress]))))
                        .addModel()
                        .condition(FlatWoodenBoatFrameBlock.FRAME_PROCESSED,
                                IntStream.rangeClosed(progress, FlatWoodenBoatFrameBlock.FULLY_PROCESSED).boxed()
                                        .toArray(Integer[]::new)));
    }

    private void woodenBoatFrameAngled(final AngledWoodenBoatFrameBlock boatFrame, final ModelFile straight,
            final ModelFile inner, final ModelFile outer) {
        AngledWoodenBoatFrameBlock.FACING.getPossibleValues()
                .forEach(facing -> AngledWoodenBoatFrameBlock.SHAPE.getPossibleValues()
                        .forEach(shape -> {
                            final var multipartBuilder = this.getMultipartBuilder(boatFrame);

                            final int yRot = FrameBlockHelper.angledBoatFrameYRot(shape, facing);

                            multipartBuilder.part()
                                    .modelFile(
                                            shape == StairsShape.STRAIGHT ? straight : shape == StairsShape.INNER_LEFT || shape == StairsShape.INNER_RIGHT ? inner : outer)
                                    .rotationY(yRot)/*.uvLock(yRot != 0)*/.addModel()
                                    .condition(AngledWoodenBoatFrameBlock.FACING, facing)
                                    .condition(AngledWoodenBoatFrameBlock.SHAPE, shape);

                            IntStream.rangeClosed(0, AngledWoodenBoatFrameBlock.FULLY_PROCESSED)
                                    .forEach(progress -> {
                                        final String modelShape = shape == StairsShape.STRAIGHT ? "straight" : shape == StairsShape.INNER_LEFT || shape == StairsShape.INNER_RIGHT ? "inner" : "outer";

                                        multipartBuilder.part()
                                                .modelFile(this.models()
                                                        .getExistingFile(AlekiShips.location(MessageFormat.format(
                                                                "block/watercraft_frame/wooden/angled/{0}/{1}",
                                                                modelShape, PROGRESS_STRINGS[progress]))))
                                                .rotationY(yRot)
                                                .uvLock(yRot != 0)
                                                .addModel()
                                                .condition(AngledWoodenBoatFrameBlock.FACING, facing)
                                                .condition(AngledWoodenBoatFrameBlock.SHAPE, shape)
                                                .condition(AngledWoodenBoatFrameBlock.FRAME_PROCESSED,
                                                        IntStream.rangeClosed(progress,
                                                                        AngledWoodenBoatFrameBlock.FULLY_PROCESSED).boxed()
                                                                .toArray(Integer[]::new));
                                    });
                        }));
    }
}