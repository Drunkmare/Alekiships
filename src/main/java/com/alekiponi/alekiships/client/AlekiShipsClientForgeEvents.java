package com.alekiponi.alekiships.client;

import com.alekiponi.alekiships.common.entity.compartment.CompartmentCloneable;
import com.alekiponi.alekiships.network.ServerboundPickCompartmentPacket;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.network.PacketDistributor;

public final class AlekiShipsClientForgeEvents {

    public static void init(final IEventBus eventBus) {
        eventBus.addListener(AlekiShipsClientForgeEvents::onClickInput);
    }

    private static void onClickInput(final InputEvent.InteractionKeyMappingTriggered event) {
        if (!event.isPickBlock()) return;

        final Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.hitResult == null) return;
        if (minecraft.player == null) return;
        if (minecraft.gameMode == null) return;
        if (!minecraft.player.getAbilities().instabuild) return;
        if (!Screen.hasControlDown()) return;

        if (minecraft.hitResult.getType() != HitResult.Type.ENTITY) return;

        final Entity entity = ((EntityHitResult) minecraft.hitResult).getEntity();

        final ItemStack pickResult = entity.getPickedResult(minecraft.hitResult);

        if (pickResult == null) return;

        if (pickResult.isEmpty()) return;

        if (!(entity instanceof CompartmentCloneable compartment)) return;

        pickResult.applyComponents(compartment.collectComponents());

        final Inventory inventory = minecraft.player.getInventory();

        inventory.setPickedItem(pickResult);
        PacketDistributor.sendToServer(new ServerboundPickCompartmentPacket(entity, minecraft.player.getMainHandItem(),
                Inventory.INVENTORY_SIZE + inventory.selected));

        event.setCanceled(true);
    }
}