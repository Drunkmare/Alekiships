package com.alekiponi.alekiships.common.compartment;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;

import com.alekiponi.alekiships.common.entity.compartment.AbstractCompartmentEntity;
import com.alekiponi.alekiships.common.entity.compartment.BlockCompartment;

import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;
import lombok.*;

@ToString
@EqualsAndHashCode
@AllArgsConstructor
@Getter(AccessLevel.PRIVATE)
public final class BlockCompartmentPlaceable<E extends AbstractCompartmentEntity & BlockCompartment> implements CompartmentPlaceable<E> {

    private final BlockCompartmentFactory<E> blockCompartmentFactory;
    private final BlockCompartment.BlockCompartmentData blockCompartmentData;

    public static <E extends AbstractCompartmentEntity & BlockCompartment> MapCodec<BlockCompartmentPlaceable<E>> codec(
            final BlockCompartmentFactory<E> blockCompartmentFactory) {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
                        BlockCompartment.BlockCompartmentData.CODEC.fieldOf("block")
                                .forGetter(BlockCompartmentPlaceable::getBlockCompartmentData))
                .apply(instance, blockCompartmentData -> new BlockCompartmentPlaceable<>(blockCompartmentFactory,
                        blockCompartmentData)));
    }

    public static <E extends AbstractCompartmentEntity & BlockCompartment> StreamCodec<ByteBuf, BlockCompartmentPlaceable<E>> streamCodec(
            final BlockCompartmentFactory<E> blockCompartmentFactory) {
        return BlockCompartment.BlockCompartmentData.STREAM_CODEC.map(
                blockCompartmentData -> new BlockCompartmentPlaceable<>(blockCompartmentFactory, blockCompartmentData),
                BlockCompartmentPlaceable::getBlockCompartmentData);
    }

    @Override
    public CompartmentPlaceableSerializer<?> getSerializer() {
        return AlekiShipsCompartmentPlaceableSerializers.BLOCK.get();
    }

    @Override
    public Optional<E> createCompartment(final Level level, final ItemStack itemStack) {
        return Optional.of(this.blockCompartmentFactory.create(level, this.blockCompartmentData.displayState()));
    }

    @FunctionalInterface
    public interface BlockCompartmentFactory<E extends AbstractCompartmentEntity & BlockCompartment> {
        E create(Level level, BlockState blockState);
    }
}