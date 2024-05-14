package com.alekiponi.alekiships.common.entity.vehiclehelper;

import com.alekiponi.alekiships.client.IngameOverlays;
import com.alekiponi.alekiships.common.entity.vehicle.AbstractVehicle;
import com.alekiponi.alekiships.common.entity.vehicle.SloopEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.Tags;

import java.util.ArrayList;

public class SailSwitchEntity extends AbstractSwitchEntity{

    public SailSwitchEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public InteractionResult interact(final Player player, final InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if((stack.is(Tags.Items.DYES) || stack.is(Items.WATER_BUCKET)) && this.getRootVehicle() instanceof SloopEntity sloop){
            int index = 0;
            for(SailSwitchEntity switchEntity : sloop.getSailSwitches()){
                if(switchEntity == this){
                    break;
                }
                index++;
            }

            if(index == 0){
                //mainsail
                if (stack.is(Items.WATER_BUCKET)) {
                    sloop.clearMainsailDye();
                    return InteractionResult.SUCCESS;
                }
                if (stack.is(Tags.Items.DYES)) {
                    final DyeColor dyeColor = DyeColor.getColor(stack);
                    if (dyeColor != null && dyeColor != sloop.getMainsailDye()) {
                        sloop.setMainsailDye(dyeColor);
                        stack.shrink(1);
                        player.swing(hand);
                        return InteractionResult.SUCCESS;
                    }
                }
            } else if (index == 1) {
                //jibsail
                if (stack.is(Items.WATER_BUCKET)) {
                    sloop.clearJibsailDye();
                    return InteractionResult.SUCCESS;
                }
                if (stack.is(Tags.Items.DYES)) {
                    final DyeColor dyeColor = DyeColor.getColor(stack);
                    if (dyeColor != null && dyeColor != sloop.getJibsailDye()) {
                        sloop.setJibsailDye(dyeColor);
                        stack.shrink(1);
                        player.swing(hand);
                        return InteractionResult.SUCCESS;
                    }
                }
            }
        }

        return super.interact(player, hand);
    }

    @Override
    public ArrayList<IngameOverlays.IconState> getIconStates(Player player) {
        ArrayList<IngameOverlays.IconState> states = new ArrayList<>();
        ItemStack handItem = player.getItemInHand(player.getUsedItemHand());

        if (handItem.is(Tags.Items.DYES) || handItem.is(Items.WATER_BUCKET)){
            // TODO don't show this when you're holding the same dye color that the sail already is
            states.add(IngameOverlays.IconState.BRUSH);
            return states;
        }

        if(this.getRootVehicle() instanceof AbstractVehicle){
            if(this.getSwitched()){
                states.add(IngameOverlays.IconState.SAIL_ARROW_DOWN);
            } else {
                states.add(IngameOverlays.IconState.SAIL_ARROW_UP);
            }
        }

        return states;
    }
}
