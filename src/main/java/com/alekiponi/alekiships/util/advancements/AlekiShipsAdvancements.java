package com.alekiponi.alekiships.util.advancements;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.common.entity.vehicle.AbstractAlekiBoatEntity;
import com.alekiponi.alekiships.common.entity.vehicle.SloopEntity;
import com.alekiponi.alekiships.common.entity.vehiclecapability.IPaintable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;

public class AlekiShipsAdvancements {

    public static void registerTriggers() {
    }

    public static final GenericTrigger FULL_BROADSIDE = registerGeneric("full_broadside");

    public static final GenericTrigger SLOOP_COMPLETED = registerGeneric("sloop_completed");

    public static final GenericTrigger ROWBOAT_COMPLETED = registerGeneric("rowboat_completed");

    public static final GenericTrigger RIDE_BARREL = registerGeneric("ride_barrel");

    public static final GenericTrigger ARMOR_STAND_ON_BOAT = registerGeneric("armor_stand_on_boat");

    public static final GenericTrigger DYE_SHIP_BLACK = registerGeneric("dye_ship_black");

    public static GenericTrigger registerGeneric(String name) {
        return CriteriaTriggers.register(new GenericTrigger(new ResourceLocation(AlekiShips.MOD_ID, name)));
    }

    public static void checkDyeShipBlack(Player player, AbstractAlekiBoatEntity boat) {
        if (boat instanceof IPaintable) {
            //TODO make generic
            if (boat instanceof SloopEntity sloop) {
                if (sloop.getMainsailDye().equals(DyeColor.BLACK) && sloop.getMainsailDye().equals(DyeColor.BLACK) && sloop.getPaintColor().isPresent() && sloop.getPaintColor().get().equals(DyeColor.BLACK)) {
                    if (sloop.getName().getString().equalsIgnoreCase("black pearl") || sloop.getName().getString().equalsIgnoreCase("the black pearl")) {
                        if (player instanceof ServerPlayer serverPlayer) {
                            AlekiShipsAdvancements.DYE_SHIP_BLACK.trigger(serverPlayer);
                        }
                    }
                }
            }
        }
    }

}
