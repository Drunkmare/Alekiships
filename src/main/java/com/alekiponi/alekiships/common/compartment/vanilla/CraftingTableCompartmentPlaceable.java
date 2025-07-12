package com.alekiponi.alekiships.common.compartment.vanilla;

import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;

import com.alekiponi.alekiships.common.compartment.AlekiShipsCompartmentPlaceableSerializers;
import com.alekiponi.alekiships.common.compartment.BlockCompartmentPlaceable;
import com.alekiponi.alekiships.common.compartment.CompartmentPlaceable;
import com.alekiponi.alekiships.common.compartment.CompartmentPlaceableSerializer;
import com.alekiponi.alekiships.common.entity.AlekiShipsEntities;
import com.alekiponi.alekiships.common.entity.compartment.BlockCompartment;
import com.alekiponi.alekiships.common.entity.compartment.SimpleBlockMenuCompartmentEntity;

import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;
import lombok.*;

@ToString
@EqualsAndHashCode
@Getter(AccessLevel.PRIVATE)
@AllArgsConstructor(staticName = "of")
public final class CraftingTableCompartmentPlaceable implements CompartmentPlaceable<SimpleBlockMenuCompartmentEntity> {

    public static final MapCodec<CraftingTableCompartmentPlaceable> CODEC = BlockCompartmentPlaceable.codec(
            CraftingTableCompartmentPlaceable::new, CraftingTableCompartmentPlaceable::getBlockState);

    public static final StreamCodec<ByteBuf, CraftingTableCompartmentPlaceable> STREAM_CODEC = BlockCompartmentPlaceable.streamCodec(
            CraftingTableCompartmentPlaceable::new, CraftingTableCompartmentPlaceable::getBlockState);

    private static final BlockCompartment.BlockCompartmentFactory<SimpleBlockMenuCompartmentEntity> FACTORY = SimpleBlockMenuCompartmentEntity.directCompartmentFactory(
            SimpleBlockMenuCompartmentEntity.CRAFTING_TABLE);
    private final BlockState blockState;

    @Override
    public CompartmentPlaceableSerializer<?> getSerializer() {
        return AlekiShipsCompartmentPlaceableSerializers.CRAFTING_TABLE.get();
    }

    @Override
    public Optional<SimpleBlockMenuCompartmentEntity> createCompartment(final Level level, final ItemStack itemStack) {
        final var smokerCompartment = FACTORY.create(AlekiShipsEntities.WORKBENCH_COMPARTMENT_ENTITY.get(), level,
                this.blockState);
        return Optional.of(smokerCompartment);
    }
}