package com.alekiponi.alekiships.common.entity;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.common.entity.vehicle.SloopUnderConstructionEntity;
import com.alekiponi.alekiships.common.item.AlekiShipsItems;
import com.alekiponi.alekiships.common.item.CannonItem;
import com.alekiponi.alekiships.network.AlekiShipsEntityDataSerializers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.common.util.NeoForgeExtraCodecs;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;

public final class EntityInput {

    /**
     * The max amount of different inputs allowed. Some (as of writing all of ours in Nifty) entities want to consume
     * and store the items used so they can drop the used items on destruction or other such things. Because of this
     * we should try to keep this high enough so that it shouldn't really ever be an issue but also not unnecessarily
     * high to minimize unused space.
     */
    public static final int MAXIMUM_SIZE = 9;
    /// An entity input with no inputs.
    public static final EntityInput EMPTY = new EntityInput(List.of());

    public static final ResourceKey<Registry<EntityInput>> KEY = ResourceKey.createRegistryKey(
            AlekiShips.location("entity_input"));

    public static final Codec<EntityInput> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    NeoForgeExtraCodecs.withAlternative(SizedIngredient.FLAT_CODEC, SizedIngredient.NESTED_CODEC)
                            .sizeLimitedListOf(MAXIMUM_SIZE).fieldOf("inputs").forGetter(entityInput -> entityInput.inputs))
            .apply(instance, EntityInput::of));

    private final List<SizedIngredient> inputs;

    private EntityInput(final List<SizedIngredient> inputs) {
        this.inputs = inputs;
    }

    public static EntityInput of(final List<SizedIngredient> inputs) {
        return inputs.isEmpty() ? EMPTY : new EntityInput(inputs);
    }

    public static EntityInput of(final SizedIngredient... inputs) {
        return of(List.of(inputs));
    }

    /**
     * Helper to get a {@link EntityInput} from a {@link HolderLookup.Provider}
     *
     * @return The {@link EntityInput} associated with the {@link ResourceKey} defaulting to {@link #EMPTY}
     */
    public static EntityInput getEntityInput(final HolderLookup.Provider provider,
            final ResourceKey<EntityInput> entityInputKey) {
        return provider.lookup(KEY).flatMap(registryLookup -> registryLookup.get(entityInputKey)).map(Holder::value)
                .orElse(EMPTY);
    }

    @CheckReturnValue
    private static ItemStack doInsert(final InputEntity inputEntity, final ItemStack insertStack,
            final @Nullable LivingEntity entity, @SuppressWarnings("SameParameterValue") final int increment) {
        final var stage = inputEntity.getInputState().inputStage;
        if (entity != null && entity.hasInfiniteMaterials()) {
            inputEntity.insert(stage, insertStack.copyWithCount(increment));
            return insertStack;
        }

        return inputEntity.insert(stage, insertStack);
    }

    public static void bootstrap(final BootstrapContext<EntityInput> context) {
        context.register(CannonItem.DEFAULT_CANNON_INPUT_KEY, of(SizedIngredient.of(AlekiShipsItems.CANNONBALL, 1)));
    }

    /**
     * Attempt to insert the provided stack for the provided {@link InputEntity}
     *
     * @param inputEntity The {@link InputEntity}
     * @param insertStack The stack to insert. Not mutated
     * @param entity      The entity which is trying to insert the stack. {@code null} when not applicable
     * @return A {@link InteractionResultHolder} with the result of the interaction and remainder
     */
    @CheckReturnValue
    public InteractionResultHolder<ItemStack> tryInsert(final InputEntity inputEntity, final ItemStack insertStack,
            final @Nullable LivingEntity entity) {
        if (this.isFinished(inputEntity)) return InteractionResultHolder.pass(insertStack);

        final var inputState = inputEntity.getInputState();
        final var inputStage = inputState.inputStage();
        assert inputStage < this.getTotalInputs();

        final var ingredient = this.getIngredient(inputStage);
        // We only care to match the Item, not it's size
        if (!ingredient.ingredient().test(insertStack)) return InteractionResultHolder.pass(insertStack);

        final ItemStack remainder = EntityInput.doInsert(inputEntity, insertStack, entity, 1);
        if (ingredient.count() < inputEntity.getContentsCount(inputStage)) {
            inputEntity.setInputState(inputState.withRemaining(inputState.remainingInputs - 1));
            return InteractionResultHolder.success(remainder);
        }

        inputEntity.setInputState(inputState.nextStateOf(this));
        return InteractionResultHolder.success(remainder);
    }

    /**
     * @return If the provided {@link InputEntity} is considered finished for this input
     */
    public boolean isFinished(final InputEntity inputEntity) {
        return inputEntity.getInputState().inputStage() >= this.getTotalInputs();
    }

    /**
     * @return The {@link SizedIngredient} for the input index
     */
    public SizedIngredient getIngredient(final @Range(from = 0, to = Integer.MAX_VALUE) int input) {
        assert !this.inputs.isEmpty();
        assert this.inputs.size() > input : "Input index exceeds input size";
        return this.inputs.get(input);
    }

    /**
     * @return The total inputs
     */
    public int getTotalInputs() {
        return this.inputs.size();
    }

    /**
     * An entity which uses {@link EntityInput} for some sort of data driven interaction such as our {@link CannonEntity}
     */
    public interface InputEntity {

        /**
         * Inserts a stack in the backing storage for the current stage.
         *
         * @param stage       The stage
         * @param insertStack The {@link ItemStack} to insert <strong>Do not mutate this</strong>
         * @return The remainder of the insertion
         */
        ItemStack insert(int stage, ItemStack insertStack);

        /**
         * Gets the current contents in the backing storage for the current stage
         *
         * @param stage The stage
         * @return How many contents this stage has
         */
        int getContentsCount(int stage);

        /**
         * @return The {@link EntityInputState}
         */
        EntityInputState getInputState();

        /**
         * @param entityInputState The {@link EntityInputState}
         */
        void setInputState(EntityInputState entityInputState);
    }

    /**
     * Simple object encapsulating important state for any {@link InputEntity}s that wish to render anything on the
     * client like our {@link CannonEntity} or {@link SloopUnderConstructionEntity} do.
     * <p>
     * An {@link EntityDataSerializer} is provided via {@link AlekiShipsEntityDataSerializers#ENTITY_INPUT_STATE}
     *
     * @param entityInputKey  The {@link EntityInput} {@link ResourceKey}
     * @param inputStage      The current input stage
     * @param remainingInputs The remaining input count for the current inputs ingredient
     */
    public record EntityInputState(ResourceKey<EntityInput> entityInputKey,
                                   @Range(from = 0, to = Integer.MAX_VALUE) int inputStage,
                                   @Range(from = 0, to = Integer.MAX_VALUE) int remainingInputs) {

        public static final StreamCodec<ByteBuf, EntityInputState> STREAM_CODEC = StreamCodec.composite(
                ResourceKey.streamCodec(KEY), EntityInputState::entityInputKey, ByteBufCodecs.VAR_INT,
                EntityInputState::inputStage, ByteBufCodecs.VAR_INT, EntityInputState::remainingInputs,
                EntityInputState::new);

        public static final String ENTITY_INPUT_KEY = "EntityInput";
        public static final String CURRENT_INPUT_KEY = "CurrentInput";
        public static final String REMAINING_COUNT_KEY = "RemainingCount";
        public static final String INPUT_STATE_KEY = "InputState";

        public static final Codec<EntityInputState> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                        ResourceKey.codec(KEY).fieldOf(ENTITY_INPUT_KEY).forGetter(EntityInputState::entityInputKey),
                        Codec.INT.optionalFieldOf(CURRENT_INPUT_KEY, 0).forGetter(EntityInputState::inputStage),
                        Codec.INT.optionalFieldOf(REMAINING_COUNT_KEY, 0).forGetter(EntityInputState::remainingInputs))
                .apply(instance, EntityInputState::new));

        /**
         * Helper to get the initial, default InputState for an input key.
         *
         * @return An {@link EntityInputState} correctly initialized with the default state
         */
        public static EntityInputState getInitialState(final HolderLookup.Provider provider,
                final ResourceKey<EntityInput> entityInputKey) {
            final var constructionInput = EntityInput.getEntityInput(provider, entityInputKey);
            final int count = constructionInput.getTotalInputs() < 1 ? 0 : constructionInput.getIngredient(0).count();

            return new EntityInputState(entityInputKey, 0, count);
        }

        public static Optional<EntityInputState> load(final CompoundTag compoundTag) {
            return CODEC.parse(NbtOps.INSTANCE, compoundTag.get(INPUT_STATE_KEY)).result();
        }

        public static EntityInputState forInput(final ResourceKey<EntityInput> entityInputKey) {
            return new EntityInputState(entityInputKey, 0, 0);
        }

        /**
         * Saves this {@link EntityInput} to the provided {@link CompoundTag}
         *
         * @param compoundTag The {@link CompoundTag}
         */
        public void save(final CompoundTag compoundTag) {
            CODEC.encodeStart(NbtOps.INSTANCE, this).ifSuccess(tag -> compoundTag.put(INPUT_STATE_KEY, tag));
        }

        public EntityInputState reset() {
            return forInput(this.entityInputKey);
        }

        public EntityInputState withRemaining(final int remaining) {
            return new EntityInputState(this.entityInputKey, this.inputStage, remaining);
        }

        public EntityInputState nextStateOf(final EntityInput entityInput) {
            final int nextInput = this.inputStage + 1;
            assert nextInput > entityInput.getTotalInputs() : MessageFormat.format(
                    "Next input of {0} exceeds valid range of [0,{1})", nextInput, entityInput.getTotalInputs());
            return new EntityInputState(this.entityInputKey, nextInput, entityInput.getIngredient(nextInput).count());
        }
    }
}