package com.alekiponi.alekiships.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import com.alekiponi.alekiships.common.block.FrameBlock;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;

/**
 * A data map value for the Empty frame -> Filled frame transition when clicking an empty frame with an item
 *
 * @param frame         The filled frame block
 * @param frameMaterial The frame material
 */
public record BoatFrame(Block frame, Holder<FrameMaterial> frameMaterial) {

    private static final Codec<Block> FRAME_BLOCK_CODEC = BuiltInRegistries.BLOCK.byNameCodec()
            .validate(block -> block instanceof FrameBlock ? DataResult.success(block) : DataResult.error(
                    () -> BuiltInRegistries.BLOCK.getKey(block) + " is not a Frame Block"));

    public static final Codec<BoatFrame> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(FRAME_BLOCK_CODEC.fieldOf("frame").forGetter(BoatFrame::frame),
                            FrameMaterial.CODEC.fieldOf("material").forGetter(BoatFrame::frameMaterial))
                    .apply(instance, BoatFrame::new));

    public FrameMaterial getFrameMaterial() {
        return this.frameMaterial.value();
    }
}