package com.alekiponi.alekiships.util;

import com.alekiponi.alekiships.events.config.AlekiShipsConfig;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.crafting.Ingredient;

import net.dries007.tfc.util.InteractionManager;

public class AlekiShipsInteractionManager
{
    public static void init()
    {
        InteractionManager.register(Ingredient.of(ItemTags.BOATS), true, ((stack, context) -> {
            if (AlekiShipsConfig.SERVER.disableVanillaBoatFunctionality.get())
            {
                return InteractionResult.FAIL;
            }
            return stack.useOn(context);
        }));
    }
}
