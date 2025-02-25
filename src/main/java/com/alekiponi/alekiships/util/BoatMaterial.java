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
public final class BoatMaterial {

    public static final Codec<BoatMaterial> DIRECT_CODEC = RecordCodecBuilder.create(
            instance -> instance.group(ComponentSerialization.CODEC.fieldOf("name").forGetter(BoatMaterial::name),
                            Codec.BOOL.optionalFieldOf("withstands_lava", false).forGetter(BoatMaterial::withstandsLava))
                    .apply(instance, BoatMaterial::new));

    public static final Codec<Holder<BoatMaterial>> CODEC = RegistryFileCodec.create(AlekiShipsRegistries.BOAT_MATERIAL,
            DIRECT_CODEC);

    @SuppressWarnings("unused")
    public static final StreamCodec<RegistryFriendlyByteBuf, BoatMaterial> STREAM_CODEC = StreamCodec.composite(
            ComponentSerialization.STREAM_CODEC, BoatMaterial::name, ByteBufCodecs.BOOL, BoatMaterial::withstandsLava,
            BoatMaterial::new);

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