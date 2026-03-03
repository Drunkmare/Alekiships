package com.alekiponi.alekiships.common.entity.vehicle;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.common.AlekiShipsRegistries;
import com.alekiponi.alekiships.common.block.AlekiShipsBlocks;
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

public final class RowboatVariants {

    public static final ResourceKey<RowboatVariant> OAK = createKey("oak");
    public static final ResourceKey<RowboatVariant> SPRUCE = createKey("spruce");
    public static final ResourceKey<RowboatVariant> BIRCH = createKey("birch");
    public static final ResourceKey<RowboatVariant> ACACIA = createKey("acacia");
    public static final ResourceKey<RowboatVariant> CHERRY = createKey("cherry");
    public static final ResourceKey<RowboatVariant> JUNGLE = createKey("jungle");
    public static final ResourceKey<RowboatVariant> DARK_OAK = createKey("dark_oak");
    public static final ResourceKey<RowboatVariant> CRIMSON = createKey("crimson");
    public static final ResourceKey<RowboatVariant> WARPED = createKey("warped");
    public static final ResourceKey<RowboatVariant> MANGROVE = createKey("mangrove");
    public static final ResourceKey<RowboatVariant> BAMBOO = createKey("bamboo");
    public static final ResourceKey<RowboatVariant> DEFAULT = OAK;

    public static final int FRAME_CAPACITY = 4;
    public static final int ROWBOAT_FRAMES = 6;
    public static final int TOTAL_PLANKS = FRAME_CAPACITY * ROWBOAT_FRAMES;

    private static ResourceKey<RowboatVariant> createKey(final String name) {
        return ResourceKey.create(AlekiShipsRegistries.ROWBOAT_VARIANT, AlekiShips.location(name));
    }

    private static ResourceLocation texture(final String wood) {
        return AlekiShips.location("entity/watercraft/rowboat/" + wood);
    }

    public static void bootstrapOverworld(final BootstrapContext<RowboatVariant> context) {
        final var lookup = context.lookup(AlekiShipsRegistries.BOAT_MATERIAL);

        context.register(OAK, RowboatVariant.builder()
                .texture(texture("oak"))
                .lootTable(Loot.OAK)
                .boatMaterial(lookup.getOrThrow(BoatMaterials.OAK))
                .repairMaterials(repairMaterials(Items.OAK_PLANKS, SoundEvents.WOOD_PLACE))
                .build());

        context.register(SPRUCE, RowboatVariant.builder()
                .texture(texture("spruce"))
                .lootTable(Loot.SPRUCE)
                .boatMaterial(lookup.getOrThrow(BoatMaterials.SPRUCE))
                .repairMaterials(repairMaterials(Items.SPRUCE_PLANKS, SoundEvents.WOOD_PLACE))
                .build());

        context.register(BIRCH, RowboatVariant.builder()
                .texture(texture("birch"))
                .lootTable(Loot.BIRCH)
                .boatMaterial(lookup.getOrThrow(BoatMaterials.BIRCH))
                .repairMaterials(repairMaterials(Items.BIRCH_PLANKS, SoundEvents.WOOD_PLACE))
                .build());

        context.register(ACACIA, RowboatVariant.builder()
                .texture(texture("acacia"))
                .lootTable(Loot.ACACIA)
                .boatMaterial(lookup.getOrThrow(BoatMaterials.ACACIA))
                .repairMaterials(repairMaterials(Items.ACACIA_PLANKS, SoundEvents.WOOD_PLACE))
                .build());

        context.register(CHERRY, RowboatVariant.builder()
                .texture(texture("cherry"))
                .lootTable(Loot.CHERRY)
                .boatMaterial(lookup.getOrThrow(BoatMaterials.CHERRY))
                .repairMaterials(repairMaterials(Items.CHERRY_PLANKS, SoundEvents.WOOD_PLACE))
                .build());

        context.register(JUNGLE, RowboatVariant.builder()
                .texture(texture("jungle"))
                .lootTable(Loot.JUNGLE)
                .boatMaterial(lookup.getOrThrow(BoatMaterials.JUNGLE))
                .repairMaterials(repairMaterials(Items.JUNGLE_PLANKS, SoundEvents.WOOD_PLACE))
                .build());

        context.register(DARK_OAK, RowboatVariant.builder()
                .texture(texture("dark_oak"))
                .lootTable(Loot.DARK_OAK)
                .boatMaterial(lookup.getOrThrow(BoatMaterials.DARK_OAK))
                .repairMaterials(repairMaterials(Items.DARK_OAK_PLANKS, SoundEvents.WOOD_PLACE))
                .build());

        context.register(MANGROVE, RowboatVariant.builder()
                .texture(texture("mangrove"))
                .lootTable(Loot.MANGROVE)
                .boatMaterial(lookup.getOrThrow(BoatMaterials.MANGROVE))
                .repairMaterials(repairMaterials(Items.MANGROVE_PLANKS, SoundEvents.WOOD_PLACE))
                .build());

        context.register(BAMBOO, RowboatVariant.builder()
                .texture(texture("bamboo"))
                .lootTable(Loot.BAMBOO)
                .boatMaterial(lookup.getOrThrow(BoatMaterials.BAMBOO))
                .repairMaterials(repairMaterials(Items.BAMBOO_PLANKS, SoundEvents.BAMBOO_WOOD_PLACE))
                .build());
    }

    public static void bootstrapNether(final BootstrapContext<RowboatVariant> context) {
        final var lookup = context.lookup(AlekiShipsRegistries.BOAT_MATERIAL);

        context.register(CRIMSON, RowboatVariant.builder()
                .texture(texture("crimson"))
                .lootTable(Loot.CRIMSON)
                .boatMaterial(lookup.getOrThrow(BoatMaterials.CRIMSON))
                .repairMaterials(repairMaterials(Items.CRIMSON_PLANKS, SoundEvents.NETHER_WOOD_PLACE))
                .build());

        context.register(WARPED, RowboatVariant.builder()
                .texture(texture("warped"))
                .lootTable(Loot.WARPED)
                .boatMaterial(lookup.getOrThrow(BoatMaterials.WARPED))
                .repairMaterials(repairMaterials(Items.WARPED_PLANKS, SoundEvents.NETHER_WOOD_PLACE))
                .build());
    }

    public static RepairMaterials repairMaterials(final Item planks, final SoundEvent woodRepairSound) {
        return RepairMaterials.builder()
                .material(builder -> builder.repairIngredient(Ingredient.of(planks))
                        .repairAmount(RowboatEntity.DAMAGE_THRESHOLD / TOTAL_PLANKS * 1.5F)
                        .repairSound(woodRepairSound))
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
            return ResourceKey.create(Registries.LOOT_TABLE, AlekiShips.location("entities/rowboat/" + name));
        }

        public static LootTable.Builder createRowboatLootTable(final Item deckItem) {
            final LootTable.Builder rowboatLootTable = LootTable.lootTable();

            rowboatLootTable.withPool(LootPool.lootPool()
                    .add(LootItem.lootTableItem(AlekiShipsBlocks.OARLOCK.get())
                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(0, 2)))));

            rowboatLootTable.withPool(LootPool.lootPool()
                    .add(LootItem.lootTableItem(deckItem)
                            .apply(SetItemCountFunction.setCount(
                                    UniformGenerator.between(TOTAL_PLANKS * DESTRUCTION_MODIFIER, TOTAL_PLANKS)))));

            return rowboatLootTable;
        }
    }
}