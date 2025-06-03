package com.alekiponi.alekiships.common.entity.compartment.vanilla.crafting;

import com.alekiponi.alekiships.common.entity.compartment.SimpleBlockMenuCompartmentEntity;
import com.alekiponi.alekiships.util.CommonHelper;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.Nullable;

public class GrindstoneCompartmentEntity extends SimpleBlockMenuCompartmentEntity {

    private static final Component CONTAINER_TITLE = Component.translatable("container.grindstone_title");
    private static final Stat<ResourceLocation> STAT = Stats.CUSTOM.get(Stats.INTERACT_WITH_GRINDSTONE);

    public GrindstoneCompartmentEntity(final EntityType<? extends GrindstoneCompartmentEntity> entityType,
            final Level level) {
        super(entityType, level, STAT, CONTAINER_TITLE);
    }

    public GrindstoneCompartmentEntity(final EntityType<? extends SimpleBlockMenuCompartmentEntity> entityType,
            final Level level, final BlockState blockState) {
        super(entityType, level, blockState, STAT, CONTAINER_TITLE);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(final int id, final Inventory playerInventory, final Player player) {
        return new GrindstoneMenu(id, playerInventory, CommonHelper.createEntityContainerLevelAccess(this)) {
            @Override
            public boolean stillValid(final Player player) {
                return CommonHelper.stillValidEntity(GrindstoneCompartmentEntity.this, player);
            }
        };
    }
}