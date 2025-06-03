package com.alekiponi.alekiships.common.compartment;

import com.alekiponi.alekiships.common.entity.compartment.AbstractCompartmentEntity;
import com.alekiponi.alekiships.common.entity.compartment.CompartmentCloneable;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityType.EntityFactory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * This is similar to {@link EntityType} in concept, in essence it cleanly wraps construction and initialization of
 * compartments via {@link CompartmentFactory} for use in {@link DirectCompartmentPlaceable}
 *
 * @param <E> The type of compartment
 */
@Slf4j
@AllArgsConstructor
public class DirectCompartmentType<E extends AbstractCompartmentEntity> {

    @NotNull
    private final Supplier<? extends EntityType<E>> entityTypeSupplier;
    @NotNull
    private final CompartmentFactory<E> compartmentFactory;

    /**
     * Create a {@link DirectCompartmentType}
     *
     * @param entityTypeSupplier A supplier for the {@link EntityType}
     * @param compartmentFactory A factory for this {@link DirectCompartmentType}
     */
    public static <E extends AbstractCompartmentEntity> DirectCompartmentType<E> of(
            final Supplier<? extends EntityType<E>> entityTypeSupplier,
            final CompartmentFactory<E> compartmentFactory) {
        return new DirectCompartmentType<>(Objects.requireNonNull(entityTypeSupplier),
                Objects.requireNonNull(compartmentFactory));
    }

    /**
     * Create a {@link DirectCompartmentType} with post initialization steps
     *
     * @param entityTypeSupplier  A supplier for the {@link EntityType}
     * @param compartmentFactory  The base compartment factory
     * @param postInitializations The post initializations that should be performed on the compartment
     */
    @SafeVarargs
    public static <E extends AbstractCompartmentEntity> DirectCompartmentType<E> postInit(
            final Supplier<? extends EntityType<E>> entityTypeSupplier, CompartmentFactory<E> compartmentFactory,
            final CompartmentPostInitialization<? super E>... postInitializations) {
        for (final var postInitialization : postInitializations) {
            compartmentFactory = compartmentFactory.postInit(postInitialization);
        }
        return of(entityTypeSupplier, compartmentFactory);
    }

    /**
     * Helper for a simple compartment type without a special factory, merely returning the result of
     * {@link EntityType#create(Level)}. You will usually want to instead use {@link #of(Supplier, CompartmentFactory)}
     *
     * @param entityTypeSupplier A supplier for the {@link EntityType}
     */
    public static <E extends AbstractCompartmentEntity> DirectCompartmentType<E> simple(
            final Supplier<? extends EntityType<E>> entityTypeSupplier) {
        return of(entityTypeSupplier, CompartmentFactory.simple());
    }

    /**
     * Create a Compartment Entity for this {@link DirectCompartmentType}
     */
    public Optional<E> create(final Level level, final ItemStack itemStack) {
        return Optional.ofNullable(this.compartmentFactory.create(this.entityTypeSupplier.get(), level, itemStack));
    }

    /**
     * Like vanillas {@link EntityFactory} but takes an additional {@link ItemStack} parameter to enable
     * the compartments to be constructed with an {@link ItemStack} parameter for easier reasoning of behavior.
     * You may also wrap a post-initialization step via {@link CompartmentFactory#postInit(CompartmentPostInitialization)}
     * like {@link CompartmentCloneable#initialize(AbstractCompartmentEntity, ItemStack)}
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
                    log.warn("Couldn't create an {}. Using EntityType#create(Level). Please inform your mod author",
                            entityType);
                    return null;
                }

                return e;
            };
        }

        @Nullable
        E create(EntityType<E> entityType, Level level, ItemStack itemStack);

        private CompartmentFactory<E> postInit(final CompartmentPostInitialization<? super E> postInitialization) {
            return (entityType, level, itemStack) -> {
                final var e = this.create(entityType, level, itemStack);
                if (e != null) postInitialization.initialize(e, itemStack);
                return e;
            };
        }
    }

    @FunctionalInterface
    public interface CompartmentPostInitialization<E extends AbstractCompartmentEntity> {
        void initialize(E compartmentEntity, ItemStack itemStack);
    }
}