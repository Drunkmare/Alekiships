package com.alekiponi.alekiships.client.render.icon;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public final class IconRenderDispatcher implements ResourceManagerReloadListener {

    public static final IconRenderDispatcher INSTANCE = new IconRenderDispatcher();
    private Map<EntityType<?>, IconRenderer<?>> iconRenderers = ImmutableMap.of();
    @Nullable
    private Entity lastIconEntity = null;

    private IconRenderDispatcher() {
    }

    public <E extends Entity> void render(final E entity, final LocalPlayer player, final GuiGraphics graphics,
            final float partialTick) {
        final IconRenderer<? super E> iconRenderer = this.getIconRenderer(entity);

        if (iconRenderer == null) {
            this.lastIconEntity = null;
            return;
        }

        try {
            final PoseStack poseStack = graphics.pose();
            poseStack.pushPose();

            if (this.lastIconEntity != entity) {
                this.lastIconEntity = entity;
                iconRenderer.resetRenderer(entity, player);
            }

            iconRenderer.render(entity, player, graphics, partialTick);

            poseStack.popPose();
        } catch (final Throwable throwable) {
            final CrashReport crashReport = CrashReport.forThrowable(throwable, "Rendering entity icon");
            final CrashReportCategory renderCategory = crashReport.addCategory("Entity getting icon rendered");
            entity.fillCrashReportCategory(renderCategory);
            final CrashReportCategory rendererDetails = crashReport.addCategory("Renderer details");
            rendererDetails.setDetail("Assigned renderer", iconRenderer);
            rendererDetails.setDetail("Screen height", graphics.guiHeight());
            rendererDetails.setDetail("Screen width", graphics.guiWidth());
            rendererDetails.setDetail("Delta", partialTick);
            throw new ReportedException(crashReport);
        }
    }

    /**
     * Unlike vanilla, it's perfectly legal for an icon renderer to not exist for a given entity type.
     *
     * @return A  potentially {@code null} {@link IconRenderer} for the entity you pass in
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public <E extends Entity> IconRenderer<? super E> getIconRenderer(final E entity) {
        return (IconRenderer<? super E>) this.iconRenderers.get(entity.getType());
    }

    /**
     * @return The last entity that had an icon renderer run. {@code null} means no entity had icons render last frame
     */
    @Nullable
    @SuppressWarnings("unused")
    public Entity getLastIconEntity() {
        return this.lastIconEntity;
    }

    @Override
    public void onResourceManagerReload(final ResourceManager resourceManager) {
        final Minecraft minecraft = Minecraft.getInstance();
        final IconRendererProvider.Context context = new IconRendererProvider.Context(this, resourceManager,
                minecraft.font);

        this.iconRenderers = IconRenderers.createIconRenderers(context);
    }
}