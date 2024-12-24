package com.alekiponi.alekiships.client.render.entity.vehicle;

import com.alekiponi.alekiships.client.model.entity.AnchorEntityModel;
import com.alekiponi.alekiships.client.model.entity.SloopConstructionModel;
import com.alekiponi.alekiships.client.model.entity.SloopEntityModel;
import com.alekiponi.alekiships.client.render.entity.vehicle.vehiclehelper.AnchorRenderer;
import com.alekiponi.alekiships.common.block.AlekiShipsBlocks;
import com.alekiponi.alekiships.common.entity.vehicle.SloopUnderConstructionEntity;
import com.alekiponi.alekiships.util.BoatMaterial;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.StairsShape;

import java.util.function.Function;

import static com.alekiponi.alekiships.common.block.AngledBoatFrameBlock.FACING;
import static com.alekiponi.alekiships.common.block.AngledBoatFrameBlock.SHAPE;

public class SloopConstructionRenderer extends EntityRenderer<SloopUnderConstructionEntity> {

    protected final ResourceLocation sloopTexture;
    protected final SloopConstructionModel sloopModel;
    protected final AnchorEntityModel anchorModel;
    private final BlockRenderDispatcher blockRenderer;

    /**
     * @param sloopTexture The texture location. Must include file extension!
     */
    public SloopConstructionRenderer(final EntityRendererProvider.Context context,
                                     final ResourceLocation sloopTexture) {
        super(context);
        this.shadowRadius = 0.8F;
        this.sloopTexture = sloopTexture;
        this.sloopModel = new SloopConstructionModel(context.bakeLayer(SloopEntityModel.LAYER_LOCATION));
        this.anchorModel = new AnchorEntityModel(context.bakeLayer(AnchorEntityModel.LAYER_LOCATION));
        this.blockRenderer = context.getBlockRenderDispatcher();
    }

    public static EntityRendererProvider<SloopUnderConstructionEntity> provider(
            final Function<String, ResourceLocation> modLocation, final BoatMaterial boatMaterial) {
        return context -> new SloopConstructionRenderer(context, modLocation.apply(
                "textures/entity/watercraft/sloop_construction/" + boatMaterial.getSerializedName() + ".png"));
    }

