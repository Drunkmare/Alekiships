package com.alekiponi.alekiships.data;

import com.google.common.collect.ImmutableMap;

import com.alekiponi.alekiships.common.entity.SloopConstructionState;
import com.alekiponi.alekiships.common.entity.vehicle.ConstructionInput;
import com.alekiponi.alekiships.common.item.AlekiShipsItems;
import com.alekiponi.alekiships.util.BoatMaterial;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import net.neoforged.neoforge.common.crafting.SizedIngredient;

public class ConstructionInputGenerators {

    public static ConstructionInput<SloopConstructionState.SloopConstructionStage> createConstructionSloop(
            final BoatMaterial boatMaterial) {
        final var builder = ImmutableMap.<SloopConstructionState.SloopConstructionStage, ConstructionInput.ProgressStage>builder();

        final BlockState deckBlock = boatMaterial.getDeckBlock();
        @SuppressWarnings("deprecation") final var soundType = deckBlock.getSoundType();

        final SoundEvent stateProgressedSound = soundType.getPlaceSound();
        final SoundEvent switchStateSound = soundType.getBreakSound();

        final Item logItem = boatMaterial.getStrippedLog();
        builder.put(SloopConstructionState.SloopConstructionStage.KEEL,
                new ConstructionInput.ProgressStage(SizedIngredient.of(logItem, 8), stateProgressedSound,
                        switchStateSound, deckBlock));

        final Item deckItem = boatMaterial.getDeckItem();
        builder.put(SloopConstructionState.SloopConstructionStage.DECK,
                new ConstructionInput.ProgressStage(SizedIngredient.of(deckItem, 20), stateProgressedSound,
                        switchStateSound, deckBlock));

        builder.put(SloopConstructionState.SloopConstructionStage.BOWSPRIT,
                new ConstructionInput.ProgressStage(SizedIngredient.of(logItem, 6), stateProgressedSound,
                        switchStateSound, deckBlock));

        builder.put(SloopConstructionState.SloopConstructionStage.MAST,
                new ConstructionInput.ProgressStage(SizedIngredient.of(logItem, 12), stateProgressedSound,
                        switchStateSound, deckBlock));

        builder.put(SloopConstructionState.SloopConstructionStage.BOOM,
                new ConstructionInput.ProgressStage(SizedIngredient.of(logItem, 8), stateProgressedSound,
                        switchStateSound, deckBlock));

        final Item sailItem = Items.WHITE_WOOL;
        final BlockState sailsSwitchBlockstate = Blocks.WHITE_WOOL.defaultBlockState();
        final SoundEvent sailsProgressedSound = SoundEvents.WOOL_PLACE;
        final SoundEvent sailsSwitchSound = SoundEvents.WOOL_BREAK;

        builder.put(SloopConstructionState.SloopConstructionStage.MAINSAIL,
                new ConstructionInput.ProgressStage(SizedIngredient.of(sailItem, 16), sailsProgressedSound,
                        sailsSwitchSound, sailsSwitchBlockstate));

        builder.put(SloopConstructionState.SloopConstructionStage.JIBSAIl,
                new ConstructionInput.ProgressStage(SizedIngredient.of(sailItem, 8), sailsProgressedSound,
                        sailsSwitchSound, sailsSwitchBlockstate));

        final Item acaciaFence = boatMaterial.getRailing();
        builder.put(SloopConstructionState.SloopConstructionStage.RAILINGS_STERN,
                new ConstructionInput.ProgressStage(SizedIngredient.of(acaciaFence, 8), stateProgressedSound,
                        switchStateSound, deckBlock));

        builder.put(SloopConstructionState.SloopConstructionStage.RAILINGS_BOW,
                new ConstructionInput.ProgressStage(SizedIngredient.of(acaciaFence, 8), stateProgressedSound,
                        switchStateSound, deckBlock));

        builder.put(SloopConstructionState.SloopConstructionStage.ANCHOR,
                new ConstructionInput.ProgressStage(SizedIngredient.of(AlekiShipsItems.ANCHOR, 1),
                        SoundEvents.METAL_PLACE, switchStateSound, deckBlock));

        builder.put(SloopConstructionState.SloopConstructionStage.RIGGING,
                new ConstructionInput.ProgressStage(SizedIngredient.of(Items.LEAD, 8), SoundEvents.LEASH_KNOT_PLACE,
                        switchStateSound, deckBlock));

        //noinspection OptionalGetWithoutIsPresent
        return ConstructionInput.of(builder.buildOrThrow(),
                boatMaterial.getEntityType(BoatMaterial.BoatType.SLOOP).get(), SoundEvents.WOOD_BREAK, deckBlock);
    }
}