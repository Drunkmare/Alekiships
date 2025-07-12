package com.alekiponi.alekiships.common.compartment;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;

import com.alekiponi.alekiships.common.entity.AlekiShipsEntities;
import com.alekiponi.alekiships.common.entity.compartment.AbstractCompartmentEntity;
import com.alekiponi.alekiships.common.entity.compartment.BlockCompartment;
import com.alekiponi.alekiships.common.entity.compartment.BlockCompartmentEntity;
import com.alekiponi.alekiships.util.AlekiShipsExtraCodecs;

import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.Optional;
import java.util.function.Function;
import lombok.*;

@ToString
@EqualsAndHashCode
@Getter(AccessLevel.PRIVATE)
@AllArgsConstructor(staticName = "of")
public final class BlockCompartmentPlaceable implements CompartmentPlaceable<BlockCompartmentEntity> {

    public static final MapCodec<BlockCompartmentPlaceable> CODEC = codec(BlockCompartmentPlaceable::new,
            BlockCompartmentPlaceable::getBlockState);

    public static final StreamCodec<ByteBuf, BlockCompartmentPlaceable> STREAM_CODEC = streamCodec(
            BlockCompartmentPlaceable::new, BlockCompartmentPlaceable::getBlockState);

    private final BlockState blockState;

    public static <E extends AbstractCompartmentEntity & BlockCompartment, P extends CompartmentPlaceable<? extends E>> MapCodec<P> codec(
            final Function<BlockState, P> blockCompartmentFactory, final Function<P, BlockState> getBlockState) {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
                        AlekiShipsExtraCodecs.BLOCK_STATE_CODEC.fieldOf("block").forGetter(getBlockState))
                .apply(instance, blockCompartmentFactory));
    }

    public static <E extends AbstractCompartmentEntity & BlockCompartment, P extends CompartmentPlaceable<? extends E>> StreamCodec<ByteBuf, P> streamCodec(
            final Function<BlockState, P> blockCompartmentFactory, final Function<P, BlockState> getBlockState) {
        return ByteBufCodecs.idMapper(Block.BLOCK_STATE_REGISTRY)
                .map(blockCompartmentFactory, getBlockState);
    }

    public static <P extends CompartmentPlaceable<?>> Function<P, DataResult<P>> hasProperty(
            final Function<P, BlockState> stateGetter, final Property<?> property) {
        return blockCompartmentPlaceable -> {
            final var state = stateGetter.apply(blockCompartmentPlaceable);
            if (!state.hasProperty(property)) {
                return DataResult.error(() -> "Block " + state + " must have property " + property.getName());
            }
            return DataResult.success(blockCompartmentPlaceable);
        };
    }

    @Override
    public CompartmentPlaceableSerializer<?> getSerializer() {
        return AlekiShipsCompartmentPlaceableSerializers.BLOCK.get();
    }

    @Override
    public Optional<BlockCompartmentEntity> createCompartment(final Level level, final ItemStack itemStack) {
        final var blockCompartmentEntity = new BlockCompartmentEntity(AlekiShipsEntities.BLOCK_COMPARTMENT_ENTITY.get(),
                level, this.blockState);
        return Optional.of(blockCompartmentEntity);
    }
}