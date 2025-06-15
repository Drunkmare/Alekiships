package com.alekiponi.alekiships.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeRegistration;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.common.compartment.CompartmentPlaceable;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import net.neoforged.neoforge.common.Tags;

import java.util.Arrays;

@JeiPlugin
public class JeiIntegration implements IModPlugin {

    public static final String CAN_PLACE_INTO_COMPARTMENTS_KEY = AlekiShips.MOD_ID + ".jei.compartment_info";
    public static final String CAN_BE_USED_TO_DYE_SHIPS_SAILS_KEY = AlekiShips.MOD_ID + ".jei.dye_info";

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
    }
}