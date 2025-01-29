package com.alekiponi.alekiships.common.entity.compartment;

import com.alekiponi.alekiships.util.AlekiShipsTags;
import com.mojang.logging.LogUtils;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityType.EntityFactory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;
import oshi.util.tuples.Pair;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * This is similar to {@link EntityType} in concept, in essence it cleanly wraps construction and initialization of
 * compartments via {@link CompartmentFactory}
 *
 * @param <E> The type of compartment
 */
// TODO should these go in an actual registry? We could have the predicate defined via datapack? Seems like it could
//  make a lot more sense now that components are a thing. {@link ChestCompartmentEntity} could use a component for the
//  used texture for example.
public class CompartmentType<E extends AbstractCompartmentEntity> {

    private static final Logger LOGGER = LogUtils.getLogger();

    private static final ArrayList<Pair<CompartmentType<? extends AbstractCompartmentEntity>, Predicate<ItemStack>>> COMPARTMENT_TYPES = new ArrayList<>();

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
     * Registers a {@link CompartmentType} to be automatically picked and constructed when empty compartments are
     * right-clicked with an {@link ItemStack} matching the CompartmentTypes ItemStack predicate.
     *
     * @param compartmentType The compartment type
     * @param predicate       The ItemStack predicate that determines if the compartment type should be chosen
     * @apiNote The predicate should be as exact as possible.
     * <p>
     * You may register the same {@link CompartmentType} multiple times. This can be useful if you have for example a
     * custom furnace that only has a different texture/model as {@link CompartmentTypes#FURNACE_COMPARTMENT}
     * will display any compatible block. Custom behavior will however require a custom compartment entity.
     */
    public static <E extends AbstractCompartmentEntity> CompartmentType<E> register(
            final CompartmentType<E> compartmentType, final Predicate<ItemStack> predicate) {
        COMPARTMENT_TYPES.add(new Pair<>(Objects.requireNonNull(compartmentType), Objects.requireNonNull(predicate)));
        return compartmentType;
    }

    /**
     * Gets an applicable {@link CompartmentType} for an {@link ItemStack}
     *
     * @param itemStack The {@link ItemStack}
     * @return An applicable compartment for the given item stack. Returns the first compartment found,
     * this means registry order can effect which is chosen
     */
    public static Optional<CompartmentType<?>> fromStack(final ItemStack itemStack) {
        if (!itemStack.is(AlekiShipsTags.Items.CAN_PLACE_IN_COMPARTMENTS)) return Optional.empty();

        for (final var predicatePair : COMPARTMENT_TYPES) {
            if (predicatePair.getB().test(itemStack)) return Optional.of(predicatePair.getA());
        }

        if (itemStack.getItem() instanceof BlockItem) {
            return Optional.of(CompartmentTypes.BLOCK_COMPARTMENT);
        }

        return Optional.empty();
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