package com.alekiponi.alekiships.common.entity;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.common.AlekiShipsRegistries;
import com.alekiponi.alekiships.common.entity.SloopConstructionState.SloopConstructionStage;
import com.alekiponi.alekiships.common.entity.vehicle.ConstructionInput;
import com.alekiponi.alekiships.common.entity.vehicle.ConstructionInput.ProgressStage;
import com.alekiponi.alekiships.common.entity.vehicle.SloopVariant;
import com.alekiponi.alekiships.common.entity.vehicle.SloopVariants;
import com.alekiponi.alekiships.common.item.AlekiShipsItems;
import com.alekiponi.alekiships.common.recipe.entity.SloopResult;

import net.minecraft.core.Holder;
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

    public static final int KEEL_LOGS = 8;
    public static final int DECK_PLANKS = 20;
    public static final int BOWSPRIT_LOGS = 6;
    public static final int MAST_LOGS = 12;
    public static final int BOOM_LOGS = 8;
    public static final int MAINSAIL_WOOL = 16;
    public static final int JIBSAIL_WOOL = 8;
    public static final int STERN_RAILINGS = 8;
    public static final int BOW_RAILINGS = 8;
    public static final int RIGGING_CABLES = 8;

    /**
     * The total amount of stripped logs it takes to asemble the sloop from the construction phase
     */
    public static final int TOTAL_STRIPPED_LOGS = KEEL_LOGS + BOWSPRIT_LOGS + MAST_LOGS + BOOM_LOGS;
    /**
     * The total amount of planks it takes to assemble the sloop from the construction phase
     */
    public static final int TOTAL_PLANKS = DECK_PLANKS;
    /**
     * The total amount of railings it takes to assemble the sloop from the construction phase
     */
    public static final int TOTAL_RAILINGS = STERN_RAILINGS + BOW_RAILINGS;
    /**
     * The total amount of wool it takes to assemble the sloop from the construction phase
     */
    public static final float TOTAL_WOOL = MAINSAIL_WOOL + JIBSAIL_WOOL;

    private static ResourceKey<ConstructionInput<SloopConstructionStage>> createKey(final String name) {
        return ResourceKey.create(AlekiShipsRegistries.CONSTRUCTION_SLOOP_INPUT, AlekiShips.location(name));
    }

    public static void bootstrapOverworld(final BootstrapContext<ConstructionInput<SloopConstructionStage>> context) {
        final var sloopVariantLookup = context.lookup(AlekiShipsRegistries.SLOOP_VARIANT);
        context.register(OAK,
                constructionSloopInput(Blocks.OAK_PLANKS.defaultBlockState(), Items.STRIPPED_OAK_LOG, Items.OAK_PLANKS,
                        Items.WHITE_WOOL, Items.OAK_FENCE, sloopVariantLookup.getOrThrow(SloopVariants.OAK)).build());
        context.register(SPRUCE,
                constructionSloopInput(Blocks.SPRUCE_PLANKS.defaultBlockState(), Items.STRIPPED_SPRUCE_LOG,
                        Items.SPRUCE_PLANKS, Items.WHITE_WOOL, Items.SPRUCE_FENCE,
                        sloopVariantLookup.getOrThrow(SloopVariants.SPRUCE)).build());
        context.register(BIRCH,
                constructionSloopInput(Blocks.BIRCH_PLANKS.defaultBlockState(), Items.STRIPPED_BIRCH_LOG,
                        Items.BIRCH_PLANKS, Items.WHITE_WOOL, Items.BIRCH_FENCE,
                        sloopVariantLookup.getOrThrow(SloopVariants.BIRCH)).build());
        context.register(ACACIA,
                constructionSloopInput(Blocks.ACACIA_PLANKS.defaultBlockState(), Items.STRIPPED_ACACIA_LOG,
                        Items.ACACIA_PLANKS, Items.WHITE_WOOL, Items.ACACIA_FENCE,
                        sloopVariantLookup.getOrThrow(SloopVariants.ACACIA)).build());
        context.register(CHERRY,
                constructionSloopInput(Blocks.CHERRY_PLANKS.defaultBlockState(), Items.STRIPPED_CHERRY_LOG,
                        Items.CHERRY_PLANKS, Items.WHITE_WOOL, Items.CHERRY_FENCE,
                        sloopVariantLookup.getOrThrow(SloopVariants.CHERRY)).build());
        context.register(JUNGLE,
                constructionSloopInput(Blocks.JUNGLE_PLANKS.defaultBlockState(), Items.STRIPPED_JUNGLE_LOG,
                        Items.JUNGLE_PLANKS, Items.WHITE_WOOL, Items.JUNGLE_FENCE,
                        sloopVariantLookup.getOrThrow(SloopVariants.JUNGLE)).build());
        context.register(DARK_OAK,
                constructionSloopInput(Blocks.DARK_OAK_PLANKS.defaultBlockState(), Items.STRIPPED_DARK_OAK_LOG,
                        Items.DARK_OAK_PLANKS, Items.WHITE_WOOL, Items.DARK_OAK_FENCE,
                        sloopVariantLookup.getOrThrow(SloopVariants.DARK_OAK)).build());
        context.register(MANGROVE,
                constructionSloopInput(Blocks.MANGROVE_PLANKS.defaultBlockState(), Items.STRIPPED_MANGROVE_LOG,
                        Items.MANGROVE_PLANKS, Items.WHITE_WOOL, Items.MANGROVE_FENCE,
                        sloopVariantLookup.getOrThrow(SloopVariants.MANGROVE)).build());
        context.register(BAMBOO,
                constructionSloopInput(Blocks.BAMBOO_PLANKS.defaultBlockState(), Items.STRIPPED_BAMBOO_BLOCK,
                        Items.BAMBOO_PLANKS, Items.WHITE_WOOL, Items.BAMBOO_FENCE,
                        sloopVariantLookup.getOrThrow(SloopVariants.BAMBOO)).build());
    }

    public static void bootstrapNether(final BootstrapContext<ConstructionInput<SloopConstructionStage>> context) {
        final var sloopVariantLookup = context.lookup(AlekiShipsRegistries.SLOOP_VARIANT);
        context.register(CRIMSON,
                constructionSloopInput(Blocks.CRIMSON_PLANKS.defaultBlockState(), Items.STRIPPED_CRIMSON_STEM,
                        Items.CRIMSON_PLANKS, Items.WHITE_WOOL, Items.CRIMSON_FENCE,
                        sloopVariantLookup.getOrThrow(SloopVariants.CRIMSON)).build());
        context.register(WARPED,
                constructionSloopInput(Blocks.WARPED_PLANKS.defaultBlockState(), Items.STRIPPED_WARPED_STEM,
                        Items.WARPED_PLANKS, Items.WHITE_WOOL, Items.WARPED_FENCE,
                        sloopVariantLookup.getOrThrow(SloopVariants.WARPED)).build());
    }

    public static ConstructionInput.ConstructionInputBuilder<SloopConstructionStage> constructionSloopInput(
            final BlockState deckBlock, final Item strippedLogItem, final Item deckItem, final Item sailItem,
            final Item railingItem, final Holder<SloopVariant> sloopVariant) {

        @SuppressWarnings("deprecation") final var deckBlockSoundType = deckBlock.getSoundType();
        final SoundEvent stateProgressedSound = deckBlockSoundType.getPlaceSound();
        final SoundEvent switchStateSound = deckBlockSoundType.getBreakSound();
        final BlockState sailsSwitchBlockstate = Blocks.WHITE_WOOL.defaultBlockState();
        @SuppressWarnings("deprecation") final var sailSoundType = sailsSwitchBlockstate.getSoundType();
        final SoundEvent sailsProgressedSound = sailSoundType.getPlaceSound();
        final SoundEvent sailsSwitchSound = sailSoundType.getBreakSound();

        return ConstructionInput.<SloopConstructionStage>builder()
                .stage(KEEL, new ProgressStage(SizedIngredient.of(strippedLogItem, KEEL_LOGS), stateProgressedSound,
                        switchStateSound, deckBlock))
                 .stage(DECK, new ProgressStage(SizedIngredient.of(deckItem, DECK_PLANKS), stateProgressedSound,
                        switchStateSound, deckBlock))
                 .stage(BOWSPRIT,
                        new ProgressStage(SizedIngredient.of(strippedLogItem, BOWSPRIT_LOGS), stateProgressedSound,
                                switchStateSound, deckBlock))
                 .stage(MAST, new ProgressStage(SizedIngredient.of(strippedLogItem, MAST_LOGS), stateProgressedSound,
                        switchStateSound, deckBlock))
                 .stage(BOOM, new ProgressStage(SizedIngredient.of(strippedLogItem, BOOM_LOGS), stateProgressedSound,
                        switchStateSound, deckBlock))
                 .stage(MAINSAIL, new ProgressStage(SizedIngredient.of(sailItem, MAINSAIL_WOOL), sailsProgressedSound,
                        sailsSwitchSound, sailsSwitchBlockstate))
                 .stage(JIBSAIl, new ProgressStage(SizedIngredient.of(sailItem, JIBSAIL_WOOL), sailsProgressedSound,
                        sailsSwitchSound, sailsSwitchBlockstate))
                 .stage(RAILINGS_STERN,
                        new ProgressStage(SizedIngredient.of(railingItem, STERN_RAILINGS), stateProgressedSound,
                                switchStateSound, deckBlock))
                 .stage(RAILINGS_BOW,
                        new ProgressStage(SizedIngredient.of(railingItem, BOW_RAILINGS), stateProgressedSound,
                                switchStateSound, deckBlock))
                 .stage(ANCHOR,
                        new ProgressStage(SizedIngredient.of(AlekiShipsItems.ANCHOR, 1), SoundEvents.METAL_PLACE,
                                switchStateSound, deckBlock))
                 .stage(RIGGING,
                        new ProgressStage(SizedIngredient.of(Items.LEAD, RIGGING_CABLES), SoundEvents.LEASH_KNOT_PLACE,
                                switchStateSound, deckBlock))
                .constructedEntity(new SloopResult(sloopVariant))
                .assembleSound(SoundEvents.WOOD_BREAK)
                .assembleBlockState(deckBlock);
    }
}