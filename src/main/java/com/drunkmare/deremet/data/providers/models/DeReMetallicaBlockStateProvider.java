package com.drunkmare.deremet.data.providers.models;

import com.drunkmare.deremet.DeReMetallica;
import com.drunkmare.deremet.common.block.DeReMetallicaBlocks;
import com.drunkmare.deremet.common.block.MillstoneProcessedFrameBlock;
import com.alekiponi.alekiships.util.BoatMaterial;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.Locale;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.IntStream;

public class DeReMetallicaBlockStateProvider extends BlockStateProvider {

    private static final String[] PROGRESS_STRINGS = {"first", "second", "third", "fourth", "fifth", "sixth", "seventh", "eighth"};

    public DeReMetallicaBlockStateProvider(final PackOutput output, final ExistingFileHelper existingFileHelper) {
        super(output, DeReMetallica.MOD_ID, existingFileHelper);
    }

    public static BiConsumer<BoatMaterial, Supplier<? extends Block>> millstoneFrameFull(
            final BlockStateProvider blockStateProvider, final ModelFile.ExistingModelFile frameFull) {
        return (wood, registryObject) -> {
            final var plankTexture = blockStateProvider.blockTexture(wood.getDeckBlock().getBlock());
            final var multipartBuilder = blockStateProvider.getMultipartBuilder(registryObject.get()).part()
                    .modelFile(frameFull).addModel().end();

            IntStream.range(0, 8).forEach(progress -> {
                boolean isExclusive = progress >= 2 && progress < 5;
                boolean hasOffsetVariant = progress >= 1 && progress <= 6;

                Integer[] frameValues = isExclusive
                        ? new Integer[]{progress}
                        : IntStream.range(progress, 8).boxed().toArray(Integer[]::new);

                for (int offsetVariant = 0; offsetVariant < (hasOffsetVariant ? 2 : 1); offsetVariant++) {
                    boolean withOffset = offsetVariant == 1;
                    String modelName = String.format(Locale.ROOT,
                            "block/wood/millstone_frame/full/%s/%s%s",
                            wood.getSerializedName(),
                            PROGRESS_STRINGS[progress],
                            withOffset ? "_offset" : "");
                    String templateName = String.format(Locale.ROOT,
                            "block/millstone_frame/full/template/%s%s",
                            PROGRESS_STRINGS[progress],
                            withOffset ? "_offset" : "");

                    final var plankModel = blockStateProvider.models().withExistingParent(
                                    modelName,
                                    new ResourceLocation(DeReMetallica.MOD_ID, templateName))
                            .texture("plank", plankTexture)
                            .texture("stone", new ResourceLocation(DeReMetallica.MOD_ID, "block/millstone"));

                    var part = multipartBuilder.part().modelFile(plankModel);

                    if (hasOffsetVariant) {
                        part.addModel()
                                .condition(MillstoneProcessedFrameBlock.FRAME_PROCESSED, frameValues)
                                .condition(MillstoneProcessedFrameBlock.COGWHEEL_OFFSET, withOffset);
                    } else {
                        part.addModel()
                                .condition(MillstoneProcessedFrameBlock.FRAME_PROCESSED, frameValues);
                    }
                }
            });
        };
    }

    @Override
    protected void registerStatesAndModels() {
        final var frameMillstoneFull = this.models().getExistingFile(this.modLoc("block/millstone_frame/full/frame"));

        DeReMetallicaBlocks.PROCESSED_MILLSTONE_FRAME.forEach(
                DeReMetallicaBlockStateProvider.millstoneFrameFull(this, frameMillstoneFull));

        this.simpleBlockWithItem(DeReMetallicaBlocks.MILLSTONE_FRAME.get(), frameMillstoneFull);
    }
}
