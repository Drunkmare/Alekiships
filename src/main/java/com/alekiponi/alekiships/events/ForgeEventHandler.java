package com.alekiponi.alekiships.events;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.common.entity.vehicle.AbstractAlekiBoatEntity;
import com.alekiponi.alekiships.common.entity.vehiclehelper.SailSwitchEntity;
import com.alekiponi.alekiships.common.entity.vehiclehelper.WindlassSwitchEntity;
import com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.EmptyCompartmentEntity;
import com.alekiponi.alekiships.events.config.AlekiShipsConfig;
import com.mojang.logging.LogUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

@Mod.EventBusSubscriber(modid = AlekiShips.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEventHandler {

    public static final Logger LOGGER = LogUtils.getLogger();
    @SubscribeEvent
    public static void onWorldLoad(LevelEvent.Load event) {
        if (event.getLevel() instanceof final ServerLevel level) {
            final MinecraftServer server = level.getServer();
            final GameRules rules = level.getGameRules();

            if (AlekiShipsConfig.SERVER.forceReducedDebugInfo.get()) {
                rules.getRule(GameRules.RULE_REDUCEDDEBUGINFO).set(true, server);
            }

        }
    }

    @SubscribeEvent
    public static void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event){
        Player player = event.getEntity();

        if(player.level().getServer().isSingleplayer() && player.level().getServer().isSingleplayerOwner(player.getGameProfile())){
            // do singleplayer behavior
        } else if(player.getVehicle() instanceof EmptyCompartmentEntity compartment){
            // do multiplayer behavior
            player.stopRiding();
            player.setPos(compartment.getRootVehicle().getDismountLocationForPassenger(player));
            if(compartment.isPassenger() && compartment.getRootVehicle() instanceof AbstractAlekiBoatEntity boat){
                for(SailSwitchEntity sail : boat.getSailSwitches()){
                    sail.setSwitched(false);
                }
                for(WindlassSwitchEntity windlass : boat.getWindlasses()){
                    windlass.setSwitched(true);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        final Entity entity = event.getTarget();
        if (entity instanceof LivingEntity living) {
            if (living.isPassenger() && living.getVehicle() instanceof EmptyCompartmentEntity && event.getEntity().isSecondaryUseActive()) {
                living.stopRiding();
                event.setCanceled(true);
                event.setCancellationResult(InteractionResult.SUCCESS);
            }
        }
        if (entity instanceof Boat) {
            if (AlekiShipsConfig.SERVER.disableVanillaBoatFunctionality.get()) {
                event.setCanceled(true);
                event.setCancellationResult(InteractionResult.FAIL);
            }
        }
    }


}
