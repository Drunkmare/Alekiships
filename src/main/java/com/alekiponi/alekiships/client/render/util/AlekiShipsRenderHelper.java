package com.alekiponi.alekiships.client.render.util;

import com.alekiponi.alekiships.common.entity.OBBEntity;
import com.alekiponi.alekiships.common.physics.OBB;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import static com.alekiponi.alekiships.common.physics.Vec2Helper.leftHandPerpendicular;
import static com.alekiponi.alekiships.common.physics.Vec2Helper.vectorTo;

public class AlekiShipsRenderHelper {
    public static void addVertexPair(VertexConsumer pConsumer, Matrix4f pMatrix, float p_174310_, float p_174311_,
                                      float p_174312_, int pEntityBlockLightLevel, int pLeashHolderBlockLightLevel,
                                      int pEntitySkyLightLevel, int pLeashHolderSkyLightLevel, float p_174317_,
                                      float p_174318_, float p_174319_, float p_174320_, int pIndex,
                                      boolean p_174322_) {
        float f = (float) pIndex / 24.0F;
        int i = (int) Mth.lerp(f, (float) pEntityBlockLightLevel, (float) pLeashHolderBlockLightLevel);
        int j = (int) Mth.lerp(f, (float) pEntitySkyLightLevel, (float) pLeashHolderSkyLightLevel);
        int k = LightTexture.pack(i, j);
        float f1 = pIndex % 2 == (p_174322_ ? 1 : 0) ? 0.7F : 1.0F;
        float f2 = 0.5F * f1;
        float f3 = 0.4F * f1;
        float f4 = 0.3F * f1;
        float f5 = p_174310_ * f;
        float f6 = p_174311_ > 0.0F ? p_174311_ * f * f : p_174311_ - p_174311_ * (1.0F - f) * (1.0F - f);
        float f7 = p_174312_ * f;
        pConsumer.vertex(pMatrix, f5 - p_174319_, f6 + p_174318_, f7 + p_174320_).color(f2, f3, f4, 1.0F).uv2(k)
                .endVertex();
        pConsumer.vertex(pMatrix, f5 + p_174319_, f6 + p_174317_ - p_174318_, f7 - p_174320_).color(f2, f3, f4, 1.0F)
                .uv2(k).endVertex();
    }

    public static <E extends Entity> void renderRope(Entity pEntity, float pPartialTicks, PoseStack pPoseStack,
                                                     MultiBufferSource pBuffer, E pLeashHolder, int blockLightLevel) {
        pPoseStack.pushPose();
        Vec3 vec3 = pLeashHolder.getRopeHoldPosition(pPartialTicks);
        double d0 = (double) (Mth.lerp(pPartialTicks, pEntity.getYRot(),
                pEntity.getYRot()) * ((float) Math.PI / 180F)) + (Math.PI / 2D);
        Vec3 vec31 = pEntity.getLeashOffset(pPartialTicks);
        double d1 = Math.cos(d0) * vec31.z + Math.sin(d0) * vec31.x;
        double d2 = Math.sin(d0) * vec31.z - Math.cos(d0) * vec31.x;
        double d3 = Mth.lerp(pPartialTicks, pEntity.xo, pEntity.getX()) + d1;
        double d4 = Mth.lerp(pPartialTicks, pEntity.yo, pEntity.getY()) + vec31.y;
        double d5 = Mth.lerp(pPartialTicks, pEntity.zo, pEntity.getZ()) + d2;
        pPoseStack.translate(d1, vec31.y, d2);
        float f = (float) (vec3.x - d3);
        float f1 = (float) (vec3.y - d4);
        float f2 = (float) (vec3.z - d5);
        float f3 = 0.025F;
        VertexConsumer vertexconsumer = pBuffer.getBuffer(RenderType.leash());
        Matrix4f matrix4f = pPoseStack.last().pose();
        float f4 = Mth.invSqrt(f * f + f2 * f2) * 0.025F / 2.0F;
        float f5 = f2 * f4;
        float f6 = f * f4;
        BlockPos blockpos = BlockPos.containing(pEntity.getEyePosition(pPartialTicks));
        BlockPos blockpos1 = BlockPos.containing(pLeashHolder.getEyePosition(pPartialTicks));
        int i = blockLightLevel;
        int j = pLeashHolder.level().getBrightness(LightLayer.BLOCK, blockpos);
        int k = pEntity.level().getBrightness(LightLayer.SKY, blockpos);
        int l = pEntity.level().getBrightness(LightLayer.SKY, blockpos1);

        for (int i1 = 0; i1 <= 24; ++i1) {
            AlekiShipsRenderHelper.addVertexPair(vertexconsumer, matrix4f, f, f1, f2, i, j, k, l, 0.025F, 0.025F, f5, f6, i1, false);
        }

        for (int j1 = 24; j1 >= 0; --j1) {
            AlekiShipsRenderHelper.addVertexPair(vertexconsumer, matrix4f, f, f1, f2, i, j, k, l, 0.025F, 0.0F, f5, f6, j1, true);
        }

        pPoseStack.popPose();
    }

