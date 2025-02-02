package com.alekiponi.alekiships.common.recipe.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import com.alekiponi.alekiships.common.entity.AlekiShipsEntities;
import com.alekiponi.alekiships.common.entity.vehicle.RowboatEntity;
import com.alekiponi.alekiships.common.entity.vehicle.RowboatVariant;

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
public final class RowboatResult implements EntityResult {

    public static final MapCodec<RowboatResult> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    RowboatVariant.CODEC.fieldOf("variant").forGetter(RowboatResult::getRowboatVariant))
            .apply(instance, RowboatResult::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, RowboatResult> STREAM_CODEC = RowboatVariant.STREAM_CODEC.map(
            RowboatResult::new, RowboatResult::getRowboatVariant);

    private final Holder<RowboatVariant> rowboatVariant;

    @Override
    public EntityResultSerializer<?> getSerializer() {
        return AlekiShipsEntityResultSerializers.ROWBOAT.get();
    }

    @Override
    public Optional<Entity> createEntity(final Level level) {
        final var rowboat = new RowboatEntity(AlekiShipsEntities.ROWBOAT.get(), level);
        rowboat.setVariant(this.rowboatVariant);
        return Optional.of(rowboat);
    }
}