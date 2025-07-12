package com.alekiponi.alekiships.common.compartment.vanilla;

import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;

import com.alekiponi.alekiships.common.compartment.AlekiShipsCompartmentPlaceableSerializers;
import com.alekiponi.alekiships.common.compartment.BlockCompartmentPlaceable;
import com.alekiponi.alekiships.common.compartment.CompartmentPlaceable;
import com.alekiponi.alekiships.common.compartment.CompartmentPlaceableSerializer;
import com.alekiponi.alekiships.common.entity.AlekiShipsEntities;
import com.alekiponi.alekiships.common.entity.compartment.CompartmentCloneable;
import com.alekiponi.alekiships.common.entity.compartment.vanilla.SmokerCompartmentEntity;

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
public final class SmokerCompartmentPlaceable implements CompartmentPlaceable<SmokerCompartmentEntity> {

    public static final MapCodec<SmokerCompartmentPlaceable> CODEC = BlockCompartmentPlaceable.codec(
                    SmokerCompartmentPlaceable::new, SmokerCompartmentPlaceable::getBlockState)
            .validate(BlockCompartmentPlaceable.hasProperty(SmokerCompartmentPlaceable::getBlockState,
                    AbstractFurnaceBlock.LIT));

    public static final StreamCodec<ByteBuf, SmokerCompartmentPlaceable> STREAM_CODEC = BlockCompartmentPlaceable.streamCodec(
            SmokerCompartmentPlaceable::new, SmokerCompartmentPlaceable::getBlockState);

    private final BlockState blockState;

    @Override
    public CompartmentPlaceableSerializer<?> getSerializer() {
        return AlekiShipsCompartmentPlaceableSerializers.SMOKER.get();
    }

    @Override
    public Optional<SmokerCompartmentEntity> createCompartment(final Level level, final ItemStack itemStack) {
        final var smokerCompartment = new SmokerCompartmentEntity(AlekiShipsEntities.SMOKER_COMPARTMENT_ENTITY.get(),
                level, this.blockState);
        CompartmentCloneable.initialize(smokerCompartment, itemStack);
        return Optional.of(smokerCompartment);
    }
}