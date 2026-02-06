package com.alekiponi.alekiships.data.util;

import com.alekiponi.alekiships.AlekiShips;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.StairsShape;

import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;

import java.util.function.Function;

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

            return ConfiguredModel.builder()
                    .modelFile(
                            shape == StairsShape.STRAIGHT ? straight : shape == StairsShape.INNER_LEFT || shape == StairsShape.INNER_RIGHT ? inner : outer)
                    .rotationY(yRot)/*.uvLock(yRot != 0)*/.build();
        };
    }
}