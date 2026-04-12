package com.drunkmare.deremet.util;

import com.alekiponi.alekiships.common.entity.vehicle.AbstractVehicle;
import com.alekiponi.alekiships.util.BoatMaterial;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Locale;
import java.util.Optional;

/**
 * The eleven vanilla wood types supported by the mod.
 *
 * Implements {@link BoatMaterial} so AlekiShips' data-gen helpers can drive
 * per-wood blockstate and model generation. The boat-specific methods
 * (railing, stripped log, entity type) return stubs — we only care about
 * {@link #getDeckBlock()} which supplies the plank texture used in datagen
 * and the plank item used in loot tables.
 */
public enum VanillaWood implements BoatMaterial {
    OAK(Blocks.OAK_PLANKS),
    SPRUCE(Blocks.SPRUCE_PLANKS),
    BIRCH(Blocks.BIRCH_PLANKS),
    ACACIA(Blocks.ACACIA_PLANKS),
    CHERRY(Blocks.CHERRY_PLANKS),
    JUNGLE(Blocks.JUNGLE_PLANKS),
    DARK_OAK(Blocks.DARK_OAK_PLANKS),
    CRIMSON(Blocks.CRIMSON_PLANKS),
    WARPED(Blocks.WARPED_PLANKS),
    MANGROVE(Blocks.MANGROVE_PLANKS),
    BAMBOO(Blocks.BAMBOO_PLANKS);

    private final Block plankBlock;

    VanillaWood(final Block plankBlock) {
        this.plankBlock = plankBlock;
    }

    // Used as the registry path suffix, e.g. "wood/millstone_frame/full/oak".
    @Override
    public String getSerializedName() {
        return this.name().toLowerCase(Locale.ROOT);
    }

    // --- BoatMaterial stubs (not used by this mod) ---

    @Override
    public Item getRailing() {
        return null;
    }

    @Override
    public Item getStrippedLog() {
        return null;
    }

    @Override
    public boolean withstandsLava() {
        return false;
    }

    // Returns the plank block state. AlekiShips uses this for the deck texture in datagen.
    @Override
    public BlockState getDeckBlock() {
        return this.plankBlock.defaultBlockState();
    }

    @Override
    public Optional<EntityType<? extends AbstractVehicle>> getEntityType(BoatType boatType) {
        return Optional.empty();
    }
}
