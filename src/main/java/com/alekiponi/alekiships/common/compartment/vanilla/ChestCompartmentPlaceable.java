package com.alekiponi.alekiships.common.compartment.vanilla;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import com.alekiponi.alekiships.common.compartment.AlekiShipsCompartmentPlaceableSerializers;
import com.alekiponi.alekiships.common.compartment.CompartmentPlaceable;
import com.alekiponi.alekiships.common.compartment.CompartmentPlaceableSerializer;
import com.alekiponi.alekiships.common.entity.compartment.vanilla.ChestCompartmentData;
import com.alekiponi.alekiships.common.entity.compartment.vanilla.ChestCompartmentEntity;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Optional;
import lombok.*;

@ToString
@EqualsAndHashCode
@Getter(AccessLevel.PRIVATE)
@AllArgsConstructor(staticName = "of")
public final class ChestCompartmentPlaceable implements CompartmentPlaceable<ChestCompartmentEntity> {

    public static final MapCodec<ChestCompartmentPlaceable> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(ChestCompartmentData.CODEC.fieldOf("chest_data")
                            .forGetter(ChestCompartmentPlaceable::getChestCompartmentData))
                    .apply(instance, ChestCompartmentPlaceable::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ChestCompartmentPlaceable> STREAM_CODEC = ChestCompartmentData.STREAM_CODEC.map(
            ChestCompartmentPlaceable::new, ChestCompartmentPlaceable::getChestCompartmentData);

    private final ChestCompartmentData chestCompartmentData;

    @Override
    public CompartmentPlaceableSerializer<?> getSerializer() {
        return AlekiShipsCompartmentPlaceableSerializers.CHEST.get();
    }

    @Override
    public Optional<ChestCompartmentEntity> createCompartment(final Level level, final ItemStack itemStack) {
        return Optional.of(ChestCompartmentEntity.create(level, this.chestCompartmentData, itemStack));
    }
}