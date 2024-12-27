package com.alekiponi.alekiships.client.render.icon;

import com.alekiponi.alekiships.client.icon.Icon;
import com.alekiponi.alekiships.common.entity.CannonEntity;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class CannonIconRenderer extends SingleIconRenderer<CannonEntity> {

    private float time = 0;

    public CannonIconRenderer(final IconRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected Icon getCurrentIcon(final CannonEntity entity, final ItemStack heldStack, final float partialTick) {
        if (!Screen.hasControlDown()) this.time += partialTick;

        if (entity.isLit()) return Icon.NONE;

        if (entity.isLoaded()) return Icon.item(Items.FLINT_AND_STEEL);

        final var requiredItems = entity.getRequiredItems();
        final var itemStack = requiredItems.length == 0 ? ItemStack.EMPTY : requiredItems[Mth.floor(
                this.time / 30) % requiredItems.length];

        return Icon.item(itemStack);
    }

    @Override
    public void resetRenderer(final CannonEntity entity, final LocalPlayer player) {
        this.time = 0;
        super.resetRenderer(entity, player);
    }
}