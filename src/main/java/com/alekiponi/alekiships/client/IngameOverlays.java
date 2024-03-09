//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.alekiponi.alekiships.client;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.common.entity.CannonEntity;
import com.alekiponi.alekiships.common.entity.vehicle.AbstractAlekiBoatEntity;
import com.alekiponi.alekiships.common.entity.vehicle.SloopEntity;
import com.alekiponi.alekiships.common.entity.vehicle.SloopUnderConstructionEntity;
import com.alekiponi.alekiships.common.entity.vehiclehelper.*;
import com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.EmptyCompartmentEntity;
import com.alekiponi.alekiships.common.item.AlekiShipsItems;
import com.alekiponi.alekiships.util.AlekiShipsHelper;
import com.alekiponi.alekiships.util.AlekiShipsTags;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.common.Tags;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.Locale;

public enum IngameOverlays {
    COMPARTMENT_STATUS(IngameOverlays::renderCompartmentStatus),
    VEHICLE_STATUS(IngameOverlays::renderVehicleStatus),
    PASSENGER_STATUS(IngameOverlays::renderPassengerStatus),
    SAILING_ELEMENT(IngameOverlays::renderSailingElement),
    SLOOP_CONSTRUCTION(IngameOverlays::renderSloopConstructionStatus),
    CANNON_LOAD_STATE(IngameOverlays::renderCannonLoadState);

    public static final ResourceLocation COMPARTMENT_ICONS = new ResourceLocation(AlekiShips.MOD_ID,
            "textures/gui/icons/compartment_icons.png");
    public static final ResourceLocation SAILING_ICONS = new ResourceLocation(AlekiShips.MOD_ID,
            "textures/gui/icons/sailing_icons.png");
    public static final ResourceLocation SPEEDOMETER_ICONS = new ResourceLocation(AlekiShips.MOD_ID,
            "textures/gui/icons/speedometer_icons.png");
    public static final Component PRESS_BUTTON = Component.translatable("press_button");
    public static final Component EJECT_PASSENGERS = Component.translatable("eject_passengers");
    private static final ItemStack FLINT_AND_STEEL = new ItemStack(Items.FLINT_AND_STEEL);
    private final IGuiOverlay overlay;
    private final String id;

    IngameOverlays(IGuiOverlay overlay) {
        this.id = this.name().toLowerCase(Locale.ROOT);
        this.overlay = overlay;
    }

    public static void registerOverlays(RegisterGuiOverlaysEvent event) {
        above(event, VanillaGuiOverlay.CROSSHAIR, COMPARTMENT_STATUS);
        above(event, VanillaGuiOverlay.CROSSHAIR, VEHICLE_STATUS);
        above(event, VanillaGuiOverlay.CROSSHAIR, PASSENGER_STATUS);
        above(event, VanillaGuiOverlay.CROSSHAIR, SLOOP_CONSTRUCTION);
        above(event, VanillaGuiOverlay.CROSSHAIR, CANNON_LOAD_STATE);
        above(event, VanillaGuiOverlay.HOTBAR, SAILING_ELEMENT);
    }

    private static void above(RegisterGuiOverlaysEvent event, VanillaGuiOverlay vanilla, IngameOverlays overlay) {
        event.registerAbove(vanilla.id(), overlay.id, overlay.overlay);
    }

    private static void renderPassengerStatus(final ForgeGui gui, final GuiGraphics graphics, final float partialTick,
            final int width, final int height) {
        final Minecraft mc = gui.getMinecraft();

        if (mc.player == null) return;

        if (!setup(gui, mc) || mc.player.isSpectator() || !mc.options.getCameraType().isFirstPerson()) return;

        final Entity entity = AlekiShipsHelper.getEntity(mc.hitResult);

        if (entity == null) return;

        final PoseStack stack = graphics.pose();

        // Targeted entity isn't riding an Empty Compartment
        if (!(entity.getVehicle() instanceof EmptyCompartmentEntity)) {
            // Targeted entity isn't an empty compartment with a rider
            if (!(entity instanceof EmptyCompartmentEntity compartment) || !compartment.hasPassenger(e -> true)) return;
        }

        stack.pushPose();

        stack.translate(width / 2F, height / 2F - 15, 0);
        stack.scale(1, 1, 1);

        // Should look like: Press Left Shift + Right Button to eject
        final String string = PRESS_BUTTON.getString() +
                " " +
                mc.options.keyShift.getTranslatedKeyMessage().getString() +
                " + " +
                mc.options.keyUse.getTranslatedKeyMessage().getString() +
                " " +
                EJECT_PASSENGERS.getString();

        graphics.drawString(mc.font, string, -mc.font.width(string) / 2, 0, Color.WHITE.getRGB(), true);

        stack.popPose();
    }

