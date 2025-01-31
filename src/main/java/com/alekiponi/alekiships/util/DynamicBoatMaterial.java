package com.alekiponi.alekiships.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import com.alekiponi.alekiships.common.AlekiShipsRegistries;

import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceLocation;

import lombok.*;
import lombok.experimental.Accessors;

/**
 * A boat material backing our actual boat variants enabling re-use
 */
@Getter
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@Accessors(fluent = true)
public final class DynamicBoatMaterial {

    public static final Codec<DynamicBoatMaterial> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    ComponentSerialization.CODEC.fieldOf("name").forGetter(DynamicBoatMaterial::name),
                    Codec.BOOL.optionalFieldOf("withstands_lava", false).forGetter(DynamicBoatMaterial::withstandsLava))
            .apply(instance, DynamicBoatMaterial::new));

    public static final Codec<Holder<DynamicBoatMaterial>> CODEC = RegistryFileCodec.create(
            AlekiShipsRegistries.BOAT_MATERIAL, DIRECT_CODEC);

    @SuppressWarnings("unused")
    public static final StreamCodec<RegistryFriendlyByteBuf, DynamicBoatMaterial> STREAM_CODEC = StreamCodec.composite(
            ComponentSerialization.STREAM_CODEC, DynamicBoatMaterial::name, ByteBufCodecs.BOOL,
            DynamicBoatMaterial::withstandsLava, DynamicBoatMaterial::new);

    /**
     * The material name
     */
    private final Component name;
    /**
     * If this material withstands lava
     */
    @Builder.Default
    private final boolean withstandsLava = false;

    /**
     * @param registryName The registry name of the boat material
     *
     * @return The lang key for the materials name
     */
    public static String getDescriptionId(final ResourceLocation registryName) {
        return registryName.toLanguageKey("boat_material");
    }
}