package com.alekiponi.alekiships.compat.waila.ui;

import com.mojang.math.Axis;
import snownee.jade.api.ui.Element;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;

import net.neoforged.neoforge.client.model.data.ModelData;

import lombok.AllArgsConstructor;

/**
 * A Jade UI Element which renders a BlockState
 */
@AllArgsConstructor
public final class BlockStateElement extends Element {

    // This is carefully chosen to visually match the standard Block Item model when rendered (so it appears seamless)
    private static final float SCALE_CONSTANT = 9 + 0.625F;
    private final BlockState blockState;
    private final ModelData modelData;

    @Override
    public Vec2 getSize() {
        return new Vec2(18, 18);
    }

    @Override
    public void render(final GuiGraphics guiGraphics, final float x, final float y, final float maxX,
            final float maxY) {
        final var pose = guiGraphics.pose();
        pose.pushPose();
        pose.translate(x + 2, y + 14, 10);
        pose.scale(SCALE_CONSTANT, -SCALE_CONSTANT, SCALE_CONSTANT);
        pose.mulPose(Axis.XP.rotationDegrees(30));
        pose.mulPose(Axis.YP.rotationDegrees(45));
        Minecraft.getInstance()
                .getBlockRenderer()
                .renderSingleBlock(this.blockState, pose, guiGraphics.bufferSource(), LightTexture.FULL_BLOCK,
                        OverlayTexture.NO_OVERLAY, this.modelData, null);
        pose.popPose();
    }
}