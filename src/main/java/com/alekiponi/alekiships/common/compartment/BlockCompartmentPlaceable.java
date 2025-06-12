package com.alekiponi.alekiships.common.compartment;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;

import com.alekiponi.alekiships.common.entity.compartment.AbstractCompartmentEntity;
import com.alekiponi.alekiships.common.entity.compartment.BlockCompartment;
import com.alekiponi.alekiships.util.AlekiShipsExtraCodecs;

import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;
import lombok.*;

@ToString
@EqualsAndHashCode
@AllArgsConstructor
@Getter(AccessLevel.PRIVATE)
public final class BlockCompartmentPlaceable<E extends AbstractCompartmentEntity & BlockCompartment> implements CompartmentPlaceable<E> {

    private final BlockCompartmentFactory<E> blockCompartmentFactory;
    private final BlockState blockState;

    public static <E extends AbstractCompartmentEntity & BlockCompartment> MapCodec<BlockCompartmentPlaceable<E>> codec(
            final BlockCompartmentFactory<E> blockCompartmentFactory) {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
                        AlekiShipsExtraCodecs.BLOCK_STATE_CODEC.fieldOf("block")
                                .forGetter(BlockCompartmentPlaceable::getBlockState))
                .apply(instance, blockState -> new BlockCompartmentPlaceable<>(blockCompartmentFactory, blockState)));
    }

    public static <E extends AbstractCompartmentEntity & BlockCompartment> StreamCodec<ByteBuf, BlockCompartmentPlaceable<E>> streamCodec(
            final BlockCompartmentFactory<E> blockCompartmentFactory) {
        return ByteBufCodecs.idMapper(Block.BLOCK_STATE_REGISTRY)
                .map(blockState -> new BlockCompartmentPlaceable<>(blockCompartmentFactory, blockState),
                        BlockCompartmentPlaceable::getBlockState);
    }

    @Override
    public CompartmentPlaceableSerializer<?> getSerializer() {
        return AlekiShipsCompartmentPlaceableSerializers.BLOCK.get();
    }

    @Override
    public Optional<E> createCompartment(final Level level, final ItemStack itemStack) {
        return Optional.of(this.blockCompartmentFactory.create(level, this.blockState));
    }

    @FunctionalInterface
    public interface BlockCompartmentFactory<E extends AbstractCompartmentEntity & BlockCompartment> {
        E create(Level level, BlockState blockState);
    }
}