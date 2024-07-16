package com.alekiponi.alekiships.events;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.common.entity.vehiclecapability.IHaveAnchorWindlass;
import com.alekiponi.alekiships.common.entity.vehiclecapability.IHaveSailSwitches;
import com.alekiponi.alekiships.common.entity.vehiclehelper.CleatEntity;
import com.alekiponi.alekiships.common.entity.vehiclehelper.SailSwitchEntity;
import com.alekiponi.alekiships.common.entity.vehiclehelper.WindlassSwitchEntity;
import com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.AbstractCompartmentEntity;
import com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.EmptyCompartmentEntity;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

@Mod.EventBusSubscriber(modid = AlekiShips.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEventHandler {

    public static final Logger LOGGER = LogUtils.getLogger();

    @SubscribeEvent
    public static void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event) {
        Player player = event.getEntity();

        if (player.level().getServer().isSingleplayer() && player.level().getServer().isSingleplayerOwner(player.getGameProfile())) {
            // do singleplayer behavior
        } else if (player.getVehicle() instanceof EmptyCompartmentEntity compartment) {
            // do multiplayer behavior
            player.stopRiding();
            player.setPos(compartment.getRootVehicle().getDismountLocationForPassenger(player));
            if (compartment.isPassenger() && compartment.getRootVehicle() instanceof IHaveSailSwitches boat) {
                for (SailSwitchEntity sail : boat.getSailSwitches()) {
                    sail.setSwitched(false);
                }
            }

            if (compartment.isPassenger() && compartment.getRootVehicle() instanceof IHaveAnchorWindlass boat) {
                for (WindlassSwitchEntity windlass : boat.getWindlasses()) {
                    windlass.setSwitched(false);
                }
            }
        }
    }

    /*
    @SubscribeEvent
    public static void onLivingOnLadder(LivingOnLadder event) {
        Player player = event.getEntity();

        if (player.level().getServer().isSingleplayer() && player.level().getServer().isSingleplayerOwner(player.getGameProfile())) {
            // do singleplayer behavior
        } else if (player.getVehicle() instanceof EmptyCompartmentEntity compartment) {
            // do multiplayer behavior
            player.stopRiding();
            player.setPos(compartment.getRootVehicle().getDismountLocationForPassenger(player));
            if (compartment.isPassenger() && compartment.getRootVehicle() instanceof IHaveSailSwitches boat) {
                for (SailSwitchEntity sail : boat.getSailSwitches()) {
                    sail.setSwitched(false);
                }
            }

            if (compartment.isPassenger() && compartment.getRootVehicle() instanceof IHaveAnchorWindlass boat) {
                for (WindlassSwitchEntity windlass : boat.getWindlasses()) {
                    windlass.setSwitched(false);
                }
            }
        }
    }

     */

    /**
     * Eject entities from compartments
     */
    @SubscribeEvent
    public static void onEntityInteract(final PlayerInteractEvent.EntityInteract event) {
        if (!(event.getTarget() instanceof final LivingEntity living)) return;
        if (!living.isPassenger()) return;
        if (!(living.getVehicle() instanceof EmptyCompartmentEntity)) return;
        if (!event.getEntity().isSecondaryUseActive()) return;

        living.stopRiding();
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
    }

    /**
     * Intercept the normal entity attack handling, so we can prevent the attack sound from being played on compartments
     */
    @SubscribeEvent
    public static void onPlayerAttack(final AttackEntityEvent event) {
        final Entity target = event.getTarget();
        if (!(target instanceof AbstractCompartmentEntity)) return;
        if (target instanceof EmptyCompartmentEntity) return;

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
     * Cancel Suffocation damage when riding our boats
     */
    @SubscribeEvent
    public static void onLivingAttack(final LivingAttackEvent event) {
        if (event.getSource().is(DamageTypes.IN_WALL)) {
            if (event.getEntity().getVehicle() instanceof EmptyCompartmentEntity) {
                event.setCanceled(true);
            }
        }
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

        for (final CleatEntity cleat : level.getEntitiesOfClass(CleatEntity.class,
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