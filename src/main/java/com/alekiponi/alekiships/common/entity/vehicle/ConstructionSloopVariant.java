package com.alekiponi.alekiships.common.entity.vehicle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import com.alekiponi.alekiships.common.AlekiShipsRegistries;
import com.alekiponi.alekiships.common.entity.SloopConstructionState;

import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public final class ConstructionSloopVariant {

    public static final Codec<ConstructionSloopVariant> DIRECT_CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            ResourceLocation.CODEC.fieldOf("texture").forGetter(sloopVariant -> sloopVariant.texture),
                            RegistryFileCodec.create(AlekiShipsRegistries.CONSTRUCTION_SLOOP_INPUT,
                                            SloopConstructionState.SloopConstructionStage.INPUT_CODEC)
                                    .fieldOf("construction_input")
                                    .forGetter(ConstructionSloopVariant::getConstructionInput))
                    .apply(instance, ConstructionSloopVariant::new));

    public static final Codec<Holder<ConstructionSloopVariant>> CODEC = RegistryFileCodec.create(
            AlekiShipsRegistries.CONSTRUCTION_SLOOP_VARIANT, DIRECT_CODEC);

    public static final StreamCodec<RegistryFriendlyByteBuf, ConstructionSloopVariant> DIRECT_STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, sloopConstructionType -> sloopConstructionType.texture,
            ByteBufCodecs.holder(AlekiShipsRegistries.CONSTRUCTION_SLOOP_INPUT,
                    SloopConstructionState.SloopConstructionStage.INPUT_STREAM_CODEC),
            ConstructionSloopVariant::getConstructionInput, ConstructionSloopVariant::new);

    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<ConstructionSloopVariant>> STREAM_CODEC = ByteBufCodecs.holder(
            AlekiShipsRegistries.CONSTRUCTION_SLOOP_VARIANT, DIRECT_STREAM_CODEC);

    private static final Component UNKNOWN = Component.literal("Unknown");

    private final ResourceLocation texture;
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private final ResourceLocation textureFull;
    @Getter
    private final Holder<ConstructionInput<SloopConstructionState.SloopConstructionStage>> constructionInput;

    @Builder(toBuilder = true)
    public ConstructionSloopVariant(final ResourceLocation texture,
            final Holder<ConstructionInput<SloopConstructionState.SloopConstructionStage>> constructionInput) {
        this.texture = texture;
        this.textureFull = BoatVariant.TEXTURE_ID_CONVERTER.idToFile(texture);
        this.constructionInput = constructionInput;
    }

    /**
     * @return The name of the sloop construction variant
     */
    public static Component name(final Holder<ConstructionSloopVariant> variantHolder) {
        return variantHolder.unwrapKey()
                .map(ResourceKey::location)
                .map(ConstructionSloopVariant::getDescriptionId)
                .<Component>map(Component::translatable).orElse(UNKNOWN);
    }

    /**
     * @param registryName The registry name of the sloop construction variant
     *
     * @return The lang key for the sloop construction variant name
     */
    public static String getDescriptionId(final ResourceLocation registryName) {
        return registryName.toLanguageKey("sloop_construction_variant");
    }

    public ResourceLocation texture() {
        return this.textureFull;
    }
}