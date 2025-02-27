package com.alekiponi.alekiships.common.recipe.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import com.alekiponi.alekiships.common.entity.AlekiShipsEntities;
import com.alekiponi.alekiships.common.entity.vehicle.ConstructionSloopVariant;
import com.alekiponi.alekiships.common.entity.vehicle.SloopUnderConstructionEntity;

import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import java.util.Optional;
import lombok.*;

@ToString
@EqualsAndHashCode
@Getter(AccessLevel.PRIVATE)
@AllArgsConstructor(staticName = "of")
public final class ConstructionSloopResult implements EntityResult {

    public static final MapCodec<ConstructionSloopResult> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(ConstructionSloopVariant.CODEC.fieldOf("variant")
                            .forGetter(ConstructionSloopResult::getConstructionSloopVariant))
                    .apply(instance, ConstructionSloopResult::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ConstructionSloopResult> STREAM_CODEC = ConstructionSloopVariant.STREAM_CODEC.map(
            ConstructionSloopResult::new, ConstructionSloopResult::getConstructionSloopVariant);

    private final Holder<ConstructionSloopVariant> constructionSloopVariant;

    @Override
    public EntityResultSerializer<?> getSerializer() {
        return AlekiShipsEntityResultSerializers.CONSTRUCTION_SLOOP.get();
    }

    @Override
    public Optional<Entity> createEntity(final Level level) {
        final var constructionSloop = new SloopUnderConstructionEntity(AlekiShipsEntities.CONSTRUCTION_SLOOP.get(),
                level);
        constructionSloop.setConstructionVariant(this.constructionSloopVariant);
        return Optional.of(constructionSloop);
    }
}