package com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.vanilla.crafting;

import com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.SimpleBlockMenuCompartmentEntity;
import com.alekiponi.alekiships.util.CommonHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CartographyTableMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class CartographyTableCompartmentEntity extends SimpleBlockMenuCompartmentEntity {

    private static final Component CONTAINER_TITLE = Component.translatable("container.cartography_table");

    public CartographyTableCompartmentEntity(final EntityType<? extends CartographyTableCompartmentEntity> entityType,
            final Level level) {
        super(entityType, level);
    }

    public CartographyTableCompartmentEntity(final EntityType<? extends CartographyTableCompartmentEntity> entityType,
            final Level level, final BlockState blockState) {
        super(entityType, level, blockState);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(final int id, final Inventory playerInventory, final Player player) {
        return new CartographyTableMenu(id, playerInventory, CommonHelper.createEntityContainerLevelAccess(this)) {
            @Override
            public boolean stillValid(final Player player) {
                return CommonHelper.stillValidEntity(CartographyTableCompartmentEntity.this, player);
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