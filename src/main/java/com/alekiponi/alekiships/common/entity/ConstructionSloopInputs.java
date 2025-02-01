package com.alekiponi.alekiships.common.entity;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.common.AlekiShipsRegistries;
import com.alekiponi.alekiships.common.entity.SloopConstructionState.SloopConstructionStage;
import com.alekiponi.alekiships.common.entity.vehicle.ConstructionInput;
import com.alekiponi.alekiships.common.entity.vehicle.ConstructionInput.ProgressStage;
import com.alekiponi.alekiships.common.item.AlekiShipsItems;
import com.alekiponi.alekiships.common.recipe.entity.SimpleResult;
import com.alekiponi.alekiships.util.BoatMaterial;
import com.alekiponi.alekiships.util.VanillaWood;

import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import net.neoforged.neoforge.common.crafting.SizedIngredient;

import static com.alekiponi.alekiships.common.entity.SloopConstructionState.SloopConstructionStage.*;

public final class ConstructionSloopInputs {

    public static final ResourceKey<ConstructionInput<SloopConstructionStage>> OAK = createKey("oak");
    public static final ResourceKey<ConstructionInput<SloopConstructionStage>> SPRUCE = createKey("spruce");
    public static final ResourceKey<ConstructionInput<SloopConstructionStage>> BIRCH = createKey("birch");
    public static final ResourceKey<ConstructionInput<SloopConstructionStage>> ACACIA = createKey("acacia");
    public static final ResourceKey<ConstructionInput<SloopConstructionStage>> CHERRY = createKey("cherry");
    public static final ResourceKey<ConstructionInput<SloopConstructionStage>> JUNGLE = createKey("jungle");
    public static final ResourceKey<ConstructionInput<SloopConstructionStage>> DARK_OAK = createKey("dark_oak");
    public static final ResourceKey<ConstructionInput<SloopConstructionStage>> CRIMSON = createKey("crimson");
    public static final ResourceKey<ConstructionInput<SloopConstructionStage>> WARPED = createKey("warped");
    public static final ResourceKey<ConstructionInput<SloopConstructionStage>> MANGROVE = createKey("mangrove");
    public static final ResourceKey<ConstructionInput<SloopConstructionStage>> BAMBOO = createKey("bamboo");

    private static ResourceKey<ConstructionInput<SloopConstructionStage>> createKey(final String name) {
        return ResourceKey.create(AlekiShipsRegistries.CONSTRUCTION_SLOOP_INPUT, AlekiShips.location(name));
    }

    public static void bootstrap(final BootstrapContext<ConstructionInput<SloopConstructionStage>> context) {
        context.register(OAK,
                constructionSloopInput(Blocks.OAK_PLANKS.defaultBlockState(), Items.STRIPPED_OAK_LOG, Items.OAK_PLANKS,
                        Items.WHITE_WOOL, Items.OAK_FENCE, VanillaWood.OAK).build());
        context.register(SPRUCE,
                constructionSloopInput(Blocks.SPRUCE_PLANKS.defaultBlockState(), Items.STRIPPED_SPRUCE_LOG,
                        Items.SPRUCE_PLANKS, Items.WHITE_WOOL, Items.SPRUCE_FENCE, VanillaWood.SPRUCE).build());
        context.register(BIRCH,
                constructionSloopInput(Blocks.BIRCH_PLANKS.defaultBlockState(), Items.STRIPPED_BIRCH_LOG,
                        Items.BIRCH_PLANKS, Items.WHITE_WOOL, Items.BIRCH_FENCE, VanillaWood.BIRCH).build());
        context.register(ACACIA,
                constructionSloopInput(Blocks.ACACIA_PLANKS.defaultBlockState(), Items.STRIPPED_ACACIA_LOG,
                        Items.ACACIA_PLANKS, Items.WHITE_WOOL, Items.ACACIA_FENCE, VanillaWood.ACACIA).build());
        context.register(CHERRY,
                constructionSloopInput(Blocks.CHERRY_PLANKS.defaultBlockState(), Items.STRIPPED_CHERRY_LOG,
                        Items.CHERRY_PLANKS, Items.WHITE_WOOL, Items.CHERRY_FENCE, VanillaWood.CHERRY).build());
        context.register(JUNGLE,
                constructionSloopInput(Blocks.JUNGLE_PLANKS.defaultBlockState(), Items.STRIPPED_JUNGLE_LOG,
                        Items.JUNGLE_PLANKS, Items.WHITE_WOOL, Items.JUNGLE_FENCE, VanillaWood.JUNGLE).build());
        context.register(DARK_OAK,
                constructionSloopInput(Blocks.DARK_OAK_PLANKS.defaultBlockState(), Items.STRIPPED_DARK_OAK_LOG,
                        Items.DARK_OAK_PLANKS, Items.WHITE_WOOL, Items.DARK_OAK_FENCE, VanillaWood.DARK_OAK).build());
        context.register(CRIMSON,
                constructionSloopInput(Blocks.CRIMSON_PLANKS.defaultBlockState(), Items.STRIPPED_CRIMSON_STEM,
                        Items.CRIMSON_PLANKS, Items.WHITE_WOOL, Items.CRIMSON_FENCE, VanillaWood.CRIMSON).build());
        context.register(WARPED,
                constructionSloopInput(Blocks.WARPED_PLANKS.defaultBlockState(), Items.STRIPPED_WARPED_STEM,
                        Items.WARPED_PLANKS, Items.WHITE_WOOL, Items.WARPED_FENCE, VanillaWood.WARPED).build());
        context.register(MANGROVE,
                constructionSloopInput(Blocks.MANGROVE_PLANKS.defaultBlockState(), Items.STRIPPED_MANGROVE_LOG,
                        Items.MANGROVE_PLANKS, Items.WHITE_WOOL, Items.MANGROVE_FENCE, VanillaWood.MANGROVE).build());
        context.register(BAMBOO,
                constructionSloopInput(Blocks.BAMBOO_PLANKS.defaultBlockState(), Items.STRIPPED_BAMBOO_BLOCK,
                        Items.BAMBOO_PLANKS, Items.WHITE_WOOL, Items.BAMBOO_FENCE, VanillaWood.BAMBOO).build());
    }

