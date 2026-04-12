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

// Generates blockstate JSON files and model JSON files for all blocks.
// Output lands in src/generated/resources/assets/de_re_metallica/.
public class DeReMetallicaBlockStateProvider extends BlockStateProvider {

    // Maps stage index (0-7) to the human-readable suffix used in model/template paths.
    private static final String[] PROGRESS_STRINGS = {"first", "second", "third", "fourth", "fifth", "sixth", "seventh", "eighth"};

    public DeReMetallicaBlockStateProvider(final PackOutput output, final ExistingFileHelper existingFileHelper) {
        super(output, DeReMetallica.MOD_ID, existingFileHelper);
    }

    /**
     * Returns a BiConsumer that generates the multipart blockstate and per-wood models
     * for a processed millstone frame.
     *
     * The blockstate uses multipart rather than variants because multiple model layers
     * are composited on top of each other (the static frame plus the stage-specific
     * overlay), and certain stages need to show different overlays depending on
     * {@code COGWHEEL_OFFSET}.
     *
     * Model naming convention:
     *   Template: block/millstone_frame/full/template/{stage}[_offset]
     *   Per-wood:  block/wood/millstone_frame/full/{wood}/{stage}[_offset]
     *
     * Stage visibility rules (which frame_processed values show a given stage model):
     *   - Stages 2, 3, 4 are exclusive (only visible at exactly that stage).
     *   - All other stages are additive (visible from that stage onward to 7).
     *   - Stages 1-6 have an offset variant controlled by COGWHEEL_OFFSET.
     */
    public static BiConsumer<BoatMaterial, Supplier<? extends Block>> millstoneFrameFull(
            final BlockStateProvider blockStateProvider, final ModelFile.ExistingModelFile frameFull) {
        return (wood, registryObject) -> {
            final var plankTexture = blockStateProvider.blockTexture(wood.getDeckBlock().getBlock());

            // The bare scaffold frame is always visible regardless of stage.
            final var multipartBuilder = blockStateProvider.getMultipartBuilder(registryObject.get()).part()
                    .modelFile(frameFull).addModel().end();

            IntStream.range(0, 8).forEach(progress -> {
                // Stages 2-4 (hammer steps) replace each other — exclusive visibility.
                // All other stages accumulate on top of previous ones — additive visibility.
                boolean isExclusive = progress >= 2 && progress < 5;

                // Only stages 1-6 need a second model variant for cogwheel offset.
                boolean hasOffsetVariant = progress >= 1 && progress <= 6;

                // Build the array of frame_processed values this model should be visible at.
                Integer[] frameValues = isExclusive
                        ? new Integer[]{progress}
                        : IntStream.range(progress, 8).boxed().toArray(Integer[]::new);

                for (int offsetVariant = 0; offsetVariant < (hasOffsetVariant ? 2 : 1); offsetVariant++) {
                    boolean withOffset = offsetVariant == 1;

                    // e.g. "block/wood/millstone_frame/full/oak/third_offset"
                    String modelName = String.format(Locale.ROOT,
                            "block/wood/millstone_frame/full/%s/%s%s",
                            wood.getSerializedName(),
                            PROGRESS_STRINGS[progress],
                            withOffset ? "_offset" : "");

                    // e.g. "block/millstone_frame/full/template/third_offset"
                    String templateName = String.format(Locale.ROOT,
                            "block/millstone_frame/full/template/%s%s",
                            PROGRESS_STRINGS[progress],
                            withOffset ? "_offset" : "");

                    // Derive the per-wood model from the template, injecting the plank and stone textures.
                    final var plankModel = blockStateProvider.models().withExistingParent(
                                    modelName,
                                    new ResourceLocation(DeReMetallica.MOD_ID, templateName))
                            .texture("plank", plankTexture)
                            .texture("stone", new ResourceLocation(DeReMetallica.MOD_ID, "block/millstone"));

                    var part = multipartBuilder.part().modelFile(plankModel);

                    // Add blockstate conditions so this part only renders at the right stage(s).
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

        // Generate blockstate + models for all 11 wood variants of the processed frame.
        DeReMetallicaBlocks.PROCESSED_MILLSTONE_FRAME.forEach(
                DeReMetallicaBlockStateProvider.millstoneFrameFull(this, frameMillstoneFull));

        // Unprocessed frame — single model, no variants.
        this.simpleBlockWithItem(DeReMetallicaBlocks.MILLSTONE_FRAME.get(), frameMillstoneFull);
    }
}
