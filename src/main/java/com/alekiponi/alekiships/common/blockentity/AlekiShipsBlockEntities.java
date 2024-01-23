package com.alekiponi.alekiships.common.blockentity;

import com.alekiponi.alekiships.AlekiShips;
import net.dries007.tfc.util.registry.RegistrationHelpers;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;
import java.util.stream.Stream;


public class AlekiShipsBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES;

    static {
        BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, AlekiShips.MOD_ID);

    }

    public AlekiShipsBlockEntities() {
    }

    private static <T extends BlockEntity> RegistryObject<BlockEntityType<T>> register(String name,
            BlockEntityType.BlockEntitySupplier<T> factory,
            Supplier<? extends Block> block) {
        return RegistrationHelpers.register(BLOCK_ENTITIES, name, factory, block);
    }

    private static <T extends BlockEntity> RegistryObject<BlockEntityType<T>> register(String name,
            BlockEntityType.BlockEntitySupplier<T> factory,
            Stream<? extends Supplier<? extends Block>> blocks) {
        return RegistrationHelpers.register(BLOCK_ENTITIES, name, factory, blocks);
    }

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}