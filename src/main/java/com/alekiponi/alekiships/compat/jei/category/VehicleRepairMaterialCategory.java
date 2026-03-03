package com.alekiponi.alekiships.compat.jei.category;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.common.entity.vehicle.AbstractAlekiBoatEntity;
import com.alekiponi.alekiships.compat.jei.recipe.VehicleRepairMaterialRecipe;
import com.alekiponi.alekiships.util.RepairMaterials;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import org.jetbrains.annotations.Nullable;

public class VehicleRepairMaterialCategory extends JEIRecipeCategory<VehicleRepairMaterialRecipe> {

    public static final String REPAIR_AMOUNT_PERCENT_KEY = "gui." + AlekiShips.MOD_ID + ".jei.category.repair_material.repair_percent";
    public static final String REPAIR_AMOUNT_ABSOLUTE_KEY = "gui." + AlekiShips.MOD_ID + ".jei.category.repair_material.repair_absolute";
    public static final String MINIMUM_DAMAGE_PERCENT_KEY = "gui." + AlekiShips.MOD_ID + ".jei.category.repair_material.min_damage_percent";
    public static final String MINIMUM_DAMAGE_ABSOLUTE_KEY = "gui." + AlekiShips.MOD_ID + ".jei.category.repair_material.min_damage_absolute";
    public static final String MAXIMUM_DAMAGE_PERCENT_KEY = "gui." + AlekiShips.MOD_ID + ".jei.category.repair_material.max_damage_percent";
    public static final String MAXIMUM_DAMAGE_ABSOLUTE_KEY = "gui." + AlekiShips.MOD_ID + ".jei.category.repair_material.max_damage_absolute";

    public static final int DAMAGE_SCALE = 100;

    public static final int TEXT_GRAY = 0xFF808080;

    private static final int WIDTH = 140;
    private static final int HEIGHT = 50;
    private final IDrawable slotBackground;
    private final String vehicleNameKey;
    private final float damageThreshold;
    private final NumberFormat numberFormat = new DecimalFormat("0.#");
    private final ScreenRectangle nameArea = new ScreenRectangle(1, 2, WIDTH - 2,
            Minecraft.getInstance().font.lineHeight);
    private final ScreenRectangle repairTextArea = new ScreenRectangle(22, 16, WIDTH - 22,
            Minecraft.getInstance().font.lineHeight);
    private final ScreenRectangle minDamageTextArea = new ScreenRectangle(22, 26, WIDTH - 22,
            Minecraft.getInstance().font.lineHeight);
    private final ScreenRectangle maxDamageTextArea = new ScreenRectangle(22, 36, WIDTH - 22,
            Minecraft.getInstance().font.lineHeight);

    public VehicleRepairMaterialCategory(final IGuiHelper guiHelper,
            final RecipeType<VehicleRepairMaterialRecipe> recipeType,
            final EntityType<? extends AbstractAlekiBoatEntity<?>> entityType, final float damageThreshold,
            final IDrawable icon, final Component title) {
        super(recipeType, guiHelper.createBlankDrawable(WIDTH, HEIGHT), icon, title);
        this.slotBackground = guiHelper.getSlotDrawable();
        this.vehicleNameKey = entityType.getDescriptionId();
        this.damageThreshold = damageThreshold;
    }

    public static Component truncateStringToWidth(final Component text, final int width, final Font fontRenderer) {
        final int ellipsisWidth = fontRenderer.width("...");
        final FormattedText truncatedText = fontRenderer.substrByWidth(text, width - ellipsisWidth);
        final String truncatedTextString = truncatedText.getString();
        return Component.literal(truncatedTextString + "...");
    }

    @Override
    public void setRecipe(final IRecipeLayoutBuilder builder, final VehicleRepairMaterialRecipe recipe,
            final IFocusGroup focuses) {
        final var inputSlotBuilder = builder.addSlot(RecipeIngredientRole.INPUT, 1, 21)
                .setBackground(this.slotBackground, -1, -1);
        inputSlotBuilder.addIngredients(VanillaTypes.ITEM_STACK,
                Arrays.asList(recipe.material().repairIngredient.getItems()));
    }

    @Override
    public void draw(final VehicleRepairMaterialRecipe recipe, final IRecipeSlotsView recipeSlotsView,
            final GuiGraphics guiGraphics, final double mouseX, final double mouseY) {
        final var minecraft = Minecraft.getInstance();
        final var font = minecraft.font;
        final Component vehicleName = Component.translatable(this.vehicleNameKey, recipe.materialName());
        if (font.width(vehicleName) > this.nameArea.width()) {
            guiGraphics.drawString(font, truncateStringToWidth(vehicleName, this.nameArea.width(), font), 1, 2,
                    0xFF505050, false);
        } else {
            guiGraphics.drawString(font, vehicleName, 1, 2, 0xFF505050, false);
        }
        this.drawMaterialData(guiGraphics, font, recipe.material());
    }

    private void drawMaterialData(final GuiGraphics guiGraphics, final Font font,
            final RepairMaterials.Material material) {
        guiGraphics.drawString(font, Component.translatable(REPAIR_AMOUNT_PERCENT_KEY,
                        this.numberFormat.format(this.scaleDamage(material.repairAmount))), this.repairTextArea.left(),
                this.repairTextArea.top(), TEXT_GRAY, false);

        final var minDamage = material.damageRange.minDamage()
                .map(this::scaleDamage).orElse(0F);
        guiGraphics.drawString(font,
                Component.translatable(MINIMUM_DAMAGE_PERCENT_KEY, this.numberFormat.format(minDamage)),
                this.minDamageTextArea.left(), this.minDamageTextArea.top(), TEXT_GRAY, false);
        final var maxDamage = material.damageRange.maxDamage()
                .map(this::scaleDamage).orElse(100F);
        guiGraphics.drawString(font,
                Component.translatable(MAXIMUM_DAMAGE_PERCENT_KEY, this.numberFormat.format(maxDamage)),
                this.maxDamageTextArea.left(), this.maxDamageTextArea.top(), TEXT_GRAY, false);
    }

    @Override
    public void getTooltip(final ITooltipBuilder tooltip, final VehicleRepairMaterialRecipe recipe,
            final IRecipeSlotsView recipeSlotsView, final double mouseX, final double mouseY) {
        final var material = recipe.material();
        if (this.repairTextArea.containsPoint((int) mouseX, (int) mouseY)) {
            tooltip.add(Component.translatable(REPAIR_AMOUNT_ABSOLUTE_KEY,
                    this.numberFormat.format(material.repairAmount)));
        }

        if (this.minDamageTextArea.containsPoint((int) mouseX, (int) mouseY)) {
            final var minDamage = material.damageRange.minDamage().orElse(0F);
            tooltip.add(Component.translatable(MINIMUM_DAMAGE_ABSOLUTE_KEY, this.numberFormat.format(minDamage)));
        }

        if (this.maxDamageTextArea.containsPoint((int) mouseX, (int) mouseY)) {
            final var maxDamage = material.damageRange.maxDamage().orElse(this.damageThreshold);
            tooltip.add(Component.translatable(MAXIMUM_DAMAGE_ABSOLUTE_KEY, this.numberFormat.format(maxDamage)));
        }
    }

    private float scaleDamage(final float damage) {
        return (damage / this.damageThreshold) * DAMAGE_SCALE;
    }

    @Override
    public @Nullable ResourceLocation getRegistryName(final VehicleRepairMaterialRecipe recipe) {
        return null;
    }
}