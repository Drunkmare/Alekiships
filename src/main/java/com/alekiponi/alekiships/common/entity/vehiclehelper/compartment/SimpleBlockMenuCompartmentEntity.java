package com.alekiponi.alekiships.common.entity.vehiclehelper.compartment;

import com.alekiponi.alekiships.common.entity.vehiclehelper.CompartmentType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stat;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.MenuConstructor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.BiFunction;

/**
 * Simple block compartment entity for blocks that provide menus like the crafting table
 */
public abstract class SimpleBlockMenuCompartmentEntity extends BlockCompartmentEntity implements SimpleBlockMenuCompartment, MenuConstructor {

    public SimpleBlockMenuCompartmentEntity(final EntityType<? extends SimpleBlockMenuCompartmentEntity> entityType,
            final Level level) {
        super(entityType, level);
    }

    public SimpleBlockMenuCompartmentEntity(
            final CompartmentType<? extends SimpleBlockMenuCompartmentEntity> entityType, final Level level,
            final ItemStack itemStack) {
        super(entityType, level, itemStack);
    }

    @Override
    public InteractionResult interact(final Player player, final InteractionHand hand) {
        player.openMenu(this.getMenuProvider());
        player.awardStat(this.getInteractionStat());
        return InteractionResult.SUCCESS;
    }

    /**
     * Gets a container level access that'll pass valid {@link Level} and {@link BlockPos} objects
     * from the compartment.
     * Note: you will likely need to override at least {@link AbstractContainerMenu#stillValid(Player)} in order for
     * it to work correctly as Menus such as {@link CraftingMenu} checks for a block in world which will fail with our
     * compartment entities.
     */
    protected ContainerLevelAccess getContainerLevelAccess() {
        return new ContainerLevelAccess() {
            @Override
            public <T> Optional<T> evaluate(final BiFunction<Level, BlockPos, T> function) {
                return Optional.of(function.apply(SimpleBlockMenuCompartmentEntity.this.level(),
                        SimpleBlockMenuCompartmentEntity.this.blockPosition()));
            }
        };
    }

    /**
     * Does a square root check using this compartments location in the world.
     * This is to simplify the likely necessary {@link AbstractContainerMenu#stillValid(Player)} override.
     * You can make an anonymous class of the menu which overrides the method without needing to register
     * a new menu type
     */
    protected boolean stillValid(final Player player) {
        return player.distanceToSqr(this.getX() + 0.5, this.getY() + 0.5, this.getZ() + 0.5) <= 64;
    }

    @Override
    public MenuProvider getMenuProvider() {
        return new SimpleMenuProvider(this, this.getContainerTitle());
    }

    protected abstract Stat<ResourceLocation> getInteractionStat();

    protected abstract Component getContainerTitle();

    @Nullable
    @Override
    public abstract AbstractContainerMenu createMenu(final int id, final Inventory playerInventory,
            final Player player);
}