package com.alekiponi.alekiships.common.recipe.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;

import java.util.Optional;
import lombok.*;

@ToString
@EqualsAndHashCode
@AllArgsConstructor
@Getter(AccessLevel.PRIVATE)
public final class SimpleResult implements EntityResult {
    public static final MapCodec<SimpleResult> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("entity").forGetter(SimpleResult::getEntityType),
                    CustomData.CODEC.optionalFieldOf("custom_data", CustomData.EMPTY).forGetter(SimpleResult::getCustomData))
            .apply(instance, SimpleResult::new));

    @SuppressWarnings("deprecation")
    public static final StreamCodec<RegistryFriendlyByteBuf, SimpleResult> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.registry(Registries.ENTITY_TYPE), SimpleResult::getEntityType, CustomData.STREAM_CODEC,
            SimpleResult::getCustomData, SimpleResult::new);

    private final EntityType<?> entityType;
    private final CustomData customData;

    public SimpleResult(final EntityType<?> entityType) {
        this(entityType, CustomData.EMPTY);
    }

    @Override
    public EntityResultSerializer<?> getSerializer() {
        return AlekiShipsEntityResultSerializers.SIMPLE.get();
    }

    @Override
    public Optional<Entity> createEntity(final Level level) {
        final var maybeEntity = Optional.<Entity>ofNullable(this.entityType.create(level));
        maybeEntity.ifPresent(this.customData::loadInto);
        return maybeEntity;
    }
}