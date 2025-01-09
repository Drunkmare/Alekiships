package com.alekiponi.alekiships.client;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.common.entity.CannonEntity;
import com.alekiponi.alekiships.common.entity.IHaveIcons;
import com.alekiponi.alekiships.common.entity.vehicle.SloopEntity;
import com.alekiponi.alekiships.common.entity.vehicle.SloopUnderConstructionEntity;
import com.alekiponi.alekiships.common.entity.vehiclehelper.ConstructionEntity;
import com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.EmptyCompartmentEntity;
import com.alekiponi.alekiships.util.CommonHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;

public final class IngameOverlays {
    public static final ResourceLocation COMPARTMENT_STATUS = ResourceLocation.fromNamespaceAndPath(AlekiShips.MOD_ID,
            "compartment_status");
    public static final ResourceLocation PASSENGER_STATUS = ResourceLocation.fromNamespaceAndPath(AlekiShips.MOD_ID,
            "passenger_status");
    public static final ResourceLocation SAILING_ELEMENT = ResourceLocation.fromNamespaceAndPath(AlekiShips.MOD_ID,
            "sailing_element");
    public static final ResourceLocation SLOOP_CONSTRUCTION = ResourceLocation.fromNamespaceAndPath(AlekiShips.MOD_ID,
            "sloop_construction");
    public static final ResourceLocation CANNON_LOAD_STATE = ResourceLocation.fromNamespaceAndPath(AlekiShips.MOD_ID,
            "cannon_load_state");

    public static final ResourceLocation COMPARTMENT_ICONS = ResourceLocation.fromNamespaceAndPath(AlekiShips.MOD_ID,
            "textures/gui/icons/compartment_icons.png");
    public static final ResourceLocation SAILING_ICONS = ResourceLocation.fromNamespaceAndPath(AlekiShips.MOD_ID,
            "textures/gui/icons/sailing_icons.png");
    public static final ResourceLocation SPEEDOMETER_ICONS = ResourceLocation.fromNamespaceAndPath(AlekiShips.MOD_ID,
            "textures/gui/icons/speedometer_icons.png");

    public static final Component PRESS_BUTTON = Component.translatable("press_button");
    public static final Component EJECT_PASSENGERS = Component.translatable("eject_passengers");
    public static final int MAGIC_STRING_COLOR = 16777215;
    private static final ItemStack FLINT_AND_STEEL = new ItemStack(Items.FLINT_AND_STEEL);

