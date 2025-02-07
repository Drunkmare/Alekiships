package com.alekiponi.alekiships.common.item.components;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public record BlockCompartmentData(BlockState displayState) {

    public static final Codec<BlockCompartmentData> CODEC = BlockState.CODEC.xmap(BlockCompartmentData::new,
            BlockCompartmentData::displayState);

    public static final StreamCodec<ByteBuf, BlockCompartmentData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.idMapper(Block.BLOCK_STATE_REGISTRY), BlockCompartmentData::displayState,
            BlockCompartmentData::new);

    public BlockCompartmentData(final Block block) {
        this(block.defaultBlockState());
    }
}