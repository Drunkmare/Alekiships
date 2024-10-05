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
import net.minecraft.world.inventory.StonecutterMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class StonecutterCompartmentEntity extends SimpleBlockMenuCompartmentEntity {

    private static final Component CONTAINER_TITLE = Component.translatable("container.stonecutter");

    public StonecutterCompartmentEntity(final EntityType<? extends StonecutterCompartmentEntity> entityType,
            final Level level) {
        super(entityType, level);
    }

    public StonecutterCompartmentEntity(final EntityType<? extends StonecutterCompartmentEntity> entityType,
            final Level level, final BlockState blockState) {
        super(entityType, level, blockState);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(final int id, final Inventory playerInventory, final Player player) {
        return new StonecutterMenu(id, playerInventory, CommonHelper.createEntityContainerLevelAccess(this)) {
            @Override
            public boolean stillValid(final Player player) {
                return CommonHelper.stillValidEntity(StonecutterCompartmentEntity.this, player);
            }
        };
    }

    @Override
    protected Stat<ResourceLocation> getInteractionStat() {
        return Stats.CUSTOM.get(Stats.INTERACT_WITH_STONECUTTER);
    }

    @Override
    protected Component getContainerTitle() {
        return CONTAINER_TITLE;
    }
}