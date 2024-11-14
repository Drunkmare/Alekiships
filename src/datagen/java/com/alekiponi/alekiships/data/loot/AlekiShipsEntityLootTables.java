package com.alekiponi.alekiships.data.loot;

import com.alekiponi.alekiships.common.block.AlekiShipsBlocks;
import com.alekiponi.alekiships.common.entity.AlekiShipsEntities;
import com.alekiponi.alekiships.common.entity.vehicle.RowboatEntity;
import com.alekiponi.alekiships.common.entity.vehicle.SloopEntity;
import com.alekiponi.alekiships.common.item.AlekiShipsItems;
import com.alekiponi.alekiships.data.DataGenHelper;
import com.alekiponi.alekiships.util.BoatMaterial;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraftforge.registries.RegistryObject;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AlekiShipsEntityLootTables extends EntityLootSubProvider {

    public static final int FRAME_CAPACITY = 4;
    public static final int ROWBOAT_FRAMES = 6;
    public static final int SLOOP_FRAMES = 24;
    public static final float DESTRUCTION_MODIFIER = 0.2F;
    private static final Set<EntityType<?>> VEHICLE_LOOT_TABLE_TYPES = Stream.concat(
                    AlekiShipsEntities.ROWBOATS.values().stream().map(RegistryObject::get),
                    AlekiShipsEntities.SLOOPS.values().stream().map(RegistryObject::get))
            .collect(Collectors.toUnmodifiableSet());

    public AlekiShipsEntityLootTables() {
        super(FeatureFlags.REGISTRY.allFlags());
    }

    public static LootTable.Builder createRowboatLootTable(final BoatMaterial material) {
        final LootTable.Builder rowboatLootTable = LootTable.lootTable();

        rowboatLootTable.withPool(DataGenHelper.lootPoolOf(
                DataGenHelper.setCountBetween(LootItem.lootTableItem(AlekiShipsBlocks.OARLOCK.get()), 0, 2)));

        rowboatLootTable.withPool(DataGenHelper.lootPoolOf(
                DataGenHelper.setCountBetween(LootItem.lootTableItem(material.getDeckItem()),
                        FRAME_CAPACITY * ROWBOAT_FRAMES * DESTRUCTION_MODIFIER, FRAME_CAPACITY * ROWBOAT_FRAMES)));

        return rowboatLootTable;
    }

    public static LootTable.Builder createSloopLootTable(final BoatMaterial material) {
        final LootTable.Builder sloopLootTable = LootTable.lootTable();

        sloopLootTable.withPool(DataGenHelper.lootPoolOf(
                DataGenHelper.setCountBetween(LootItem.lootTableItem(AlekiShipsBlocks.CLEAT.get()), 1, 3)));

        sloopLootTable.withPool(
                DataGenHelper.lootPoolOf(DataGenHelper.setCountBetween(LootItem.lootTableItem(Items.LEAD), 2, 7)));

        sloopLootTable.withPool(DataGenHelper.lootPoolOf(
                DataGenHelper.setCountBetween(LootItem.lootTableItem(AlekiShipsItems.ANCHOR.get()), 0, 1)));

        sloopLootTable.withPool(DataGenHelper.lootPoolOf(
                DataGenHelper.setCountBetween(LootItem.lootTableItem(material.getStrippedLog()), 0, 20)));

        sloopLootTable.withPool(DataGenHelper.lootPoolOf(
                DataGenHelper.setCountBetween(LootItem.lootTableItem(material.getDeckItem()), 0, 15)));

        sloopLootTable.withPool(DataGenHelper.lootPoolOf(
                DataGenHelper.setCountBetween(LootItem.lootTableItem(material.getRailing()), 0, 10)));

        sloopLootTable.withPool(DataGenHelper.lootPoolOf(
                DataGenHelper.setCountBetween(LootItem.lootTableItem(material.getDeckItem()),
                        FRAME_CAPACITY * SLOOP_FRAMES * DESTRUCTION_MODIFIER, FRAME_CAPACITY * SLOOP_FRAMES)));

        return sloopLootTable;
    }

    @Override
    public void generate() {
        AlekiShipsEntities.ROWBOATS.forEach(this::dropRowboatTable);
        AlekiShipsEntities.SLOOPS.forEach(this::dropSloopTable);
    }

    protected final void dropSloopTable(final BoatMaterial material,
            RegistryObject<EntityType<SloopEntity>> registryObject) {
        this.add(registryObject.get(), createSloopLootTable(material));
    }

    protected final void dropRowboatTable(final BoatMaterial material,
            RegistryObject<EntityType<RowboatEntity>> registryObject) {
        this.add(registryObject.get(), createRowboatLootTable(material));
    }

    @Override
    protected boolean canHaveLootTable(final EntityType<?> entityType) {
        return VEHICLE_LOOT_TABLE_TYPES.contains(entityType) || super.canHaveLootTable(entityType);
    }

    @Override
    protected Stream<EntityType<?>> getKnownEntityTypes() {
        return AlekiShipsEntities.ENTITY_TYPES.getEntries().stream().map(RegistryObject::get);
    }
}
