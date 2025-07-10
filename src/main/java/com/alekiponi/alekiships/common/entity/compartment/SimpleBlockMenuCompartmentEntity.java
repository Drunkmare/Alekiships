package com.alekiponi.alekiships.common.entity.compartment;

import com.alekiponi.alekiships.util.reflect.asm.SyntheticCompartmentMenuClassGenerator;

import net.minecraft.Util;
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
import net.minecraft.world.inventory.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import lombok.Builder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * Simple block compartment entity for blocks that provide menus like the crafting table
 */
@Slf4j
public final class SimpleBlockMenuCompartmentEntity extends BlockCompartmentEntity implements SimpleBlockMenuCompartment, MenuConstructor {

    /**
     * The {@link FactorySettings} for the Crafting Table
     */
    public static final FactorySettings CRAFTING_TABLE = FactorySettings.builder()
            .menuClass(CraftingMenu.class)
            .statName(Stats.INTERACT_WITH_CRAFTING_TABLE)
            .title(Component.translatable("container.crafting"))
            .build();

    /**
     * The {@link FactorySettings} for the Stonecutter
     */
    public static final FactorySettings STONECUTTER = FactorySettings.builder()
            .menuClass(StonecutterMenu.class)
            .statName(Stats.INTERACT_WITH_STONECUTTER)
            .title(Component.translatable("container.stonecutter"))
            .build();

    /**
     * The {@link FactorySettings} for the Cartography Table
     */
    public static final FactorySettings CARTOGRAPHY_TABLE = FactorySettings.builder()
            .menuClass(CartographyTableMenu.class)
            .statName(Stats.INTERACT_WITH_CARTOGRAPHY_TABLE)
            .title(Component.translatable("container.cartography_table"))
            .build();

    /**
     * The {@link FactorySettings} for the Smithing Table
     */
    public static final FactorySettings SMITHING_TABLE = FactorySettings.builder()
            .menuClass(SmithingMenu.class)
            .statName(Stats.INTERACT_WITH_SMITHING_TABLE)
            .title(Component.translatable("container.upgrade"))
            .build();

    /**
     * The {@link FactorySettings} for the Grindstone
     */
    public static final FactorySettings GRINDSTONE = FactorySettings.builder()
            .menuClass(GrindstoneMenu.class)
            .statName(Stats.INTERACT_WITH_GRINDSTONE)
            .title(Component.translatable("container.grindstone_title"))
            .build();

    /**
     * The {@link FactorySettings} for the Loom
     */
    public static final FactorySettings LOOM = FactorySettings.builder()
            .menuClass(LoomMenu.class)
            .statName(Stats.INTERACT_WITH_LOOM)
            .title(Component.translatable("container.loom"))
            .build();

    private static final Function<Class<? extends AbstractContainerMenu>, Class<? extends AbstractContainerMenu>> MENU_GENERATOR = Util.<Class<? extends AbstractContainerMenu>, Class<? extends AbstractContainerMenu>>memoize(
            SyntheticCompartmentMenuClassGenerator::generateMenuClass);

    @Nullable
    private final ResourceLocation statName;
    private final Component title;
    private final SimpleMenuConstructor menuConstructor;

    private SimpleBlockMenuCompartmentEntity(final EntityType<? extends SimpleBlockMenuCompartmentEntity> entityType,
            final Level level, final BlockState blockState, @Nullable final ResourceLocation statName,
            final Component title, final SimpleMenuConstructor menuConstructor) {
        super(entityType, level, blockState);
        this.menuConstructor = menuConstructor;
        this.statName = statName;
        this.title = title;
    }

    /**
     * @param settings   The {@link FactorySettings} to use
     * @param blockState The default BlockState to use
     */
    public static EntityType.EntityFactory<SimpleBlockMenuCompartmentEntity> entityFactory(
            final FactorySettings settings, final BlockState blockState) {
        return entityFactory(settings.menuClass, settings.statName, settings.title, blockState);
    }

    /**
     * @param settings The {@link FactorySettings} to use
     */
    public static BlockCompartmentFactory<SimpleBlockMenuCompartmentEntity> directCompartmentFactory(
            final FactorySettings settings) {
        return directCompartmentFactory(settings.menuClass, settings.statName, settings.title);
    }

