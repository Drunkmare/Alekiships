package com.alekiponi.alekiships.client.model.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import com.alekiponi.alekiships.AlekiShips;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class ConstructionEntityModel<T extends Entity> extends EntityModel<T> {

    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(AlekiShips.MOD_ID, "construction_entity"), "main");
    private final ModelPart bone;

    public ConstructionEntityModel() {
        ModelPart root = createBodyLayer().bakeRoot();
        this.bone = root.getChild("bone");
    }

    @SuppressWarnings("unused")
    public static LayerDefinition createBodyLayer() {//@formatter:off
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition bone = partdefinition.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(0, 3).addBox(-0.5F, -3.5F, -0.5F, 1.0F, 9.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-1.5F, -2.5F, -2.5F, 3.0F, 3.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 16.9187F, 0.6048F, -0.3927F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 16, 16);
    }//@formatter:on

    @Override
    public void setupAnim(T pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw,
            float pHeadPitch) {

    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay,
            int color) {
        bone.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }


}
