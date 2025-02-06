package com.alekiponi.alekiships.data.providers.models;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.common.block.*;
import com.alekiponi.alekiships.data.util.FrameBlockHelper;
import com.alekiponi.alekiships.util.VanillaWood;

import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;

import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class AlekiShipsBlockStateProvider extends BlockStateProvider {

    public AlekiShipsBlockStateProvider(final PackOutput output, final ExistingFileHelper existingFileHelper) {
        super(output, AlekiShips.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        final var frameFlat = this.models().getExistingFile(FrameBlockHelper.FLAT_FRAME);

        AlekiShipsBlocks.WOODEN_BOAT_FRAME_FLAT.forEach(this.woodenBoatFrameFlat(frameFlat));

        final ModelFile.ExistingModelFile angledFrameStraight = this.models()
                .getExistingFile(FrameBlockHelper.ANGLED_FRAME_STRAIGHT);
        final ModelFile.ExistingModelFile angledFrameInner = this.models()
                .getExistingFile(FrameBlockHelper.ANGLED_FRAME_INNER);
        final ModelFile.ExistingModelFile angledFrameOuter = this.models()
                .getExistingFile(FrameBlockHelper.ANGLED_FRAME_OUTER);

        AlekiShipsBlocks.WOODEN_BOAT_FRAME_ANGLED.forEach(
                this.woodenBoatFrameAngled(angledFrameStraight, angledFrameInner, angledFrameOuter));

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
            return ConfiguredModel.builder().modelFile(modelFunc.apply(blockState)).rotationY(yRot).uvLock(yRot != 0)
                    .build();
        }, ignoredProperties);
    }

    private BiConsumer<VanillaWood, DeferredBlock<FlatWoodenBoatFrameBlock>> woodenBoatFrameFlat(
            final ModelFile frameFlat) {
        return (vanillaWood, flatFrame) -> {
            final var plankTexture = this.blockTexture(vanillaWood.getDeckBlockBlock());
            final var woodName = vanillaWood.getSerializedName();
            FrameBlockHelper.woodenBoatFrameFlat(this, frameFlat, plankTexture, flatFrame.get(), woodName);
        };
    }

    private BiConsumer<VanillaWood, Supplier<? extends AngledWoodenBoatFrameBlock>> woodenBoatFrameAngled(
            final ModelFile straight, final ModelFile inner, final ModelFile outer) {
        return (wood, angledFrame) -> {
            final var plankTexture = this.blockTexture(wood.getDeckBlockBlock());
            final var woodName = wood.getSerializedName();

            FrameBlockHelper.woodenBoatFrameAngled(this, straight, inner, outer, plankTexture, angledFrame.get(),
                    woodName);
        };
    }
}