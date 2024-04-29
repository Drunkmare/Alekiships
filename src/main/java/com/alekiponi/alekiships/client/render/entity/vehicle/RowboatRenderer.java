package com.alekiponi.alekiships.client.render.entity.vehicle;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.client.BoatAtlases;
import com.alekiponi.alekiships.client.model.entity.RowboatEntityModel;
import com.alekiponi.alekiships.client.render.ShipSheets;
import com.alekiponi.alekiships.client.reosurces.BoatAtlasHolder;
import com.alekiponi.alekiships.common.entity.vehicle.RowboatEntity;
import com.alekiponi.alekiships.util.AlekiShipsHelper;
import com.alekiponi.alekiships.util.VanillaWood;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.DyeColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.EnumMap;

@OnlyIn(Dist.CLIENT)
public class RowboatRenderer extends EntityRenderer<RowboatEntity> {

    public static final ResourceLocation DAMAGE_OVERLAY = new ResourceLocation(AlekiShips.MOD_ID,
            "textures/entity/watercraft/rowboat/damage_overlay.png");
    private static final BoatAtlasHolder ROWBOAT_ATLAS = BoatAtlases.getRowboatAtlas();
    protected final RowboatEntityModel rowboatModel = new RowboatEntityModel();
    protected final ResourceLocation rowboatTexture;
    protected final EnumMap<DyeColor, ResourceLocation> paintTextures;

    /**
     * This is primarily for us as it hardcodes the Firmaciv namespace.
     */
    public RowboatRenderer(final EntityRendererProvider.Context context, final VanillaWood vanillaWood) {
        this(context, new ResourceLocation(AlekiShips.MOD_ID,
                        "textures/entity/watercraft/rowboat/" + vanillaWood.getSerializedName()),
                AlekiShipsHelper.mapOfKeys(DyeColor.class, dyeColor -> new ResourceLocation(AlekiShips.MOD_ID,
                        "textures/entity/watercraft/rowboat/" + vanillaWood.getSerializedName() + "/" + dyeColor.getSerializedName())));
    }

    /**
     * @param rowboatTexture The texture location
     * @param paintTextures  The texture locations for when the rowboat is painted
     */
    public RowboatRenderer(final EntityRendererProvider.Context context, final ResourceLocation rowboatTexture,
            final EnumMap<DyeColor, ResourceLocation> paintTextures) {
        super(context);
        this.shadowRadius = 1;
        this.rowboatTexture = rowboatTexture;
        this.paintTextures = paintTextures;
    }

    @Override
    public void render(final RowboatEntity rowboatEntity, final float entityYaw, final float partialTicks,
            final PoseStack poseStack, final MultiBufferSource bufferSource, final int packedLight) {
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

        final VertexConsumer baseVertexConsumer = ROWBOAT_ATLAS.getSprite(this.getTextureLocation(rowboatEntity))
                .wrap(bufferSource.getBuffer(this.rowboatModel.renderType(ShipSheets.ROWBOAT_SHEET)));

        if (rowboatEntity.tickCount < 1) {
            poseStack.popPose();
            super.render(rowboatEntity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
            return;
        }

        this.rowboatModel.renderToBuffer(poseStack, baseVertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, 1, 1, 1,
                1);

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
            float alpha = Mth.clamp((rowboatEntity.getDamage() / (rowboatEntity.getDamageThreshold())) * 0.75f, 0,
                    0.5f);
            this.rowboatModel.renderToBuffer(poseStack, damageVertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, 1,
                    1, 1, alpha);
        }


        poseStack.popPose();
        super.render(rowboatEntity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(final RowboatEntity rowboatEntity) {
        return rowboatEntity.getPaintColor().map(this.paintTextures::get).orElse(this.rowboatTexture);
    }
}