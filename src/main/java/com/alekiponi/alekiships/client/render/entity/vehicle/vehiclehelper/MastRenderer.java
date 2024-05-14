package com.alekiponi.alekiships.client.render.entity.vehicle.vehiclehelper;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.client.model.entity.AnchorEntityModel;
import com.alekiponi.alekiships.client.render.util.AlekiShipsRenderHelper;
import com.alekiponi.alekiships.common.entity.vehicle.AbstractAlekiBoatEntity;
import com.alekiponi.alekiships.common.entity.vehiclehelper.AnchorEntity;
import com.alekiponi.alekiships.common.entity.vehiclehelper.MastEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BannerRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.BannerBlock;
import net.minecraft.world.level.block.WallBannerBlock;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RotationSegment;

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
                       final PoseStack poseStack, final MultiBufferSource bufferSource, final int packedLight) {
        if (!(mast.getTrueVehicle() instanceof AbstractAlekiBoatEntity)) return;

        if(!(mast.getBanner().getItem() instanceof BannerItem)) return;

        AbstractAlekiBoatEntity ship = (AbstractAlekiBoatEntity) mast.getTrueVehicle();

        final float rotation = ship.getWindLocalRotation();
        final float height = mast.getBbHeight()+0.4175f;

        poseStack.pushPose();

        poseStack.translate(0, height, 0f);

        poseStack.pushPose();
        poseStack.mulPose(Axis.ZP.rotationDegrees(90));
        poseStack.scale(0.6766667F, -0.6666667F, -0.6666667F);
        poseStack.translate(0,1.935f,0);
        VertexConsumer vertexconsumer = ModelBakery.BANNER_BASE.buffer(bufferSource, RenderType::entitySolid);
        this.bar.render(poseStack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();

        poseStack.mulPose(Axis.YP.rotationDegrees(rotation));
        poseStack.mulPose(Axis.ZP.rotationDegrees(90));

        poseStack.translate(0,0,-0.05f);

        renderBanner(mast.getBanner(), partialTicks, poseStack, bufferSource, packedLight, OverlayTexture.NO_OVERLAY);

        poseStack.popPose();

        super.render(mast, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
    }

    public void renderBanner(ItemStack bannerItem, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {

        List<Pair<Holder<BannerPattern>, DyeColor>> list = BannerBlockEntity.createPatterns(((BannerItem) bannerItem.getItem()).getColor(), BannerBlockEntity.getItemPatterns(bannerItem));
        pPoseStack.pushPose();
        pPoseStack.scale(0.6666667F, -0.6666667F, -0.6666667F);


        BannerRenderer.renderPatterns(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, this.flag, ModelBakery.BANNER_BASE, true, list);
        pPoseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(final MastEntity pEntity) {
        return null;
    }
}