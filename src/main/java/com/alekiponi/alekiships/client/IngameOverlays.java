//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.alekiponi.alekiships.client;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.common.entity.CannonEntity;
import com.alekiponi.alekiships.common.entity.IHaveIcons;
import com.alekiponi.alekiships.common.entity.vehicle.SloopEntity;
import com.alekiponi.alekiships.common.entity.vehicle.SloopUnderConstructionEntity;
import com.alekiponi.alekiships.common.entity.vehiclehelper.*;
import com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.EmptyCompartmentEntity;
import com.alekiponi.alekiships.util.CommonHelper;
import com.mojang.blaze3d.vertex.PoseStack;
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
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Locale;

public enum IngameOverlays {
    COMPARTMENT_STATUS(IngameOverlays::renderEntityIcons),
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
    public static final int MAGIC_STRING_COLOR = 16777215;
    private final IGuiOverlay overlay;
    private final String id;

    IngameOverlays(final IGuiOverlay overlay) {
        this.id = this.name().toLowerCase(Locale.ROOT);
        this.overlay = overlay;
    }

    public static void registerOverlays(final RegisterGuiOverlaysEvent event) {
        above(event, VanillaGuiOverlay.CROSSHAIR, COMPARTMENT_STATUS);
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

        final Entity entity = CommonHelper.getEntity(mc.hitResult);

        if (entity == null) return;

        final PoseStack stack = graphics.pose();

        // Targeted entity isn't riding an Empty Compartment
        if (!(entity.getVehicle() instanceof EmptyCompartmentEntity)) {
            // Targeted entity isn't an empty compartment with a rider
            if (!(entity instanceof EmptyCompartmentEntity compartment) || !compartment.hasPassenger(e -> true)) {
                return;
            }
        }

        // The passenger of the compartment isn't living
        if ((!(entity instanceof EmptyCompartmentEntity) && !(entity instanceof LivingEntity)) || (entity instanceof ArmorStand)){
            return;
        }
        // The passenger isn't a stupid gd armor stand
        if(entity instanceof EmptyCompartmentEntity compartment && compartment.getFirstPassenger() instanceof ArmorStand){
            return;
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

        final Entity entity = CommonHelper.getEntity(mc.hitResult);

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
            graphics.drawString(mc.font, countString, 17 - mc.font.width(countString), 9, MAGIC_STRING_COLOR, true);
            stack.popPose();
        }

        stack.popPose();
    }

    private static void renderCannonLoadState(final ForgeGui gui, final GuiGraphics graphics, final float partialTick,
                                              final int width, final int height) {
        final Minecraft mc = gui.getMinecraft();

        if (mc.player == null) return;

        if (!setup(gui, mc) || mc.player.isSpectator() || !mc.options.getCameraType().isFirstPerson()) return;

        final Entity entity = CommonHelper.getEntity(mc.hitResult);

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

        if (sloopEntity.getPilotCompartment() == null) return;
        if (!sloopEntity.getPilotCompartment().hasExactlyOnePlayerPassenger()) return;
        if (sloopEntity.getPilotCompartment().getFirstPassenger() != mc.player) return;

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

    private static void renderEntityIcons(final ForgeGui gui, final GuiGraphics graphics, final float partialTick,
                                          final int width, final int height) {
        final Minecraft mc = gui.getMinecraft();

        if (mc.player == null) return;

        final Player player = mc.player;

        if (!setup(gui, mc) || player.isSpectator() || !mc.options.getCameraType().isFirstPerson()) return;

        final Entity entity = CommonHelper.getEntity(mc.hitResult);

        if (entity instanceof IHaveIcons) {
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

            ArrayList<IconState> states = ((IHaveIcons) entity).getIconStates(player);

            for (int i = 0; i < states.size(); i++) {
                IconState state = states.get(i);
                int offset = -12 * i;

                switch (state) {
                    case NONE -> {

                    }
                    case HELM -> {
                        graphics.blit(COMPARTMENT_ICONS, offset, 0, Icon.offset(Icon.HELM), 0, 9, 9);
                    }
                    case BLOCK -> {
                        graphics.blit(COMPARTMENT_ICONS, offset, 0, Icon.offset(Icon.BLOCK), 0, 9, 9);
                    }
                    case SAIL_ARROW_UP -> {
                        graphics.blit(COMPARTMENT_ICONS, offset, 0, Icon.offset(Icon.SAIL), 0, 9, 9);
                        graphics.blit(COMPARTMENT_ICONS, offset, -10, Icon.offset(Icon.ARROW_UP), 0, 9, 9);
                    }
                    case SAIL_ARROW_DOWN -> {
                        graphics.blit(COMPARTMENT_ICONS, offset, 0, Icon.offset(Icon.SAIL), 0, 9, 9);
                        graphics.blit(COMPARTMENT_ICONS, offset, 10, Icon.offset(Icon.ARROW_DOWN), 0, 9, 9);
                    }
                    case PADDLE -> {
                        graphics.blit(COMPARTMENT_ICONS, offset, 0, Icon.offset(Icon.PADDLE), 0, 9, 9);
                    }
                    case SEAT -> {
                        graphics.blit(COMPARTMENT_ICONS, offset, 0, Icon.offset(Icon.SEAT), 0, 9, 9);
                    }
                    case EJECT -> {
                        graphics.blit(COMPARTMENT_ICONS, offset, 0, Icon.offset(Icon.EJECT), 0, 9, 9);
                    }
                    case LEAD -> {
                        graphics.blit(COMPARTMENT_ICONS, offset, 0, Icon.offset(Icon.LEAD), 0, 9, 9);
                    }
                    case ANCHOR_ARROW_UP -> {
                        graphics.blit(COMPARTMENT_ICONS, offset, 0, Icon.offset(Icon.ANCHOR), 0, 9, 9);
                        graphics.blit(COMPARTMENT_ICONS, offset, -10, Icon.offset(Icon.ARROW_UP), 0, 9, 9);
                    }
                    case ANCHOR_ARROW_DOWN -> {
                        graphics.blit(COMPARTMENT_ICONS, offset, 0, Icon.offset(Icon.ANCHOR), 0, 9, 9);
                        graphics.blit(COMPARTMENT_ICONS, offset, 10, Icon.offset(Icon.ARROW_DOWN), 0, 9, 9);
                    }
                    case BRUSH -> {
                        graphics.blit(COMPARTMENT_ICONS, offset, 0, Icon.offset(Icon.BRUSH), 0, 9, 9);
                    }
                    case HAMMER -> {
                        graphics.blit(COMPARTMENT_ICONS, offset, 0, Icon.offset(Icon.HAMMER), 0, 9, 9);
                    }
                }
            }

            stack.popPose();
        }

    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean setup(final ForgeGui gui, final Minecraft minecraft) {
        if (!minecraft.options.hideGui && minecraft.getCameraEntity() instanceof Player) {
            gui.setupOverlayRenderState(true, false);
            return true;
        } else {
            return false;
        }
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
