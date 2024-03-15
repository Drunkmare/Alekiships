package com.alekiponi.alekiships.common.entity.vehiclehelper;

import com.alekiponi.alekiships.common.entity.AlekiShipsEntities;
import com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.AbstractCompartmentEntity;
import com.alekiponi.alekiships.util.AlekiShipsTags;
import com.google.common.collect.ImmutableSet;
import net.minecraft.Util;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.flag.FeatureFlag;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.network.PlayMessages;
import oshi.util.tuples.Pair;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

/**
 * Besides {@link #register(Supplier, Predicate)} and {@link #fromStack(ItemStack)} This is essentially to keep the vanilla
 * {@link EntityType#create(Level)} api but with an additional ItemStack parameter to allow for constructor logic based
 * on the ItemStack. For example using the stack NBT to initialize the compartment
 *
 * @param <T> The type of compartment
 */
public class CompartmentType<T extends AbstractCompartmentEntity> extends EntityType<T> {
    private static final ArrayList<Pair<Supplier<? extends CompartmentType<? extends AbstractCompartmentEntity>>, Predicate<ItemStack>>> COMPARTMENT_TYPES = new ArrayList<>();
    private final CompartmentFactory<T> factory;

    @SuppressWarnings("unused")
    public CompartmentType(final EntityFactory<T> entityFactory, final MobCategory mobCategory, final boolean serialize,
            final boolean summon, final boolean fireImmune, final boolean canSpawnFarFromPlayer,
            final ImmutableSet<Block> immuneTo, final EntityDimensions dimensions, final int clientTrackingRange,
            final int updateInterval, final FeatureFlagSet requiredFeatures,
            final CompartmentFactory<T> compartmentFactory) {
        super(entityFactory, mobCategory, serialize, summon, fireImmune, canSpawnFarFromPlayer, immuneTo, dimensions,
                clientTrackingRange, updateInterval, requiredFeatures);
        this.factory = compartmentFactory;
    }

    public CompartmentType(final EntityFactory<T> entityFactory, final MobCategory mobCategory, final boolean serialize,
            final boolean summon, final boolean fireImmune, final boolean canSpawnFarFromPlayer,
            final ImmutableSet<Block> immuneTo, final EntityDimensions dimensions, final int clientTrackingRange,
            final int updateInterval, final FeatureFlagSet requiredFeatures,
            final Predicate<EntityType<?>> velocityUpdateSupplier,
            final ToIntFunction<EntityType<?>> trackingRangeSupplier,
            final ToIntFunction<EntityType<?>> updateIntervalSupplier,
            @Nullable final BiFunction<PlayMessages.SpawnEntity, Level, T> customClientFactory,
            final CompartmentFactory<T> factory) {
        //noinspection DataFlowIssue    customClientFactory can be null
        super(entityFactory, mobCategory, serialize, summon, fireImmune, canSpawnFarFromPlayer, immuneTo, dimensions,
                clientTrackingRange, updateInterval, requiredFeatures, velocityUpdateSupplier, trackingRangeSupplier,
                updateIntervalSupplier, customClientFactory);
        this.factory = factory;
    }

    /**
     * Registers a {@link CompartmentType} to be automatically picked and constructed when empty compartments are
     * right-clicked with an {@link ItemStack} matching the CompartmentTypes ItemStack predicate.
     *
     * @param compartmentTypeSupplier A supplier for the compartment type
     * @param predicate               The ItemStack predicate that determines if the compartment type should be chosen
     * @return The same supplier which was passed in to allow for simplification of registration code.
     * @apiNote The predicate should be as exact as possible.
     * <p>
     * You may register the same {@link CompartmentType} multiple times. This can be useful if you have for example a
     * custom furnace that only has a different texture/model as {@link AlekiShipsEntities#FURNACE_COMPARTMENT_ENTITY}
     * will display any block. Custom behavior will however require a custom compartment entity.
     */
    public static <S extends Supplier<? extends CompartmentType<?>>> S register(final S compartmentTypeSupplier,
            final Predicate<ItemStack> predicate) {
        COMPARTMENT_TYPES.add(
                new Pair<>(Objects.requireNonNull(compartmentTypeSupplier), Objects.requireNonNull(predicate)));
        return compartmentTypeSupplier;
    }

    /**
     * Gets an applicable compartment type for an item stack
     *
     * @param itemStack The item stack
     * @return An applicable compartment for the given item stack. Returns the first compartment found,
     * this means registry order can effect which is chosen
     */
    public static Optional<CompartmentType<?>> fromStack(final ItemStack itemStack) {
        if (!itemStack.is(AlekiShipsTags.Items.CAN_PLACE_IN_COMPARTMENTS)) return Optional.empty();

        for (final var predicatePair : COMPARTMENT_TYPES) {
            if (predicatePair.getB().test(itemStack)) return Optional.of(predicatePair.getA().get());
        }

        if (itemStack.getItem() instanceof BlockItem) {
            return Optional.of(AlekiShipsEntities.BLOCK_COMPARTMENT_ENTITY.get());
        }

        return Optional.empty();
    }

    /**
     * Create a Compartment Entity for this CompartmentType.
     * Respects the levels enabled features
     */
    @Nullable
    public T create(final Level level, final ItemStack itemStack) {
        return !this.isEnabled(level.enabledFeatures()) ? null : this.factory.create(this, level, itemStack);
    }

