package com.alekiponi.alekiships.common.recipe.entity;

import com.mojang.serialization.Codec;

import com.alekiponi.alekiships.common.AlekiShipsBuiltInRegistries;
import com.alekiponi.alekiships.common.AlekiShipsRegistries;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import java.util.Optional;

public interface EntityResult {
    Codec<EntityResult> CODEC = AlekiShipsBuiltInRegistries.ENTITY_RESULT_SERIALIZERS.byNameCodec()
            .dispatch(EntityResult::getSerializer, EntityResultSerializer::codec);
    StreamCodec<RegistryFriendlyByteBuf, EntityResult> STREAM_CODEC = ByteBufCodecs.registry(
                    AlekiShipsRegistries.ENTITY_RESULT_SERIALIZER)
            .dispatch(EntityResult::getSerializer, EntityResultSerializer::streamCodec);

    EntityResultSerializer<?> getSerializer();

    Optional<Entity> createEntity(Level level);
}