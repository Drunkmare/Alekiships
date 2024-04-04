package com.alekiponi.alekiships.network;

import com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.CompartmentCloneable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ServerBoundPickCompartmentPacket {

    private final int compartmentID;
    private final ItemStack itemStack;
    private final int slotIndex;

    public ServerBoundPickCompartmentPacket(final int compartmentID, final ItemStack itemStack, final int slotIndex) {
        this.compartmentID = compartmentID;
        this.itemStack = itemStack.copy();
        this.slotIndex = slotIndex;
    }

    protected ServerBoundPickCompartmentPacket(final FriendlyByteBuf byteBuf) {
        this.compartmentID = byteBuf.readInt();
        this.itemStack = byteBuf.readItem();
        this.slotIndex = byteBuf.readInt();
    }

    public void encoder(final FriendlyByteBuf byteBuf) {
        byteBuf.writeInt(this.compartmentID);
        byteBuf.writeItemStack(this.itemStack, false);
        byteBuf.writeInt(this.slotIndex);
    }

    public void handle(final Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            final ServerPlayer player = context.get().getSender();
            if (player == null) return;

            if (!player.gameMode.isCreative()) return;

            if (!this.itemStack.isItemEnabled(player.level().enabledFeatures())) return;

            if (!this.itemStack.isEmpty()) {

                final Entity entity = player.level().getEntity(this.compartmentID);

                if (entity instanceof CompartmentCloneable compartment) {
                    this.itemStack.addTagElement(BlockItem.BLOCK_ENTITY_TAG, compartment.saveForItemStack());
                }
            }

            final boolean validSlot = this.slotIndex >= 1 && this.slotIndex <= 45;
            final boolean nonEmptyStack = this.itemStack.isEmpty() || this.itemStack.getDamageValue() >= 0 && this.itemStack.getCount() <= 64 && !this.itemStack.isEmpty();
            if (validSlot && nonEmptyStack) {
                player.inventoryMenu.getSlot(this.slotIndex).setByPlayer(this.itemStack);
                player.inventoryMenu.broadcastChanges();
            } else if (this.slotIndex < 0 && nonEmptyStack /*&& dropSpamTickCount < 200*/) {
//                dropSpamTickCount += 20;
                player.drop(this.itemStack, true);
            }
        });
    }
}