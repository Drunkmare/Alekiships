package com.alekiponi.alekiships.data.providers.models;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.common.block.*;
import com.alekiponi.alekiships.util.BoatMaterial;
import com.alekiponi.alekiships.util.VanillaWood;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.Locale;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.IntStream;

public class AlekiShipsBlockStateProvider extends BlockStateProvider {

    private static final String[] PROGRESS_STRINGS = {"first", "second", "third", "fourth"};

    public AlekiShipsBlockStateProvider(final PackOutput output, final ExistingFileHelper existingFileHelper) {
        super(output, AlekiShips.MOD_ID, existingFileHelper);
    }

    /**
     * @return The state mapper for our simple angled boat frames
     */
    public static Function<BlockState, ConfiguredModel[]> angledBoatFrame(final ModelFile straight,
            final ModelFile inner, final ModelFile outer) {
        return blockState -> {
            final StairsShape shape = blockState.getValue(StairBlock.SHAPE);
            final int yRot;
            if (shape == StairsShape.INNER_LEFT || shape == StairsShape.OUTER_LEFT) {
                yRot = ((int) blockState.getValue(StairBlock.FACING).toYRot() + 270) % 360;
            } else {
                yRot = (int) blockState.getValue(StairBlock.FACING).toYRot();
            }

            return ConfiguredModel.builder().modelFile(
                            shape == StairsShape.STRAIGHT ? straight : shape == StairsShape.INNER_LEFT || shape == StairsShape.INNER_RIGHT ? inner : outer)
                    .rotationY(yRot)/*.uvLock(yRot != 0)*/.build();
        };
    }

    /**
     * @return A consumer that generates the models and blockstate for the passed in boat material and flat frame block
     */
    public static BiConsumer<BoatMaterial, Supplier<? extends Block>> woodenBoatFrameFlat(
            final BlockStateProvider blockStateProvider, final ModelFile.ExistingModelFile frameFlat) {
        return (wood, registryObject) -> {
            final var plankTexture = blockStateProvider.blockTexture(wood.getDeckBlock().getBlock());
            final var multipartBuilder = blockStateProvider.getMultipartBuilder(registryObject.get()).part()
                    .modelFile(frameFlat).addModel().end();

            IntStream.range(0, 4).forEach(progress -> {
                final var plankModel = blockStateProvider.models().withExistingParent(
                        String.format(Locale.ROOT, "block/wood/watercraft_frame/flat/%s/%s", wood.getSerializedName(),
                                PROGRESS_STRINGS[progress]), new ResourceLocation(AlekiShips.MOD_ID,
                                String.format(Locale.ROOT, "block/watercraft_frame/flat/template/%s",
                                        PROGRESS_STRINGS[progress]))).texture("plank", plankTexture);

                multipartBuilder.part().modelFile(plankModel).addModel()
                        .condition(FlatWoodenBoatFrameBlock.FRAME_PROCESSED,
                                IntStream.range(progress, 4).boxed().toArray(Integer[]::new));
            });
        };
    }

    /**
     * @return A consumer that generates the models and blockstate for the passed in boat material and angled frame block
     */
    public static BiConsumer<VanillaWood, Supplier<? extends Block>> woodenBoatFrameAngled(
            final BlockStateProvider blockStateProvider, final ModelFile.ExistingModelFile straight,
            final ModelFile.ExistingModelFile inner, final ModelFile.ExistingModelFile outer) {
        return (wood, registryObject) -> {
            final var plankTexture = blockStateProvider.blockTexture(wood.getDeckBlock().getBlock());

            AngledWoodenBoatFrameBlock.FACING.getPossibleValues().forEach(facing -> {
                AngledWoodenBoatFrameBlock.SHAPE.getPossibleValues().forEach(shape -> {
                    final var multipartBuilder = blockStateProvider.getMultipartBuilder(registryObject.get());

                    final int yRot;
                    if (shape == StairsShape.INNER_LEFT || shape == StairsShape.OUTER_LEFT) {
                        yRot = ((int) facing.toYRot() + 270) % 360;
                    } else {
                        yRot = (int) facing.toYRot();
                    }

                    multipartBuilder.part().modelFile(
                                    shape == StairsShape.STRAIGHT ? straight : shape == StairsShape.INNER_LEFT || shape == StairsShape.INNER_RIGHT ? inner : outer)
                            .rotationY(yRot)/*.uvLock(yRot != 0)*/.addModel()
                            .condition(AngledWoodenBoatFrameBlock.FACING, facing)
                            .condition(AngledWoodenBoatFrameBlock.SHAPE, shape);

                    IntStream.range(0, 4).forEach(progress -> {
                        final String modelShape = shape == StairsShape.STRAIGHT ? "straight" : shape == StairsShape.INNER_LEFT || shape == StairsShape.INNER_RIGHT ? "inner" : "outer";
                        final var plankModel = blockStateProvider.models().withExistingParent(
                                        String.format(Locale.ROOT, "block/wood/watercraft_frame/angled/%s/%s/%s",
                                                wood.getSerializedName(), modelShape, PROGRESS_STRINGS[progress]),
                                        new ResourceLocation(AlekiShips.MOD_ID,
                                                String.format(Locale.ROOT, "block/watercraft_frame/angled/template/%s/%s",
                                                        modelShape, PROGRESS_STRINGS[progress])))
                                .texture("plank", plankTexture);

                        multipartBuilder.part().modelFile(plankModel).rotationY(yRot).uvLock(yRot != 0).addModel()
                                .condition(AngledWoodenBoatFrameBlock.FACING, facing)
                                .condition(AngledWoodenBoatFrameBlock.SHAPE, shape)
                                .condition(AngledWoodenBoatFrameBlock.FRAME_PROCESSED,
                                        IntStream.range(progress, 4).boxed().toArray(Integer[]::new));
                    });
                });
            });
        };
    }

    @Override
    protected void registerStatesAndModels() {
        final var frameFlat = this.models().getExistingFile(this.modLoc("block/watercraft_frame/flat/frame"));

        AlekiShipsBlocks.WOODEN_BOAT_FRAME_FLAT.forEach(
                AlekiShipsBlockStateProvider.woodenBoatFrameFlat(this, frameFlat));

        final ModelFile.ExistingModelFile angledFrameStraight = this.models()
                .getExistingFile(this.modLoc("block/watercraft_frame/angled/straight"));
        final ModelFile.ExistingModelFile angledFrameInner = this.models()
                .getExistingFile(this.modLoc("block/watercraft_frame/angled/inner"));
        final ModelFile.ExistingModelFile angledFrameOuter = this.models()
                .getExistingFile(this.modLoc("block/watercraft_frame/angled/outer"));

        AlekiShipsBlocks.WOODEN_BOAT_FRAME_ANGLED.forEach(
                woodenBoatFrameAngled(this, angledFrameStraight, angledFrameInner, angledFrameOuter));

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

    public void angledBoatFrame(final Block block, final ModelFile.ExistingModelFile straight,
            final ModelFile.ExistingModelFile inner, final ModelFile.ExistingModelFile outer) {
        this.getVariantBuilder(block)
                .forAllStatesExcept(angledBoatFrame(straight, inner, outer), AngledBoatFrameBlock.WATERLOGGED);
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
            return ConfiguredModel.builder().modelFile(modelFunc.apply(blockState)).rotationY(yRot).uvLock(yRot != 0)
                    .build();
        }, ignoredProperties);
    }
}