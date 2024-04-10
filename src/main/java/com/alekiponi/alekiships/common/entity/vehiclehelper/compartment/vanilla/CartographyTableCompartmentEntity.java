package com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.vanilla;

import com.alekiponi.alekiships.common.entity.vehiclehelper.CompartmentType;
import com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.SimpleBlockMenuCompartmentEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CartographyTableMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class CartographyTableCompartmentEntity extends SimpleBlockMenuCompartmentEntity {
    private static final Component CONTAINER_TITLE = Component.translatable("container.cartography_table");

    public CartographyTableCompartmentEntity(
            final CompartmentType<? extends CartographyTableCompartmentEntity> compartmentType, final Level level) {
        super(compartmentType, level);
    }

    public CartographyTableCompartmentEntity(
            final CompartmentType<? extends CartographyTableCompartmentEntity> compartmentType, final Level level,
            final ItemStack itemStack) {
        super(compartmentType, level, itemStack);
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(final int id, final Inventory playerInventory,
            final Player player) {
        return new CartographyTableMenu(id, playerInventory, this.getContainerLevelAccess()) {
            @Override
            public boolean stillValid(final Player player) {
                return CartographyTableCompartmentEntity.this.stillValid(player);
            }
        };
    }

    @Override
    protected Stat<ResourceLocation> getInteractionStat() {
        return Stats.CUSTOM.get(Stats.INTERACT_WITH_CARTOGRAPHY_TABLE);
    }

    @Override
    protected Component getContainerTitle() {
        return CONTAINER_TITLE;
    }
}