    /**
     * Like vanillas {@link EntityType.EntityFactory} but takes an additional {@link ItemStack} parameter
     *
     * @param <T> The type of compartment
     */
    public interface CompartmentFactory<T extends AbstractCompartmentEntity> {
        T create(final CompartmentType<T> entityType, final Level level, final ItemStack itemStack);
    }

    /**
     * Mostly a copy of {@link EntityType.Builder} but it additionally takes a compartment
     * factory and an item stack predicate
     */
    @SuppressWarnings({"unused", "UnusedReturnValue"})
    public static class Builder<T extends AbstractCompartmentEntity> extends EntityType.Builder<T> {
        private final CompartmentFactory<T> compartmentFactory;
        private Predicate<EntityType<?>> velocityUpdateSupplier = entityType -> true;
        private ToIntFunction<EntityType<?>> trackingRangeSupplier = entityType -> entityType.clientTrackingRange;
        private ToIntFunction<EntityType<?>> updateIntervalSupplier = entityType -> entityType.updateInterval;
        @Nullable
        private BiFunction<PlayMessages.SpawnEntity, Level, T> customClientFactory;

        private Builder(final EntityType.EntityFactory<T> entityFactory, final CompartmentFactory<T> compartmentFactory,
                final MobCategory mobCategory) {
            super(entityFactory, mobCategory);
            this.compartmentFactory = compartmentFactory;
        }

        /**
         * Overload for {@link MobCategory#MISC}
         */
        public static <T extends AbstractCompartmentEntity> Builder<T> of(final EntityFactory<T> entityFactory,
                final CompartmentFactory<T> compartmentFactory) {
            return new Builder<>(entityFactory, compartmentFactory, MobCategory.MISC);
        }

        public static <T extends AbstractCompartmentEntity> Builder<T> of(final EntityFactory<T> entityFactory,
                final CompartmentFactory<T> compartmentFactory, final MobCategory mobCategory) {
            return new Builder<>(entityFactory, compartmentFactory, mobCategory);
        }

        /**
         * Overload for {@link MobCategory#MISC}
         */
        public static <T extends AbstractCompartmentEntity> Builder<T> createBasic(
                final EntityFactory<T> entityFactory) {
            return createBasic(entityFactory, MobCategory.MISC);
        }

        /**
         * Creates a compartment with an empty {@link CompartmentFactory}
         */
        public static <T extends AbstractCompartmentEntity> Builder<T> createBasic(final EntityFactory<T> entityFactory,
                final MobCategory mobCategory) {
            //noinspection DataFlowIssue
            return of(entityFactory, (entityType, level, itemStack) -> null, mobCategory);
        }

        /**
         * This is named stupid because type erasure won't let this shadow {@link EntityType.Builder#createNothing(MobCategory)}
         * Honestly this method probably isn't ever even going to get used, but I'd hate for somebody to need it
         */
        public static <T extends AbstractCompartmentEntity> Builder<T> createNothing2(final MobCategory mobCategory) {
            //noinspection DataFlowIssue
            return of((entityType, level) -> null, (entityType, level, itemStack) -> null, mobCategory);
        }

        @Override
        public Builder<T> sized(final float pWidth, final float pHeight) {
            return (Builder<T>) super.sized(pWidth, pHeight);
        }

        @Override
        public Builder<T> noSummon() {
            return (Builder<T>) super.noSummon();
        }

        @Override
        public Builder<T> noSave() {
            return (Builder<T>) super.noSave();
        }

        @Override
        public Builder<T> fireImmune() {
            return (Builder<T>) super.fireImmune();
        }

        @Override
        public Builder<T> immuneTo(final Block... pBlocks) {
            return (Builder<T>) super.immuneTo(pBlocks);
        }

        @Override
        public Builder<T> canSpawnFarFromPlayer() {
            return (Builder<T>) super.canSpawnFarFromPlayer();
        }

        @Override
        public Builder<T> clientTrackingRange(final int pClientTrackingRange) {
            return (Builder<T>) super.clientTrackingRange(pClientTrackingRange);
        }

        @Override
        public Builder<T> updateInterval(final int pUpdateInterval) {
            return (Builder<T>) super.updateInterval(pUpdateInterval);
        }

        @Override
        public Builder<T> requiredFeatures(final FeatureFlag... pRequiredFeatures) {
            return (Builder<T>) super.requiredFeatures(pRequiredFeatures);
        }

        @Override
        public Builder<T> setUpdateInterval(final int interval) {
            this.updateIntervalSupplier = t -> interval;
            return this;
        }

        @Override
        public Builder<T> setTrackingRange(final int range) {
            this.trackingRangeSupplier = t -> range;
            return this;
        }

        @Override
        public Builder<T> setShouldReceiveVelocityUpdates(final boolean value) {
            this.velocityUpdateSupplier = t -> value;
            return this;
        }

        @Override
        public Builder<T> setCustomClientFactory(
                final BiFunction<PlayMessages.SpawnEntity, Level, T> customClientFactory) {
            this.customClientFactory = customClientFactory;
            return this;
        }

        @Override
        public CompartmentType<T> build(final String key) {
            if (this.serialize) {
                Util.fetchChoiceType(References.ENTITY_TREE, key);
            }

            return new CompartmentType<>(this.factory, this.category, this.serialize, this.summon, this.fireImmune,
                    this.canSpawnFarFromPlayer, this.immuneTo, this.dimensions, this.clientTrackingRange,
                    this.updateInterval, this.requiredFeatures, this.velocityUpdateSupplier, this.trackingRangeSupplier,
                    this.updateIntervalSupplier, this.customClientFactory, this.compartmentFactory);
        }
    }
}