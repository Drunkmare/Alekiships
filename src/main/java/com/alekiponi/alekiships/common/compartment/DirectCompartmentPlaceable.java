package com.alekiponi.alekiships.common.compartment;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import com.alekiponi.alekiships.common.AlekiShipsBuiltInRegistries;
import com.alekiponi.alekiships.common.AlekiShipsRegistries;
import com.alekiponi.alekiships.common.entity.compartment.AbstractCompartmentEntity;
import com.alekiponi.alekiships.common.entity.compartment.vanilla.EnderChestCompartmentEntity;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Optional;
import java.util.function.Supplier;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

/**
 * This type is special and is for compartments that cannot or do not fit a generic construction format. For example
 * {@link EnderChestCompartmentEntity} simply doesn't make sense as its own
 */
@Slf4j
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@Getter(AccessLevel.PRIVATE)
public final class DirectCompartmentPlaceable implements CompartmentPlaceable<AbstractCompartmentEntity> {

    public static final MapCodec<DirectCompartmentPlaceable> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(AlekiShipsBuiltInRegistries.DIRECT_COMPARTMENT_TYPES.byNameCodec()
                            .fieldOf("compartment_type")
                            .forGetter(DirectCompartmentPlaceable::getDirectCompartmentType))
                    .apply(instance, DirectCompartmentPlaceable::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, DirectCompartmentPlaceable> STREAM_CODEC = ByteBufCodecs.registry(
                    AlekiShipsRegistries.DIRECT_COMPARTMENT_TYPE)
            .map(DirectCompartmentPlaceable::new, DirectCompartmentPlaceable::getDirectCompartmentType);

    private final DirectCompartmentType<?> directCompartmentType;

    public DirectCompartmentPlaceable(final Supplier<? extends DirectCompartmentType<?>> directCompartmentType) {
        this(directCompartmentType.get());
    }

    public static DirectCompartmentPlaceable of(
            final Supplier<? extends DirectCompartmentType<?>> directCompartmentType) {
        return new DirectCompartmentPlaceable(directCompartmentType);
    }

    @Override
    public CompartmentPlaceableSerializer<?> getSerializer() {
        return AlekiShipsCompartmentPlaceableSerializers.DIRECT.get();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Optional<AbstractCompartmentEntity> createCompartment(final Level level, final ItemStack itemStack) {
        return (Optional<AbstractCompartmentEntity>) this.directCompartmentType.create(level, itemStack);
    }
}