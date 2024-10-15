package com.alekiponi.alekiships.data.loot;

import java.util.function.BiConsumer;
import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.common.block.AlekiShipsBlocks;
import com.alekiponi.alekiships.common.item.AlekiShipsItems;
import com.alekiponi.alekiships.data.DataGenHelper;
import com.alekiponi.alekiships.util.VanillaWood;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

public class AlekiShipsStructureLoot implements LootTableSubProvider {

    public static LootTable.Builder rowboatStructureLoot(final Item plankItem) {
        final LootTable.Builder lootTable = LootTable.lootTable();

        lootTable.withPool(DataGenHelper.lootPoolOf(plankItem, 0, 16).setRolls(ConstantValue.exactly(3)));

        lootTable.withPool(DataGenHelper.lootPoolOf(AlekiShipsBlocks.BOAT_FRAME_ANGLED.get(), 1, 3)
                .setRolls(ConstantValue.exactly(3)));

        lootTable.withPool(
                DataGenHelper.lootPoolOf(AlekiShipsBlocks.OARLOCK.get(), 0, 1).setRolls(ConstantValue.exactly(2)));

        lootTable.withPool(DataGenHelper.lootPoolOf(Items.SCAFFOLDING, 0, 5).setRolls(ConstantValue.exactly(2)));

        lootTable.withPool(
                DataGenHelper.lootPoolOf(AlekiShipsItems.OAR.get(), 0, 1).setRolls(ConstantValue.exactly(2)));

        lootTable.withPool(DataGenHelper.lootPoolOf(Items.LEAD, 0, 1));

        return lootTable;
    }

    public static LootTable.Builder sloopStructureLoot(final Item plankItem) {
        final LootTable.Builder lootTable = LootTable.lootTable();

        lootTable.withPool(DataGenHelper.lootPoolOf(plankItem, 0, 96));

        lootTable.withPool(DataGenHelper.lootPoolOf(AlekiShipsBlocks.BOAT_FRAME_ANGLED.get(), 1, 3)
                .setRolls(ConstantValue.exactly(2)));

        lootTable.withPool(DataGenHelper.lootPoolOf(AlekiShipsBlocks.BOAT_FRAME_FLAT.get(), 3, 7));

        lootTable.withPool(DataGenHelper.lootPoolOf(AlekiShipsBlocks.CLEAT.get(), 0, 4));

        lootTable.withPool(DataGenHelper.lootPoolOf(Items.SCAFFOLDING, 0, 5));

        lootTable.withPool(DataGenHelper.lootPoolOf(Items.SLIME_BALL, 8));

        lootTable.withPool(DataGenHelper.lootPoolOf(Items.LEAD));

        return lootTable;
    }

    public static LootTable.Builder sloopHutStructureLoot(final Item plankItem) {
        final LootTable.Builder lootTable = LootTable.lootTable();

        lootTable.withPool(DataGenHelper.lootPoolOf(AlekiShipsBlocks.CLEAT.get(), 0, 2));

        lootTable.withPool(DataGenHelper.lootPoolOf(AlekiShipsItems.ANCHOR.get()));

        lootTable.withPool(DataGenHelper.lootPoolOf(Items.SCAFFOLDING, 0, 10));

        lootTable.withPool(DataGenHelper.lootPoolOf(Items.BREAD, 3, 10));

        lootTable.withPool(DataGenHelper.lootPoolOf(AlekiShipsItems.CANNONBALL.get(), 1, 6));

        lootTable.withPool(DataGenHelper.lootPoolOf(Items.FLINT_AND_STEEL, 0, 1));

        lootTable.withPool(DataGenHelper.lootPoolOf(AlekiShipsItems.MUSIC_DISC_PIRATE_CRAFTING.get(), 0, 1));

        lootTable.withPool(DataGenHelper.lootPoolOf(plankItem, 0, 32));

        return lootTable;
    }

    @Override
    public void generate(final BiConsumer<ResourceLocation, LootTable.Builder> output) {
        for (final VanillaWood wood : VanillaWood.values()) {
            output.accept(new ResourceLocation(AlekiShips.MOD_ID, "structures/rowboat/" + wood.getSerializedName()),
                    rowboatStructureLoot(wood.getDeckItem()));
            output.accept(new ResourceLocation(AlekiShips.MOD_ID, "structures/sloop/" + wood.getSerializedName()),
                    sloopStructureLoot(wood.getDeckItem()));
            output.accept(new ResourceLocation(AlekiShips.MOD_ID, "structures/sloop_hut/" + wood.getSerializedName()),
                    sloopHutStructureLoot(wood.getDeckItem()));
        }
    }
}