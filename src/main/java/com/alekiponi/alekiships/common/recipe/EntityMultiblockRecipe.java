package com.alekiponi.alekiships.common.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.common.AlekiShipsBuiltInRegistries;
import com.alekiponi.alekiships.common.recipe.entity.EntityResult;
import com.alekiponi.alekiships.mixins.accessors.RecipeManagerAccessor;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.Optional;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Builder
@AllArgsConstructor
public class EntityMultiblockRecipe extends NoopRecipe {

    public static final MapCodec<EntityMultiblockRecipe> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(MultiblockPattern.MAP_CODEC.forGetter(EntityMultiblockRecipe::getPattern),
                            EntityResult.CODEC.fieldOf("entity").forGetter(EntityMultiblockRecipe::getEntityResult))
                    .apply(instance, EntityMultiblockRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, EntityMultiblockRecipe> STREAM_CODEC = StreamCodec.composite(
            MultiblockPattern.STREAM_CODEC, EntityMultiblockRecipe::getPattern, EntityResult.STREAM_CODEC,
            EntityMultiblockRecipe::getEntityResult, EntityMultiblockRecipe::new);

    public static final String SUCCESSFULLY_ASSEMBLED = AlekiShips.MOD_ID + ".entityMultiblock.assembled";
    public static final String FAILED_TO_ASEMBLE = AlekiShips.MOD_ID + ".entityMultiblock.matchFail";

    private final MultiblockPattern pattern;
    private final EntityResult entityResult;

    /**
     * @param level          The level
     * @param multiblockRoot The root block pos
     *
     * @return The best matching entity multiblock
     */
    public static Optional<EntityMultiblockRecipeMatchResult> getBestMatch(final Level level,
            final BlockPos multiblockRoot) {
        final var profiler = level.getProfiler();
        profiler.push("entityMultiblockValidation");
        final var cache = PatternBlockStateCache.create(level);
        final var maybeMatch = ((RecipeManagerAccessor) level.getRecipeManager()).invoke$byType(
                        AlekiShipsRecipeTypes.ENTITY_MULTIBLOCK_RECIPE.get())
                .stream()
                .map(entityMultiblockRecipe -> matches(entityMultiblockRecipe, multiblockRoot, cache))
                .max(EntityMultiblockRecipeMatchResult::compareTo);
        profiler.pop();
        return maybeMatch;
    }

    /**
     * @param multiblockRecipe The multiblock recipe
     * @param startPos         The staring position (usually the block which was placed/interacted with)
     * @param cache            The cache
     */
    public static EntityMultiblockRecipeMatchResult matches(final RecipeHolder<EntityMultiblockRecipe> multiblockRecipe,
            final BlockPos startPos, final PatternBlockStateCache cache) {
        return new EntityMultiblockRecipeMatchResult(multiblockRecipe,
                multiblockRecipe.value().pattern.matches(startPos, cache));
    }

    /**
     * @param level    The level
     * @param blockPos The block pos to check for a multiblock
     * @param player   The player to inform of the result
     */
    public static void tryAssembleMultiblock(final Level level, final BlockPos blockPos, @Nullable final Player player,
            final boolean reportNoMatch) {
        if (level.isClientSide) return;

        final var bestMatch = getBestMatch(level, blockPos);

        final var profiler = level.getProfiler();
        profiler.push("entityMultiblockAssembly");
        bestMatch.ifPresent(match -> {
            if (match.success()) {
                match.assemble(level);
                if (player != null) {
                    player.sendSystemMessage(
                            Component.translatable(SUCCESSFULLY_ASSEMBLED, match.multiblockRecipe.toString()));
                }
                return;
            }
            if (reportNoMatch && player != null) {
                player.sendSystemMessage(Component.translatable(FAILED_TO_ASEMBLE, match.multiblockRecipe.toString()));
            }
        });
        profiler.pop();
    }

    /**
     * Assemble the entity
     *
     * @param level The level
     *
     * @return An optional containing the entity or empty if it was unable to be constructed
     */
    public Optional<Entity> assemble(final Level level) {
        return this.entityResult.createEntity(level);
    }

    /**
     * Assemble the recipe with the multiblock match result
     *
     * @param level              The level
     * @param patternMatchResult The pattern match result
     */
    public void assemble(final Level level, final MultiblockPatternMatchResult patternMatchResult) {
        patternMatchResult.assemble(level, () -> this.assemble(level).ifPresentOrElse(e -> {
            patternMatchResult.positionEntity(e);
            level.addFreshEntity(e);
        }, () -> log.warn("Broken Entity Multiblock recipe entity result {} with serializer {}", this.entityResult,
                AlekiShipsBuiltInRegistries.ENTITY_RESULT_SERIALIZERS.getKey(this.entityResult.getSerializer()))));
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return AlekiShipsRecipeSerializers.ENTITY_MULTIBLOCK_RECIPE.get();
    }

    @Override
    public RecipeType<?> getType() {
        return AlekiShipsRecipeTypes.ENTITY_MULTIBLOCK_RECIPE.get();
    }

    public record EntityMultiblockRecipeMatchResult(RecipeHolder<EntityMultiblockRecipe> multiblockRecipe,
            MultiblockPatternMatchResult patternMatchResult) implements Comparable<EntityMultiblockRecipeMatchResult> {

        public void assemble(final Level level) {
            if (this.failed()) return;
            this.multiblockRecipe.value().assemble(level, this.patternMatchResult);
        }

        /**
         * @return If this match result was a failure
         */
        @CheckReturnValue
        public boolean failed() {
            return this.patternMatchResult.failed();
        }

        /**
         * @return If this match result was a success
         */
        @CheckReturnValue
        public boolean success() {
            return this.patternMatchResult.success();
        }

        @Override
        public int compareTo(final EntityMultiblockRecipeMatchResult o) {
            return this.patternMatchResult.compareTo(o.patternMatchResult);
        }
    }
}