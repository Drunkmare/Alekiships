package com.alekiponi.alekiships.client.render.entity.vehicle.vehiclehelper;

import com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.AbstractCompartmentEntity;
import com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.LidCompartment;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.Material;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Calendar;

@OnlyIn(Dist.CLIENT)
public class ChestCompartmentRenderer<CompartmentEntity extends AbstractCompartmentEntity & LidCompartment> extends CompartmentRenderer<CompartmentEntity> {

    private static final String BOTTOM = "bottom";
    private static final String LID = "lid";
    private static final String LOCK = "lock";
    protected final boolean xmasTextures;
    private final ModelPart lid;
    private final ModelPart bottom;
    private final ModelPart lock;

    public ChestCompartmentRenderer(final EntityRendererProvider.Context context) {
        super(context);

        final Calendar calendar = Calendar.getInstance();
        this.xmasTextures = calendar.get(Calendar.MONTH) + 1 == 12 && calendar.get(
                Calendar.DAY_OF_MONTH) >= 24 && calendar.get(Calendar.DAY_OF_MONTH) <= 26;

        final ModelPart modelpart = context.bakeLayer(ModelLayers.CHEST);
        this.bottom = modelpart.getChild(BOTTOM);
        this.lid = modelpart.getChild(LID);
        this.lock = modelpart.getChild(LOCK);
    }

    @Override
    protected void renderCompartmentContents(final CompartmentEntity compartmentEntity, final float partialTicks,
            final PoseStack poseStack, final MultiBufferSource bufferSource, final int packedLight) {

        float openAngle = compartmentEntity.getOpenNess(partialTicks);
        openAngle = 1 - openAngle;
        openAngle = 1 - openAngle * openAngle * openAngle;

        final Material material = this.getMaterial(compartmentEntity);
        final VertexConsumer vertexConsumer = material.buffer(bufferSource, RenderType::entityCutout);

        poseStack.mulPose(Axis.YP.rotationDegrees(180));
        poseStack.translate(-1, 0, -1);
        this.render(poseStack, vertexConsumer, openAngle, packedLight);
    }

    private void render(final PoseStack poseStack, final VertexConsumer vertexConsumer, final float lidAngle,
            final int packedLight) {
        this.lid.xRot = (float) (-lidAngle * Math.PI / 2);
        this.lock.xRot = this.lid.xRot;
        this.lid.render(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY);
        this.lock.render(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY);
        this.bottom.render(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY);
    }

    /**
     * Gets the material to use for rendering the chest model.
     * See: {@link #xmasTextures} if there's a different texture for Christmas
     *
     * @param compartmentEntity The compartment
     * @return The material for rendering the chest model
     */
    protected Material getMaterial(final CompartmentEntity compartmentEntity) {
        return this.xmasTextures ? Sheets.CHEST_XMAS_LOCATION : Sheets.CHEST_LOCATION;
    }
}