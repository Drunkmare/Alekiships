package com.alekiponi.alekiships.common.entity.vehicle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import com.alekiponi.alekiships.common.AlekiShipsRegistries;
import com.alekiponi.alekiships.util.DynamicBoatMaterial;
import com.alekiponi.alekiships.util.RepairMaterials;

import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.function.Consumer;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public final class SloopVariant implements BoatVariant {

    public static final Codec<SloopVariant> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    ResourceLocation.CODEC.fieldOf("texture").forGetter(sloopVariant -> sloopVariant.texture),
                    BoatVariant.boatMaterialField(), BoatVariant.repairMaterialField(), BoatVariant.lootTableField())
            .apply(instance, SloopVariant::new));

    public static final Codec<Holder<SloopVariant>> CODEC = RegistryFileCodec.create(AlekiShipsRegistries.SLOOP_VARIANT,
            DIRECT_CODEC);

    public static final StreamCodec<RegistryFriendlyByteBuf, SloopVariant> DIRECT_STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, sloopVariant -> sloopVariant.texture,
            ByteBufCodecs.holderRegistry(AlekiShipsRegistries.BOAT_MATERIAL), SloopVariant::getBoatMaterial,
            RepairMaterials.STREAM_CODEC, SloopVariant::getRepairMaterials,
            ResourceKey.streamCodec(Registries.LOOT_TABLE), SloopVariant::getLootTable, SloopVariant::new);

    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<SloopVariant>> STREAM_CODEC = ByteBufCodecs.holder(
            AlekiShipsRegistries.SLOOP_VARIANT, DIRECT_STREAM_CODEC);

    public static final String SERIALIZATION_KEY = "sloop_variant";

    private final ResourceLocation texture;
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private final ResourceLocation textureFull;
    @Getter
    private final Holder<DynamicBoatMaterial> boatMaterial;
    @Getter
    private final RepairMaterials repairMaterials;
    @Getter
    private final ResourceKey<LootTable> lootTable;

    @Builder(toBuilder = true)
    public SloopVariant(final ResourceLocation texture, final Holder<DynamicBoatMaterial> boatMaterial,
            final RepairMaterials repairMaterials, final ResourceKey<LootTable> lootTable) {
        this.texture = texture;
        this.textureFull = TEXTURE_ID_CONVERTER.idToFile(texture);
        this.boatMaterial = boatMaterial;
        this.repairMaterials = repairMaterials;
        this.lootTable = lootTable;
    }

    public static void load(final CompoundTag compoundTag, final RegistryAccess registryAccess,
            final Consumer<Holder<SloopVariant>> setSloopVariant) {
        BoatVariant.load(compoundTag, AlekiShipsRegistries.SLOOP_VARIANT, SERIALIZATION_KEY, registryAccess,
                setSloopVariant);
    }

    public static void save(final CompoundTag compoundTag, final Holder<SloopVariant> sloopVariant) {
        BoatVariant.save(compoundTag, SERIALIZATION_KEY, sloopVariant);
    }

    public ResourceLocation texture() {
        return this.textureFull;
    }
}