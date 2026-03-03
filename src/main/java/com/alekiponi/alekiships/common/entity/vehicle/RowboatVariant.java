package com.alekiponi.alekiships.common.entity.vehicle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import com.alekiponi.alekiships.common.AlekiShipsRegistries;
import com.alekiponi.alekiships.util.BoatMaterial;
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
public final class RowboatVariant implements BoatVariant {

    public static final Codec<RowboatVariant> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    ResourceLocation.CODEC.fieldOf("texture").forGetter(rowboatVariant -> rowboatVariant.texture),
                    BoatVariant.boatMaterialField(), BoatVariant.repairMaterialField(), BoatVariant.lootTableField())
            .apply(instance, RowboatVariant::new));

    public static final Codec<Holder<RowboatVariant>> CODEC = RegistryFileCodec.create(
            AlekiShipsRegistries.ROWBOAT_VARIANT, DIRECT_CODEC);

    public static final StreamCodec<RegistryFriendlyByteBuf, RowboatVariant> DIRECT_STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, rowboatVariant -> rowboatVariant.texture,
            ByteBufCodecs.holderRegistry(AlekiShipsRegistries.BOAT_MATERIAL), RowboatVariant::getBoatMaterial,
            RepairMaterials.STREAM_CODEC, RowboatVariant::getRepairMaterials,
            ResourceKey.streamCodec(Registries.LOOT_TABLE), RowboatVariant::getLootTable, RowboatVariant::new);

    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<RowboatVariant>> STREAM_CODEC = ByteBufCodecs.holder(
            AlekiShipsRegistries.ROWBOAT_VARIANT, DIRECT_STREAM_CODEC);

    public static final String SERIALIZATION_KEY = "rowboat_variant";

    private final ResourceLocation texture;
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private final ResourceLocation textureFull;
    @Getter
    private final Holder<BoatMaterial> boatMaterial;
    @Getter
    private final RepairMaterials repairMaterials;
    @Getter
    private final ResourceKey<LootTable> lootTable;

    @Builder(toBuilder = true)
    public RowboatVariant(final ResourceLocation texture, final Holder<BoatMaterial> boatMaterial,
            final RepairMaterials repairMaterials, final ResourceKey<LootTable> lootTable) {
        this.texture = texture;
        this.textureFull = TEXTURE_ID_CONVERTER.idToFile(texture);
        this.boatMaterial = boatMaterial;
        this.repairMaterials = repairMaterials;
        this.lootTable = lootTable;
    }

    public static void load(final CompoundTag compoundTag, final RegistryAccess registryAccess,
            final Consumer<Holder<RowboatVariant>> setRowboatVariant) {
        BoatVariant.load(compoundTag, AlekiShipsRegistries.ROWBOAT_VARIANT, SERIALIZATION_KEY, registryAccess,
                setRowboatVariant);
    }

    public static void save(final CompoundTag compoundTag, final Holder<RowboatVariant> rowboatVariant) {
        BoatVariant.save(compoundTag, SERIALIZATION_KEY, rowboatVariant);
    }

    public ResourceLocation texture() {
        return this.textureFull;
    }
}