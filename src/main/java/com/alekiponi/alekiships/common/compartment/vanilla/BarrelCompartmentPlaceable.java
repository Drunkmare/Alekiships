package com.alekiponi.alekiships.common.compartment.vanilla;

import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;

import com.alekiponi.alekiships.common.compartment.AlekiShipsCompartmentPlaceableSerializers;
import com.alekiponi.alekiships.common.compartment.BlockCompartmentPlaceable;
import com.alekiponi.alekiships.common.compartment.CompartmentPlaceable;
import com.alekiponi.alekiships.common.compartment.CompartmentPlaceableSerializer;
import com.alekiponi.alekiships.common.entity.AlekiShipsEntities;
import com.alekiponi.alekiships.common.entity.compartment.CompartmentCloneable;
import com.alekiponi.alekiships.common.entity.compartment.vanilla.BarrelCompartmentEntity;

import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;
import lombok.*;

@ToString
@EqualsAndHashCode
@Getter(AccessLevel.PRIVATE)
@AllArgsConstructor(staticName = "of")
public final class BarrelCompartmentPlaceable implements CompartmentPlaceable<BarrelCompartmentEntity> {

    public static final MapCodec<BarrelCompartmentPlaceable> CODEC = BlockCompartmentPlaceable.codec(
                    BarrelCompartmentPlaceable::new, BarrelCompartmentPlaceable::getBlockState)
            .validate(
                    BlockCompartmentPlaceable.hasProperty(BarrelCompartmentPlaceable::getBlockState, BarrelBlock.OPEN));

    public static final StreamCodec<ByteBuf, BarrelCompartmentPlaceable> STREAM_CODEC = BlockCompartmentPlaceable.streamCodec(
            BarrelCompartmentPlaceable::new, BarrelCompartmentPlaceable::getBlockState);

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