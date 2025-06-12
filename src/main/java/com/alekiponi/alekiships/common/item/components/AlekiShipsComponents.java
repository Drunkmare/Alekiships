package com.alekiponi.alekiships.common.item.components;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.common.AlekiShipsRegistries;
import com.alekiponi.alekiships.common.compartment.CompartmentPlaceable;
import com.alekiponi.alekiships.common.entity.EntityInput;
import com.alekiponi.alekiships.util.AlekiShipsExtraCodecs;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.UnaryOperator;

public final class AlekiShipsComponents {

    public static final DeferredRegister.DataComponents COMPONENTS = DeferredRegister.createDataComponents(
            AlekiShips.MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ResourceKey<EntityInput>>> ENTITY_INPUT = register(
            "entity_input", builder -> builder.persistent(ResourceKey.codec(AlekiShipsRegistries.ENTITY_INPUT))
                    .networkSynchronized(ResourceKey.streamCodec(AlekiShipsRegistries.ENTITY_INPUT))
                    .cacheEncoding());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<CompartmentPlaceable<?>>> COMPARTMENT_PLACEABLE = register(
            "compartment_placeable", builder -> builder.persistent(CompartmentPlaceable.CODEC)
                    .networkSynchronized(CompartmentPlaceable.STREAM_CODEC)
                    .cacheEncoding());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<BlockState>> BLOCK_COMPARTMENT_BLOCK = register(
            "block_compartment_block", builder -> builder.persistent(AlekiShipsExtraCodecs.BLOCK_STATE_CODEC)
                    .networkSynchronized(ByteBufCodecs.idMapper(Block.BLOCK_STATE_REGISTRY))
                    .cacheEncoding());

    private static <T> DeferredHolder<DataComponentType<?>, DataComponentType<T>> register(final String name,
            final UnaryOperator<DataComponentType.Builder<T>> builder) {
        return COMPONENTS.registerComponentType(name, builder);
    }
}