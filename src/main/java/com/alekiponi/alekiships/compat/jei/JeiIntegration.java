package com.alekiponi.alekiships.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.common.AlekiShipsRegistries;
import com.alekiponi.alekiships.common.compartment.CompartmentPlaceable;
import com.alekiponi.alekiships.common.entity.AlekiShipsEntities;
import com.alekiponi.alekiships.common.entity.vehicle.AbstractAlekiBoatEntity;
import com.alekiponi.alekiships.common.entity.vehicle.BoatVariant;
import com.alekiponi.alekiships.common.entity.vehicle.RowboatEntity;
import com.alekiponi.alekiships.common.entity.vehicle.SloopEntity;
import com.alekiponi.alekiships.common.item.AlekiShipsItems;
import com.alekiponi.alekiships.compat.jei.category.VehicleRepairMaterialCategory;
import com.alekiponi.alekiships.compat.jei.recipe.VehicleRepairMaterialRecipe;
import com.alekiponi.alekiships.util.ClientHelper;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import net.neoforged.neoforge.common.Tags;

import java.util.Arrays;
import java.util.function.Supplier;
import java.util.stream.Stream;

@JeiPlugin
public class JeiIntegration implements IModPlugin {

    public static final String CAN_PLACE_INTO_COMPARTMENTS_KEY = AlekiShips.MOD_ID + ".jei.compartment_info";
    public static final String CAN_BE_USED_TO_DYE_SHIPS_SAILS_KEY = AlekiShips.MOD_ID + ".jei.dye_info";

    public static final RecipeType<VehicleRepairMaterialRecipe> REPAIR_MATERIAL_ROWBOAT = RecipeType.create(
            AlekiShips.MOD_ID, "repair_materials_rowboat", VehicleRepairMaterialRecipe.class);
    public static final RecipeType<VehicleRepairMaterialRecipe> REPAIR_MATERIALS_SLOOP = RecipeType.create(
            AlekiShips.MOD_ID, "repair_materials_sloop", VehicleRepairMaterialRecipe.class);

    private static void registerBoatTypeRecipes(final IRecipeRegistration registration,
            final RecipeType<VehicleRepairMaterialRecipe> recipeType,
            final Stream<? extends Holder<? extends BoatVariant>> variants) {
        registration.addRecipes(recipeType, variants.flatMap(VehicleRepairMaterialRecipe::of)
                .toList());
    }

    private static void registerRepairCategory(final IRecipeCategoryRegistration registration,
            final IGuiHelper guiHelper, final RecipeType<VehicleRepairMaterialRecipe> recipeType,
            final Supplier<? extends EntityType<? extends AbstractAlekiBoatEntity<?>>> entityType,
            final float damageThreshold, final IDrawable icon, final Component title) {
        registration.addRecipeCategories(
                new VehicleRepairMaterialCategory(guiHelper, recipeType, entityType.get(), damageThreshold, icon,
                        title));
    }

    @Override
    public ResourceLocation getPluginUid() {
        return AlekiShips.location("jei");
    }

    @Override
    public void registerRecipes(final IRecipeRegistration registration) {
        registration.addItemStackInfo(BuiltInRegistries.ITEM.stream()
                .map(ItemStack::new)
                .filter(itemStack -> CompartmentPlaceable.fromStack(itemStack).isPresent())
                .toList(), Component.translatable(CAN_PLACE_INTO_COMPARTMENTS_KEY));
        registration.addItemStackInfo(Arrays.asList(Ingredient.of(Tags.Items.DYES).getItems()),
                Component.translatable(CAN_BE_USED_TO_DYE_SHIPS_SAILS_KEY));

        final var level = ClientHelper.getLevel();
        assert level != null;
        final var registryAccess = level.registryAccess();
        registerBoatTypeRecipes(registration, REPAIR_MATERIAL_ROWBOAT,
                registryAccess.registryOrThrow(AlekiShipsRegistries.ROWBOAT_VARIANT).holders());
        registerBoatTypeRecipes(registration, REPAIR_MATERIALS_SLOOP,
                registryAccess.registryOrThrow(AlekiShipsRegistries.SLOOP_VARIANT).holders());
    }

    @Override
    public void registerCategories(final IRecipeCategoryRegistration registration) {
        final var guiHelper = registration.getJeiHelpers().getGuiHelper();
        registerRepairCategory(registration, guiHelper, REPAIR_MATERIAL_ROWBOAT, AlekiShipsEntities.ROWBOAT,
                RowboatEntity.DAMAGE_THRESHOLD,
                guiHelper.createDrawableItemStack(AlekiShipsItems.ROWBOAT_ICON_ONLY.toStack()),
                Component.literal("Repair Materials Rowboat"));
        registerRepairCategory(registration, guiHelper, REPAIR_MATERIALS_SLOOP, AlekiShipsEntities.SLOOP,
                SloopEntity.DAMAGE_THRESHOLD,
                guiHelper.createDrawableItemStack(AlekiShipsItems.SLOOP_ICON_ONLY.toStack()),
                Component.literal("Repair Materials Sloop"));
    }
}