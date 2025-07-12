package com.alekiponi.alekiships.common.compartment.vanilla;

import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;

import com.alekiponi.alekiships.common.compartment.AlekiShipsCompartmentPlaceableSerializers;
import com.alekiponi.alekiships.common.compartment.BlockCompartmentPlaceable;
import com.alekiponi.alekiships.common.compartment.CompartmentPlaceable;
import com.alekiponi.alekiships.common.compartment.CompartmentPlaceableSerializer;
import com.alekiponi.alekiships.common.entity.AlekiShipsEntities;
import com.alekiponi.alekiships.common.entity.compartment.CompartmentCloneable;
import com.alekiponi.alekiships.common.entity.compartment.vanilla.FurnaceCompartmentEntity;

import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;
import lombok.*;

@ToString
@EqualsAndHashCode
@Getter(AccessLevel.PRIVATE)
@AllArgsConstructor(staticName = "of")
public final class FurnaceCompartmentPlaceable implements CompartmentPlaceable<FurnaceCompartmentEntity> {

    public static final MapCodec<FurnaceCompartmentPlaceable> CODEC = BlockCompartmentPlaceable.codec(
                    FurnaceCompartmentPlaceable::new, FurnaceCompartmentPlaceable::getBlockState)
            .validate(BlockCompartmentPlaceable.hasProperty(FurnaceCompartmentPlaceable::getBlockState,
                    AbstractFurnaceBlock.LIT));

    public static final StreamCodec<ByteBuf, FurnaceCompartmentPlaceable> STREAM_CODEC = BlockCompartmentPlaceable.streamCodec(
            FurnaceCompartmentPlaceable::new, FurnaceCompartmentPlaceable::getBlockState);

    private final BlockState blockState;

    @Override
    public CompartmentPlaceableSerializer<?> getSerializer() {
        return AlekiShipsCompartmentPlaceableSerializers.FURNACE.get();
    }

    @Override
    public Optional<FurnaceCompartmentEntity> createCompartment(final Level level, final ItemStack itemStack) {
        final var furnaceCompartment = new FurnaceCompartmentEntity(AlekiShipsEntities.FURNACE_COMPARTMENT_ENTITY.get(),
                level, this.blockState);
        CompartmentCloneable.initialize(furnaceCompartment, itemStack);
        return Optional.of(furnaceCompartment);
    }
}