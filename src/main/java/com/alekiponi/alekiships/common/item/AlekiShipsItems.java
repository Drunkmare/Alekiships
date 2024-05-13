package com.alekiponi.alekiships.common.item;

import com.alekiponi.alekiships.AlekiShips;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class AlekiShipsItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS,
            AlekiShips.MOD_ID);

    public static final RegistryObject<Item> CANNONBALL = ITEMS.register("cannonball",
            () -> new Item(new Item.Properties().stacksTo(16)));

    public static final RegistryObject<Item> CANNON = ITEMS.register("cannon",
            () -> new CannonItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> OAR = ITEMS.register("oar",
            () -> new Item(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> ANCHOR = ITEMS.register("anchor",
            () -> new Item(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> ROWBOAT_ICON_ONLY = ITEMS.register("rowboat_icon_only",
            () -> new Item(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> SLOOP_ICON_ONLY = ITEMS.register("sloop_icon_only",
            () -> new Item(new Item.Properties().stacksTo(1)));
}