package com.alekiponi.alekiships.data.advancements;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.common.item.AlekiShipsItems;
import com.alekiponi.alekiships.data.providers.AlekiShipsLanguageProvider;
import com.alekiponi.alekiships.util.advancements.AlekiShipsAdvancements;
import com.alekiponi.alekiships.util.advancements.GenericTrigger;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeAdvancementProvider;

import java.util.function.Consumer;

public class AlekiShipsAdvancementGenerator implements ForgeAdvancementProvider.AdvancementGenerator {

    private static final ResourceLocation SMELT_IRON = new ResourceLocation("story/smelt_iron");
    private static final String CANNON_TITLE = "alekiships.advancements.cannon.title";
    private static final String CANNON_DESCRIPTION = "alekiships.advancements.cannon.description";
    private static final String ROWBOAT_COMPLETED_TITLE = "alekiships.advancements.rowboat_completed.title";
    private static final String ROWBOAT_COMPLETED_DESCRIPTION = "alekiships.advancements.rowboat_completed.description";
    private static final String RIDE_BARREL_TITLE = "alekiships.advancements.ride_barrel.title";
    private static final String RIDE_BARREL_DESCRIPTION = "alekiships.advancements.ride_barrel.description";
    private static final String DYE_SHIP_BLACK_TITLE = "alekiships.advancements.dye_ship_black.title";
    private static final String DYE_SHIP_BLACK_DESCRIPTION = "alekiships.advancements.dye_ship_black.description";
    private static final String ARMOR_STAND_ON_BOAT_TITLE = "alekiships.advancements.armor_stand_on_boat.title";
    private static final String ARMOR_STAND_ON_BOAT_DESCRIPTION = "alekiships.advancements.armor_stand_on_boat.description";
    private static final String SLOOP_COMPLETED_TITLE = "alekiships.advancements.sloop_completed.title";
    private static final String SLOOP_COMPLETED_DESCRIPTION = "alekiships.advancements.sloop_completed.description";
    private static final String FULL_BROADSIDE_TITLE = "alekiships.advancements.full_broadside.title";
    private static final String FULL_BROADSIDE_DESCRIPTION = "alekiships.advancements.full_broadside.description";

    /**
     * Solves a stupid problem stupidly. Whatever
     */
    private static AbstractCriterionTriggerInstance getTriggerInstance(final GenericTrigger sloopCompleted) {
        return new AbstractCriterionTriggerInstance(sloopCompleted.getId(), ContextAwarePredicate.ANY) {
        };
    }

    public static void addTranslations(final AlekiShipsLanguageProvider.TranslationWriter translationWriter) {
        translationWriter.add(CANNON_TITLE, "Incoming Cannon Event");
        translationWriter.add(CANNON_DESCRIPTION, "Craft a cannon");
        translationWriter.add(ROWBOAT_COMPLETED_TITLE, "The Montlake Cut");
        translationWriter.add(ROWBOAT_COMPLETED_DESCRIPTION, "Build a Rowboat");
        translationWriter.add(RIDE_BARREL_TITLE, "Lost at Sea");
        translationWriter.add(RIDE_BARREL_DESCRIPTION, "Ride a barrel");
        translationWriter.add(FULL_BROADSIDE_TITLE, "Master and Commander");
        translationWriter.add(FULL_BROADSIDE_DESCRIPTION, "Fire a new broadside");
        translationWriter.add(ARMOR_STAND_ON_BOAT_TITLE, "Wilson!!!!!!!");
        translationWriter.add(ARMOR_STAND_ON_BOAT_DESCRIPTION, "Place an inanimate companion on a boat");
        translationWriter.add(SLOOP_COMPLETED_TITLE, "Yesler's Wharf");
        translationWriter.add(SLOOP_COMPLETED_DESCRIPTION, "Build a Sloop");
        translationWriter.add(DYE_SHIP_BLACK_TITLE, "The best pirate I've ever seen");
        translationWriter.add(DYE_SHIP_BLACK_DESCRIPTION, "Name a black ship The Black Pearl");
    }

