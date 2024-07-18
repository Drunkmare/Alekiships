package com.alekiponi.alekiships.client.render.flywheel.compartment;

import com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.AbstractCompartmentEntity;
import com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.LidCompartment;
import com.jozufozu.flywheel.api.visualization.VisualizationContext;
import com.jozufozu.flywheel.lib.instance.TransformedInstance;
import com.jozufozu.flywheel.lib.material.Materials;
import com.jozufozu.flywheel.lib.model.ModelCache;
import com.jozufozu.flywheel.lib.model.SingleMeshModel;
import com.jozufozu.flywheel.lib.model.part.ModelPartConverter;
import com.jozufozu.flywheel.lib.visual.SimpleTickableVisual;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.resources.model.Material;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Calendar;

@OnlyIn(Dist.CLIENT)
public class ChestCompartmentVisual<Compartment extends AbstractCompartmentEntity & LidCompartment> extends CompartmentVisual<Compartment> implements SimpleTickableVisual {

    private static final ModelCache<Material> BOTTOM_MODELS = new ModelCache<>(
            key -> new SingleMeshModel(ModelPartConverter.convert(ModelLayers.CHEST, key.sprite(), "bottom"),
                    Materials.CHEST));
    private static final ModelCache<Material> LID_MODELS = new ModelCache<>(
            key -> new SingleMeshModel(ModelPartConverter.convert(ModelLayers.CHEST, key.sprite(), "lid"),
                    Materials.CHEST));
    private static final ModelCache<Material> LOCK_MODELS = new ModelCache<>(
            material -> new SingleMeshModel(ModelPartConverter.convert(ModelLayers.CHEST, material.sprite(), "lock"),
                    Materials.CHEST));

    private TransformedInstance bottom;
    private TransformedInstance lid;
    private TransformedInstance lock;

    private float lastProgress = Float.NaN;

    public ChestCompartmentVisual(final VisualizationContext ctx, final Compartment entity) {
        super(ctx, entity);
    }

    protected static boolean isChristmas() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.MONTH) + 1 == 12 && calendar.get(Calendar.DATE) >= 24 && calendar.get(
                Calendar.DATE) <= 26;
    }

    @Override
    public void init(final float partialTick) {
        final Material texture = this.getMaterial(this.entity);

        this.bottom = this.getTransformedInstance(BOTTOM_MODELS.get(texture)).createInstance();
        this.lid = this.getTransformedInstance(LID_MODELS.get(texture)).createInstance();
        this.lock = this.getTransformedInstance(LOCK_MODELS.get(texture)).createInstance();

        super.init(partialTick);
    }

    private void applyLidTransform(float progress, final PoseStack poseStack) {
        progress = 1.0F - progress;
        progress = 1.0F - progress * progress * progress;

        final float angleX = -(progress * ((float) Math.PI / 2F));

        poseStack.mulPose(Axis.YP.rotationDegrees(180));
        poseStack.translate(-1, 0, -1);

        this.bottom.setTransform(poseStack).setChanged();

        this.lid.setTransform(poseStack).translate(0, 9D / 16D, 1D / 16D).rotateX(angleX)
                .translate(0, -9D / 16D, -1D / 16D).setChanged();

        this.lock.setTransform(poseStack).translate(0, 8D / 16D, 0).rotateX(angleX).translate(0, -8D / 16D, 0)
                .setChanged();
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

    @Override
    public void tick() {
        this.updateLight();
    }

    /**
     * Gets the material to use for rendering the chest model.
     * See: {@link #isChristmas()} if there's a different texture for Christmas
     *
     * @param compartmentEntity The compartment
     * @return The material for rendering the chest model
     */
    protected Material getMaterial(final Compartment compartmentEntity) {
        return ChestCompartmentVisual.isChristmas() ? Sheets.CHEST_XMAS_LOCATION : Sheets.CHEST_LOCATION;
    }

    protected void updateLight() {
        this.relight(this.entity.blockPosition(), this.bottom, this.lid, this.lock);
    }

    @Override
    protected void _delete() {
        super._delete();
        this.bottom.delete();
        this.lid.delete();
        this.lock.delete();
    }
}