package com.alekiponi.alekiships.client;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.ContainerCompartmentEntity;
import com.alekiponi.alekiships.network.PacketHandler;
import com.alekiponi.alekiships.network.ServerBoundPickCompartmentPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AlekiShips.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class ClientEvents {

    @SubscribeEvent
    public static void onClickInput(final InputEvent.InteractionKeyMappingTriggered event) {
        if (!event.isPickBlock()) return;

        final Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.hitResult == null) return;
        if (minecraft.player == null) return;
        if (minecraft.gameMode == null) return;
        if (!minecraft.player.getAbilities().instabuild) return;
        if (!Screen.hasControlDown()) return;

        if (minecraft.hitResult.getType() != HitResult.Type.ENTITY) return;

        final Entity entity = ((EntityHitResult) minecraft.hitResult).getEntity();

        if (!(entity instanceof ContainerCompartmentEntity compartment)) return;

        final ItemStack pickResult = compartment.getPickedResult(minecraft.hitResult);

        if (pickResult.isEmpty()) return;

        addNBTData(pickResult, compartment);

        final Inventory inventory = minecraft.player.getInventory();

        inventory.setPickedItem(pickResult);
        PacketHandler.CHANNEL.sendToServer(
                new ServerBoundPickCompartmentPacket(compartment, minecraft.player.getMainHandItem(),
                        Inventory.INVENTORY_SIZE + inventory.selected));

        event.setCanceled(true);
    }

    private static void addNBTData(final ItemStack itemStack, final ContainerCompartmentEntity compartment) {
        compartment.saveToStack(itemStack);

        final CompoundTag compoundTag = new CompoundTag();
        final ListTag listtag = new ListTag();
        listtag.add(StringTag.valueOf("\"(+NBT)\""));
        compoundTag.put("Lore", listtag);
        itemStack.addTagElement("display", compoundTag);
    }
}