    public static ConstructionInput.ConstructionInputBuilder<SloopConstructionStage> constructionSloopInput(
            final BlockState deckBlock, final Item strippedLogItem, final Item deckItem, final Item sailItem,
            final Item railingItem, final BoatMaterial boatMaterial) {

        @SuppressWarnings("deprecation") final var deckBlockSoundType = deckBlock.getSoundType();
        final SoundEvent stateProgressedSound = deckBlockSoundType.getPlaceSound();
        final SoundEvent switchStateSound = deckBlockSoundType.getBreakSound();
        final BlockState sailsSwitchBlockstate = Blocks.WHITE_WOOL.defaultBlockState();
        @SuppressWarnings("deprecation") final var sailSoundType = sailsSwitchBlockstate.getSoundType();
        final SoundEvent sailsProgressedSound = sailSoundType.getPlaceSound();
        final SoundEvent sailsSwitchSound = sailSoundType.getBreakSound();

        return ConstructionInput.<SloopConstructionStage>builder()
                .stage(KEEL, new ProgressStage(SizedIngredient.of(strippedLogItem, 8), stateProgressedSound,
                        switchStateSound, deckBlock))
                 .stage(DECK,
                        new ProgressStage(SizedIngredient.of(deckItem, 20), stateProgressedSound, switchStateSound,
                                deckBlock))
                 .stage(BOWSPRIT, new ProgressStage(SizedIngredient.of(strippedLogItem, 6), stateProgressedSound,
                        switchStateSound, deckBlock))
                 .stage(MAST, new ProgressStage(SizedIngredient.of(strippedLogItem, 12), stateProgressedSound,
                        switchStateSound, deckBlock))
                 .stage(BOOM, new ProgressStage(SizedIngredient.of(strippedLogItem, 8), stateProgressedSound,
                        switchStateSound, deckBlock))
                 .stage(MAINSAIL,
                        new ProgressStage(SizedIngredient.of(sailItem, 16), sailsProgressedSound, sailsSwitchSound,
                                sailsSwitchBlockstate))
                 .stage(JIBSAIl,
                        new ProgressStage(SizedIngredient.of(sailItem, 8), sailsProgressedSound, sailsSwitchSound,
                                sailsSwitchBlockstate))
                 .stage(RAILINGS_STERN,
                        new ProgressStage(SizedIngredient.of(railingItem, 8), stateProgressedSound, switchStateSound,
                                deckBlock))
                 .stage(RAILINGS_BOW,
                        new ProgressStage(SizedIngredient.of(railingItem, 8), stateProgressedSound, switchStateSound,
                                deckBlock))
                 .stage(ANCHOR,
                        new ProgressStage(SizedIngredient.of(AlekiShipsItems.ANCHOR, 1), SoundEvents.METAL_PLACE,
                                switchStateSound, deckBlock))
                 .stage(RIGGING, new ProgressStage(SizedIngredient.of(Items.LEAD, 8), SoundEvents.LEASH_KNOT_PLACE,
                        switchStateSound, deckBlock))
                .constructedEntity(new SimpleResult(boatMaterial.getEntityType(BoatMaterial.BoatType.SLOOP).get()))
                .assembleSound(SoundEvents.WOOD_BREAK)
                .assembleBlockState(deckBlock);
    }
}