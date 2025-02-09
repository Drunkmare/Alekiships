package com.alekiponi.alekiships.client.render.icon.vehicle;

import com.alekiponi.alekiships.client.icon.Icon;
import com.alekiponi.alekiships.client.render.icon.IconRendererProvider;
import com.alekiponi.alekiships.client.render.icon.SingleIconRenderer;
import com.alekiponi.alekiships.common.entity.vehicle.SloopEntity;
import com.alekiponi.alekiships.util.AlekiShipsTags;

import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import net.neoforged.neoforge.common.Tags;

import java.util.Objects;
import java.util.Optional;

public class SloopIconRenderer extends SingleIconRenderer<SloopEntity> {

    public SloopIconRenderer(final IconRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected Icon getCurrentIcon(final SloopEntity entity, final ItemStack heldStack, final float partialTick) {
        if (heldStack.is(AlekiShipsTags.Items.ICEBREAKER_UPGRADES) && !entity.breaksIce()) {
            return Icon.DefaultIcons.HAMMER;
        }

        if (heldStack.is(Items.NAME_TAG)) {
            return Icon.DefaultIcons.BRUSH;
        }

        if (heldStack.is(entity.getDropItem()) && entity.getDamage() > 0) {
            return Icon.DefaultIcons.HAMMER;
        }

        if (heldStack.is(Tags.Items.DYES)) {
            final Optional<DyeColor> paintColor = entity.getPaintColor();
            if (paintColor.isEmpty() || !Objects.equals(DyeColor.getColor(heldStack), paintColor.get())) {
                return Icon.DefaultIcons.BRUSH;
            }
        }

        if (heldStack.is(Items.WATER_BUCKET)) {
            if (entity.getPaintColor().isPresent()) {
                return Icon.DefaultIcons.BRUSH;
            }
        }

        return Icon.NONE;
    }
}