    private static void renderSloopConstructionStatus(final ForgeGui gui, final GuiGraphics graphics,
            final float partialTick, final int width, final int height) {
        final Minecraft mc = gui.getMinecraft();

        if (mc.player == null) return;

        if (!setup(gui, mc) || mc.player.isSpectator() || !mc.options.getCameraType().isFirstPerson()) return;

        final Entity entity = AlekiShipsHelper.getEntity(mc.hitResult);

        if (!(entity instanceof ConstructionEntity constructionEntity) || !(constructionEntity.getRootVehicle() instanceof SloopUnderConstructionEntity sloop))
            return;

        final PoseStack stack = graphics.pose();
        stack.pushPose();
        stack.translate(width / 2F, height / 2F - 15, 0);
        stack.scale(1, 1, 1);

        final ItemStack itemStack = new ItemStack(sloop.getCurrentRequiredItem(), sloop.getNumberItemsLeft());

        graphics.renderFakeItem(itemStack, 0, 0);
        if (itemStack.getCount() != 1) {
            stack.pushPose();
            final String countString = String.valueOf(itemStack.getCount());
            stack.translate(0, 0, 200);
            graphics.drawString(mc.font, countString, 19 - 2 - mc.font.width(countString), 6 + 3, 16777215, true);
            stack.popPose();
        }

        stack.popPose();
    }

    private static void renderCannonLoadState(final ForgeGui gui, final GuiGraphics graphics, final float partialTick,
            final int width, final int height) {
        final Minecraft mc = gui.getMinecraft();

        if (mc.player == null) return;

        if (!setup(gui, mc) || mc.player.isSpectator() || !mc.options.getCameraType().isFirstPerson()) return;

        final Entity entity = AlekiShipsHelper.getEntity(mc.hitResult);

        if (!(entity instanceof CannonEntity cannon)) return;

        if (cannon.isLit()) return;

        final PoseStack stack = graphics.pose();

        stack.pushPose();
        stack.translate(width / 2F, height / 2F - 15, 0);
        stack.scale(1, 1, 1);

        if (cannon.isLoaded()) {
            graphics.renderFakeItem(FLINT_AND_STEEL, 0, 0);
        } else {
            graphics.renderFakeItem(cannon.nextRequiredItem(), 0, 0);
        }

        stack.popPose();
    }

