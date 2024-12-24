package com.alekiponi.alekiships.client.render.entity.vehicle;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.client.model.entity.RowboatEntityModel;
import com.alekiponi.alekiships.client.render.AlekiShipsRenderTypes;
import com.alekiponi.alekiships.common.entity.vehicle.RowboatEntity;
import com.alekiponi.alekiships.util.BoatMaterial;
import com.alekiponi.alekiships.util.CommonHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.item.DyeColor;

import java.text.MessageFormat;
import java.util.EnumMap;
import java.util.function.Function;

public class RowboatRenderer extends EntityRenderer<RowboatEntity> {

    public static final ResourceLocation DAMAGE_OVERLAY = AlekiShips.location(
            "textures/entity/watercraft/rowboat/damage_overlay.png");

    public static final EnumMap<DyeColor, ResourceLocation> PAINT_OVERLAYS = CommonHelper.mapOfKeys(DyeColor.class,
            dyeColor -> AlekiShips.location(MessageFormat.format("textures/entity/watercraft/rowboat/paint/{0}.png",
                    dyeColor.getSerializedName())));

    protected final RowboatEntityModel rowboatModel;
    protected final ResourceLocation rowboatTexture;

    /**
     * @param rowboatTexture The texture location
     */
    public RowboatRenderer(final EntityRendererProvider.Context context, final ResourceLocation rowboatTexture) {
        super(context);
        this.rowboatModel = new RowboatEntityModel(context.bakeLayer(RowboatEntityModel.LAYER_LOCATION));
        this.shadowRadius = 1;
        this.rowboatTexture = rowboatTexture;
    }

    public static EntityRendererProvider<RowboatEntity> provider(final Function<String, ResourceLocation> modLocation,
            final BoatMaterial boatMaterial) {
        return context -> new RowboatRenderer(context,
                modLocation.apply("textures/entity/watercraft/rowboat/" + boatMaterial.getSerializedName() + ".png"));
    }

    @Override
    public void render(final RowboatEntity rowboatEntity, final float entityYaw, final float partialTicks,
            final PoseStack poseStack, final MultiBufferSource bufferSource, int packedLight) {
        if (LightTexture.block(packedLight) < rowboatEntity.getCompartmentBlockLight()) {
            packedLight = LightTexture.pack(rowboatEntity.getCompartmentBlockLight(),
                    getSkyLightLevel(rowboatEntity, rowboatEntity.blockPosition()));
        }

        poseStack.pushPose();
        poseStack.translate(0, 0.4375D, 0);
        poseStack.mulPose(Axis.YP.rotationDegrees(180 - entityYaw));

        poseStack.translate(0, 1.0625f, 0);
        poseStack.scale(-1, -1, 1);
        poseStack.mulPose(Axis.YP.rotationDegrees(0));

        if (rowboatEntity.getDamage() > rowboatEntity.getDamageThreshold()) {
            poseStack.mulPose(Axis.ZP.rotationDegrees(rowboatEntity.getId() % 30));
        }

        this.rowboatModel.setupAnim(rowboatEntity, partialTicks, 0, -0.1F, 0, 0);

        final VertexConsumer baseVertexConsumer = bufferSource.getBuffer(this.getRenderType(rowboatEntity));

        if (rowboatEntity.tickCount < 1) {
            poseStack.popPose();
            super.render(rowboatEntity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
            return;
        }

        this.rowboatModel.renderToBuffer(poseStack, baseVertexConsumer, packedLight, OverlayTexture.NO_OVERLAY);

        switch (rowboatEntity.getOars()) {
            case ZERO -> {
            }
            case ONE -> this.rowboatModel.getOarStarboard()
                    .render(poseStack, baseVertexConsumer, packedLight, OverlayTexture.NO_OVERLAY);
            case TWO -> {
                this.rowboatModel.getOarStarboard()
                        .render(poseStack, baseVertexConsumer, packedLight, OverlayTexture.NO_OVERLAY);
                this.rowboatModel.getOarPort()
                        .render(poseStack, baseVertexConsumer, packedLight, OverlayTexture.NO_OVERLAY);
            }
        }

        if (!rowboatEntity.isUnderWater() && rowboatEntity.getDamage() < rowboatEntity.getDamageThreshold() * 0.9) {
            final VertexConsumer waterMaskVertexConsumer = bufferSource.getBuffer(RenderType.waterMask());
            this.rowboatModel.getWaterocclusion()
                    .render(poseStack, waterMaskVertexConsumer, packedLight, OverlayTexture.NO_OVERLAY);
        }

        if (0 < rowboatEntity.getDamage()) {
            final VertexConsumer damageVertexConsumer = bufferSource.getBuffer(
                    RenderType.entityTranslucent(DAMAGE_OVERLAY));
            int alpha = Math.round(
                    Mth.clamp((rowboatEntity.getDamage() / (rowboatEntity.getDamageThreshold())) * 0.75f, 0,
                            0.5f) * 255);
            this.rowboatModel.renderToBuffer(poseStack, damageVertexConsumer, packedLight, OverlayTexture.NO_OVERLAY,
                    FastColor.ARGB32.color(alpha, 1, 1, 1));
        }


        poseStack.popPose();
        super.render(rowboatEntity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
    }

    private RenderType getRenderType(final RowboatEntity rowboatEntity) {
        return rowboatEntity.getPaintColor()
                .map(dyeColor -> AlekiShipsRenderTypes.paintedEntityCutoutNoCull(this.getTextureLocation(rowboatEntity),
                        PAINT_OVERLAYS.get(dyeColor)))
                .orElseGet(() -> this.rowboatModel.renderType(this.getTextureLocation(rowboatEntity)));
    }

    @Override
    public ResourceLocation getTextureLocation(final RowboatEntity rowboatEntity) {
        return this.rowboatTexture;
    }
}