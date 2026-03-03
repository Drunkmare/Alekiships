package com.alekiponi.alekiships.common.entity.vehicle;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.common.AlekiShipsRegistries;
import com.alekiponi.alekiships.common.block.AlekiShipsBlocks;
import com.alekiponi.alekiships.common.entity.ConstructionSloopInputs;
import com.alekiponi.alekiships.common.item.AlekiShipsItems;
import com.alekiponi.alekiships.util.BoatMaterials;
import com.alekiponi.alekiships.util.RepairMaterials;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

public final class SloopVariants {

    public static final ResourceKey<SloopVariant> OAK = createKey("oak");
    public static final ResourceKey<SloopVariant> SPRUCE = createKey("spruce");
    public static final ResourceKey<SloopVariant> BIRCH = createKey("birch");
    public static final ResourceKey<SloopVariant> ACACIA = createKey("acacia");
    public static final ResourceKey<SloopVariant> CHERRY = createKey("cherry");
    public static final ResourceKey<SloopVariant> JUNGLE = createKey("jungle");
    public static final ResourceKey<SloopVariant> DARK_OAK = createKey("dark_oak");
    public static final ResourceKey<SloopVariant> CRIMSON = createKey("crimson");
    public static final ResourceKey<SloopVariant> WARPED = createKey("warped");
    public static final ResourceKey<SloopVariant> MANGROVE = createKey("mangrove");
    public static final ResourceKey<SloopVariant> BAMBOO = createKey("bamboo");
    public static final ResourceKey<SloopVariant> DEFAULT = OAK;

    public static final int FRAME_CAPACITY = 4;
    public static final int SLOOP_FRAMES = 24;
    public static final int TOTAL_PLANKS = FRAME_CAPACITY * SLOOP_FRAMES + ConstructionSloopInputs.TOTAL_PLANKS;

    private static ResourceKey<SloopVariant> createKey(final String name) {
        return ResourceKey.create(AlekiShipsRegistries.SLOOP_VARIANT, AlekiShips.location(name));
    }

    private static ResourceLocation texture(final String wood) {
        return AlekiShips.location("entity/watercraft/sloop/" + wood);
    }

