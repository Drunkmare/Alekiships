package com.alekiponi.alekiships.client.render.entity.vehicle.vehiclehelper;

import com.alekiponi.alekiships.common.entity.vehicle.AbstractAlekiBoatEntity;
import com.alekiponi.alekiships.common.entity.vehicle.AbstractVehicle;
import com.alekiponi.alekiships.common.entity.vehiclehelper.MastEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BannerRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BannerPatternLayers;

import java.util.List;

public class MastRenderer extends EntityRenderer<MastEntity> {
    private static final int BANNER_WIDTH = 20;
    private static final int BANNER_HEIGHT = 40;
    private static final int MAX_PATTERNS = 16;
    public static final String FLAG = "flag";
    private static final String POLE = "pole";
    private static final String BAR = "bar";
    private final ModelPart flag;
    private final ModelPart pole;
    private final ModelPart bar;
    public MastRenderer(final EntityRendererProvider.Context context) {
        super(context);
        ModelPart modelpart = context.bakeLayer(ModelLayers.BANNER);
        this.flag = modelpart.getChild("flag");
        this.pole = modelpart.getChild("pole");
        this.bar = modelpart.getChild("bar");
    }

    @Override
    public void render(final MastEntity mast, final float entityYaw, final float partialTicks,
                       final PoseStack poseStack, final MultiBufferSource bufferSource, int packedLight) {
        AbstractVehicle vehicle = mast.getTrueVehicle();
        if (vehicle != null && LightTexture.block(packedLight) < vehicle.getCompartmentBlockLight()) {
            packedLight = LightTexture.pack(Math.max(0, vehicle.getCompartmentBlockLight() - 1), getSkyLightLevel(mast, mast.blockPosition()));
        }

        if (!(mast.getTrueVehicle() instanceof AbstractAlekiBoatEntity ship)) return;

        if(!(mast.getBanner().getItem() instanceof BannerItem)) return;

        final float rotation = ship.getWindLocalRotation() + ship.getYRot();
        final float height = mast.getBbHeight()+0.4175f;

        poseStack.pushPose();

        poseStack.translate(0, height, 0f);

        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(180 - ship.getYRot()));
        poseStack.mulPose(Axis.ZP.rotationDegrees(90));

        poseStack.scale(0.6766667F, -0.6666667F, -0.6666667F);
        poseStack.translate(0,1.935f,0);
        VertexConsumer vertexconsumer = ModelBakery.BANNER_BASE.buffer(bufferSource, RenderType::entitySolid);
        this.bar.render(poseStack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();

        // render banner itself
        float windLocalAngle = Mth.wrapDegrees(ship.getWindLocalRotation() - ship.getYRot());
        poseStack.mulPose(Axis.YP.rotationDegrees(rotation));
        poseStack.mulPose(Axis.ZP.rotationDegrees(90));

        poseStack.translate(0,0,-0.05f);

        renderBanner(mast.getBanner(), partialTicks, poseStack, bufferSource, packedLight, OverlayTexture.NO_OVERLAY);

        poseStack.popPose();

        super.render(mast, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
    }

    public void renderBanner(ItemStack bannerItem, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        final BannerPatternLayers patterns = bannerItem.getOrDefault(DataComponents.BANNER_PATTERNS,
                BannerPatternLayers.EMPTY);
        final DyeColor color = ((BannerItem) bannerItem.getItem()).getColor();

        pPoseStack.pushPose();
        pPoseStack.scale(0.6666667F, -0.6666667F, -0.6666667F);
        BannerRenderer.renderPatterns(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, this.flag,
                ModelBakery.BANNER_BASE, true, color, patterns);
        pPoseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(final MastEntity pEntity) {
        return null;
    }
}