    private static void renderSailingElement(final ForgeGui gui, final GuiGraphics graphics, final float partialTick,
            final int width, final int height) {
        final Minecraft mc = gui.getMinecraft();

        if (mc.player == null) return;

        if (!setup(gui, mc) || mc.player.isSpectator()) return;

        if (!(mc.player.getRootVehicle() instanceof SloopEntity sloopEntity)) return;

        if (sloopEntity.getControllingCompartment() == null) return;
        if (!sloopEntity.getControllingCompartment().hasExactlyOnePlayerPassenger()) return;
        if (sloopEntity.getControllingCompartment().getFirstPassenger() != mc.player) return;

        final PoseStack stack = graphics.pose();
        stack.pushPose();

        stack.scale(1, 1, 1);

        final int x = width / 2;
        final int y = height - gui.rightHeight;

        stack.translate(x + 1, y + 4, 0);
        if (height % 2 != 0) {
            stack.translate(0, 0.5F, 0);
        }
        if (width % 2 != 0) {
            stack.translate(0.5f, 0, 0);
        }

        final double smoothSpeedMS = sloopEntity.getSmoothSpeedMS();
        final String displayBoatSpeed = new DecimalFormat("###.#").format(smoothSpeedMS * 3.6) + " km/h";

        final int frameIndex;
        {
            final int windSpeed = Mth.clamp((int) (sloopEntity.getLocalWindAngleAndSpeed()[1] * 160), 1, 20);
            final int ticksBetweenFrames = Mth.clamp(Math.abs(windSpeed - 20), 1, 20);
            final int ticks = sloopEntity.tickCount / ticksBetweenFrames;
            frameIndex = ticks % 32;
        }

        final int offhandOffset = !mc.player.getOffhandItem().isEmpty() ? 26 : 3;

        // TODO config to add numerical speed instead, config for units
        if (mc.options.renderDebug) {
            graphics.drawString(mc.font, displayBoatSpeed, -134, -8 - offhandOffset, Color.WHITE.getRGB(), true);
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

        graphics.blit(SAILING_ICONS, -126, 3 - offhandOffset, 32 * angle, 32 * (angle / 8), 32, 32);
        graphics.blit(SPEEDOMETER_ICONS, -126 - 8, 3 - offhandOffset + 32 - 16, 16 * speedometerIndex,
                16 * (speedometerIndex / 16), 16, 16);
        graphics.blit(SPEEDOMETER_ICONS, -126 - 8, 3 - offhandOffset, 16 * frameIndex,
                32 + 16 * (frameIndex / 16), 16, 16);
        stack.popPose();
    }

    private static void renderCompartmentStatus(final ForgeGui gui, final GuiGraphics graphics, final float partialTick,
            final int width, final int height) {
        final Minecraft mc = gui.getMinecraft();

        if (mc.player == null) return;

        final Player player = mc.player;

        if (!setup(gui, mc) || player.isSpectator() || !mc.options.getCameraType().isFirstPerson()) return;

        final Entity entity = AlekiShipsHelper.getEntity(mc.hitResult);

        final PoseStack stack = graphics.pose();

        stack.pushPose();

        stack.scale(1, 1, 1);
        stack.translate(width / 2F - 5 - 12, height / 2F - 5, 0);

        if (height % 2 != 0) {
            stack.translate(0, 0.5F, 0);
        }

        if (width % 2 != 0) {
            stack.translate(0.5F, 0, 0);
        }

        if (entity instanceof EmptyCompartmentEntity emptyCompartmentEntity && emptyCompartmentEntity.isPassenger() && !emptyCompartmentEntity.isVehicle()) {
            if (emptyCompartmentEntity.getTrueVehicle() != null && emptyCompartmentEntity.getTrueVehicle()
                    .getPilotVehiclePartAsEntity() != null) {
                if (emptyCompartmentEntity.getTrueVehicle().getPilotVehiclePartAsEntity().getFirstPassenger()
                        .is(emptyCompartmentEntity)) {
                    graphics.blit(COMPARTMENT_ICONS, 0, 0, CompIcon.iconOffset(CompIcon.HELM), 0, 9, 9);
                    if (emptyCompartmentEntity.getTrueVehicle()
                            .pilotCompartmentAcceptsNonPlayers() && player.getItemInHand(
                            player.getUsedItemHand()).is(AlekiShipsTags.Items.CAN_PLACE_IN_COMPARTMENTS)) {
                        graphics.blit(COMPARTMENT_ICONS, -12, 0, CompIcon.iconOffset(CompIcon.BLOCK), 0, 9, 9);
                    }

                } else if (player.getItemInHand(player.getUsedItemHand())
                        .is(AlekiShipsTags.Items.CAN_PLACE_IN_COMPARTMENTS)) {
                    graphics.blit(COMPARTMENT_ICONS, 0, 0, CompIcon.iconOffset(CompIcon.BLOCK), 0, 9, 9);
                } else if (player.getItemInHand(player.getUsedItemHand())
                        .is(AlekiShipsItems.CANNON.get()) && !emptyCompartmentEntity.canAddOnlyBLocks() && emptyCompartmentEntity.canAddCannons() && emptyCompartmentEntity.getRootVehicle() instanceof SloopEntity) {
                    graphics.blit(COMPARTMENT_ICONS, 0, 0, CompIcon.iconOffset(CompIcon.BLOCK), 0, 9, 9);
                } else if (!emptyCompartmentEntity.isVehicle() && !emptyCompartmentEntity.canAddOnlyBLocks()) {
                    graphics.blit(COMPARTMENT_ICONS, 0, 0, CompIcon.iconOffset(CompIcon.SEAT), 0, 9, 9);
                } else if (!emptyCompartmentEntity.isVehicle() && emptyCompartmentEntity.canAddOnlyBLocks()) {
                    graphics.blit(COMPARTMENT_ICONS, 0, 0, CompIcon.iconOffset(CompIcon.BLOCK), 0, 9, 9);
                }
            }
        } else if (entity instanceof VehicleCleatEntity vehicleCleatEntity && vehicleCleatEntity.isPassenger() && !vehicleCleatEntity.isLeashed()) {
            if (vehicleCleatEntity.getVehicle().getVehicle() != null) {
                graphics.blit(COMPARTMENT_ICONS, 0, 0, 54, 0, 9, 9);
            }
        } else if (entity instanceof SailSwitchEntity sailSwitch && sailSwitch.isPassenger()) {
            boolean flag = false;
            for (ItemStack item : player.getHandSlots()) {
                if (item.is(Tags.Items.DYES) || item.is(Items.WATER_BUCKET)) {
                    graphics.blit(COMPARTMENT_ICONS, 0, 0, CompIcon.iconOffset(CompIcon.BRUSH), 0, 9, 9);
                    flag = true;
                    break;
                }
            }

            if (sailSwitch.getVehicle().getVehicle() != null && !flag) {
                if (sailSwitch.getSwitched()) {
                    graphics.blit(COMPARTMENT_ICONS, 0, 0, CompIcon.iconOffset(CompIcon.SAIL), 0, 9, 9);
                    graphics.blit(COMPARTMENT_ICONS, 0, 10, CompIcon.iconOffset(CompIcon.ARROW_DOWN), 0, 9, 9);
                } else {
                    graphics.blit(COMPARTMENT_ICONS, 0, 0, CompIcon.iconOffset(CompIcon.SAIL), 0, 9, 9);
                    graphics.blit(COMPARTMENT_ICONS, 0, -10, CompIcon.iconOffset(CompIcon.ARROW_UP), 0, 9, 9);
                }
            }
        } else if (entity instanceof WindlassSwitchEntity windlassSwitch && windlassSwitch.isPassenger()) {
            if (windlassSwitch.getVehicle().getVehicle() != null) {
                if (!windlassSwitch.getSwitched()) {
                    graphics.blit(COMPARTMENT_ICONS, 0, 0, CompIcon.iconOffset(CompIcon.ANCHOR), 0, 9, 9);
                    graphics.blit(COMPARTMENT_ICONS, 0, 10, CompIcon.iconOffset(CompIcon.ARROW_DOWN), 0, 9, 9);
                } else {
                    graphics.blit(COMPARTMENT_ICONS, 0, 0, CompIcon.iconOffset(CompIcon.ANCHOR), 0, 9, 9);
                    graphics.blit(COMPARTMENT_ICONS, 0, -10, CompIcon.iconOffset(CompIcon.ARROW_UP), 0, 9, 9);
                }
            }
        }

        stack.popPose();
    }

    private static void renderVehicleStatus(final ForgeGui gui, final GuiGraphics graphics, final float partialTick,
            final int width, final int height) {
        final Minecraft mc = gui.getMinecraft();

        if (mc.player == null) return;

        final Player player = mc.player;

        if (!setup(gui, mc) || player.isSpectator() || !mc.options.getCameraType().isFirstPerson()) return;

        final AbstractAlekiBoatEntity vehicle;
        {
            final Entity entity = AlekiShipsHelper.getEntity(mc.hitResult);

            if (entity instanceof AbstractAlekiBoatEntity) {
                vehicle = (AbstractAlekiBoatEntity) entity;
            } else if (entity instanceof VehicleCollisionEntity collisionEntity && collisionEntity.getRootVehicle() instanceof AbstractAlekiBoatEntity e) {
                vehicle = e;
            } else return;
        }

        if (vehicle.isTiny()) return;

        final PoseStack stack = graphics.pose();

        stack.pushPose();

        stack.scale(1, 1, 1);
        stack.translate(width / 2F - 5 - 12, height / 2F - 5, 0);

        if (height % 2 != 0) {
            stack.translate(0, 0.5F, 0);
        }

        if (width % 2 != 0) {
            stack.translate(0.5F, 0, 0);
        }

        for (final ItemStack itemStack : player.getHandSlots()) {
            if (itemStack.is(vehicle.getDropItem())) {
                graphics.blit(COMPARTMENT_ICONS, 0, 0, CompIcon.iconOffset(CompIcon.HAMMER), 0, 9, 9);
                return;
            }

            if (itemStack.is(Tags.Items.DYES) || itemStack.is(Items.WATER_BUCKET)) {
                graphics.blit(COMPARTMENT_ICONS, 0, 0, CompIcon.iconOffset(CompIcon.BRUSH), 0, 9, 9);
                return;
            }
        }
    }

    public static boolean setup(ForgeGui gui, Minecraft minecraft) {
        if (!minecraft.options.hideGui && minecraft.getCameraEntity() instanceof Player) {
            gui.setupOverlayRenderState(true, false);
            return true;
        } else {
            return false;
        }
    }

    public enum CompIcon {
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

        CompIcon(final int index) {
            this.index = index;
        }

        public static int iconOffset(final CompIcon icon) {
            return icon.index * 9;
        }
    }
}