    @Override
    public void render(final SloopUnderConstructionEntity constructionEntity, final float entityYaw,
                       final float partialTicks, final PoseStack poseStack, final MultiBufferSource bufferSource,
                       final int packedLight) {
        poseStack.pushPose();
        poseStack.translate(0, 0.5, 0);
        poseStack.mulPose(Axis.YP.rotationDegrees(180 - entityYaw));

        poseStack.translate(0, 1.0625F, 0);
        poseStack.scale(-1, -1, 1);
        poseStack.mulPose(Axis.YP.rotationDegrees(0));
        /*
        if (constructionEntity.getDamage() > constructionEntity.getDamageThreshold()) {
            poseStack.mulPose(Axis.ZP.rotationDegrees(constructionEntity.getId() % 30));
        }*/
        //sloopModel.setupAnim(constructionEntity, partialTicks, 0, -0.1F, 0, 0);


        if (constructionEntity.tickCount < 1) {
            poseStack.popPose();
            super.render(constructionEntity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
            return;
        }
        poseStack.translate(0, 1.57F, 0);
        final VertexConsumer vertexConsumer = bufferSource.getBuffer(this.sloopModel.renderType(getTextureLocation(constructionEntity)));
        this.sloopModel.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY);


        if(constructionEntity.getConstructionStage().ordinal() >= SloopUnderConstructionEntity.ConstructionState.DECK.ordinal()){
            //render keel
            this.sloopModel.getKeel().render(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY);
        }
        if(constructionEntity.getConstructionStage().ordinal() >= SloopUnderConstructionEntity.ConstructionState.BOWSPRIT.ordinal()){
            //render deck
            for(ModelPart part : sloopModel.getDeck()){
                part.render(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY);
            }
        }
        if(constructionEntity.getConstructionStage().ordinal() >= SloopUnderConstructionEntity.ConstructionState.MAST.ordinal()){
            //render bowsprit
            this.sloopModel.getBowsprit().render(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY);
        }
        if(constructionEntity.getConstructionStage().ordinal() >= SloopUnderConstructionEntity.ConstructionState.BOOM.ordinal()){
            //render mast
            this.sloopModel.getMast().render(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY);
        }
        if(constructionEntity.getConstructionStage().ordinal() >= SloopUnderConstructionEntity.ConstructionState.MAINSAIL.ordinal()){
            //render boom and gaff
            /*
            for(ModelPart part : sloopModel.getBoomGaff()){
            }*/
            sloopModel.getBoomGaff()[0].render(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY);
        }
        if(constructionEntity.getConstructionStage().ordinal() >= SloopUnderConstructionEntity.ConstructionState.JIBSAIl.ordinal()){
            //render mainsail furled
            this.sloopModel.getMainsail().render(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY);
        }
        if(constructionEntity.getConstructionStage().ordinal() >= SloopUnderConstructionEntity.ConstructionState.RAILINGS_STERN.ordinal()){
            //render jibsail furled
            poseStack.pushPose();
            poseStack.translate(0,-1.57F,0);
            this.sloopModel.getJibsailFurled().render(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY);
            poseStack.popPose();
        }
        if(constructionEntity.getConstructionStage().ordinal() >= SloopUnderConstructionEntity.ConstructionState.RAILINGS_BOW.ordinal()){
            //render stern railing
            for(ModelPart part : sloopModel.getSternRailing()){
                part.render(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY);
            }
        }
        if(constructionEntity.getConstructionStage().ordinal() >= SloopUnderConstructionEntity.ConstructionState.ANCHOR.ordinal()){
            //render bow railing

            for(ModelPart part : sloopModel.getBowRailing()){
                part.render(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY);
            }
        }
        if(constructionEntity.getConstructionStage().ordinal() >= SloopUnderConstructionEntity.ConstructionState.RIGGING.ordinal()){
            poseStack.pushPose();

            poseStack.translate(1.34f+1.3,-2.52+1.6,-2.52-0.75);
            poseStack.mulPose(Axis.XP.rotationDegrees(90));
            poseStack.mulPose(Axis.ZP.rotationDegrees(60));

            final VertexConsumer vertexConsumerAnchor = bufferSource.getBuffer(this.anchorModel.renderType(
                    AnchorRenderer.ANCHOR));
            this.anchorModel.renderToBuffer(poseStack, vertexConsumerAnchor, packedLight, OverlayTexture.NO_OVERLAY);
            poseStack.popPose();
        }



        //render scaffolding blocks

        poseStack.translate(0, -1.58F, 0);

        BlockState blockstate = AlekiShipsBlocks.BOAT_FRAME_ANGLED.get().defaultBlockState().setValue(FACING, Direction.WEST).setValue(SHAPE, StairsShape.OUTER_RIGHT);
        poseStack.pushPose();
        poseStack.translate(16.0 / 16.0, 9.0 / 16.0, 32.0 / 16.0);
        this.blockRenderer.renderSingleBlock(blockstate, poseStack, bufferSource, packedLight,
                OverlayTexture.NO_OVERLAY);
        poseStack.popPose();

        blockstate = AlekiShipsBlocks.BOAT_FRAME_ANGLED.get().defaultBlockState().setValue(FACING, Direction.EAST).setValue(SHAPE, StairsShape.OUTER_LEFT);
        poseStack.pushPose();
        poseStack.translate(-32.0 / 16.0, 9.0 / 16.0, 32.0 / 16.0);
        this.blockRenderer.renderSingleBlock(blockstate, poseStack, bufferSource, packedLight,
                OverlayTexture.NO_OVERLAY);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.translate(0 / 16.0, 9.0 / 16.0, 32.0 / 16.0);
        blockstate = AlekiShipsBlocks.BOAT_FRAME_ANGLED.get().defaultBlockState();
        this.blockRenderer.renderSingleBlock(blockstate, poseStack, bufferSource, packedLight,
                OverlayTexture.NO_OVERLAY);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.translate(-16.0 / 16.0, 9.0 / 16.0, 32.0 / 16.0);
        blockstate = AlekiShipsBlocks.BOAT_FRAME_ANGLED.get().defaultBlockState();
        this.blockRenderer.renderSingleBlock(blockstate, poseStack, bufferSource, packedLight,
                OverlayTexture.NO_OVERLAY);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.translate(-32.0 / 16.0, 9.0 / 16.0, 16.0 / 16.0);
        blockstate = AlekiShipsBlocks.BOAT_FRAME_ANGLED.get().defaultBlockState().setValue(FACING, Direction.EAST);
        this.blockRenderer.renderSingleBlock(blockstate, poseStack, bufferSource, packedLight,
                OverlayTexture.NO_OVERLAY);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.translate(-32.0 / 16.0, 9.0 / 16.0, 0.0 / 16.0);
        blockstate = AlekiShipsBlocks.BOAT_FRAME_ANGLED.get().defaultBlockState().setValue(FACING, Direction.EAST);
        this.blockRenderer.renderSingleBlock(blockstate, poseStack, bufferSource, packedLight,
                OverlayTexture.NO_OVERLAY);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.translate(-32.0 / 16.0, 9.0 / 16.0, -16.0 / 16.0);
        blockstate = AlekiShipsBlocks.BOAT_FRAME_ANGLED.get().defaultBlockState().setValue(FACING, Direction.EAST);
        this.blockRenderer.renderSingleBlock(blockstate, poseStack, bufferSource, packedLight,
                OverlayTexture.NO_OVERLAY);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.translate(-32.0 / 16.0, 9.0 / 16.0, -32.0 / 16.0);
        blockstate = AlekiShipsBlocks.BOAT_FRAME_ANGLED.get().defaultBlockState().setValue(FACING, Direction.SOUTH).setValue(SHAPE, StairsShape.OUTER_LEFT);
        this.blockRenderer.renderSingleBlock(blockstate, poseStack, bufferSource, packedLight,
                OverlayTexture.NO_OVERLAY);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.translate(16.0 / 16.0, 9.0 / 16.0, 16.0 / 16.0);
        blockstate = AlekiShipsBlocks.BOAT_FRAME_ANGLED.get().defaultBlockState().setValue(FACING, Direction.WEST);
        this.blockRenderer.renderSingleBlock(blockstate, poseStack, bufferSource, packedLight,
                OverlayTexture.NO_OVERLAY);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.translate(16.0 / 16.0, 9.0 / 16.0, 0.0 / 16.0);
        blockstate = AlekiShipsBlocks.BOAT_FRAME_ANGLED.get().defaultBlockState().setValue(FACING, Direction.WEST);
        this.blockRenderer.renderSingleBlock(blockstate, poseStack, bufferSource, packedLight,
                OverlayTexture.NO_OVERLAY);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.translate(16.0 / 16.0, 9.0 / 16.0, -16.0 / 16.0);
        blockstate = AlekiShipsBlocks.BOAT_FRAME_ANGLED.get().defaultBlockState().setValue(FACING, Direction.WEST);
        this.blockRenderer.renderSingleBlock(blockstate, poseStack, bufferSource, packedLight,
                OverlayTexture.NO_OVERLAY);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.translate(16.0 / 16.0, 9.0 / 16.0, -32.0 / 16.0);
        blockstate = AlekiShipsBlocks.BOAT_FRAME_ANGLED.get().defaultBlockState().setValue(FACING, Direction.SOUTH).setValue(SHAPE, StairsShape.OUTER_RIGHT);
        this.blockRenderer.renderSingleBlock(blockstate, poseStack, bufferSource, packedLight,
                OverlayTexture.NO_OVERLAY);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.translate(0 / 16.0, 9.0 / 16.0, -32.0 / 16.0);
        blockstate = AlekiShipsBlocks.BOAT_FRAME_ANGLED.get().defaultBlockState().setValue(FACING, Direction.WEST).setValue(SHAPE, StairsShape.INNER_LEFT);
        this.blockRenderer.renderSingleBlock(blockstate, poseStack, bufferSource, packedLight,
                OverlayTexture.NO_OVERLAY);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.translate(-16 / 16.0, 9.0 / 16.0, -32.0 / 16.0);
        blockstate = AlekiShipsBlocks.BOAT_FRAME_ANGLED.get().defaultBlockState().setValue(FACING, Direction.EAST).setValue(SHAPE, StairsShape.INNER_RIGHT);
        this.blockRenderer.renderSingleBlock(blockstate, poseStack, bufferSource, packedLight,
                OverlayTexture.NO_OVERLAY);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.translate(0 / 16.0, 9.0 / 16.0, -48.0 / 16.0);
        blockstate = AlekiShipsBlocks.BOAT_FRAME_ANGLED.get().defaultBlockState().setValue(FACING, Direction.WEST);
        this.blockRenderer.renderSingleBlock(blockstate, poseStack, bufferSource, packedLight,
                OverlayTexture.NO_OVERLAY);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.translate(-16 / 16.0, 9.0 / 16.0, -48.0 / 16.0);
        blockstate = AlekiShipsBlocks.BOAT_FRAME_ANGLED.get().defaultBlockState().setValue(FACING, Direction.EAST);
        this.blockRenderer.renderSingleBlock(blockstate, poseStack, bufferSource, packedLight,
                OverlayTexture.NO_OVERLAY);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.translate(0 / 16.0, 9.0 / 16.0, -64.0 / 16.0);
        blockstate = AlekiShipsBlocks.BOAT_FRAME_ANGLED.get().defaultBlockState().setValue(FACING, Direction.SOUTH).setValue(SHAPE, StairsShape.OUTER_RIGHT);
        this.blockRenderer.renderSingleBlock(blockstate, poseStack, bufferSource, packedLight,
                OverlayTexture.NO_OVERLAY);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.translate(-16 / 16.0, 9.0 / 16.0, -64.0 / 16.0);
        blockstate = AlekiShipsBlocks.BOAT_FRAME_ANGLED.get().defaultBlockState().setValue(FACING, Direction.SOUTH).setValue(SHAPE, StairsShape.OUTER_LEFT);
        this.blockRenderer.renderSingleBlock(blockstate, poseStack, bufferSource, packedLight,
                OverlayTexture.NO_OVERLAY);
        poseStack.popPose();

        for (int i = 0; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                poseStack.pushPose();
                poseStack.translate(-16 * i / 16.0, 17.0 / 16.0, 16.0 * j / 16.0);
                blockstate = AlekiShipsBlocks.BOAT_FRAME_FLAT.get().defaultBlockState();
                this.blockRenderer.renderSingleBlock(blockstate, poseStack, bufferSource, packedLight,
                        OverlayTexture.NO_OVERLAY);
                poseStack.popPose();
            }
        }





        poseStack.popPose();
        super.render(constructionEntity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(final SloopUnderConstructionEntity constructionEntity) {
        return this.sloopTexture;
    }
}