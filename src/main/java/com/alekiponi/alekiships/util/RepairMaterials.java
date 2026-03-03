package com.alekiponi.alekiships.util;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;

import com.alekiponi.alekiships.common.entity.vehicle.AbstractVehicle;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;
import org.jetbrains.annotations.Unmodifiable;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.Tolerate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ToString
@EqualsAndHashCode
@Accessors(fluent = true)
@Getter(AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class RepairMaterials {

    public static final Codec<RepairMaterials> CODEC = ExtraCodecs.nonEmptyList(Material.CODEC.listOf())
            .xmap(RepairMaterials::new, RepairMaterials::materials);

    public static final StreamCodec<RegistryFriendlyByteBuf, RepairMaterials> STREAM_CODEC = Material.STREAM_CODEC.apply(
                    ByteBufCodecs.list())
            .map(RepairMaterials::new, RepairMaterials::materials);

    @Unmodifiable
    public final List<Material> materials;

    public static RepairMaterialsBuilder builder() {return new RepairMaterialsBuilder();}

    /**
     * @param vehicle     The vehicle entity
     * @param repairStack The repair stack
     *
     * @return If the provided stack can repair the given vehicle entity
     */
    public boolean isValidForRepair(final AbstractVehicle vehicle, final ItemStack repairStack) {
        return this.materials.stream().anyMatch(material -> material.isValidForRepair(vehicle, repairStack));
    }

    /**
     * @param vehicle The vehicle entity to repair
     * @param player  The player doing the repair attempt
     * @param hand    The hand being used
     *
     * @return {@link InteractionResult#SUCCESS} for a successful repair or {@link InteractionResult#PASS} if the
     * interaction did nothing.
     */
    public InteractionResult tryRepair(final AbstractVehicle vehicle, final Player player, final InteractionHand hand) {
        for (final Material material : this.materials) {
            final var repairResult = material.tryRepair(vehicle, player, hand);
            if (repairResult.consumesAction()) return repairResult;
        }
        return InteractionResult.PASS;
    }

    public static final class RepairMaterialsBuilder {

        private final ImmutableList.Builder<Material> materials = ImmutableList.builder();

        private RepairMaterialsBuilder() {}

        public RepairMaterialsBuilder material(final UnaryOperator<Material.MaterialBuilder> builderConsumer) {
            return this.material(builderConsumer.apply(Material.builder()));
        }

        public RepairMaterialsBuilder material(final Material.MaterialBuilder builder) {
            return this.material(builder.build());
        }

        public RepairMaterialsBuilder material(final Material material) {
            this.materials.add(material);
            return this;
        }

        public RepairMaterials build() {
            return new RepairMaterials(this.materials.build());
        }
    }

    /**
     * A material used for repairs
     */
    @Builder
    @ToString
    @EqualsAndHashCode
    @Accessors(fluent = true)
    @Getter(AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Material {

        public static final Codec<Material> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Ingredient.CODEC.fieldOf("repair_ingredient").forGetter(Material::repairIngredient),
                Codec.FLOAT.fieldOf("repair_amount").forGetter(Material::repairAmount),
                SoundEvent.CODEC.fieldOf("repair_sound").forGetter(Material::repairSound),
                RepairMaterials.DamageRange.CODEC.optionalFieldOf("damage_requirement", RepairMaterials.DamageRange.ANY)
                        .forGetter(Material::damageRange)).apply(instance, Material::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, Material> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC, Material::repairIngredient, ByteBufCodecs.FLOAT,
                Material::repairAmount, SoundEvent.STREAM_CODEC, Material::repairSound,
                RepairMaterials.DamageRange.STREAM_CODEC, Material::damageRange, Material::new);
        /**
         * An ingredient for the valid repair materials
         */
        public final Ingredient repairIngredient;
        /**
         * The amount to repair
         */
        public final float repairAmount;
        /**
         * The repair sound
         */
        public final Holder<SoundEvent> repairSound;
        /**
         * The range of damage values this material is valid for
         */
        @Builder.Default
        public final RepairMaterials.DamageRange damageRange = RepairMaterials.DamageRange.ANY;

        /**
         * @param vehicle     The vehicle entity
         * @param repairStack The repair stack
         *
         * @return If the provided stack can repair the given vehicle entity
         */
        private boolean isValidForRepair(final AbstractVehicle vehicle, final ItemStack repairStack) {
            if (!this.repairIngredient.test(repairStack)) return false;
            return this.damageRange.isInRange(vehicle.getDamage());
        }

        /**
         * @param vehicle The vehicle entity to repair
         * @param player  The player doing the repair attempt
         * @param hand    The hand being used
         *
         * @return {@link InteractionResult#SUCCESS} for a successful repair or {@link InteractionResult#PASS} if the
         * interaction did nothing.
         */
        private InteractionResult tryRepair(final AbstractVehicle vehicle, final Player player,
                final InteractionHand hand) {
            final ItemStack repairStack = player.getItemInHand(hand);
            if (!this.isValidForRepair(vehicle, repairStack)) return InteractionResult.PASS;

            if (player.hasInfiniteMaterials()) {
                vehicle.setDamage(0);
                return InteractionResult.SUCCESS;
            }

            repairStack.shrink(1);
            log.debug("Repaired {} by {} using {}", vehicle, this.repairAmount, repairStack.getItem());
            vehicle.setDamage(Math.clamp(vehicle.getDamage() - this.repairAmount, 0, vehicle.getDamageThreshold()));
            vehicle.level()
                    .playSound(null, vehicle, this.repairSound.value(), SoundSource.PLAYERS, 1.5F,
                            vehicle.getRandom().nextFloat() * 0.1F + 0.9F);
            return InteractionResult.SUCCESS;
        }

        public static final class MaterialBuilder {
            @Tolerate
            public MaterialBuilder repairSound(final SoundEvent repairSound) {
                return this.repairSound(BuiltInRegistries.SOUND_EVENT.getResourceKey(repairSound)
                        .flatMap(BuiltInRegistries.SOUND_EVENT::getHolder).orElseThrow());
            }
        }
    }

    /**
     * @param minDamage The minimum health
     * @param maxDamage The maximum health
     */
    public record DamageRange(Optional<Float> minDamage, Optional<Float> maxDamage) {

        public static final Codec<DamageRange> CODEC = RecordCodecBuilder.<DamageRange>create(
                instance -> instance.group(Codec.FLOAT.optionalFieldOf("min").forGetter(DamageRange::minDamage),
                                Codec.FLOAT.optionalFieldOf("max").forGetter(DamageRange::maxDamage))
                        .apply(instance, DamageRange::new)).validate(DamageRange::validate);

        public static final StreamCodec<ByteBuf, DamageRange> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.optional(ByteBufCodecs.FLOAT), DamageRange::minDamage,
                ByteBufCodecs.optional(ByteBufCodecs.FLOAT), DamageRange::maxDamage, DamageRange::new);

        public static final DamageRange ANY = new DamageRange(Optional.empty(), Optional.empty());

        private static DataResult<DamageRange> validate(final DamageRange damageRange) {
            // Neither field is present
            if (damageRange.minDamage.or(damageRange::maxDamage).isEmpty()) {
                return DataResult.error(() -> "Requires at least one of [min, max]");
            }

            return DataResult.success(damageRange);
        }

        public static DamageRange of(final float min, final float max) {
            return new DamageRange(Optional.of(min), Optional.of(max));
        }

        /**
         * @param min The minimum required damage
         */
        public static DamageRange min(final float min) {
            return new DamageRange(Optional.of(min), Optional.empty());
        }

        /**
         * @param max The maximum allowed damage
         */
        public static DamageRange max(final float max) {
            return new DamageRange(Optional.empty(), Optional.of(max));
        }

        public boolean isInRange(final float damage) {
            final var min = this.minDamage.orElse(0F);
            final var max = this.maxDamage.orElse(Float.MAX_VALUE);
            return damage > min && damage < max;
        }
    }
}