package com.alekiponi.alekiships.client.render.flywheel.compartment;

import com.alekiponi.alekiships.common.entity.vehiclehelper.AbstractVehiclePart;
import com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.AbstractCompartmentEntity;
import com.jozufozu.flywheel.api.instance.Instancer;
import com.jozufozu.flywheel.api.model.Model;
import com.jozufozu.flywheel.api.visualization.VisualizationContext;
import com.jozufozu.flywheel.lib.instance.InstanceTypes;
import com.jozufozu.flywheel.lib.instance.TransformedInstance;
import com.jozufozu.flywheel.lib.visual.SimpleEntityVisual;
import com.jozufozu.flywheel.lib.visual.component.HitboxComponent;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public abstract class CompartmentVisual<Compartment extends AbstractCompartmentEntity> extends SimpleEntityVisual<Compartment> {

    private final PoseStack poseStack = new PoseStack();

    protected CompartmentVisual(final VisualizationContext ctx, final Compartment entity) {
        super(ctx, entity);
    }

    @Override
    public void init(final float partialTick) {
        this.addComponent(new HitboxComponent(this.visualizationContext, this.entity));
        this.updateInstances(partialTick);
        super.init(partialTick);
    }

    @Override
    public final void beginFrame(final Context context) {
        super.beginFrame(context);

        if (!this.isVisible(context.frustum())) return;

        this.updateInstances(context.partialTick());
    }

    private void updateInstances(final float partialTick) {
        this.poseStack.setIdentity();

        this.poseStack.pushPose();

        final Vector3f visualPosition = this.getVisualPosition(partialTick);
        this.poseStack.translate(visualPosition.x, visualPosition.y, visualPosition.z);

        final float rotation;
        if (this.entity.getTrueVehicle() != null && this.entity.getVehicle() instanceof AbstractVehiclePart vehiclePart && this.entity.tickCount < 2) {
            rotation = this.entity.getTrueVehicle().getYRot() + vehiclePart.getCompartmentRotation();
        } else {
            rotation = this.entity.getYRot();
        }

        this.poseStack.mulPose(Axis.YP.rotationDegrees(180 - rotation));

        float renderSize = 0.6875f;
        if (this.entity.isPassenger() && this.entity.getTrueVehicle() != null) {
            renderSize = this.entity.getTrueVehicle().renderSizeForCompartments();
        }

        this.poseStack.scale(renderSize, renderSize, renderSize);
        this.poseStack.translate(-0.5F, 0, -0.5F);

        this.updateContents(this.poseStack, partialTick);

        this.poseStack.popPose();
    }

    /**
     * Render the contents of the compartment. Pre scaled, rotated and translated.
     *
     * @param poseStack   The pose stack
     * @param partialTick The partial tick for this frame
     */
    protected abstract void updateContents(final PoseStack poseStack, final float partialTick);

    protected final Instancer<TransformedInstance> getTransformedInstance(final Model model) {
        return this.instancerProvider.instancer(InstanceTypes.TRANSFORMED, model);
    }
}