    public static void bootstrapOverworld(final BootstrapContext<SloopVariant> context) {
        final var lookup = context.lookup(AlekiShipsRegistries.BOAT_MATERIAL);

        context.register(OAK, SloopVariant.builder()
                .texture(texture("oak"))
                .lootTable(Loot.OAK)
                .boatMaterial(lookup.getOrThrow(BoatMaterials.OAK))
                .repairMaterials(repairMaterials(Items.OAK_PLANKS, Items.STRIPPED_OAK_LOG, Items.OAK_FENCE,
                        SoundEvents.WOOD_PLACE))
                .build());

        context.register(SPRUCE, SloopVariant.builder()
                .texture(texture("spruce"))
                .lootTable(Loot.SPRUCE)
                .boatMaterial(lookup.getOrThrow(BoatMaterials.SPRUCE))
                .repairMaterials(repairMaterials(Items.SPRUCE_PLANKS, Items.STRIPPED_SPRUCE_LOG, Items.SPRUCE_FENCE,
                        SoundEvents.WOOD_PLACE))
                .build());

        context.register(BIRCH, SloopVariant.builder()
                .texture(texture("birch"))
                .lootTable(Loot.BIRCH)
                .boatMaterial(lookup.getOrThrow(BoatMaterials.BIRCH))
                .repairMaterials(repairMaterials(Items.BIRCH_PLANKS, Items.STRIPPED_BIRCH_LOG, Items.BIRCH_FENCE,
                        SoundEvents.WOOD_PLACE))
                .build());

        context.register(ACACIA, SloopVariant.builder()
                .texture(texture("acacia"))
                .lootTable(Loot.ACACIA)
                .boatMaterial(lookup.getOrThrow(BoatMaterials.ACACIA))
                .repairMaterials(repairMaterials(Items.ACACIA_PLANKS, Items.STRIPPED_ACACIA_LOG, Items.ACACIA_FENCE,
                        SoundEvents.WOOD_PLACE))
                .build());

        context.register(CHERRY, SloopVariant.builder()
                .texture(texture("cherry"))
                .lootTable(Loot.CHERRY)
                .boatMaterial(lookup.getOrThrow(BoatMaterials.CHERRY))
                .repairMaterials(repairMaterials(Items.CHERRY_PLANKS, Items.STRIPPED_CHERRY_LOG, Items.CHERRY_FENCE,
                        SoundEvents.WOOD_PLACE))
                .build());

        context.register(JUNGLE, SloopVariant.builder()
                .texture(texture("jungle"))
                .lootTable(Loot.JUNGLE)
                .boatMaterial(lookup.getOrThrow(BoatMaterials.JUNGLE))
                .repairMaterials(repairMaterials(Items.JUNGLE_PLANKS, Items.STRIPPED_JUNGLE_LOG, Items.JUNGLE_FENCE,
                        SoundEvents.WOOD_PLACE))
                .build());

        context.register(DARK_OAK, SloopVariant.builder()
                .texture(texture("dark_oak"))
                .lootTable(Loot.DARK_OAK)
                .boatMaterial(lookup.getOrThrow(BoatMaterials.DARK_OAK))
                .repairMaterials(
                        repairMaterials(Items.DARK_OAK_PLANKS, Items.STRIPPED_DARK_OAK_LOG, Items.DARK_OAK_FENCE,
                                SoundEvents.WOOD_PLACE))
                .build());

        context.register(MANGROVE, SloopVariant.builder()
                .texture(texture("mangrove"))
                .lootTable(Loot.MANGROVE)
                .boatMaterial(lookup.getOrThrow(BoatMaterials.MANGROVE))
                .repairMaterials(
                        repairMaterials(Items.MANGROVE_PLANKS, Items.STRIPPED_MANGROVE_LOG, Items.MANGROVE_FENCE,
                                SoundEvents.WOOD_PLACE))
                .build());

        context.register(BAMBOO, SloopVariant.builder()
                .texture(texture("bamboo"))
                .lootTable(Loot.BAMBOO)
                .boatMaterial(lookup.getOrThrow(BoatMaterials.BAMBOO))
                .repairMaterials(repairMaterials(Items.BAMBOO_PLANKS, Items.STRIPPED_BAMBOO_BLOCK, Items.BAMBOO_FENCE,
                        SoundEvents.BAMBOO_WOOD_PLACE))
                .build());
    }

    public static void bootstrapNether(final BootstrapContext<SloopVariant> context) {
        final var lookup = context.lookup(AlekiShipsRegistries.BOAT_MATERIAL);

        context.register(CRIMSON, SloopVariant.builder()
                .texture(texture("crimson"))
                .lootTable(Loot.CRIMSON)
                .boatMaterial(lookup.getOrThrow(BoatMaterials.CRIMSON))
                .repairMaterials(repairMaterials(Items.CRIMSON_PLANKS, Items.STRIPPED_CRIMSON_STEM, Items.CRIMSON_FENCE,
                        SoundEvents.NETHER_WOOD_PLACE))
                .build());

        context.register(WARPED, SloopVariant.builder()
                .texture(texture("warped"))
                .lootTable(Loot.WARPED)
                .boatMaterial(lookup.getOrThrow(BoatMaterials.WARPED))
                .repairMaterials(repairMaterials(Items.WARPED_PLANKS, Items.STRIPPED_WARPED_HYPHAE, Items.WARPED_FENCE,
                        SoundEvents.NETHER_WOOD_PLACE))
                .build());
    }

