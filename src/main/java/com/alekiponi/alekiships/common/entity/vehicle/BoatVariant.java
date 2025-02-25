package com.alekiponi.alekiships.common.entity.vehicle;

import com.mojang.serialization.codecs.RecordCodecBuilder;

import com.alekiponi.alekiships.util.BoatMaterial;
import com.alekiponi.alekiships.util.RepairMaterials;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * A variant representing a particular boat.
 *
 * @see SloopVariant
 * @see RowboatVariant
 */
public interface BoatVariant {

    FileToIdConverter TEXTURE_ID_CONVERTER = new FileToIdConverter("textures", ".png");

    /**
     * Simple helper for the {@link BoatMaterial} codec field
     */
    static <T extends BoatVariant> RecordCodecBuilder<T, Holder<BoatMaterial>> boatMaterialField() {
        return BoatMaterial.CODEC.fieldOf("material").forGetter(BoatVariant::getBoatMaterial);
    }

    /**
     * Simple helper for the {@link RepairMaterials} codec field
     */
    static <T extends BoatVariant> RecordCodecBuilder<T, RepairMaterials> repairMaterialField() {
        return RepairMaterials.CODEC.fieldOf("repair_material").forGetter(BoatVariant::getRepairMaterials);
    }

    /**
     * Simple helper for the Loot table resource key codec field
     */
    static <T extends BoatVariant> RecordCodecBuilder<T, ResourceKey<LootTable>> lootTableField() {
        return ResourceKey.codec(Registries.LOOT_TABLE).fieldOf("loot_table").forGetter(BoatVariant::getLootTable);
    }

    /**
     * Helper for saving a boat type
     */
    static <T extends BoatVariant> void save(final CompoundTag compoundTag, final String serializationKey,
            final Holder<T> boatType) {
        boatType.unwrapKey()
                .ifPresent(resourceKey -> compoundTag.putString(serializationKey, resourceKey.location().toString()));
    }

    /**
     * Helper for loading a boat type
     */
    static <T extends BoatVariant> void load(final CompoundTag compoundTag, final ResourceKey<Registry<T>> registryKey,
            final String serializationKey, final RegistryAccess registryAccess, final Consumer<Holder<T>> setBoatType) {
        Optional.ofNullable(ResourceLocation.tryParse(compoundTag.getString(serializationKey)))
                .map(resourceLocation -> ResourceKey.create(registryKey, resourceLocation))
                .flatMap(resourceKey -> registryAccess.registryOrThrow(registryKey).getHolder(resourceKey))
                .ifPresent(setBoatType);
    }

    /**
     * @return A Holder of the boat material this boat is made of
     */
    Holder<BoatMaterial> getBoatMaterial();

    /**
     * @return The repair material for this
     */
    RepairMaterials getRepairMaterials();

    /**
     * @return The loot table used by this boat
     */
    ResourceKey<LootTable> getLootTable();
}