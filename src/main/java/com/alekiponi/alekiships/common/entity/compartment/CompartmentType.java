package com.alekiponi.alekiships.common.entity.compartment;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityType.EntityFactory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

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
     *
     * @param entityTypeSupplier A supplier for the {@link EntityType}
     */
    public static <E extends AbstractCompartmentEntity> CompartmentType<E> simple(
            final Supplier<? extends EntityType<E>> entityTypeSupplier) {
        return of(entityTypeSupplier, CompartmentFactory.simple());
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
     * You may also wrap a post-initialization step via {@link CompartmentFactory#postInit(CompartmentPostInitialization)} like
     * {@link CompartmentCloneable#initialize(AbstractCompartmentEntity, ItemStack)}
     *
     * @param <E> The type of compartment
     */
    @FunctionalInterface
    public interface CompartmentFactory<E extends AbstractCompartmentEntity> {

        /**
         * A simple compartment factory returning the result of {@link EntityType#create(Level)}
         */
        static <E extends AbstractCompartmentEntity> CompartmentFactory<E> simple() {
            return (entityType, level, itemStack) -> {
                final var e = entityType.create(level);

                if (e == null) {
                    LOGGER.warn("Couldn't create an {}. Using EntityType#create(Level). Please inform your mod author",
                            entityType);
                    return null;
                }

                return e;
            };
        }

        @Nullable
        E create(final EntityType<E> entityType, final Level level, final ItemStack itemStack);

        /**
         * @param postInitialization The compartment post initialization
         *
         * @return A factory which invokes the provided post initialization and reports any errors
         */
        default CompartmentFactory<E> postInit(final CompartmentPostInitialization<E> postInitialization) {
            return (entityType, level, itemStack) -> {
                final var e = this.create(entityType, level, itemStack);
                if (e == null) return null;

                final var initializationResult = postInitialization.initialize(e, itemStack);
                if (initializationResult.wasSuccessful()) return e;

                LOGGER.error(initializationResult.errorMessage);
                return null;
            };
        }
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