    public static void renderLinePolygon(PoseStack pPoseStack, VertexConsumer pConsumer, double[][] pBottomVertices, double pMinY, double pMaxY, float pRed, float pGreen, float pBlue, float pAlpha) {
        renderLinePolygon(pPoseStack, pConsumer, pBottomVertices, pMinY, pMaxY, pRed, pGreen, pBlue, pAlpha, pRed, pGreen, pBlue);
    }

    public static void renderLinePolygon(PoseStack pPoseStack, VertexConsumer pConsumer, double[][] pBottomVertices, double pMinY, double pMaxY, float pRed, float pGreen, float pBlue, float pAlpha, float pRed2, float pGreen2, float pBlue2) {
        Matrix4f lastPose = pPoseStack.last().pose();
        Matrix3f lastNormal = pPoseStack.last().normal();

        int vertexCount = pBottomVertices.length;

        float maxY = (float)pMaxY;
        float minY = (float)pMinY;

        Vec2[] vertices = new Vec2[vertexCount];
        for(int i = 0; i < vertexCount; i++){
            vertices[i] = new Vec2((float)pBottomVertices[i][0], (float)pBottomVertices[i][1]);
        }

        // collect a list of edges... each two consecutive vertices should make an edge
        Vec2[][] edges = new Vec2[vertexCount][2];

        edges[0][0] = vertices[vertexCount-1];
        edges[0][1] = vertices[0];

        for(int i = 1; i < vertexCount; i++){
            edges[i][0] = vertices[i-1];
            edges[i][1] = vertices[i];
        }

        // for each edge, generate a normal which is perpendicular to the vector between the two vertices in the side
        Vec2[] normals = new Vec2[vertexCount];

        for(int i = 0; i < vertexCount; i ++){
            normals[i] = leftHandPerpendicular(vectorTo(edges[i][0], edges[i][1])).normalized();
            //normals[i] = vectorTo(edges[i][0], edges[i][1]).normalized();
        }


        for(int i = 0; i < vertexCount; i++){

            // create i given horizontal sides, bottom

            pConsumer.vertex(lastPose, edges[i][0].x, minY, edges[i][0].y )
                    .color(pRed, pGreen2, pBlue2, pAlpha)
                    .normal(lastNormal, normals[i].x, 0.0F, normals[i].y)
                    .endVertex();

            pConsumer.vertex(lastPose, edges[i][0].x,  minY, edges[i][0].y )
                    .color(pRed, pGreen2, pBlue2, pAlpha)
                    .normal(lastNormal, normals[i].x, 0.0F, normals[i].y)
                    .endVertex();

            // create i given horizontal sides, top

            pConsumer.vertex(lastPose, edges[i][1].x,  maxY, edges[i][1].y )
                    .color(pRed, pGreen2, pBlue2, pAlpha)
                    .normal(lastNormal, normals[i].x, 0.0F, normals[i].y)
                    .endVertex();

            pConsumer.vertex(lastPose, edges[i][1].x,  maxY, edges[i][1].y )
                    .color(pRed, pGreen2, pBlue2, pAlpha)
                    .normal(lastNormal, normals[i].x, 0.0F, normals[i].y)
                    .endVertex();


            // add the vertices for the top and bottom sides

            pConsumer.vertex(lastPose, vertices[i].x,  maxY, vertices[i].y)
                    .color(pRed, pGreen2, pBlue2, pAlpha)
                    .normal(lastNormal, 0, 1.0F, 0)
                    .endVertex();

            pConsumer.vertex(lastPose, vertices[i].x, minY, vertices[i].y )
                    .color(pRed, pGreen2, pBlue2, pAlpha)
                    .normal(lastNormal, 0, -1.0F, 0)
                    .endVertex();


        }



        pConsumer.vertex(lastPose, vertices[vertexCount-1].x,  maxY, vertices[vertexCount-1].y)
                .color(pRed, pGreen2, pBlue2, pAlpha)
                .normal(lastNormal, 0, 1.0F, 0)
                .endVertex();

        for(int i = 0; i < vertexCount; i++){

            // add the vertices for the top and bottom sides

            pConsumer.vertex(lastPose, vertices[i].x,  maxY, vertices[i].y)
                    .color(pRed, pGreen2, pBlue2, pAlpha)
                    .normal(lastNormal, 0, 1.0F, 0)
                    .endVertex();

            pConsumer.vertex(lastPose, vertices[i].x,  maxY, vertices[i].y)
                    .color(pRed, pGreen2, pBlue2, pAlpha)
                    .normal(lastNormal, 0, 1.0F, 0)
                    .endVertex();
        }

        pConsumer.vertex(lastPose, vertices[0].x,  maxY, vertices[0].y)
                .color(pRed, pGreen2, pBlue2, pAlpha)
                .normal(lastNormal, 0, 1.0F, 0)
                .endVertex();


        pConsumer.vertex(lastPose, vertices[vertexCount-1].x, minY, vertices[vertexCount-1].y )
                .color(pRed, pGreen2, pBlue2, pAlpha)
                .normal(lastNormal, 0, -1.0F, 0)
                .endVertex();

        for(int i = 0; i < vertexCount; i++){

            // add the vertices for the top and bottom sides

            pConsumer.vertex(lastPose, vertices[i].x, minY, vertices[i].y )
                    .color(pRed, pGreen2, pBlue2, pAlpha)
                    .normal(lastNormal, 0, -1.0F, 0)
                    .endVertex();

            pConsumer.vertex(lastPose, vertices[i].x, minY, vertices[i].y )
                    .color(pRed, pGreen2, pBlue2, pAlpha)
                    .normal(lastNormal, 0, -1.0F, 0)
                    .endVertex();

        }

        pConsumer.vertex(lastPose, vertices[0].x, minY, vertices[0].y )
                .color(pRed, pGreen2, pBlue2, pAlpha)
                .normal(lastNormal, 0, -1.0F, 0)
                .endVertex();

    }

