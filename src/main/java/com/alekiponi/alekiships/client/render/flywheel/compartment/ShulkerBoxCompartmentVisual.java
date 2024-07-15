package com.alekiponi.alekiships.client.render.flywheel.compartment;

import com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.vanilla.ShulkerBoxCompartmentEntity;
import com.jozufozu.flywheel.api.visualization.VisualizationContext;
import com.jozufozu.flywheel.lib.instance.TransformedInstance;
import com.jozufozu.flywheel.lib.material.Materials;
import com.jozufozu.flywheel.lib.model.ModelCache;
import com.jozufozu.flywheel.lib.model.SingleMeshModel;
import com.jozufozu.flywheel.lib.model.part.ModelPartConverter;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.resources.model.Material;
import net.minecraft.world.item.DyeColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ShulkerBoxCompartmentVisual extends CompartmentVisual<ShulkerBoxCompartmentEntity> {

    private static final ModelCache<Material> BASE_MODELS = new ModelCache<>(
            texture -> new SingleMeshModel(ModelPartConverter.convert(ModelLayers.SHULKER, texture.sprite(), "base"),
                    Materials.SHULKER));

    private static final ModelCache<Material> LID_MODELS = new ModelCache<>(
            texture -> new SingleMeshModel(ModelPartConverter.convert(ModelLayers.SHULKER, texture.sprite(), "lid"),
                    Materials.SHULKER));

    private TransformedInstance base;
    private TransformedInstance lid;
    private float lastProgress = Float.NaN;

    public ShulkerBoxCompartmentVisual(final VisualizationContext ctx, final ShulkerBoxCompartmentEntity entity) {
        super(ctx, entity);
    }

    @Override
    public void init(final float partialTick) {
        final DyeColor dyecolor = this.entity.getColor();
        final Material texture = dyecolor == null ? Sheets.DEFAULT_SHULKER_TEXTURE_LOCATION : Sheets.SHULKER_TEXTURE_LOCATION.get(
                dyecolor.getId());

        this.base = this.getTransformedInstance(BASE_MODELS.get(texture)).createInstance();
        this.lid = this.getTransformedInstance(LID_MODELS.get(texture)).createInstance();

        super.init(partialTick);
    }

    @Override
    protected void updateContents(final PoseStack poseStack, final float partialTick) {
        final float progress = this.entity.getOpenNess(partialTick);
        if (this.lastProgress != progress) {
            this.lastProgress = progress;
        }

        this.updateLight();
        this.applyLidTransform(this.lastProgress, poseStack);
    }

    private void applyLidTransform(final float progress, final PoseStack poseStack) {
        poseStack.translate(0.5F, 0.5F, 0.5F);
        poseStack.scale(0.9995F, 0.9995F, 0.9995F);
        poseStack.scale(1, -1, -1);
        poseStack.translate(0, -1, 0);

        this.base.setTransform(poseStack).setChanged();
        this.lid.setTransform(poseStack).translateY(-progress * 0.5).rotate(Axis.YP.rotationDegrees(270 * progress))
                .setChanged();
    }

    private void updateLight() {
        this.relight(this.entity.blockPosition(), this.base, this.lid);
    }

    @Override
    protected void _delete() {
        super._delete();
        this.base.delete();
        this.lid.delete();
    }
}