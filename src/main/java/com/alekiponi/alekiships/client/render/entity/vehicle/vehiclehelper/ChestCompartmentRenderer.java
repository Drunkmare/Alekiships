package com.alekiponi.alekiships.client.render.entity.vehicle.vehiclehelper;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import com.alekiponi.alekiships.common.entity.compartment.AbstractCompartmentEntity;
import com.alekiponi.alekiships.common.entity.compartment.LidCompartment;
import com.alekiponi.alekiships.common.entity.compartment.vanilla.ChestCompartmentEntity;
import com.alekiponi.alekiships.common.entity.vehicle.AbstractVehicle;

import net.minecraft.Util;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;

import java.util.Calendar;
import java.util.function.Function;

public class ChestCompartmentRenderer<CompartmentEntity extends AbstractCompartmentEntity & LidCompartment> extends CompartmentRenderer<CompartmentEntity> {

    private static final String BOTTOM = "bottom";
    private static final String LID = "lid";
    private static final String LOCK = "lock";
    private static final Function<ResourceLocation, Material> MATERIAL_FUNCTION = Util.memoize(
            resourceLocation -> new Material(Sheets.CHEST_SHEET, resourceLocation));

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
            final PoseStack poseStack, final MultiBufferSource bufferSource, int packedLight) {
        AbstractVehicle vehicle = compartmentEntity.getTrueVehicle();
        if (LightTexture.block(packedLight) < compartmentEntity.getCompartmentBlockLight()) {
            packedLight = LightTexture.pack(compartmentEntity.getCompartmentBlockLight(),
                    getSkyLightLevel(compartmentEntity, compartmentEntity.blockPosition()));
        }
        if (vehicle != null && LightTexture.block(packedLight) < vehicle.getCompartmentBlockLight()) {
            packedLight = LightTexture.pack(Math.max(0, vehicle.getCompartmentBlockLight() - 1),
                    getSkyLightLevel(compartmentEntity, compartmentEntity.blockPosition()));
        }

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
     *
     * @return The material for rendering the chest model
     */
    protected Material getMaterial(final CompartmentEntity compartmentEntity) {
        if (this.xmasTextures) {
            return Sheets.CHEST_XMAS_LOCATION;
        }

        if (compartmentEntity instanceof final ChestCompartmentEntity chestCompartment) {
            return MATERIAL_FUNCTION.apply(chestCompartment.getChestCompartmentData().texture());
        }

        return Sheets.CHEST_LOCATION;
    }
}