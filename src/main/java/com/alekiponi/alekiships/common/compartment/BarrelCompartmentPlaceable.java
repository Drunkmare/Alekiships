package com.alekiponi.alekiships.common.compartment;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;

import com.alekiponi.alekiships.common.entity.AlekiShipsEntities;
import com.alekiponi.alekiships.common.entity.compartment.BlockCompartment;
import com.alekiponi.alekiships.common.entity.compartment.CompartmentCloneable;
import com.alekiponi.alekiships.common.entity.compartment.vanilla.BarrelCompartmentEntity;

import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Optional;
import lombok.*;

@ToString
@EqualsAndHashCode
@Getter(AccessLevel.PRIVATE)
@AllArgsConstructor(staticName = "of")
public class BarrelCompartmentPlaceable implements CompartmentPlaceable<BarrelCompartmentEntity> {

    public static final MapCodec<BarrelCompartmentPlaceable> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(BlockCompartment.BlockCompartmentData.CODEC.fieldOf("block")
                            .forGetter(BarrelCompartmentPlaceable::getBlockCompartmentData))
                    .apply(instance, BarrelCompartmentPlaceable::new));

    public static final StreamCodec<ByteBuf, BarrelCompartmentPlaceable> STREAM_CODEC = BlockCompartment.BlockCompartmentData.STREAM_CODEC.map(
            BarrelCompartmentPlaceable::new, BarrelCompartmentPlaceable::getBlockCompartmentData);

    private final BlockCompartment.BlockCompartmentData blockCompartmentData;

    @Override
    public CompartmentPlaceableSerializer<?> getSerializer() {
        return AlekiShipsCompartmentPlaceableSerializers.BARREL.get();
    }

    @Override
    public Optional<BarrelCompartmentEntity> createCompartment(final Level level, final ItemStack itemStack) {
        final var barrelCompartment = new BarrelCompartmentEntity(AlekiShipsEntities.BARREL_COMPARTMENT_ENTITY.get(),
                level, this.blockCompartmentData.displayState());
        CompartmentCloneable.initialize(barrelCompartment, itemStack);
        return Optional.of(barrelCompartment);
    }
}