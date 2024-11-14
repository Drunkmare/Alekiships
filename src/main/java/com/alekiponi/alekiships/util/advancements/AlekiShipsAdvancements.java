package com.alekiponi.alekiships.util.advancements;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.common.entity.vehicle.AbstractAlekiBoatEntity;
import com.alekiponi.alekiships.common.entity.vehicle.SloopEntity;
import com.alekiponi.alekiships.common.entity.vehiclecapability.IPaintable;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Optional;

public final class AlekiShipsAdvancements {

    public static final DeferredRegister<CriterionTrigger<?>> TRIGGERS = DeferredRegister.create(
            Registries.TRIGGER_TYPE, AlekiShips.MOD_ID);

    public static final Trigger FULL_BROADSIDE = registerGeneric("full_broadside");
    public static final Trigger SLOOP_COMPLETED = registerGeneric("sloop_completed");
    public static final Trigger ROWBOAT_COMPLETED = registerGeneric("rowboat_completed");
    public static final Trigger RIDE_BARREL = registerGeneric("ride_barrel");
    public static final Trigger ARMOR_STAND_ON_BOAT = registerGeneric("armor_stand_on_boat");
    public static final Trigger DYE_SHIP_BLACK = registerGeneric("dye_ship_black");

    public static Trigger registerGeneric(final String name) {
        return new Trigger(TRIGGERS.register(name, GenericTrigger::new));
    }

    public static void checkDyeShipBlack(Player player, AbstractAlekiBoatEntity boat) {
        if (boat instanceof IPaintable) {
            //TODO make generic
            if (boat instanceof SloopEntity sloop) {
                if (sloop.getMainsailDye().equals(DyeColor.BLACK) && sloop.getMainsailDye()
                        .equals(DyeColor.BLACK) && sloop.getPaintColor().isPresent() && sloop.getPaintColor().get()
                        .equals(DyeColor.BLACK)) {
                    if (sloop.getName().getString().equalsIgnoreCase("black pearl") || sloop.getName().getString()
                            .equalsIgnoreCase("the black pearl")) {
                        if (player instanceof ServerPlayer serverPlayer) {
                            AlekiShipsAdvancements.DYE_SHIP_BLACK.trigger(serverPlayer);
                        }
                    }
                }
            }
        }
    }

    public record Trigger(DeferredHolder<CriterionTrigger<?>, GenericTrigger> holder) {
        public void trigger(final ServerPlayer player) {
            holder.value().trigger(player);
        }

        public Criterion<?> criterion() {
            return new Criterion<>(holder.value(), new GenericTrigger.TriggerInstance(Optional.empty()));
        }
    }
}
