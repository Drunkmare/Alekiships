package com.alekiponi.alekiships.common.compartment;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;

import com.alekiponi.alekiships.common.entity.AlekiShipsEntities;
import com.alekiponi.alekiships.common.entity.compartment.CompartmentCloneable;
import com.alekiponi.alekiships.common.entity.compartment.vanilla.BarrelCompartmentEntity;
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
@Getter(AccessLevel.PRIVATE)
@AllArgsConstructor(staticName = "of")
public class BarrelCompartmentPlaceable implements CompartmentPlaceable<BarrelCompartmentEntity> {

    public static final MapCodec<BarrelCompartmentPlaceable> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(AlekiShipsExtraCodecs.BLOCK_STATE_CODEC.fieldOf("block")
                            .forGetter(BarrelCompartmentPlaceable::getBlockState))
                    .apply(instance, BarrelCompartmentPlaceable::new));

    public static final StreamCodec<ByteBuf, BarrelCompartmentPlaceable> STREAM_CODEC = ByteBufCodecs.idMapper(
                    Block.BLOCK_STATE_REGISTRY)
            .map(BarrelCompartmentPlaceable::new, BarrelCompartmentPlaceable::getBlockState);

    private final BlockState blockState;

    @Override
    public CompartmentPlaceableSerializer<?> getSerializer() {
        return AlekiShipsCompartmentPlaceableSerializers.BARREL.get();
    }

    @Override
    public Optional<BarrelCompartmentEntity> createCompartment(final Level level, final ItemStack itemStack) {
        final var barrelCompartment = new BarrelCompartmentEntity(AlekiShipsEntities.BARREL_COMPARTMENT_ENTITY.get(),
                level, this.blockState);
        CompartmentCloneable.initialize(barrelCompartment, itemStack);
        return Optional.of(barrelCompartment);
    }
}