package com.alekiponi.alekiships.common.compartment.vanilla;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;

import com.alekiponi.alekiships.common.compartment.AlekiShipsCompartmentPlaceableSerializers;
import com.alekiponi.alekiships.common.compartment.CompartmentPlaceable;
import com.alekiponi.alekiships.common.compartment.CompartmentPlaceableSerializer;
import com.alekiponi.alekiships.common.entity.compartment.vanilla.ShulkerBoxCompartmentEntity;
import com.alekiponi.alekiships.util.CommonHelper;

import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.EnumMap;
import java.util.Optional;
import lombok.*;

@ToString
@EqualsAndHashCode
@Getter(AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class ShulkerBoxCompartmentPlaceable implements CompartmentPlaceable<ShulkerBoxCompartmentEntity> {

    public static final ShulkerBoxCompartmentPlaceable NO_DYE = new ShulkerBoxCompartmentPlaceable(Optional.empty());
    public static final EnumMap<DyeColor, ShulkerBoxCompartmentPlaceable> DYED_BOXES = CommonHelper.mapOfKeys(
            DyeColor.class, color -> new ShulkerBoxCompartmentPlaceable(Optional.ofNullable(color)));

    public static final MapCodec<ShulkerBoxCompartmentPlaceable> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                            DyeColor.CODEC.optionalFieldOf("color").forGetter(ShulkerBoxCompartmentPlaceable::getDyeColor))
                    .apply(instance, ShulkerBoxCompartmentPlaceable::of));

    public static final StreamCodec<ByteBuf, ShulkerBoxCompartmentPlaceable> STREAM_CODEC = ByteBufCodecs.optional(
                    DyeColor.STREAM_CODEC)
            .map(ShulkerBoxCompartmentPlaceable::of, ShulkerBoxCompartmentPlaceable::getDyeColor);

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private final Optional<DyeColor> dyeColor;

    public static ShulkerBoxCompartmentPlaceable of() {
        return NO_DYE;
    }

    public static ShulkerBoxCompartmentPlaceable of(final DyeColor color) {
        return DYED_BOXES.get(color);
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static ShulkerBoxCompartmentPlaceable of(final Optional<DyeColor> color) {
        return color.map(ShulkerBoxCompartmentPlaceable::of).orElseGet(ShulkerBoxCompartmentPlaceable::of);
    }

    @Override
    public CompartmentPlaceableSerializer<?> getSerializer() {
        return AlekiShipsCompartmentPlaceableSerializers.SHULKER_BOX.get();
    }

    @Override
    public Optional<ShulkerBoxCompartmentEntity> createCompartment(final Level level, final ItemStack itemStack) {
        return Optional.of(ShulkerBoxCompartmentEntity.create(level, itemStack, this.dyeColor.orElse(null)));
    }
}