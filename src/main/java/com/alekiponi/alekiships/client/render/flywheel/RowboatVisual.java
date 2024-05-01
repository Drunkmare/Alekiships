package com.alekiponi.alekiships.client.render.flywheel;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.client.BoatAtlases;
import com.alekiponi.alekiships.client.model.entity.RowboatEntityModel;
import com.alekiponi.alekiships.client.render.ShipSheets;
import com.alekiponi.alekiships.common.entity.vehicle.RowboatEntity;
import com.jozufozu.flywheel.api.instance.Instancer;
import com.jozufozu.flywheel.api.visual.VisualFrameContext;
import com.jozufozu.flywheel.api.visual.VisualTickContext;
import com.jozufozu.flywheel.api.visualization.VisualizationContext;
import com.jozufozu.flywheel.lib.instance.InstanceTypes;
import com.jozufozu.flywheel.lib.instance.TransformedInstance;
import com.jozufozu.flywheel.lib.material.CutoutShaders;
import com.jozufozu.flywheel.lib.material.SimpleMaterial;
import com.jozufozu.flywheel.lib.model.ModelCache;
import com.jozufozu.flywheel.lib.model.SingleMeshModel;
import com.jozufozu.flywheel.lib.model.part.ModelPartConverter;
import com.jozufozu.flywheel.lib.visual.SimpleDynamicVisual;
import com.jozufozu.flywheel.lib.visual.SimpleEntityVisual;
import com.jozufozu.flywheel.lib.visual.SimpleTickableVisual;
import com.jozufozu.flywheel.lib.visual.components.FireComponent;
import com.jozufozu.flywheel.lib.visual.components.HitboxComponent;
import com.jozufozu.flywheel.lib.visual.components.ShadowComponent;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.DyeColor;

import java.util.Optional;

public class RowboatVisual<T extends RowboatEntity> extends SimpleEntityVisual<T> implements SimpleTickableVisual, SimpleDynamicVisual {

    private final static ModelCache<ResourceLocation> ROWBOAT_MODELS = new ModelCache<>(sprite -> new SingleMeshModel(
            ModelPartConverter.convert(RowboatEntityModel.LAYER_LOCATION,
                    BoatAtlases.getRowboatAtlas().getSprite(sprite)), Materials.ROWBOAT));
    private final PoseStack poseStack = new PoseStack();
    private TransformedInstance boatModel;
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private Optional<DyeColor> lastPaintColor;

    public RowboatVisual(final VisualizationContext context, final T entity) {
        super(context, entity);
    }

    @Override
    public void init(final float partialTick) {
        // Debug command /summon alekiships:rowboat/oak ~ ~ ~ {"paint":0b}
        this.addComponent(new ShadowComponent(this.visualizationContext, this.entity).radius(1));
        this.addComponent(new FireComponent(this.visualizationContext, this.entity));
        this.addComponent(new HitboxComponent(this.visualizationContext, this.entity));

        this.lastPaintColor = this.entity.getPaintColor();

        this.boatModel = this.createBoatInstance(this.getResourceLocation());

        this.updateInstances(partialTick);
        this.updateLight();

        super.init(partialTick);
    }

    private ResourceLocation getResourceLocation() {
        return this.lastPaintColor.map(ShipSheets.ROWBOAT_TEXTURE_LOCATION::get)
                .orElse(new ResourceLocation(AlekiShips.MOD_ID, "textures/entity/watercraft/rowboat/oak"));
    }

    private TransformedInstance createBoatInstance(final ResourceLocation resourceLocation) {
        return this.getInstancer(resourceLocation).createInstance();
    }

    private Instancer<TransformedInstance> getInstancer(final ResourceLocation resourceLocation) {
        return this.instancerProvider.instancer(InstanceTypes.TRANSFORMED, ROWBOAT_MODELS.get(resourceLocation));
    }

    @Override
    public void beginFrame(final VisualFrameContext context) {
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

        this.boatModel.setTransform(poseStack).setChanged();
    }

    public void updateLight() {
        this.relight(this.entity.blockPosition(), this.boatModel);
    }

    @Override
    public void tick(final VisualTickContext unused) {
        final Optional<DyeColor> paintColor = this.entity.getPaintColor();
        if (paintColor != this.lastPaintColor) {
            this.lastPaintColor = paintColor;
            final ResourceLocation resourceLocation = this.getResourceLocation();
//            this.getInstancer(resourceLocation).stealInstance(this.boatModel);
            this.boatModel = this.createBoatInstance(resourceLocation);
        }
    }

    @Override
    protected void _delete() {
        super._delete();
        this.boatModel.delete();
    }
}
