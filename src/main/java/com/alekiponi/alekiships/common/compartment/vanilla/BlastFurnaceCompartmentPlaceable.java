package com.alekiponi.alekiships.common.compartment.vanilla;

import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;

import com.alekiponi.alekiships.common.compartment.AlekiShipsCompartmentPlaceableSerializers;
import com.alekiponi.alekiships.common.compartment.BlockCompartmentPlaceable;
import com.alekiponi.alekiships.common.compartment.CompartmentPlaceable;
import com.alekiponi.alekiships.common.compartment.CompartmentPlaceableSerializer;
import com.alekiponi.alekiships.common.entity.AlekiShipsEntities;
import com.alekiponi.alekiships.common.entity.compartment.CompartmentCloneable;
import com.alekiponi.alekiships.common.entity.compartment.vanilla.BlastFurnaceCompartmentEntity;

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
public final class BlastFurnaceCompartmentPlaceable implements CompartmentPlaceable<BlastFurnaceCompartmentEntity> {

    public static final MapCodec<BlastFurnaceCompartmentPlaceable> CODEC = BlockCompartmentPlaceable.codec(
                    BlastFurnaceCompartmentPlaceable::new, BlastFurnaceCompartmentPlaceable::getBlockState)
            .validate(BlockCompartmentPlaceable.hasProperty(BlastFurnaceCompartmentPlaceable::getBlockState,
                    AbstractFurnaceBlock.LIT));

    public static final StreamCodec<ByteBuf, BlastFurnaceCompartmentPlaceable> STREAM_CODEC = BlockCompartmentPlaceable.streamCodec(
            BlastFurnaceCompartmentPlaceable::new, BlastFurnaceCompartmentPlaceable::getBlockState);

    private final BlockState blockState;

    @Override
    public CompartmentPlaceableSerializer<?> getSerializer() {
        return AlekiShipsCompartmentPlaceableSerializers.BLAST_FURNACE.get();
    }

    @Override
    public Optional<BlastFurnaceCompartmentEntity> createCompartment(final Level level, final ItemStack itemStack) {
        final var blastFurnaceCompartment = new BlastFurnaceCompartmentEntity(
                AlekiShipsEntities.BLAST_FURNACE_COMPARTMENT_ENTITY.get(), level, this.blockState);
        CompartmentCloneable.initialize(blastFurnaceCompartment, itemStack);
        return Optional.of(blastFurnaceCompartment);
    }
}