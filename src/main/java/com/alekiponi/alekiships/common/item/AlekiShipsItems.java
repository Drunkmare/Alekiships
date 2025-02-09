package com.alekiponi.alekiships.common.item;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.common.item.components.AlekiShipsComponents;
import com.alekiponi.alekiships.common.sounds.AlekiShipsJukeboxSongs;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class AlekiShipsItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(AlekiShips.MOD_ID);

    public static final DeferredItem<Item> CANNONBALL = ITEMS.register("cannonball",
            () -> new Item(new Item.Properties().stacksTo(16)));

    public static final DeferredItem<Item> MUSIC_DISC_PIRATE_CRAFTING = ITEMS.register("music_disc_pirate_crafting",
            () -> new Item(new Item.Properties().stacksTo(1).rarity(Rarity.RARE)
                    .jukeboxPlayable(AlekiShipsJukeboxSongs.PIRATE_CRAFTING)));

    public static final DeferredItem<CannonItem> CANNON = ITEMS.register("cannon", () -> new CannonItem(
            new Item.Properties().stacksTo(1)
                    .component(AlekiShipsComponents.ENTITY_INPUT, CannonItem.DEFAULT_CANNON_INPUT_KEY)));

    public static final DeferredItem<Item> OAR = ITEMS.register("oar",
            () -> new Item(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> ANCHOR = ITEMS.register("anchor",
            () -> new Item(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> ROWBOAT_ICON_ONLY = ITEMS.register("rowboat_icon_only",
            () -> new Item(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> SLOOP_ICON_ONLY = ITEMS.register("sloop_icon_only",
            () -> new Item(new Item.Properties().stacksTo(1)));
}