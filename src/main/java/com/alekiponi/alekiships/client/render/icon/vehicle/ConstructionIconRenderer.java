package com.alekiponi.alekiships.client.render.icon.vehicle;

import com.alekiponi.alekiships.client.icon.Icon;
import com.alekiponi.alekiships.client.render.icon.IconRendererProvider;
import com.alekiponi.alekiships.client.render.icon.SingleIconRenderer;
import com.alekiponi.alekiships.common.entity.vehiclehelper.ConstructionEntity;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

public class ConstructionIconRenderer extends SingleIconRenderer<ConstructionEntity> {
    private final Font font;
    private float time = 0;

    public ConstructionIconRenderer(final IconRendererProvider.Context context) {
        super(context);
        this.font = context.font();
    }

    @Override
    protected Icon getCurrentIcon(final ConstructionEntity entity, final ItemStack heldStack, final float partialTick) {
        if (!Screen.hasControlDown()) this.time += partialTick;

        final var requiredItems = entity.getRequiredItems();

        final var itemStack = requiredItems.length == 0 ? ItemStack.EMPTY : requiredItems[Mth.floor(
                this.time / 30) % requiredItems.length];

        if (!itemStack.isEmpty()) {
            return Icon.itemWithDecorations(this.font, itemStack);
        }

        return Icon.NONE;
    }

    @Override
    public void resetRenderer(final ConstructionEntity entity, final LocalPlayer player) {
        this.time = 0;
        super.resetRenderer(entity, player);
    }
}