    public static void registerOverlays(final RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.CROSSHAIR, COMPARTMENT_STATUS, IngameOverlays::renderCompartmentStatus);
        event.registerAbove(VanillaGuiLayers.CROSSHAIR, PASSENGER_STATUS, IngameOverlays::renderPassengerStatus);
        event.registerAbove(VanillaGuiLayers.CROSSHAIR, SLOOP_CONSTRUCTION,
                IngameOverlays::renderSloopConstructionStatus);
        event.registerAbove(VanillaGuiLayers.CROSSHAIR, CANNON_LOAD_STATE, IngameOverlays::renderCannonLoadState);
        event.registerAbove(VanillaGuiLayers.HOTBAR, SAILING_ELEMENT, IngameOverlays::renderSailingElement);
    }

    private static void renderCompartmentStatus(final GuiGraphics guiGraphics, final DeltaTracker deltaTracker) {
        final Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player == null) return;
        if (minecraft.gameMode == null) return;

        final Player player = minecraft.player;

        if (minecraft.gameMode.getPlayerMode() == GameType.SPECTATOR || !minecraft.options.getCameraType()
                .isFirstPerson()) return;

        final Entity entity = CommonHelper.getEntity(minecraft.hitResult);

        if (entity instanceof IHaveIcons) {
            final PoseStack stack = guiGraphics.pose();

            stack.pushPose();

            stack.scale(1, 1, 1);

            final int width = guiGraphics.guiWidth();
            final int height = guiGraphics.guiHeight();
            stack.translate(width / 2F - 5 - 12, height / 2F - 5, 0);

            if (height % 2 != 0) {
                stack.translate(0, 0.5F, 0);
            }

            if (width % 2 != 0) {
                stack.translate(0.5F, 0, 0);
            }

            ArrayList<IconState> states = ((IHaveIcons) entity).getIconStates(player);

            for (int i = 0; i < states.size(); i++) {
                IconState state = states.get(i);
                int offset = -12 * i;

                switch (state) {
                    case NONE -> {

                    }
                    case HELM -> {
                        guiGraphics.blit(COMPARTMENT_ICONS, offset, 0, Icon.offset(Icon.HELM), 0, 9, 9);
                    }
                    case BLOCK -> {
                        guiGraphics.blit(COMPARTMENT_ICONS, offset, 0, Icon.offset(Icon.BLOCK), 0, 9, 9);
                    }
                    case SAIL_ARROW_UP -> {
                        guiGraphics.blit(COMPARTMENT_ICONS, offset, 0, Icon.offset(Icon.SAIL), 0, 9, 9);
                        guiGraphics.blit(COMPARTMENT_ICONS, offset, -10, Icon.offset(Icon.ARROW_UP), 0, 9, 9);
                    }
                    case SAIL_ARROW_DOWN -> {
                        guiGraphics.blit(COMPARTMENT_ICONS, offset, 0, Icon.offset(Icon.SAIL), 0, 9, 9);
                        guiGraphics.blit(COMPARTMENT_ICONS, offset, 10, Icon.offset(Icon.ARROW_DOWN), 0, 9, 9);
                    }
                    case PADDLE -> {
                        guiGraphics.blit(COMPARTMENT_ICONS, offset, 0, Icon.offset(Icon.PADDLE), 0, 9, 9);
                    }
                    case SEAT -> {
                        guiGraphics.blit(COMPARTMENT_ICONS, offset, 0, Icon.offset(Icon.SEAT), 0, 9, 9);
                    }
                    case EJECT -> {
                        guiGraphics.blit(COMPARTMENT_ICONS, offset, 0, Icon.offset(Icon.EJECT), 0, 9, 9);
                    }
                    case LEAD -> {
                        guiGraphics.blit(COMPARTMENT_ICONS, offset, 0, Icon.offset(Icon.LEAD), 0, 9, 9);
                    }
                    case ANCHOR_ARROW_UP -> {
                        guiGraphics.blit(COMPARTMENT_ICONS, offset, 0, Icon.offset(Icon.ANCHOR), 0, 9, 9);
                        guiGraphics.blit(COMPARTMENT_ICONS, offset, -10, Icon.offset(Icon.ARROW_UP), 0, 9, 9);
                    }
                    case ANCHOR_ARROW_DOWN -> {
                        guiGraphics.blit(COMPARTMENT_ICONS, offset, 0, Icon.offset(Icon.ANCHOR), 0, 9, 9);
                        guiGraphics.blit(COMPARTMENT_ICONS, offset, 10, Icon.offset(Icon.ARROW_DOWN), 0, 9, 9);
                    }
                    case BRUSH -> {
                        guiGraphics.blit(COMPARTMENT_ICONS, offset, 0, Icon.offset(Icon.BRUSH), 0, 9, 9);
                    }
                    case HAMMER -> {
                        guiGraphics.blit(COMPARTMENT_ICONS, offset, 0, Icon.offset(Icon.HAMMER), 0, 9, 9);
                    }
                }
            }

            stack.popPose();
        }

    }

    private static void renderPassengerStatus(final GuiGraphics guiGraphics, final DeltaTracker deltaTracker) {
        final Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) return;
        if (minecraft.gameMode == null) return;

        if (minecraft.gameMode.getPlayerMode() != GameType.SPECTATOR || !minecraft.options.getCameraType()
                .isFirstPerson()) return;

        final Entity entity = CommonHelper.getEntity(minecraft.hitResult);

        if (entity == null) return;

        final PoseStack stack = guiGraphics.pose();

        // Targeted entity isn't riding an Empty Compartment
        if (!(entity.getVehicle() instanceof EmptyCompartmentEntity)) {
            // Targeted entity isn't an empty compartment with a rider
            if (!(entity instanceof EmptyCompartmentEntity compartment) || !compartment.hasPassenger(e -> true)) {
                return;
            }
        }

        // The passenger of the compartment isn't living
        if ((!(entity instanceof EmptyCompartmentEntity) && !(entity instanceof LivingEntity)) || (entity instanceof ArmorStand)) {
            return;
        }
        // The passenger isn't a stupid gd armor stand
        if (entity instanceof EmptyCompartmentEntity compartment && compartment.getFirstPassenger() instanceof ArmorStand) {
            return;
        }

        stack.pushPose();

        stack.translate(guiGraphics.guiWidth() / 2F, guiGraphics.guiHeight() / 2F - 15, 0);
        stack.scale(1, 1, 1);

        // Should look like: Press Left Shift + Right Button to eject
        final String string = PRESS_BUTTON.getString() + " " + minecraft.options.keyShift.getTranslatedKeyMessage()
                .getString() + " + " + minecraft.options.keyUse.getTranslatedKeyMessage()
                .getString() + " " + EJECT_PASSENGERS.getString();

        guiGraphics.drawString(minecraft.font, string, -minecraft.font.width(string) / 2, 0, Color.WHITE.getRGB(),
                true);

        stack.popPose();
    }

    private static void renderSloopConstructionStatus(final GuiGraphics guiGraphics, final DeltaTracker deltaTracker) {
        final Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player == null) return;
        if (minecraft.gameMode == null) return;

        if (minecraft.gameMode.getPlayerMode() == GameType.SPECTATOR || !minecraft.options.getCameraType()
                .isFirstPerson()) return;

        final Entity entity = CommonHelper.getEntity(minecraft.hitResult);

        if (!(entity instanceof ConstructionEntity constructionEntity) || !(constructionEntity.getRootVehicle() instanceof SloopUnderConstructionEntity sloop))
            return;

        final var width = guiGraphics.guiWidth();
        final var height = guiGraphics.guiHeight();

        final PoseStack stack = guiGraphics.pose();
        stack.pushPose();
        stack.translate(width / 2F, height / 2F - 15, 0);
        stack.scale(1, 1, 1);

        // TODO We should cycle through the valid stacks
        final ItemStack itemStack = sloop.getRequiredItems()[0];

        if (!itemStack.isEmpty()) {
            guiGraphics.renderFakeItem(itemStack, 0, 0);
            guiGraphics.renderItemDecorations(minecraft.font, itemStack, 0, 0);
        }

        stack.popPose();
    }

    // TODO this should be done via the eventual icon renderer system
    private static void renderCannonLoadState(final GuiGraphics guiGraphics, final DeltaTracker deltaTracker) {
        final Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player == null) return;
        if (minecraft.gameMode == null) return;

        if (minecraft.gameMode.getPlayerMode() == GameType.SPECTATOR || !minecraft.options.getCameraType()
                .isFirstPerson()) return;

        final Entity entity = CommonHelper.getEntity(minecraft.hitResult);

        if (!(entity instanceof CannonEntity cannon)) return;

        if (cannon.isLit()) return;

        final PoseStack stack = guiGraphics.pose();

        stack.pushPose();
        stack.translate(guiGraphics.guiWidth() / 2F, guiGraphics.guiHeight() / 2F - 15, 0);
        stack.scale(1, 1, 1);

        if (cannon.isLoaded()) {
            guiGraphics.renderFakeItem(FLINT_AND_STEEL, 0, 0);
        } else {
            // TODO We should cycle through the valid stacks
            final ItemStack[] requiredItems = cannon.getRequiredItems();
            guiGraphics.renderFakeItem(requiredItems[0], 0, 0);
        }

        stack.popPose();
    }

    private static void renderSailingElement(final GuiGraphics guiGraphics, final DeltaTracker deltaTracker) {
        final Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player == null) return;
        if (minecraft.gameMode == null) return;

        if (minecraft.gameMode.getPlayerMode() != GameType.SPECTATOR) return;

        final Minecraft mc = minecraft;

        if (!(mc.player.getRootVehicle() instanceof SloopEntity sloopEntity)) return;

        if (sloopEntity.getPilotCompartment() == null) return;
        if (!sloopEntity.getPilotCompartment().hasExactlyOnePlayerPassenger()) return;
        if (sloopEntity.getPilotCompartment().getFirstPassenger() != mc.player) return;

        final PoseStack stack = guiGraphics.pose();
        stack.pushPose();

        stack.scale(1, 1, 1);

        final int x = guiGraphics.guiWidth() / 2;
        final int y = guiGraphics.guiHeight() - minecraft.gui.rightHeight;

        stack.translate(x + 1, y + 4, 0);
        if (guiGraphics.guiHeight() % 2 != 0) {
            stack.translate(0, 0.5F, 0);
        }
        if (guiGraphics.guiWidth() % 2 != 0) {
            stack.translate(0.5f, 0, 0);
        }

        final double smoothSpeedMS = sloopEntity.getSmoothSpeedMS();
        final String displayBoatSpeed = new DecimalFormat("###.#").format(smoothSpeedMS * 3.6) + " km/h";

        final int frameIndex;
        {
            final int windSpeed = Mth.clamp((int) (sloopEntity.getLocalWindSpeed() * 160), 1, 20);
            final int ticksBetweenFrames = Mth.clamp(Math.abs(windSpeed - 20), 1, 20);
            final int ticks = sloopEntity.tickCount / ticksBetweenFrames;
            frameIndex = ticks % 32;
        }

        final int offhandOffset = !mc.player.getOffhandItem().isEmpty() ? 26 : 3;

        // TODO config to add numerical speed instead, config for units
        if (mc.gui.getDebugOverlay().showDebugScreen()) {
            guiGraphics.drawString(mc.font, displayBoatSpeed, -134, -8 - offhandOffset, Color.WHITE.getRGB(), true);
        }

        final int angle;
        {
            int deferredAngle = Math.round((Mth.wrapDegrees(sloopEntity.getWindLocalRotation()) / 360) * 64) + 32;
            if (deferredAngle == 64) {
                deferredAngle = 0;
            }
            angle = deferredAngle;
        }

        final int speedometerIndex = Mth.clamp((int) (smoothSpeedMS - 2) * 2, 0, 31);

        guiGraphics.blit(SAILING_ICONS, -126, 3 - offhandOffset, 32 * angle, 32 * (angle / 8), 32, 32);
        guiGraphics.blit(SPEEDOMETER_ICONS, -126 - 8, 3 - offhandOffset + 32 - 16, 16 * speedometerIndex,
                16 * (speedometerIndex / 16), 16, 16);
        guiGraphics.blit(SPEEDOMETER_ICONS, -126 - 8, 3 - offhandOffset, 16 * frameIndex, 32 + 16 * (frameIndex / 16),
                16, 16);
        stack.popPose();
    }

    public enum Icon {
        HELM(0),
        BLOCK(1),
        SAIL(2),
        PADDLE(3),
        SEAT(4),
        EJECT(5),
        LEAD(6),
        ARROW_UP(7),
        ARROW_DOWN(8),
        ANCHOR(9),
        BRUSH(10),
        HAMMER(11);

        public final int index;

        Icon(final int index) {
            this.index = index;
        }

        public static int offset(final Icon icon) {
            return icon.index * 9;
        }
    }

    public enum IconState {
        NONE,
        HELM,
        BLOCK,
        SAIL_ARROW_UP,
        SAIL_ARROW_DOWN,
        PADDLE,
        SEAT,
        EJECT,
        LEAD,
        ANCHOR_ARROW_UP,
        ANCHOR_ARROW_DOWN,
        BRUSH,
        HAMMER,
    }
}