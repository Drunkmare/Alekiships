package com.alekiponi.alekiships.data.loot;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.common.block.AlekiShipsBlocks;
import com.alekiponi.alekiships.common.item.AlekiShipsItems;
import com.alekiponi.alekiships.data.DataGenHelper;
import com.alekiponi.alekiships.util.VanillaWood;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.function.BiConsumer;

public class AlekiShipsStructureLoot implements LootTableSubProvider {

    public AlekiShipsStructureLoot(@SuppressWarnings("unused") final HolderLookup.Provider provider) {

    }

    public static LootTable.Builder rowboatStructureLoot(final Item plankItem) {
        final LootTable.Builder lootTable = LootTable.lootTable();

        lootTable.withPool(DataGenHelper.lootPoolOf(plankItem, 0, 16).setRolls(ConstantValue.exactly(3)));

        lootTable.withPool(
                DataGenHelper.lootPoolOf(AlekiShipsBlocks.BOAT_FRAME_ANGLED, 1, 3).setRolls(ConstantValue.exactly(3)));

        lootTable.withPool(DataGenHelper.lootPoolOf(AlekiShipsBlocks.OARLOCK, 0, 1).setRolls(ConstantValue.exactly(2)));

        lootTable.withPool(DataGenHelper.lootPoolOf(Items.SCAFFOLDING, 0, 5).setRolls(ConstantValue.exactly(2)));

        lootTable.withPool(DataGenHelper.lootPoolOf(AlekiShipsItems.OAR, 0, 1).setRolls(ConstantValue.exactly(2)));

        lootTable.withPool(DataGenHelper.lootPoolOf(Items.LEAD, 0, 1));

        return lootTable;
    }

    public static LootTable.Builder sloopStructureLoot(final Item plankItem) {
        final LootTable.Builder lootTable = LootTable.lootTable();

        lootTable.withPool(DataGenHelper.lootPoolOf(plankItem, 0, 96));

        lootTable.withPool(
                DataGenHelper.lootPoolOf(AlekiShipsBlocks.BOAT_FRAME_ANGLED, 1, 3).setRolls(ConstantValue.exactly(2)));

        lootTable.withPool(DataGenHelper.lootPoolOf(AlekiShipsBlocks.BOAT_FRAME_FLAT, 3, 7));

        lootTable.withPool(DataGenHelper.lootPoolOf(AlekiShipsBlocks.CLEAT, 0, 4));

        lootTable.withPool(DataGenHelper.lootPoolOf(Items.SCAFFOLDING, 0, 5));

        lootTable.withPool(DataGenHelper.lootPoolOf(Items.SLIME_BALL, 8));

        lootTable.withPool(DataGenHelper.lootPoolOf(Items.LEAD));

        return lootTable;
    }

    public static LootTable.Builder sloopHutStructureLoot(final Item plankItem) {
        final LootTable.Builder lootTable = LootTable.lootTable();

        lootTable.withPool(DataGenHelper.lootPoolOf(AlekiShipsBlocks.CLEAT, 0, 2));

        lootTable.withPool(DataGenHelper.lootPoolOf(AlekiShipsItems.ANCHOR));

        lootTable.withPool(DataGenHelper.lootPoolOf(Items.SCAFFOLDING, 0, 10));

        lootTable.withPool(DataGenHelper.lootPoolOf(Items.BREAD, 3, 10));

        lootTable.withPool(DataGenHelper.lootPoolOf(AlekiShipsItems.CANNONBALL, 1, 6));

        lootTable.withPool(DataGenHelper.lootPoolOf(Items.FLINT_AND_STEEL, 0, 1));

        lootTable.withPool(DataGenHelper.lootPoolOf(AlekiShipsItems.MUSIC_DISC_PIRATE_CRAFTING, 0, 1));

        lootTable.withPool(DataGenHelper.lootPoolOf(plankItem, 0, 32));

        return lootTable;
    }

    @Override
    public void generate(final BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output) {
        for (final VanillaWood wood : VanillaWood.values()) {
            output.accept(ResourceKey.create(Registries.LOOT_TABLE,
                            ResourceLocation.fromNamespaceAndPath(AlekiShips.MOD_ID,
                                    "structures/rowboat/" + wood.getSerializedName())),
                    rowboatStructureLoot(wood.getDeckItem()));
            output.accept(ResourceKey.create(Registries.LOOT_TABLE,
                    ResourceLocation.fromNamespaceAndPath(AlekiShips.MOD_ID,
                            "structures/sloop/" + wood.getSerializedName())), sloopStructureLoot(wood.getDeckItem()));
            output.accept(ResourceKey.create(Registries.LOOT_TABLE,
                            ResourceLocation.fromNamespaceAndPath(AlekiShips.MOD_ID,
                                    "structures/sloop_hut/" + wood.getSerializedName())),
                    sloopHutStructureLoot(wood.getDeckItem()));
        }
    }
}