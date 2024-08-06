package com.alekiponi.alekiships.data.advancements;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.common.item.AlekiShipsItems;
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

    public static final ResourceLocation SMELT_IRON = new ResourceLocation("story/smelt_iron");

    /**
     * Solves a stupid problem stupidly. Whatever
     */
    private static AbstractCriterionTriggerInstance getTriggerInstance(final GenericTrigger sloopCompleted) {
        return new AbstractCriterionTriggerInstance(sloopCompleted.getId(), ContextAwarePredicate.ANY) {
        };
    }

    @Override
    public void generate(final HolderLookup.Provider registries, final Consumer<Advancement> writer,
            final ExistingFileHelper existingFileHelper) {
        Advancement.Builder.advancement().parent(SMELT_IRON)
                .display(AlekiShipsItems.CANNON.get(), Component.translatable("alekiships.advancements.cannon.title"),
                        Component.translatable("alekiships.advancements.cannon.description"), null, FrameType.TASK,
                        true, true, false)
                .addCriterion("cannon", InventoryChangeTrigger.TriggerInstance.hasItems(AlekiShipsItems.CANNON.get()))
                .requirements(new String[][]{{"cannon"}})
                .save(writer, new ResourceLocation(AlekiShips.MOD_ID, "cannon"), existingFileHelper);

        final Advancement rowboatCompleted = Advancement.Builder.advancement().parent(SMELT_IRON)
                .display(AlekiShipsItems.ROWBOAT_ICON_ONLY.get(),
                        Component.translatable("alekiships.advancements.rowboat_completed.title"),
                        Component.translatable("alekiships.advancements.rowboat_completed.description"), null,
                        FrameType.TASK, true, true, false)
                .addCriterion("rowboat", getTriggerInstance(AlekiShipsAdvancements.ROWBOAT_COMPLETED))
                .requirements(new String[][]{{"rowboat"}})
                .save(writer, new ResourceLocation(AlekiShips.MOD_ID, "rowboat_completed"), existingFileHelper);

        Advancement.Builder.advancement().parent(rowboatCompleted)
                .display(Items.BARREL, Component.translatable("alekiships.advancements.ride_barrel.title"),
                        Component.translatable("alekiships.advancements.ride_barrel.description"), null, FrameType.TASK,
                        true, true, true).addCriterion("barrel", getTriggerInstance(AlekiShipsAdvancements.RIDE_BARREL))
                .requirements(new String[][]{{"barrel"}})
                .save(writer, new ResourceLocation(AlekiShips.MOD_ID, "ride_barrel"), existingFileHelper);

        Advancement.Builder.advancement().parent(rowboatCompleted).display(AlekiShipsItems.CANNONBALL.get(),
                        Component.translatable("alekiships.advancements.full_broadside.title"),
                        Component.translatable("alekiships.advancements.full_broadside.description"), null, FrameType.CHALLENGE,
                        true, true, true).addCriterion("broadside", getTriggerInstance(AlekiShipsAdvancements.FULL_BROADSIDE))
                .requirements(new String[][]{{"broadside"}})
                .save(writer, new ResourceLocation(AlekiShips.MOD_ID, "full_broadside"), existingFileHelper);

        Advancement.Builder.advancement().parent(rowboatCompleted)
                .display(Items.ARMOR_STAND, Component.translatable("alekiships.advancements.armor_stand_on_boat.title"),
                        Component.translatable("alekiships.advancements.armor_stand_on_boat.description"), null,
                        FrameType.CHALLENGE, true, true, true)
                .addCriterion("armorstand", getTriggerInstance(AlekiShipsAdvancements.ARMOR_STAND_ON_BOAT))
                .requirements(new String[][]{{"armorstand"}})
                .save(writer, new ResourceLocation(AlekiShips.MOD_ID, "armor_stand_on_boat"), existingFileHelper);

        final Advancement sloopCompleted = Advancement.Builder.advancement().parent(rowboatCompleted)
                .display(AlekiShipsItems.SLOOP_ICON_ONLY.get(),
                        Component.translatable("alekiships.advancements.sloop_completed.title"),
                        Component.translatable("alekiships.advancements.sloop_completed.description"), null,
                        FrameType.TASK, true, true, false)
                .addCriterion("sloop", getTriggerInstance(AlekiShipsAdvancements.SLOOP_COMPLETED))
                .requirements(new String[][]{{"sloop"}})
                .save(writer, new ResourceLocation(AlekiShips.MOD_ID, "sloop_completed"), existingFileHelper);

        Advancement.Builder.advancement().parent(sloopCompleted)
                .display(Items.SKELETON_SKULL, Component.translatable("alekiships.advancements.dye_ship_black.title"),
                        Component.translatable("alekiships.advancements.dye_ship_black.description"), null,
                        FrameType.CHALLENGE, true, true, true)
                .addCriterion("dye", getTriggerInstance(AlekiShipsAdvancements.DYE_SHIP_BLACK))
                .requirements(new String[][]{{"dye"}})
                .save(writer, new ResourceLocation(AlekiShips.MOD_ID, "dye_ship_black"), existingFileHelper);
    }
}