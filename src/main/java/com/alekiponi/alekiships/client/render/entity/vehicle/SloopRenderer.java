package com.alekiponi.alekiships.client.render.entity.vehicle;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.client.model.entity.SloopEntityModel;
import com.alekiponi.alekiships.common.entity.vehicle.SloopEntity;
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
public class SloopRenderer extends EntityRenderer<SloopEntity> {

    public static final ResourceLocation DAMAGE_OVERLAY = new ResourceLocation(AlekiShips.MOD_ID,
            "textures/entity/watercraft/sloop/damage_overlay.png");
    public static final EnumMap<DyeColor, ResourceLocation> SAIL_TEXTURES = AlekiShipsHelper.mapOfKeys(DyeColor.class,
            dyeColor -> new ResourceLocation(AlekiShips.MOD_ID,
                    "textures/entity/watercraft/sloop/sails/" + dyeColor.getSerializedName() + ".png"));
    protected final ResourceLocation sloopTexture;
    protected final EnumMap<DyeColor, ResourceLocation> paintTextures;
    protected final SloopEntityModel sloopModel = new SloopEntityModel();

    /**
     * This is primarily for us as it hardcodes the Firmaciv namespace.
     */
    public SloopRenderer(final EntityRendererProvider.Context context, final VanillaWood vanillaWood) {
        this(context, new ResourceLocation(AlekiShips.MOD_ID,
                        "textures/entity/watercraft/sloop/" + vanillaWood.getSerializedName() + "/normal.png"),
                AlekiShipsHelper.mapOfKeys(DyeColor.class, dyeColor -> new ResourceLocation(AlekiShips.MOD_ID,
                        "textures/entity/watercraft/sloop/" + vanillaWood.getSerializedName() + "/" + dyeColor.getSerializedName() + ".png")));
    }

    /**
     * @param sloopTexture  The texture location. Must include file extension!
     * @param paintTextures The texture locations for when the sloop is painted. Must include file extension!
     */
    public SloopRenderer(final EntityRendererProvider.Context context, final ResourceLocation sloopTexture,
            final EnumMap<DyeColor, ResourceLocation> paintTextures) {
        super(context);
        this.shadowRadius = 0.8F;
        this.sloopTexture = sloopTexture;
        this.paintTextures = paintTextures;
    }

    @Override
    public void render(final SloopEntity sloopEntity, final float entityYaw, final float partialTicks,
            final PoseStack poseStack, final MultiBufferSource bufferSource, final int packedLight) {
        poseStack.pushPose();
        poseStack.translate(0, 0.5, 0);
        poseStack.mulPose(Axis.YP.rotationDegrees(180 - entityYaw));

        poseStack.translate(0, 1.0625f, 0);
        poseStack.scale(-1, -1, 1);
        poseStack.mulPose(Axis.YP.rotationDegrees(0));

        if (sloopEntity.getDamage() > sloopEntity.getDamageThreshold()) {
            poseStack.mulPose(Axis.ZP.rotationDegrees(sloopEntity.getId() % 30));
        }

        this.sloopModel.setupAnim(sloopEntity, partialTicks, 0, -0.1F, 0, 0);
        final VertexConsumer vertexconsumer = bufferSource.getBuffer(
                this.sloopModel.renderType(this.getTextureLocation(sloopEntity)));

        if (sloopEntity.tickCount < 1) {
            poseStack.popPose();
            super.render(sloopEntity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
            return;
        }

        //TODO this is setup code for distance sail LODs

        //TODO get the player from the minecraft instance instead

        //TODO also cancel animation when LOD is triggered
        /*
        if(this.level().isClientSide()){
            Player nearestPlayer = this.level().getNearestPlayer(this, 32*16);
            if(nearestPlayer != null){
                float distance = this.distanceTo(nearestPlayer);
                if(distance > 4*16){
                    //render the simple sail instead
                }
            }
        }*/


        this.sloopModel.renderToBuffer(poseStack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);

        if (sloopEntity.getMainsailActive()) {
            this.sloopModel.getMainsailDeployedParts()
                    .render(poseStack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
            this.sloopModel.getMainsail().render(poseStack,
                    bufferSource.getBuffer(RenderType.entityCutout(this.getMainsailTexture(sloopEntity))), packedLight,
                    OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
        } else {
            this.sloopModel.getMainsailFurledParts()
                    .render(poseStack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
            // TODO we do need to render this twice as the sail is included in this model part.
            //  change the model (or don't it's probably not that big of a deal)
            this.sloopModel.getMainsailFurledParts().render(poseStack,
                    bufferSource.getBuffer(RenderType.entityCutout(this.getMainsailTexture(sloopEntity))), packedLight,
                    OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
        }

        if (sloopEntity.getJibsailActive()) {
            this.sloopModel.getJibsail().render(poseStack,
                    bufferSource.getBuffer(RenderType.entityCutout(this.getJibsailTexture(sloopEntity))), packedLight,
                    OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
        } else {
            this.sloopModel.getJibsailFurled().render(poseStack,
                    bufferSource.getBuffer(RenderType.entityCutout(this.getJibsailTexture(sloopEntity))), packedLight,
                    OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
        }

        if (!sloopEntity.isUnderWater() && sloopEntity.getDamage() < sloopEntity.getDamageThreshold() * 0.9) {
            final VertexConsumer vertexconsumer1 = bufferSource.getBuffer(RenderType.waterMask());
            this.sloopModel.getWaterocclusion()
                    .render(poseStack, vertexconsumer1, packedLight, OverlayTexture.NO_OVERLAY);
        }

        if (sloopEntity.getDamage() > 0) {
            final VertexConsumer damageVertexConsumer = bufferSource.getBuffer(
                    RenderType.entityTranslucent(DAMAGE_OVERLAY));
            final float alpha = Mth.clamp((sloopEntity.getDamage() / (sloopEntity.getDamageThreshold())) * 0.75f, 0,
                    0.75f);
            this.sloopModel.renderToBuffer(poseStack, damageVertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, 1,
                    1, 1, alpha);
        }

        poseStack.popPose();
        super.render(sloopEntity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(final SloopEntity sloopEntity) {
        final DyeColor paintColor = sloopEntity.getPaintColor();
        return paintColor == null ? this.sloopTexture : this.paintTextures.get(paintColor);
    }

    public ResourceLocation getMainsailTexture(final SloopEntity sloopEntity) {
        return SAIL_TEXTURES.get(sloopEntity.getMainsailDye());
    }

    public ResourceLocation getJibsailTexture(final SloopEntity sloopEntity) {
        return SAIL_TEXTURES.get(sloopEntity.getJibsailDye());
    }
}