    public static RepairMaterials repairMaterials(final Item planks, final Item strippedLog, final Item railingItem,
            final SoundEvent woodRepairSound) {
        return RepairMaterials.builder()
                .material(builder -> builder.repairIngredient(Ingredient.of(planks))
                        .repairAmount(SloopEntity.DAMAGE_THRESHOLD / TOTAL_PLANKS * 1.5F)
                        .repairSound(woodRepairSound)
                        .damageRange(RepairMaterials.DamageRange.max(SloopEntity.DAMAGE_THRESHOLD * 0.8F)))
                .material(builder -> builder.repairIngredient(Ingredient.of(strippedLog))
                        .repairAmount(SloopEntity.DAMAGE_THRESHOLD / ConstructionSloopInputs.TOTAL_STRIPPED_LOGS * 2F)
                        .repairSound(woodRepairSound)
                        .damageRange(RepairMaterials.DamageRange.of(SloopEntity.DAMAGE_THRESHOLD * 0.4F,
                                SloopEntity.DAMAGE_THRESHOLD * 0.6F)))
                .material(builder -> builder.repairIngredient(Ingredient.of(railingItem))
                        .repairAmount(SloopEntity.DAMAGE_THRESHOLD / ConstructionSloopInputs.TOTAL_RAILINGS * 1.5F)
                        .repairSound(woodRepairSound)
                        .damageRange(RepairMaterials.DamageRange.min(SloopEntity.DAMAGE_THRESHOLD * 0.6F)))
                .material(builder -> builder.repairIngredient(Ingredient.of(Items.WHITE_WOOL))
                        .repairAmount(SloopEntity.DAMAGE_THRESHOLD / ConstructionSloopInputs.TOTAL_WOOL * 1.5F)
                        .repairSound(SoundEvents.WOOL_PLACE)
                        .damageRange(RepairMaterials.DamageRange.max(SloopEntity.DAMAGE_THRESHOLD * 0.2F)))
                .material(builder -> builder.repairIngredient(Ingredient.of(AlekiShipsItems.ANCHOR))
                        .repairAmount(SloopEntity.DAMAGE_THRESHOLD / 4)
                        .repairSound(SoundEvents.METAL_PLACE)
                        .damageRange(RepairMaterials.DamageRange.min(SloopEntity.DAMAGE_THRESHOLD / 2)))
                .build();
    }

    public static final class Loot {
        public static final ResourceKey<LootTable> OAK = createKey("oak");
        public static final ResourceKey<LootTable> SPRUCE = createKey("spruce");
        public static final ResourceKey<LootTable> BIRCH = createKey("birch");
        public static final ResourceKey<LootTable> ACACIA = createKey("acacia");
        public static final ResourceKey<LootTable> CHERRY = createKey("cherry");
        public static final ResourceKey<LootTable> JUNGLE = createKey("jungle");
        public static final ResourceKey<LootTable> DARK_OAK = createKey("dark_oak");
        public static final ResourceKey<LootTable> CRIMSON = createKey("crimson");
        public static final ResourceKey<LootTable> WARPED = createKey("warped");
        public static final ResourceKey<LootTable> MANGROVE = createKey("mangrove");
        public static final ResourceKey<LootTable> BAMBOO = createKey("bamboo");

        public static final float DESTRUCTION_MODIFIER = 0.2F;

        private static ResourceKey<LootTable> createKey(final String name) {
            return ResourceKey.create(Registries.LOOT_TABLE, AlekiShips.location("entities/sloop/" + name));
        }

        public static LootTable.Builder createSloopLootTable(final Item strippedLog, final Item deckItem,
                final Item railing) {
            final LootTable.Builder sloopLootTable = LootTable.lootTable();

            sloopLootTable.withPool(LootPool.lootPool()
                    .add(LootItem.lootTableItem(AlekiShipsBlocks.CLEAT.get())
                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3)))));

            sloopLootTable.withPool(LootPool.lootPool()
                    .add(LootItem.lootTableItem(Items.LEAD)
                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 7)))));

            sloopLootTable.withPool(LootPool.lootPool()
                    .add(LootItem.lootTableItem(AlekiShipsItems.ANCHOR.get())
                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(0, 1)))));

            sloopLootTable.withPool(LootPool.lootPool()
                    .add(LootItem.lootTableItem(strippedLog)
                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(0, 20)))));

            sloopLootTable.withPool(LootPool.lootPool()
                    .add(LootItem.lootTableItem(deckItem)
                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(0, 15)))));

            sloopLootTable.withPool(LootPool.lootPool()
                    .add(LootItem.lootTableItem(railing)
                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(0, 10)))));

            sloopLootTable.withPool(LootPool.lootPool()
                    .add(LootItem.lootTableItem(deckItem)
                            .apply(SetItemCountFunction.setCount(
                                    UniformGenerator.between(TOTAL_PLANKS * DESTRUCTION_MODIFIER, TOTAL_PLANKS)))));

            return sloopLootTable;
        }
    }
}