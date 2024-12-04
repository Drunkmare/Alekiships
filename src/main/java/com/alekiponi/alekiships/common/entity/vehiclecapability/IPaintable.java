package com.alekiponi.alekiships.common.entity.vehiclecapability;

import java.util.Optional;
import com.alekiponi.alekiships.common.entity.vehicle.AbstractAlekiBoatEntity;
import javax.annotation.Nullable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;

import static com.alekiponi.alekiships.util.advancements.AlekiShipsAdvancements.*;

public interface IPaintable {
    void clearPaint();
    void setPaintColor(final DyeColor paintColor);

    @Nullable
    default InteractionResult interactPaint(final Player player, final InteractionHand hand){
        final ItemStack heldItem = player.getItemInHand(hand);
        if (heldItem.is(Tags.Items.DYES)) {
            final DyeColor dyeColor = DyeColor.getColor(heldItem);
            if (dyeColor != null) {
                final Optional<DyeColor> paintColor = this.getPaintColor();
                if (paintColor.isEmpty() || paintColor.get() != dyeColor) {
                    heldItem.shrink(1);
                    this.setPaintColor(dyeColor);
                    player.swing(hand);
                    if (this instanceof AbstractAlekiBoatEntity boat) {
                        checkDyeShipBlack(player, boat);
                    }
                    return InteractionResult.SUCCESS;
                }
            }
        }

        if (heldItem.is(Items.WATER_BUCKET)) {
            this.clearPaint();
            player.swing(hand);

            if (!player.getAbilities().instabuild)
            {
                heldItem.setCount(0);
                player.setItemInHand(hand, Items.BUCKET.getDefaultInstance());
            }

            return InteractionResult.SUCCESS;
        }

        return null;
    }

    /**
     * @return The paint color of the boat
     */
    Optional<DyeColor> getPaintColor();

}
