package com.alekiponi.alekiships.network;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.CompartmentCloneable;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public record ServerboundPickCompartmentPacket(int entityId, ItemStack itemStack,
                                               int slotIndex) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ServerboundPickCompartmentPacket> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(AlekiShips.MOD_ID, "pick_compartment"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundPickCompartmentPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ServerboundPickCompartmentPacket::entityId,
            ItemStack.validatedStreamCodec(ItemStack.OPTIONAL_STREAM_CODEC),
            ServerboundPickCompartmentPacket::itemStack, ByteBufCodecs.VAR_INT,
            ServerboundPickCompartmentPacket::slotIndex, ServerboundPickCompartmentPacket::new);

    public ServerboundPickCompartmentPacket(final Entity entity, final ItemStack itemStack, final int slotIndex) {
        this(entity.getId(), itemStack.copy(), slotIndex);
    }

    void handle(@Nullable final ServerPlayer player) {
        if (player == null) return;

        if (!player.gameMode.isCreative()) return;

        if (!this.itemStack.isItemEnabled(player.level().enabledFeatures())) return;

        if (!this.itemStack.isEmpty()) {

            final Entity entity = player.level().getEntity(this.entityId);

            if (entity instanceof CompartmentCloneable compartment) {
                this.itemStack.applyComponents(compartment.collectComponents());
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
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}