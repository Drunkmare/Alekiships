package com.alekiponi.alekiships.common.entity.compartment;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.common.item.components.AlekiShipsComponents;
import com.mojang.logging.LogUtils;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityType.EntityFactory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.registries.RegistryBuilder;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * This is similar to {@link EntityType} in concept, in essence it cleanly wraps construction and initialization of
 * compartments via {@link CompartmentFactory}
 *
 * @param <E> The type of compartment
 */
public class CompartmentType<E extends AbstractCompartmentEntity> {

    public static final ResourceKey<Registry<CompartmentType<?>>> REGISTRY_KEY = ResourceKey.createRegistryKey(
            AlekiShips.location("compartment_type"));
    public static final Registry<CompartmentType<?>> REGISTRY = new RegistryBuilder<>(REGISTRY_KEY).sync(true).create();

    private static final Logger LOGGER = LogUtils.getLogger();

    private final Supplier<? extends EntityType<E>> entityTypeSupplier;
    private final CompartmentFactory<E> compartmentFactory;

    private CompartmentType(final Supplier<? extends EntityType<E>> entityTypeSupplier,
            final CompartmentFactory<E> compartmentFactory) {
        this.entityTypeSupplier = entityTypeSupplier;
        this.compartmentFactory = compartmentFactory;
    }

    /**
     * Create a {@link CompartmentType}
     *
     * @param entityTypeSupplier A supplier for the {@link EntityType}
     * @param compartmentFactory A factory for this {@link CompartmentType}
     */
    public static <E extends AbstractCompartmentEntity> CompartmentType<E> of(
            final Supplier<? extends EntityType<E>> entityTypeSupplier,
            final CompartmentFactory<E> compartmentFactory) {
        return new CompartmentType<>(Objects.requireNonNull(entityTypeSupplier),
                Objects.requireNonNull(compartmentFactory));
    }

    /**
     * Helper for a simple compartment type without a special factory, merely returning the result of
     * {@link EntityType#create(Level)}. You will usually want to instead use {@link #of(Supplier, CompartmentFactory)}
     * or {@link #postInit(Supplier, CompartmentPostInitialization[])}
     *
     * @param entityTypeSupplier A supplier for the {@link EntityType}
     */
    public static <E extends AbstractCompartmentEntity> CompartmentType<E> simple(
            final Supplier<? extends EntityType<E>> entityTypeSupplier) {
        return of(entityTypeSupplier, (entityType, level, itemStack) -> entityType.create(level));
    }

    /**
     * Use this for a {@link CompartmentType} whose {@link #compartmentFactory} relies on post-init steps.
     * In particular post-init steps conforming to the {@link CompartmentPostInitialization} interface. For
     * built-in examples see {@link CompartmentCloneable#initialize(AbstractCompartmentEntity, ItemStack)} and
     * {@link BlockCompartment#initialize(AbstractCompartmentEntity, ItemStack)} among others.
     *
     * @param entityTypeSupplier  The supplier for the {@link EntityType} associated with this {@link CompartmentType}
     * @param postInitializations An array of {@link CompartmentPostInitialization}s that will be run to create a
     *                            compartment via {@link #create(Level, ItemStack)}
     */
    @SafeVarargs
    public static <E extends AbstractCompartmentEntity> CompartmentType<E> postInit(
            final Supplier<? extends EntityType<E>> entityTypeSupplier,
            final CompartmentPostInitialization<E>... postInitializations) {
        return of(entityTypeSupplier, (entityType, level, itemStack) -> {
            final E e = entityType.create(level);

            if (e == null) {
                LOGGER.warn(
                        "Couldn't create an {}. If this is intentional the compartment type shouldn't be registered",
                        entityType);
                return null;
            }

            for (final CompartmentPostInitialization<E> postInitialization : postInitializations) {
                final InitializationResult initializationResult = postInitialization.initialize(e, itemStack);
                if (initializationResult.wasSuccessful()) continue;

                LOGGER.error(initializationResult.errorMessage);
                return null;
            }

            return e;
        });
    }

    /**
     * Gets an applicable {@link CompartmentType} for an {@link ItemStack}
     *
     * @param itemStack The {@link ItemStack}
     * @return An applicable compartment for the given item stack. Returns the first compartment found,
     * this means registry order can effect which is chosen
     */
    public static Optional<CompartmentType<?>> fromStack(final ItemStack itemStack) {
        final var compartmentPlaceable = itemStack.get(AlekiShipsComponents.COMPARTMENT_PLACEABLE);
        return compartmentPlaceable == null ? Optional.empty() : Optional.of(compartmentPlaceable.compartmentType());
    }

    /**
     * Create a Compartment Entity for this {@link CompartmentType}
     */
    public Optional<E> create(final Level level, final ItemStack itemStack) {
        return Optional.ofNullable(this.compartmentFactory.create(this.entityTypeSupplier.get(), level, itemStack));
    }

    public EntityType<E> entityType() {
        return this.entityTypeSupplier.get();
    }

    /**
     * Like vanillas {@link EntityFactory} but takes an additional {@link ItemStack} parameter to enable
     * the compartments to be constructed with an {@link ItemStack} parameter for easier reasoning of behavior.
     * You may also wrap a post-initialization step via the factory like
     * {@link CompartmentCloneable#initialize(AbstractCompartmentEntity, ItemStack)}
     *
     * @param <T> The type of compartment
     */
    @FunctionalInterface
    public interface CompartmentFactory<T extends AbstractCompartmentEntity> {
        @Nullable
        T create(final EntityType<T> entityType, final Level level, final ItemStack itemStack);
    }

    @FunctionalInterface
    public interface CompartmentPostInitialization<E extends AbstractCompartmentEntity> {

        InitializationResult initialize(final E compartmentEntity, final ItemStack itemStack);
    }

    public static final class InitializationResult {

        @Nullable
        private final String errorMessage;

        private InitializationResult(final @Nullable String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public static InitializationResult success() {
            return new InitializationResult(null);
        }

        public static InitializationResult fail(final String message) {
            return new InitializationResult(Objects.requireNonNull(message));
        }

        public boolean wasSuccessful() {
            return this.errorMessage == null;
        }
    }
}