package com.alekiponi.alekiships.client.event;

import com.alekiponi.alekiships.client.render.icon.IconRenderer;
import com.alekiponi.alekiships.client.render.icon.IconRendererProvider;
import com.alekiponi.alekiships.client.render.icon.IconRenderers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.fml.LogicalSide;
import net.neoforged.fml.event.IModBusEvent;
import org.jetbrains.annotations.ApiStatus;

/**
 * Fired for on different events/actions relating to {@linkplain IconRenderer icon renderers}.
 *
 * <p>These events are fired on the mod-specific event bus, only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
 *
 * @see IconRenderersEvent.RegisterIconRenderersEvent
 */
public abstract class IconRenderersEvent extends Event implements IModBusEvent {
    @ApiStatus.Internal
    protected IconRenderersEvent() {
    }

    /**
     * Fired for registering icon renderers at the appropriate time.
     *
     * <p>This event is not {@linkplain ICancellableEvent cancellable} and does not have a result.
     *
     * <p>This event is fired on the mod-specific event bus, only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
     */
    public static class RegisterIconRenderersEvent extends IconRenderersEvent {
        @ApiStatus.Internal
        public RegisterIconRenderersEvent() {
        }

        public <E extends Entity> void registerIconRenderer(final EntityType<? extends E> entityType,
                final IconRendererProvider<? super E> iconRendererProvider) {
            IconRenderers.register(entityType, iconRendererProvider);
        }
    }
}