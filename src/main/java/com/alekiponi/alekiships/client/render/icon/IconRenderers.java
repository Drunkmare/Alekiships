package com.alekiponi.alekiships.client.render.icon;

import com.google.common.collect.ImmutableMap;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class IconRenderers {

    private static final Map<EntityType<?>, IconRendererProvider<?>> PROVIDERS = new HashMap<>();

    /**
     * Register an {@link EntityType} to an {@link IconRenderer}
     *
     * @param entityType           The entity type
     * @param iconRendererProvider An {@link IconRendererProvider}
     */
    public static <E extends Entity> void register(final EntityType<? extends E> entityType,
            final IconRendererProvider<? super E> iconRendererProvider) {
        PROVIDERS.put(Objects.requireNonNull(entityType), Objects.requireNonNull(iconRendererProvider));
    }

    public static Map<EntityType<?>, IconRenderer<?>> createIconRenderers(final IconRendererProvider.Context context) {
        final ImmutableMap.Builder<EntityType<?>, IconRenderer<?>> builder = ImmutableMap.builder();
        PROVIDERS.forEach((entityType, iconRendererProvider) -> {
            try {
                builder.put(entityType, iconRendererProvider.create(context));
            } catch (final Exception exception) {
                throw new IllegalArgumentException(
                        "Failed to create icon renderer for " + BuiltInRegistries.ENTITY_TYPE.getKey(entityType),
                        exception);
            }
        });

        return builder.build();
    }
}