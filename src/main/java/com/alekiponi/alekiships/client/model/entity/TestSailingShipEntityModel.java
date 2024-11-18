package com.alekiponi.alekiships.client.model.entity;// Made with Blockbench 4.9.4
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class TestSailingShipEntityModel<T extends Entity> extends EntityModel<T> {
    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath("modid", "anothershipfortesting"), "main");
    private final ModelPart static_parts;
    private final ModelPart sail1;
    private final ModelPart sail2;
    private final ModelPart sail3;

    public TestSailingShipEntityModel() {
        ModelPart root = createBodyLayer().bakeRoot();
        this.static_parts = root.getChild("static_parts");
        this.sail1 = root.getChild("sail1");
        this.sail2 = root.getChild("sail2");
        this.sail3 = root.getChild("sail3");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition static_parts = partdefinition.addOrReplaceChild("static_parts", CubeListBuilder.create().texOffs(0, 708).addBox(-70.0F, 61.0F, -323.0F, 128.0F, 16.0F, 300.0F, new CubeDeformation(0.0F))
                .texOffs(830, 712).addBox(-13.0F, -77.0F, -124.0F, 2.0F, 154.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(73, 581).addBox(-63.0F, 43.0F, -300.0F, 2.0F, 2.0F, 251.0F, new CubeDeformation(0.0F))
                .texOffs(160, 771).addBox(47.0F, 43.0F, -300.0F, 2.0F, 2.0F, 251.0F, new CubeDeformation(0.0F))
                .texOffs(830, 712).addBox(-13.0F, -77.0F, -189.0F, 2.0F, 154.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(6.0F, -53.0F, 173.0F));

        PartDefinition cube_r1 = static_parts.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(830, 712).addBox(-1.0F, -76.0F, -1.0F, 2.0F, 154.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-12.0F, 0.0F, 0.0F, -0.3054F, 0.0F, 0.0F));

        PartDefinition cube_r2 = static_parts.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(326, 548).addBox(68.0F, -15.5F, 49.0F, 95.0F, 15.0F, 92.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-18.0F, 77.0F, -173.0F, 0.0F, -0.7854F, 0.0F));

        PartDefinition sail1 = partdefinition.addOrReplaceChild("sail1", CubeListBuilder.create().texOffs(120, 72).addBox(-7.0F, -117.0F, -47.0F, 2.0F, 85.0F, 45.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -6.0F, 169.0F));

        PartDefinition sail2 = partdefinition.addOrReplaceChild("sail2", CubeListBuilder.create().texOffs(176, 91).addBox(-7.0F, -147.0F, -53.0F, 2.0F, 85.0F, 45.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 52.0F));

        PartDefinition sail3 = partdefinition.addOrReplaceChild("sail3", CubeListBuilder.create().texOffs(147, 91).addBox(-7.0F, -147.0F, -53.0F, 2.0F, 85.0F, 45.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, -13.0F));

        return LayerDefinition.create(meshdefinition, 1024, 1024);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        static_parts.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        sail1.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        sail2.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        sail3.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }
}