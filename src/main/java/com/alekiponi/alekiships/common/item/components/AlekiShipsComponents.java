package com.alekiponi.alekiships.common.item.components;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.common.entity.EntityInput;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class AlekiShipsComponents {

    public static final DeferredRegister.DataComponents COMPONENTS = DeferredRegister.createDataComponents(
            AlekiShips.MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ResourceKey<EntityInput>>> ENTITY_INPUT = COMPONENTS.registerComponentType(
            "entity_input", builder -> builder.persistent(ResourceKey.codec(EntityInput.KEY))
                    .networkSynchronized(ResourceKey.streamCodec(EntityInput.KEY)).cacheEncoding());
}