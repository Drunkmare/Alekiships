package com.alekiponi.alekiships.common.recipe.ingredient.block.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import com.alekiponi.alekiships.common.AlekiShipsRegistries;
import com.alekiponi.alekiships.common.block.entity.AlekishipsBlockEntities;
import com.alekiponi.alekiships.common.block.entity.FrameBlockEntity;
import com.alekiponi.alekiships.util.FrameMaterial;

import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import lombok.AccessLevel;
import lombok.Getter;

public final class FrameBlockEntityIngredient extends BlockEntityIngredient.NeverNullBlockEntityIngredient<FrameBlockEntity> {

    public static final MapCodec<FrameBlockEntityIngredient> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                            FrameMaterial.CODEC.fieldOf("material").forGetter(FrameBlockEntityIngredient::getMaterial))
                    .apply(instance, FrameBlockEntityIngredient::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, FrameBlockEntityIngredient> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.holderRegistry(AlekiShipsRegistries.FRAME_MATERIAL), FrameBlockEntityIngredient::getMaterial,
            FrameBlockEntityIngredient::new);

    @Getter(AccessLevel.PRIVATE)
    private final Holder<FrameMaterial> material;

    public FrameBlockEntityIngredient(final Holder<FrameMaterial> material) {
        super(AlekishipsBlockEntities.FRAME_BLOCK.get());
        this.material = material;
    }

    @Override
    protected boolean matches(final FrameBlockEntity blockEntity) {
        return blockEntity.getMaterial().equals(this.material.value());
    }

    @Override
    protected void setup(final FrameBlockEntity blockEntity) {
        blockEntity.setFrameMaterial(this.material);
    }

    @Override
    public BlockEntityIngredientSerializer<?> getSerializer() {
        return AlekiShipsBlockEntityIngredientSerializers.FRAME.get();
    }
}