    /**
     * @param menuClass  The menu class to use. Must have a constructor with the signature int, {@link Inventory}, {@link ContainerLevelAccess}
     * @param statName   The interaction state name {@code null} to prevent awarding a stat
     * @param title      The menu title
     * @param blockState The default BlockState to use
     *
     * @see #entityFactory(FactorySettings, BlockState)
     */
    public static <M extends AbstractContainerMenu> EntityType.EntityFactory<SimpleBlockMenuCompartmentEntity> entityFactory(
            final Class<M> menuClass, @Nullable final ResourceLocation statName, final Component title,
            final BlockState blockState) {
        final var menuConstructor = getSimpleMenuConstructor(MENU_GENERATOR.apply(menuClass));
        return (entityType, level) -> new SimpleBlockMenuCompartmentEntity(entityType, level, blockState, statName,
                title, menuConstructor);
    }

    /**
     * @param menuClass The menu class to use. Must have a constructor with the signature int, {@link Inventory}, {@link ContainerLevelAccess}
     * @param statName  The interaction state name {@code null} to prevent awarding a stat
     * @param title     The menu title
     *
     * @see #directCompartmentFactory(FactorySettings)
     */
    public static <M extends AbstractContainerMenu> BlockCompartmentFactory<SimpleBlockMenuCompartmentEntity> directCompartmentFactory(
            final Class<M> menuClass, @Nullable final ResourceLocation statName, final Component title) {
        final var menuConstructor = getSimpleMenuConstructor(MENU_GENERATOR.apply(menuClass));
        return (entityType, level, blockState) -> new SimpleBlockMenuCompartmentEntity(entityType, level, blockState,
                statName, title, menuConstructor);
    }

    @SneakyThrows(NoSuchMethodException.class)
    private static <M extends AbstractContainerMenu> SimpleMenuConstructor getSimpleMenuConstructor(
            final Class<M> generatedMenuClass) {
        final Constructor<M> constructor = generatedMenuClass.getConstructor(int.class, Inventory.class,
                SimpleBlockMenuCompartmentEntity.class);
        return (id, inventory, player, compartment) -> {
            try {
                return constructor.newInstance(id, inventory, compartment);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                log.error("Failed to construct {}", generatedMenuClass.getName());
                log.error("Reported error", e);
                return null;
            }
        };
    }

    private static Stat<ResourceLocation> getStat(final ResourceLocation statName) {
        return Stats.CUSTOM.get(statName);
    }

    @Override
    public InteractionResult interact(final Player player, final InteractionHand hand) {
        if (player.level().isClientSide) return InteractionResult.SUCCESS;

        player.openMenu(this.getMenuProvider());
        if (this.statName != null) player.awardStat(getStat(this.statName));

        return InteractionResult.CONSUME;
    }

    @Override
    public MenuProvider getMenuProvider() {
        return new SimpleMenuProvider(this, this.title);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(final int containerId, final Inventory playerInventory,
            final Player player) {
        return this.menuConstructor.createMenu(containerId, playerInventory, player, this);
    }

    @FunctionalInterface
    protected interface SimpleMenuConstructor {
        @Nullable AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player,
                SimpleBlockMenuCompartmentEntity compartment);
    }

    /**
     * A type to hold arguments common to {@link SimpleBlockMenuCompartmentEntity#entityFactory(Class, ResourceLocation, Component, BlockState)} and
     * {@link #directCompartmentFactory(Class, ResourceLocation, Component)}.
     *
     * @param menuClass The menu class to use. Must have a constructor with the signature int, {@link Inventory}, {@link ContainerLevelAccess}
     * @param statName  The interaction state name {@code null} to prevent awarding a stat
     * @param title     The menu title
     *
     * @see #CRAFTING_TABLE
     * @see #STONECUTTER
     * @see #CARTOGRAPHY_TABLE
     * @see #SMITHING_TABLE
     * @see #GRINDSTONE
     * @see #LOOM
     */
    @Builder
    public record FactorySettings(@NotNull Class<? extends AbstractContainerMenu> menuClass,
            @Nullable ResourceLocation statName, @NotNull Component title) {}
}