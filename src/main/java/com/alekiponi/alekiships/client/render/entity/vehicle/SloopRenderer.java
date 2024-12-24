package com.alekiponi.alekiships.client.render.entity.vehicle;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.client.model.entity.SloopEntityModel;
import com.alekiponi.alekiships.client.render.AlekiShipsRenderTypes;
import com.alekiponi.alekiships.common.entity.vehicle.SloopEntity;
import com.alekiponi.alekiships.util.BoatMaterial;
import com.alekiponi.alekiships.util.CommonHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.gui.Font;
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
import net.minecraft.world.phys.Vec3;

import java.text.MessageFormat;
import java.util.EnumMap;
import java.util.function.Function;

import static com.alekiponi.alekiships.client.render.util.AlekiShipsRenderHelper.renderTextLine;

public class SloopRenderer extends EntityRenderer<SloopEntity> {

    public static final ResourceLocation DAMAGE_OVERLAY = ResourceLocation.fromNamespaceAndPath(AlekiShips.MOD_ID,
            "textures/entity/watercraft/sloop/damage_overlay.png");

    public static final EnumMap<DyeColor, ResourceLocation> SAIL_TEXTURES = CommonHelper.mapOfKeys(DyeColor.class,
            dyeColor -> ResourceLocation.fromNamespaceAndPath(AlekiShips.MOD_ID,
                    "textures/entity/watercraft/sloop/sails/" + dyeColor.getSerializedName() + ".png"));

    public static final EnumMap<DyeColor, ResourceLocation> PAINT_OVERLAYS = CommonHelper.mapOfKeys(DyeColor.class,
            dyeColor -> AlekiShips.location(MessageFormat.format("textures/entity/watercraft/sloop/paint/{0}.png",
                    dyeColor.getSerializedName())));

    protected final ResourceLocation sloopTexture;
    protected final SloopEntityModel sloopModel;
    private final Font font;

    /**
     * @param sloopTexture The texture location
     */
    public SloopRenderer(final EntityRendererProvider.Context context, final ResourceLocation sloopTexture) {
        super(context);
        this.shadowRadius = 0.8F;
        this.sloopTexture = sloopTexture;
        this.sloopModel = new SloopEntityModel(context.bakeLayer(SloopEntityModel.LAYER_LOCATION));
        this.font = context.getFont();
    }

    public static EntityRendererProvider<SloopEntity> provider(final Function<String, ResourceLocation> modLocation,
            final BoatMaterial boatMaterial) {
        return context -> new SloopRenderer(context,
                modLocation.apply("textures/entity/watercraft/sloop/" + boatMaterial.getSerializedName() + ".png"));
    }

