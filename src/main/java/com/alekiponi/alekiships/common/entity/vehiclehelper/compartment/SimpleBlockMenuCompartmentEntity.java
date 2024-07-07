package com.alekiponi.alekiships.common.entity.vehiclehelper.compartment;

import com.alekiponi.alekiships.common.entity.vehiclehelper.CompartmentType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stat;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuConstructor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

/**
 * Simple block compartment entity for blocks that provide menus like the crafting table
 */
public abstract class SimpleBlockMenuCompartmentEntity extends BlockCompartmentEntity implements SimpleBlockMenuCompartment, MenuConstructor {

    public SimpleBlockMenuCompartmentEntity(
            final CompartmentType<? extends SimpleBlockMenuCompartmentEntity> compartmentType, final Level level) {
        super(compartmentType, level);
    }

    public SimpleBlockMenuCompartmentEntity(
            final CompartmentType<? extends SimpleBlockMenuCompartmentEntity> entityType, final Level level,
            final ItemStack itemStack) {
        super(entityType, level, itemStack);
    }

    @Override
    public InteractionResult interact(final Player player, final InteractionHand hand) {
        if (player.level().isClientSide) return InteractionResult.SUCCESS;

        player.openMenu(this.getMenuProvider());
        final Stat<ResourceLocation> interactionStat = this.getInteractionStat();
        if (interactionStat != null) player.awardStat(interactionStat);

        return InteractionResult.CONSUME;
    }

    @Override
    public final MenuProvider getMenuProvider() {
        return new SimpleMenuProvider(this, this.getContainerTitle());
    }

    /**
     * @return The stat object for interactions or {@code null} for no stat
     */
    @Nullable
    protected abstract Stat<ResourceLocation> getInteractionStat();

    /**
     * @return The title of the container
     */
    protected abstract Component getContainerTitle();

    @Nullable
    @Override
    public abstract AbstractContainerMenu createMenu(final int id, final Inventory playerInventory,
            final Player player);
}