    public static void renderPolygonalHitbox(PoseStack pPoseStack, VertexConsumer pBuffer, OBBEntity pEntity, float pPartialTicks){
        AABB aabb = pEntity.getBoundingBox().move(-pEntity.getX(), -pEntity.getY(), -pEntity.getZ());
        OBB obb = pEntity.getPolyBoundingBox();

        AlekiShipsRenderHelper.renderLinePolygon(pPoseStack, pBuffer, obb.getLowerVerticesForRender(), aabb.minY, aabb.maxY, 1.0f, 1.0f,1.0f,1.0f);

        Vec3 vec3 = pEntity.getViewVector(pPartialTicks);
        Matrix4f matrix4f = pPoseStack.last().pose();
        Matrix3f matrix3f = pPoseStack.last().normal();
        pBuffer.vertex(matrix4f, 0.0F, pEntity.getEyeHeight(), 0.0F).color(0, 0, 255, 255).normal(matrix3f, (float)vec3.x, (float)vec3.y, (float)vec3.z).endVertex();
        pBuffer.vertex(matrix4f, (float)(vec3.x * 2.0D), (float)((double)pEntity.getEyeHeight() + vec3.y * 2.0D), (float)(vec3.z * 2.0D)).color(0, 0, 255, 255).normal(matrix3f, (float)vec3.x, (float)vec3.y, (float)vec3.z).endVertex();

    }



}