    @Override
    public void render(final SloopEntity sloopEntity, final float entityYaw, final float partialTicks,
            final PoseStack poseStack, final MultiBufferSource bufferSource, int packedLight) {
        if (LightTexture.block(packedLight) < sloopEntity.getCompartmentBlockLight()) {
            packedLight = LightTexture.pack(sloopEntity.getCompartmentBlockLight(),
                    getSkyLightLevel(sloopEntity, sloopEntity.blockPosition()));
        }

        poseStack.pushPose();
        poseStack.translate(0, 0.5, 0);
        poseStack.mulPose(Axis.YP.rotationDegrees(180 - entityYaw));

        poseStack.translate(0, 1.0625f, 0);
        poseStack.scale(-1, -1, 1);
        poseStack.mulPose(Axis.YP.rotationDegrees(0));

        if (!sloopEntity.isFunctional()) {
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


        this.sloopModel.renderToBuffer(poseStack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY);

        if (sloopEntity.hasCustomName()) {
            this.sloopModel.getNameplate().render(poseStack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY);
        }
        if (sloopEntity.breaksIce()) {
            this.sloopModel.getIcebreaker().render(poseStack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY);
        }

        if (sloopEntity.getMainsailActive()) {
            this.sloopModel.getMainsailDeployedParts()
                    .render(poseStack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY);
            this.sloopModel.getMainsail().render(poseStack,
                    bufferSource.getBuffer(RenderType.entityCutout(this.getMainsailTexture(sloopEntity))), packedLight,
                    OverlayTexture.NO_OVERLAY);
        } else {
            this.sloopModel.getMainsailFurledParts()
                    .render(poseStack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY);
            // TODO we do need to render this twice as the sail is included in this model part.
            //  change the model (or don't it's probably not that big of a deal)
            this.sloopModel.getMainsailFurledParts().render(poseStack,
                    bufferSource.getBuffer(RenderType.entityCutout(this.getMainsailTexture(sloopEntity))), packedLight,
                    OverlayTexture.NO_OVERLAY);
        }

        if (sloopEntity.getJibsailActive()) {
            this.sloopModel.getJibsail().render(poseStack,
                    bufferSource.getBuffer(RenderType.entityCutout(this.getJibsailTexture(sloopEntity))), packedLight,
                    OverlayTexture.NO_OVERLAY);
        } else {
            this.sloopModel.getJibsailFurled().render(poseStack,
                    bufferSource.getBuffer(RenderType.entityCutout(this.getJibsailTexture(sloopEntity))), packedLight,
                    OverlayTexture.NO_OVERLAY);
        }

        if (!sloopEntity.isUnderWater() && sloopEntity.getDamage() < sloopEntity.getDamageThreshold() * 0.9) {
            final VertexConsumer vertexconsumer1 = bufferSource.getBuffer(RenderType.waterMask());
            this.sloopModel.getWaterocclusion()
                    .render(poseStack, vertexconsumer1, packedLight, OverlayTexture.NO_OVERLAY);
        }

        if (sloopEntity.getDamage() > 0) {
            final VertexConsumer damageVertexConsumer = bufferSource.getBuffer(
                    RenderType.entityTranslucent(DAMAGE_OVERLAY));
            final int alpha = Math.round(
                    Mth.clamp((sloopEntity.getDamage() / (sloopEntity.getDamageThreshold())) * 0.75f, 0, 0.75f) * 255);
            this.sloopModel.renderToBuffer(poseStack, damageVertexConsumer, packedLight, OverlayTexture.NO_OVERLAY,
                    FastColor.ARGB32.color(alpha, 1, 1, 1));
        }

        poseStack.popPose();
        if (sloopEntity.hasCustomName() && sloopEntity.isFunctional()) {
            String name = sloopEntity.getName().getString();
            int length = 20;
            if (name.length() > length) {
                name = name.substring(0, length);
            }
            float scale = 0.015625F * 1F;
            Vec3 pos = new Vec3(-1.327, 0.885, 2.4).yRot((float) Math.toRadians(-entityYaw));
            if (name.length() > 12) {
                scale = scale / (name.length() / 13f);
            }
            renderTextLine(pos, -60 - entityYaw, scale, font, name, poseStack, bufferSource, packedLight, 10, 40);
            pos = new Vec3(0.9, 1.005, -3.1).yRot((float) Math.toRadians(-entityYaw));
            renderTextLine(pos, 180 - entityYaw, scale, font, name, poseStack, bufferSource, packedLight, 10, 40);
        }

        super.render(sloopEntity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
    }

    private RenderType getRenderType(final SloopEntity sloopEntity) {
        return sloopEntity.getPaintColor()
                .map(dyeColor -> AlekiShipsRenderTypes.paintedEntityCutoutNoCull(this.getTextureLocation(sloopEntity),
                        PAINT_OVERLAYS.get(dyeColor)))
                .orElseGet(() -> this.sloopModel.renderType(this.getTextureLocation(sloopEntity)));
    }

    @Override
    public ResourceLocation getTextureLocation(final SloopEntity sloopEntity) {
        return this.sloopTexture;
    }

    public ResourceLocation getMainsailTexture(final SloopEntity sloopEntity) {
        return SAIL_TEXTURES.get(sloopEntity.getMainsailDye());
    }

    public ResourceLocation getJibsailTexture(final SloopEntity sloopEntity) {
        return SAIL_TEXTURES.get(sloopEntity.getJibsailDye());
    }
}