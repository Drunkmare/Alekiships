package com.alekiponi.alekiships.data.loot;

import com.alekiponi.alekiships.common.entity.AlekiShipsEntities;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;

import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class AlekiShipsEntityLootTables extends EntityLootSubProvider {

    private static final Set<EntityType<?>> VEHICLE_LOOT_TABLE_TYPES = Set.of();

    public AlekiShipsEntityLootTables(final HolderLookup.Provider provider) {
        super(FeatureFlags.REGISTRY.allFlags(), provider);
    }

    @Override
    public void generate() {}

    @Override
    protected boolean canHaveLootTable(final EntityType<?> entityType) {
        return VEHICLE_LOOT_TABLE_TYPES.contains(entityType) || super.canHaveLootTable(entityType);
    }

    @Override
    protected Stream<EntityType<?>> getKnownEntityTypes() {
        return AlekiShipsEntities.ENTITY_TYPES.getEntries().stream().map(Supplier::get);
    }
}