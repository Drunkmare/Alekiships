package com.alekiponi.alekiships.events;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.common.entity.vehicle.AbstractAlekiBoatEntity;
import com.alekiponi.alekiships.common.entity.vehiclehelper.SailSwitchEntity;
import com.alekiponi.alekiships.common.entity.vehiclehelper.VehicleCleatEntity;
import com.alekiponi.alekiships.common.entity.vehiclehelper.WindlassSwitchEntity;
import com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.AbstractCompartmentEntity;
import com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.EmptyCompartmentEntity;
import com.alekiponi.alekiships.events.config.AlekiShipsConfig;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
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

    /**
     * Intercept the normal entity attack handling, so we can prevent the attack sound from being played on compartments
     */
    @SubscribeEvent
    public static void onPlayerAttack(final AttackEntityEvent event) {
        final Entity target = event.getTarget();
        if (!(target instanceof AbstractCompartmentEntity)) return;

        final Player player = event.getEntity();
        final double attackDamage = player.getAttributeValue(Attributes.ATTACK_DAMAGE);

        if (target.hurt(player.damageSources().playerAttack((player)), (float) attackDamage)) {
            player.setLastHurtMob(target);
            player.causeFoodExhaustion(0.1F);
        }

        player.resetAttackStrengthTicker();
        event.setCanceled(true);
    }

    /**
     * Try leash our cleats to the clicked fence
     */
    @SubscribeEvent
    public static void onBlockClick(final PlayerInteractEvent.RightClickBlock event) {
        final Level level = event.getLevel();

        // Only do server logic
        if (level.isClientSide()) return;

        final BlockPos blockPos = event.getPos();
        final BlockState blockState = level.getBlockState(blockPos);

        // Must click on a fence
        if (!blockState.is(BlockTags.FENCES)) return;

        final Player player = event.getEntity();

        LeashFenceKnotEntity knotEntity = null;
        boolean leashedSomething = false;

        for (final VehicleCleatEntity cleat : level.getEntitiesOfClass(VehicleCleatEntity.class,
                new AABB(blockPos.getX() - 7, blockPos.getY() - 7, blockPos.getZ() - 7, blockPos.getX() + 7,
                        blockPos.getY() + 7, blockPos.getZ() + 7))) {
            if (cleat.getLeashHolder() == player) {
                if (knotEntity == null) {
                    knotEntity = LeashFenceKnotEntity.getOrCreateKnot(level, blockPos);
                    knotEntity.playPlacementSound();
                }

                cleat.setLeashedTo(knotEntity, true);
                leashedSomething = true;
            }
        }

        if (leashedSomething) {
            level.gameEvent(GameEvent.BLOCK_ATTACH, blockPos, GameEvent.Context.of(player));
        }

        event.setCancellationResult(InteractionResult.SUCCESS);
    }
}