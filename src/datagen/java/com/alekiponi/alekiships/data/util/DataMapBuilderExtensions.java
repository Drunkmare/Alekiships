package com.alekiponi.alekiships.data.util;

import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;

import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.data.DataMapProvider;

public final class DataMapBuilderExtensions {

    public static <T, R> DataMapProvider.Builder<T, R> add(final DataMapProvider.Builder<T, R> builder,
            final Holder<R> object, final T value, final ICondition... conditions) {
        return builder.add(object, value, false, conditions);
    }

    @SuppressWarnings("deprecation")
    public static <T> DataMapProvider.Builder<T, Item> add(final DataMapProvider.Builder<T, Item> builder,
            final Item item, final T value, final ICondition... conditions) {
        return builder.add(item.builtInRegistryHolder(), value, false, conditions);
    }
}