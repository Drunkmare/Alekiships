package com.alekiponi.alekiships.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.common.AlekiShipsRegistries;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

import lombok.*;
import lombok.experimental.Accessors;

/**
 * A material for one of our boat frames
 */
@Getter
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@Accessors(fluent = true)
public final class FrameMaterial {

    public static final Codec<FrameMaterial> DIRECT_CODEC = RecordCodecBuilder.create(
            instance -> instance.group(ResourceLocation.CODEC.fieldOf("texture").forGetter(FrameMaterial::texture),
                    ComponentSerialization.CODEC.fieldOf("name").forGetter(FrameMaterial::name),
                    SoundEvent.CODEC.optionalFieldOf("sound", Holder.direct(SoundEvents.WOOD_PLACE))
                            .forGetter(FrameMaterial::sound)).apply(instance, FrameMaterial::new));

    public static final Codec<Holder<FrameMaterial>> CODEC = RegistryFileCodec.create(
            AlekiShipsRegistries.FRAME_MATERIAL, DIRECT_CODEC);

    public static final StreamCodec<RegistryFriendlyByteBuf, FrameMaterial> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, FrameMaterial::texture, ComponentSerialization.TRUSTED_STREAM_CODEC,
            FrameMaterial::name, ByteBufCodecs.holderRegistry(Registries.SOUND_EVENT), FrameMaterial::sound,
            FrameMaterial::new);

    public static final ResourceKey<FrameMaterial> OAK = createKey("oak");
    public static final ResourceKey<FrameMaterial> SPRUCE = createKey("spruce");
    public static final ResourceKey<FrameMaterial> BIRCH = createKey("birch");
    public static final ResourceKey<FrameMaterial> ACACIA = createKey("acacia");
    public static final ResourceKey<FrameMaterial> CHERRY = createKey("cherry");
    public static final ResourceKey<FrameMaterial> JUNGLE = createKey("jungle");
    public static final ResourceKey<FrameMaterial> DARK_OAK = createKey("dark_oak");
    public static final ResourceKey<FrameMaterial> CRIMSON = createKey("crimson");
    public static final ResourceKey<FrameMaterial> WARPED = createKey("warped");
    public static final ResourceKey<FrameMaterial> MANGROVE = createKey("mangrove");
    public static final ResourceKey<FrameMaterial> BAMBOO = createKey("bamboo");
    public static final ResourceKey<FrameMaterial> DEFAULT = OAK;

    /**
     * The materials texture. Must be in the vanilla blocks atlas
     */
    private final ResourceLocation texture;
    /**
     * The materials name
     */
    private final Component name;
    /**
     * The sound this material makes
     */
    @Builder.Default
    private final Holder<SoundEvent> sound = Holder.direct(SoundEvents.WOOD_PLACE);

    private static ResourceKey<FrameMaterial> createKey(final String name) {
        return ResourceKey.create(AlekiShipsRegistries.FRAME_MATERIAL, AlekiShips.location(name));
    }

    public static void bootstrapOverworld(final BootstrapContext<FrameMaterial> context) {
        context.register(OAK, getBuilder("oak").build());
        context.register(SPRUCE, getBuilder("spruce").build());
        context.register(BIRCH, getBuilder("birch").build());
        context.register(ACACIA, getBuilder("acacia").build());
        context.register(CHERRY, getBuilder("cherry").build());
        context.register(JUNGLE, getBuilder("jungle").build());
        context.register(DARK_OAK, getBuilder("dark_oak").build());
        context.register(MANGROVE, getBuilder("mangrove").build());
        context.register(BAMBOO, getBuilder("bamboo").sound(Holder.direct(SoundEvents.BAMBOO_PLACE))
                .build());
    }

    public static void bootstrapNether(final BootstrapContext<FrameMaterial> context) {
        context.register(CRIMSON, getBuilder("crimson").sound(Holder.direct(SoundEvents.NETHER_WOOD_PLACE))
                .build());
        context.register(WARPED, getBuilder("warped").sound(Holder.direct(SoundEvents.NETHER_WOOD_PLACE))
                .build());
    }

    private static FrameMaterialBuilder getBuilder(final String wood) {
        return FrameMaterial.builder()
                .texture(ResourceLocation.withDefaultNamespace("block/" + wood + "_planks"))
                .name(Component.translatable(getDescriptionId(AlekiShips.location(wood))));
    }

    /**
     * @param registryName The registry name of the frame material
     *
     * @return The lang key for the materials name
     */
    public static String getDescriptionId(final ResourceLocation registryName) {
        return registryName.toLanguageKey("frame_material");
    }

    public SoundEvent getSound() {
        return this.sound.value();
    }
}