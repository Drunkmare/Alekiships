package com.alekiponi.alekiships.network;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.common.entity.EntityInput;
import com.alekiponi.alekiships.common.entity.SloopConstructionState;
import com.alekiponi.alekiships.wind.Wind;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.world.item.DyeColor;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.Optional;
import java.util.function.Supplier;

public final class AlekiShipsEntityDataSerializers {

    public static final DeferredRegister<EntityDataSerializer<?>> ENTITY_DATA_SERIALIZERS = DeferredRegister.create(
            NeoForgeRegistries.ENTITY_DATA_SERIALIZERS, AlekiShips.MOD_ID);

    public static final Supplier<EntityDataSerializer<DyeColor>> DYE_COLOR = register("dye_color",
            () -> EntityDataSerializer.forValueType(DyeColor.STREAM_CODEC));

    public static final Supplier<EntityDataSerializer<Wind>> WIND = register("wind",
            () -> EntityDataSerializer.forValueType(Wind.STREAM_CODEC));

    public static final DeferredHolder<EntityDataSerializer<?>, EntityDataSerializer<EntityInput.EntityInputState>> ENTITY_INPUT_STATE = register(
            "entity_input_state", () -> EntityDataSerializer.forValueType(EntityInput.EntityInputState.STREAM_CODEC));

    public static final DeferredHolder<EntityDataSerializer<?>, EntityDataSerializer<SloopConstructionState>> SLOOP_CONSTRUCTION_STATE = register(
            "sloop_construction_state", () -> EntityDataSerializer.forValueType(SloopConstructionState.STREAM_CODEC));

    public static final Supplier<EntityDataSerializer<Optional<DyeColor>>> OPTIONAL_DYE_COLOR = register(
            "optional_dye_color",
            () -> EntityDataSerializer.forValueType(ByteBufCodecs.optional(DyeColor.STREAM_CODEC)));

    private static <T extends EntityDataSerializer<?>> DeferredHolder<EntityDataSerializer<?>, T> register(
            final String name, final Supplier<T> dataSerializer) {
        return ENTITY_DATA_SERIALIZERS.register(name, dataSerializer);
    }
}