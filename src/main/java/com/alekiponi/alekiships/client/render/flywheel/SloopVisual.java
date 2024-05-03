package com.alekiponi.alekiships.client.render.flywheel;

import com.alekiponi.alekiships.client.BoatAtlases;
import com.alekiponi.alekiships.client.model.entity.SloopEntityModel;
import com.alekiponi.alekiships.common.entity.vehicle.SloopEntity;
import com.jozufozu.flywheel.api.instance.Instancer;
import com.jozufozu.flywheel.api.visual.DynamicVisual;
import com.jozufozu.flywheel.api.visualization.VisualizationContext;
import com.jozufozu.flywheel.lib.instance.InstanceTypes;
import com.jozufozu.flywheel.lib.instance.TransformedInstance;
import com.jozufozu.flywheel.lib.model.ModelCache;
import com.jozufozu.flywheel.lib.model.SingleMeshModel;
import com.jozufozu.flywheel.lib.model.part.ModelPartConverter;
import com.jozufozu.flywheel.lib.visual.SimpleDynamicVisual;
import com.jozufozu.flywheel.lib.visual.SimpleEntityVisual;
import com.jozufozu.flywheel.lib.visual.SimpleEntityVisualizer;
import com.jozufozu.flywheel.lib.visual.SimpleTickableVisual;
import com.jozufozu.flywheel.lib.visual.component.FireComponent;
import com.jozufozu.flywheel.lib.visual.component.HitboxComponent;
import com.jozufozu.flywheel.lib.visual.component.ShadowComponent;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.DyeColor;

import java.util.EnumMap;
import java.util.Optional;

public class SloopVisual extends SimpleEntityVisual<SloopEntity> implements SimpleTickableVisual, SimpleDynamicVisual {

    private final static ModelCache<ResourceLocation> SLOOP_MODELS = new ModelCache<>(sprite -> new SingleMeshModel(
            ModelPartConverter.convert(SloopEntityModel.LAYER_LOCATION, BoatAtlases.getSloopAtlas().getSprite(sprite)),
            Materials.SLOOP));
    private final PoseStack poseStack = new PoseStack();
    private final ResourceLocation unpaintedTexture;
    private final EnumMap<DyeColor, ResourceLocation> paintedTextures;
    private TransformedInstance boatModel;
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private Optional<DyeColor> lastPaintColor = this.entity.getPaintColor();

    private SloopVisual(final VisualizationContext ctx, final SloopEntity entity,
            final ResourceLocation unpaintedTexture, final EnumMap<DyeColor, ResourceLocation> paintedTextures) {
        super(ctx, entity);
        this.unpaintedTexture = unpaintedTexture;
        this.paintedTextures = paintedTextures;
    }

    /**
     * Creates a factory that captures the passed in base texture location and map of paint textures to prevent
     * accidental creation on each invocation of the factory.
     *
     * @param baseTexture     The base texture of the sloop (no paint)
     * @param paintedTextures The painted textures of the sloop
     * @return A factory for the sloop visual that uses the passed textures
     */
    public static SimpleEntityVisualizer.Factory<SloopEntity> create(final ResourceLocation baseTexture,
            final EnumMap<DyeColor, ResourceLocation> paintedTextures) {
        return (context, entity) -> new SloopVisual(context, entity, baseTexture, paintedTextures);
    }

    @Override
    public void init(final float partialTick) {
        this.addComponent(new ShadowComponent(this.visualizationContext, this.entity).radius(1));
        this.addComponent(new FireComponent(this.visualizationContext, this.entity));
        this.addComponent(new HitboxComponent(this.visualizationContext, this.entity));

        this.boatModel = this.getInstancer().createInstance();

        this.updateInstances(partialTick);
        this.updateLight();

        super.init(partialTick);
    }

    @Override
    public void beginFrame(final DynamicVisual.Context context) {
        super.beginFrame(context);

        if (!this.isVisible(context.frustum())) {
            return;
        }

        this.updateInstances(context.partialTick());
    }

    private void updateInstances(final float partialTick) {
        this.poseStack.setIdentity();

        final double posX = Mth.lerp(partialTick, this.entity.xOld, this.entity.getX());
        final double posY = Mth.lerp(partialTick, this.entity.yOld, this.entity.getY());
        final double posZ = Mth.lerp(partialTick, this.entity.zOld, this.entity.getZ());

        this.poseStack.translate(posX - this.renderOrigin.getX(), posY - this.renderOrigin.getY(),
                posZ - this.renderOrigin.getZ());

        final float yaw = Mth.lerp(partialTick, this.entity.yRotO, this.entity.getYRot());

        this.poseStack.translate(0, 0.4375D, 0);
        this.poseStack.mulPose(Axis.YP.rotationDegrees(180 - yaw));
        this.poseStack.translate(0, 1.0625f, 0);

        this.poseStack.scale(-1, -1, 1);
        this.poseStack.mulPose(Axis.YP.rotationDegrees(0));

        // TODO renders the entire sloop model including the open and closed version of the sails.
        //  They also don't animate
        this.boatModel.setTransform(this.poseStack).setChanged();
    }

    @Override
    public void tick() {
        final Optional<DyeColor> paintColor = this.entity.getPaintColor();
        if (!paintColor.equals(this.lastPaintColor)) {
            this.lastPaintColor = paintColor;
            this.getInstancer().stealInstance(this.boatModel);
        }
    }

    private void updateLight() {
        this.relight(this.entity.blockPosition(), this.boatModel);
    }

    private Instancer<TransformedInstance> getInstancer() {
        return this.instancerProvider.instancer(InstanceTypes.TRANSFORMED,
                SLOOP_MODELS.get(this.getResourceLocation()));
    }

    private ResourceLocation getResourceLocation() {
        return this.entity.getPaintColor().map(this.paintedTextures::get).orElse(this.unpaintedTexture);
    }

    @Override
    protected void _delete() {
        super._delete();
        this.boatModel.delete();
    }
}