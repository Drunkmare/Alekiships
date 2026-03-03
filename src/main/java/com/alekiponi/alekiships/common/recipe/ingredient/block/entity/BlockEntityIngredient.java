package com.alekiponi.alekiships.common.recipe.ingredient.block.entity;

import com.mojang.serialization.Codec;

import com.alekiponi.alekiships.common.AlekiShipsBuiltInRegistries;
import com.alekiponi.alekiships.common.AlekiShipsRegistries;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.Optional;
import java.util.function.Predicate;
import org.jetbrains.annotations.Nullable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

public interface BlockEntityIngredient extends Predicate<@Nullable BlockEntity> {
    Codec<BlockEntityIngredient> CODEC = AlekiShipsBuiltInRegistries.BLOCK_ENTITY_RESULT_SERIALIZERS.byNameCodec()
            .dispatch(BlockEntityIngredient::getSerializer, BlockEntityIngredientSerializer::codec);
    StreamCodec<RegistryFriendlyByteBuf, BlockEntityIngredient> STREAM_CODEC = ByteBufCodecs.registry(
                    AlekiShipsRegistries.BLOCK_ENTITY_RESULT_SERIALIZER)
            .dispatch(BlockEntityIngredient::getSerializer, BlockEntityIngredientSerializer::streamCodec);

    /**
     * Try and cast the provided BlockEntity to whatever the Block Entity Type is for
     *
     * @param blockEntityType The block entity type
     * @param blockEntity     The block entity
     */
    @SuppressWarnings("unchecked")
    static <T extends BlockEntity> Optional<T> tryCast(final BlockEntityType<T> blockEntityType,
            final BlockEntity blockEntity) {
        return blockEntityType == blockEntity.getType() ? Optional.of(((T) blockEntity)) : Optional.empty();
    }

    /**
     * {@inheritDoc}
     *
     * @implSpec A {@code null} {@link BlockEntity} is a valid and expected case. How exactly this should be handled is up to
     * implementation, for example no BlockEntity might be valid but if there is one it must have certain data present.
     * Matching against NBT is intentionally unsupported, it complicates the API and is a generally expensive test
     */
    @Override
    boolean test(@Nullable BlockEntity blockEntity);

    /**
     * @param blockEntity A block Entity to initialize.
     *
     * @implSpec Must do any setup required for the {@link #test(BlockEntity)} implementation to return true. Only
     * expected to be used by the multiblock placing logic and visualizations
     */
    void initialize(BlockEntity blockEntity);

    BlockEntityIngredientSerializer<?> getSerializer();

    /**
     * A {@link BlockEntityIngredient} which will only match if a BE exists and passes {@link #matches(BlockEntity)}
     */
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    abstract class NeverNullBlockEntityIngredient<B extends BlockEntity> implements BlockEntityIngredient {

        private final BlockEntityType<B> type;

        @Override
        public final boolean test(@Nullable final BlockEntity blockEntity) {
            return blockEntity != null && BlockEntityIngredient.tryCast(this.type, blockEntity)
                    .map(this::matches).orElse(false);
        }

        @Override
        public void initialize(final BlockEntity blockEntity) {
            BlockEntityIngredient.tryCast(this.type, blockEntity).ifPresent(this::setup);
        }

        /**
         * @param blockEntity The block entity
         *
         * @return If the provided BlockEntity matches
         */
        protected abstract boolean matches(B blockEntity);

        /**
         * @param blockEntity The block entity
         */
        protected abstract void setup(B blockEntity);
    }

    /**
     * A {@link BlockEntityIngredient} which matches {@code null} BEs but if one exists must pass {@link #matches(BlockEntity)}
     */
    @SuppressWarnings("unused")
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    abstract class MaybeNullBlockEntityIngredient<B extends BlockEntity> implements BlockEntityIngredient {

        private final BlockEntityType<B> type;

        @Override
        public final boolean test(@Nullable final BlockEntity blockEntity) {
            return blockEntity == null || BlockEntityIngredient.tryCast(this.type, blockEntity)
                    .map(this::matches).orElse(false);
        }

        @Override
        public final void initialize(final BlockEntity blockEntity) {
            BlockEntityIngredient.tryCast(this.type, blockEntity).ifPresent(this::setup);
        }

        /**
         * @param blockEntity The block entity
         *
         * @return If the provided BlockEntity matches
         */
        protected abstract boolean matches(B blockEntity);

        /**
         * @param blockEntity The block entity
         */
        protected abstract void setup(B blockEntity);
    }
}