    @Override
    public void generate(final HolderLookup.Provider registries, final Consumer<Advancement> writer,
            final ExistingFileHelper existingFileHelper) {
        // Ensure you update #addTranslations when adding a new advancement
        Advancement.Builder.advancement().parent(SMELT_IRON)
                .display(AlekiShipsItems.CANNON.get(), Component.translatable(CANNON_TITLE),
                        Component.translatable(CANNON_DESCRIPTION), null, FrameType.TASK, true, true, false)
                .addCriterion("cannon", InventoryChangeTrigger.TriggerInstance.hasItems(AlekiShipsItems.CANNON.get()))
                .requirements(new String[][]{{"cannon"}})
                .save(writer, new ResourceLocation(AlekiShips.MOD_ID, "cannon"), existingFileHelper);

        final Advancement rowboatCompleted = Advancement.Builder.advancement().parent(SMELT_IRON)
                .display(AlekiShipsItems.ROWBOAT_ICON_ONLY.get(), Component.translatable(ROWBOAT_COMPLETED_TITLE),
                        Component.translatable(ROWBOAT_COMPLETED_DESCRIPTION), null, FrameType.TASK, true, true, false)
                .addCriterion("rowboat", getTriggerInstance(AlekiShipsAdvancements.ROWBOAT_COMPLETED))
                .requirements(new String[][]{{"rowboat"}})
                .save(writer, new ResourceLocation(AlekiShips.MOD_ID, "rowboat_completed"), existingFileHelper);

        Advancement.Builder.advancement().parent(rowboatCompleted)
                .display(Items.BARREL, Component.translatable(RIDE_BARREL_TITLE),
                        Component.translatable(RIDE_BARREL_DESCRIPTION), null, FrameType.TASK, true, true, true)
                .addCriterion("barrel", getTriggerInstance(AlekiShipsAdvancements.RIDE_BARREL))
                .requirements(new String[][]{{"barrel"}})
                .save(writer, new ResourceLocation(AlekiShips.MOD_ID, "ride_barrel"), existingFileHelper);

        Advancement.Builder.advancement().parent(rowboatCompleted)
                .display(AlekiShipsItems.CANNONBALL.get(), Component.translatable(FULL_BROADSIDE_TITLE),
                        Component.translatable(FULL_BROADSIDE_DESCRIPTION), null, FrameType.CHALLENGE, true, true, true)
                .addCriterion("broadside", getTriggerInstance(AlekiShipsAdvancements.FULL_BROADSIDE))
                .requirements(new String[][]{{"broadside"}})
                .save(writer, new ResourceLocation(AlekiShips.MOD_ID, "full_broadside"), existingFileHelper);

        Advancement.Builder.advancement().parent(rowboatCompleted)
                .display(Items.ARMOR_STAND, Component.translatable(ARMOR_STAND_ON_BOAT_TITLE),
                        Component.translatable(ARMOR_STAND_ON_BOAT_DESCRIPTION), null, FrameType.CHALLENGE, true, true,
                        true).addCriterion("armorstand", getTriggerInstance(AlekiShipsAdvancements.ARMOR_STAND_ON_BOAT))
                .requirements(new String[][]{{"armorstand"}})
                .save(writer, new ResourceLocation(AlekiShips.MOD_ID, "armor_stand_on_boat"), existingFileHelper);

        final Advancement sloopCompleted = Advancement.Builder.advancement().parent(rowboatCompleted)
                .display(AlekiShipsItems.SLOOP_ICON_ONLY.get(), Component.translatable(SLOOP_COMPLETED_TITLE),
                        Component.translatable(SLOOP_COMPLETED_DESCRIPTION), null, FrameType.TASK, true, true, false)
                .addCriterion("sloop", getTriggerInstance(AlekiShipsAdvancements.SLOOP_COMPLETED))
                .requirements(new String[][]{{"sloop"}})
                .save(writer, new ResourceLocation(AlekiShips.MOD_ID, "sloop_completed"), existingFileHelper);

        Advancement.Builder.advancement().parent(sloopCompleted)
                .display(Items.SKELETON_SKULL, Component.translatable(DYE_SHIP_BLACK_TITLE),
                        Component.translatable(DYE_SHIP_BLACK_DESCRIPTION), null, FrameType.CHALLENGE, true, true, true)
                .addCriterion("dye", getTriggerInstance(AlekiShipsAdvancements.DYE_SHIP_BLACK))
                .requirements(new String[][]{{"dye"}})
                .save(writer, new ResourceLocation(AlekiShips.MOD_ID, "dye_ship_black"), existingFileHelper);
    }
}