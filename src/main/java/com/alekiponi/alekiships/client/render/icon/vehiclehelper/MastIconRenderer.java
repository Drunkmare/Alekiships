package com.alekiponi.alekiships.client.render.icon.vehiclehelper;

import com.alekiponi.alekiships.client.icon.Icon;
import com.alekiponi.alekiships.client.render.icon.IconRenderer;
import com.alekiponi.alekiships.client.render.icon.IconRendererProvider;
import com.alekiponi.alekiships.common.entity.vehiclehelper.MastEntity;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.ItemStack;

public class MastIconRenderer extends IconPassthroughRenderer<MastEntity> {

    public MastIconRenderer(final IconRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(final MastEntity entity, final LocalPlayer player, final GuiGraphics graphics,
            final float partialTick) {
        for (final ItemStack itemStack : player.getHandSlots()) {
            if (!(itemStack.getItem() instanceof BannerItem) || itemStack.is(entity.getBanner().getItem())) continue;

            IconRenderer.renderIcon(graphics, Icon.DefaultIcons.BRUSH, partialTick);
            return;
        }

        super.render(entity, player, graphics, partialTick);
    }
}