package com.drunkmare.deremet.common.item;

import com.drunkmare.deremet.DeReMetallica;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

// Item registry. BlockItems for blocks are registered here via DeReMetallicaBlocks.
// Add any standalone items here as the mod grows.
public final class DeReMetallicaItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS,
            DeReMetallica.MOD_ID);
}
