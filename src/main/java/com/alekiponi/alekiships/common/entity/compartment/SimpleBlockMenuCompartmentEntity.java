package com.alekiponi.alekiships.common.entity.compartment;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuConstructor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import org.jetbrains.annotations.Nullable;

/**
 * Simple block compartment entity for blocks that provide menus like the crafting table
 */
public abstract class SimpleBlockMenuCompartmentEntity extends BlockCompartmentEntity implements SimpleBlockMenuCompartment, MenuConstructor {

    @Nullable
    protected final Stat<?> interactionStat;
    protected final Component title;

    protected SimpleBlockMenuCompartmentEntity(final EntityType<? extends SimpleBlockMenuCompartmentEntity> entityType,
            final Level level, @Nullable final Stat<?> interactionStat, final Component title) {
        super(entityType, level);
        this.interactionStat = interactionStat;
        this.title = title;
    }

    protected SimpleBlockMenuCompartmentEntity(final EntityType<? extends SimpleBlockMenuCompartmentEntity> entityType,
            final Level level, final BlockState blockState, @Nullable final Stat<?> interactionStat,
            final Component title) {
        super(entityType, level, blockState);
        this.interactionStat = interactionStat;
        this.title = title;
    }

    protected static Stat<ResourceLocation> getStat(final ResourceLocation statName) {
        return Stats.CUSTOM.get(statName);
    }

    }

    @Override
    public InteractionResult interact(final Player player, final InteractionHand hand) {
        if (player.level().isClientSide) return InteractionResult.SUCCESS;

        player.openMenu(this.getMenuProvider());
        if (this.interactionStat != null) player.awardStat(this.interactionStat);

        return InteractionResult.CONSUME;
    }

    @Override
    public final MenuProvider getMenuProvider() {
        return new SimpleMenuProvider(this, this.title);
    }

    @Nullable
    @Override
    public abstract AbstractContainerMenu createMenu(final int id, final Inventory playerInventory,
            final Player player);
}