package com.alekiponi.alekiships.compat.jei.recipe;

import com.alekiponi.alekiships.common.entity.vehicle.BoatVariant;
import com.alekiponi.alekiships.util.RepairMaterials;

import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;

import java.util.stream.Stream;
import lombok.Value;
import lombok.experimental.Accessors;

@Value
@Accessors(fluent = true)
public class VehicleRepairMaterialRecipe {

    Component materialName;
    RepairMaterials.Material material;

    public static Stream<VehicleRepairMaterialRecipe> of(final Holder<? extends BoatVariant> boatVariant) {
        final var variant = boatVariant.value();
        final var name = variant.getBoatMaterial().value().name();
        return variant.getRepairMaterials().materials.stream()
                .map(material -> new VehicleRepairMaterialRecipe(name, material));
    }
}