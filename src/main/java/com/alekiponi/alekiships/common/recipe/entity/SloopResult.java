package com.alekiponi.alekiships.common.recipe.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import com.alekiponi.alekiships.common.entity.AlekiShipsEntities;
import com.alekiponi.alekiships.common.entity.vehicle.SloopEntity;
import com.alekiponi.alekiships.common.entity.vehicle.SloopVariant;

import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import java.util.Optional;
import lombok.*;

@ToString
@EqualsAndHashCode
@AllArgsConstructor
@Getter(AccessLevel.PRIVATE)
public class SloopResult implements EntityResult {

    public static final MapCodec<SloopResult> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(SloopVariant.CODEC.fieldOf("variant").forGetter(SloopResult::getSloopVariant))
                    .apply(instance, SloopResult::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, SloopResult> STREAM_CODEC = SloopVariant.STREAM_CODEC.map(
            SloopResult::new, SloopResult::getSloopVariant);

    private final Holder<SloopVariant> sloopVariant;

    @Override
    public EntityResultSerializer<?> getSerializer() {
        return AlekiShipsEntityResultSerializers.SLOOP.get();
    }

    @Override
    public Optional<Entity> createEntity(final Level level) {
        final var sloop = new SloopEntity(AlekiShipsEntities.SLOOP.get(), level);
        sloop.setVariant(this.sloopVariant);
        return Optional.of(sloop);
    }
}