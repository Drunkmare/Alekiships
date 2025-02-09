package com.alekiponi.alekiships.common.entity.vehiclecapability;

import com.alekiponi.alekiships.util.AlekiShipsTags;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;

import java.util.Optional;

public interface IPaintable {

    /**
     * The interaction handler for paintable entities
     *
     * @param player          The player who's interacting
     * @param hand            The hand used to interact
     * @param paintableEntity The paintable entity
     *
     * @see InteractionResult#consumesAction()
     */
    static <E extends Entity & IPaintable> InteractionResult interactPaint(final Player player,
            final InteractionHand hand, final E paintableEntity) {
        return interactPaint(player, hand, paintableEntity.level(), paintableEntity.position(), paintableEntity);
    }

    /**
     * The full-fledged interaction handler
     *
     * @param player     The player who's interacting
     * @param hand       The hand used to interact
     * @param level      The level
     * @param position   The position
     * @param iPaintable The paintable
     *
     * @see InteractionResult#consumesAction()
     */
    static InteractionResult interactPaint(final Player player, final InteractionHand hand, final Level level,
            final Vec3 position, final IPaintable iPaintable) {
        final ItemStack heldItem = player.getItemInHand(hand);
        final Optional<DyeColor> paintColor = iPaintable.getPaintColor();

        if (heldItem.is(Tags.Items.DYES)) {
            final DyeColor dyeColor = DyeColor.getColor(heldItem);
            if (dyeColor != null) {
                if (paintColor.isEmpty() || paintColor.get() != dyeColor) {
                    heldItem.shrink(1);
                    level.playSound(player, position.x, position.y, position.z, SoundEvents.DYE_USE,
                            SoundSource.PLAYERS, 1, 1);
                    iPaintable.setPaintColor(dyeColor);
                    return InteractionResult.SUCCESS;
                }
            }
        }

        if (paintColor.isEmpty()) return InteractionResult.PASS;

        final IFluidHandlerItem fluidHandler = heldItem.getCapability(Capabilities.FluidHandler.ITEM);
        if (fluidHandler == null) return InteractionResult.PASS;

        for (int tankIndex = 0; tankIndex < fluidHandler.getTanks(); tankIndex++) {
            if (!fluidHandler.getFluidInTank(tankIndex).is(AlekiShipsTags.Fluids.PAINT_REMOVER)) continue;
            if (player.getAbilities().instabuild) return InteractionResult.SUCCESS;

            final FluidStack drained = fluidHandler.drain(FluidType.BUCKET_VOLUME, IFluidHandler.FluidAction.EXECUTE);
            final SoundEvent sound = drained.getFluidType().getSound(drained, SoundActions.BUCKET_EMPTY);
            level.playSound(player, position.x, position.y, position.z,
                    sound != null ? sound : SoundEvents.BUCKET_EMPTY, SoundSource.PLAYERS, 1, 1);
            player.setItemInHand(hand, fluidHandler.getContainer());
            iPaintable.clearPaint();
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    /**
     * Clears the paint state
     */
    void clearPaint();

    /**
     * @return The paint color of the boat
     */
    Optional<DyeColor> getPaintColor();

    /**
     * @param paintColor A {@link DyeColor}
     */
    void setPaintColor(final DyeColor paintColor);
}