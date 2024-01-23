package com.alekiponi.alekiships.common.item;

import com.alekiponi.alekiships.AlekiShips;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class AlekiShipsItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, AlekiShips.MOD_ID);

    public static final RegistryObject<Item> CANNONBALL = ITEMS.register("cannonball",
            () -> new Item(new Item.Properties().stacksTo(16)));

    public static final RegistryObject<Item> CANNON = ITEMS.register("cannon",
            () -> new CannonItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> OAR = ITEMS.register("oar",
            () -> new Item(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> CANNON_BARREL = ITEMS.register("cannon_barrel",
            () -> new Item(new Item.Properties().stacksTo(4)));

    public static final RegistryObject<Item> LARGE_TRIANGULAR_SAIL = ITEMS.register("large_triangular_sail",
            () -> new Item(new Item.Properties().stacksTo(4)));

    public static final RegistryObject<Item> MEDIUM_TRIANGULAR_SAIL = ITEMS.register("medium_triangular_sail",
            () -> new Item(new Item.Properties().stacksTo(4)));

    public static final RegistryObject<Item> SMALL_TRIANGULAR_SAIL = ITEMS.register("small_triangular_sail",
            () -> new Item(new Item.Properties().stacksTo(4)));

    public static final RegistryObject<Item> ANCHOR = ITEMS.register("anchor",
            () -> new Item(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> COPPER_BOLT = ITEMS.register("copper_bolt",
            () -> new Item(new Item.Properties().stacksTo(1)));


    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
