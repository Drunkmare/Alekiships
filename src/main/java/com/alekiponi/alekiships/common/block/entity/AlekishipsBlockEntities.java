package com.alekiponi.alekiships.common.block.entity;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.common.block.AlekiShipsBlocks;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Arrays;
import java.util.function.Supplier;
import java.util.stream.Stream;

public final class AlekishipsBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(
            Registries.BLOCK_ENTITY_TYPE, AlekiShips.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FrameBlockEntity>> FRAME_BLOCK = register(
            "frame_block", FrameBlockEntity::new, AlekiShipsBlocks.WOODEN_BOAT_FRAME_ANGLED,
            AlekiShipsBlocks.WOODEN_BOAT_FRAME_FLAT);

    @SafeVarargs
    private static <E extends BlockEntity> DeferredHolder<BlockEntityType<?>, BlockEntityType<E>> register(
            final String name, final BlockEntityType.BlockEntitySupplier<E> factory,
            final Supplier<? extends Block>... validBlocks) {
        return registerBlocks(name, factory, Arrays.stream(validBlocks)
                .map(Supplier::get));
    }

    private static <E extends BlockEntity> DeferredHolder<BlockEntityType<?>, BlockEntityType<E>> registerBlocks(
            final String name, final BlockEntityType.BlockEntitySupplier<E> factory,
            final Stream<? extends Block> validBlocks) {
        return BLOCK_ENTITIES.register(name,
                () -> BlockEntityType.Builder.of(factory, validBlocks.toArray(Block[]::new))
                        .build(null));
    }
}