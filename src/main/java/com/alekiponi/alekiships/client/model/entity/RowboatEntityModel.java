package com.alekiponi.alekiships.client.model.entity;// Made with Blockbench 4.8.3
// Exported for Minecraft version 1.17 or later with Mojang mappings

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.common.entity.vehicle.RowboatEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class RowboatEntityModel extends EntityModel<RowboatEntity> {
    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            new ResourceLocation(AlekiShips.MOD_ID, "rowboat_entity"), "main");

    private final ModelPart waterocclusion;
    private final ModelPart hull;
    private final ModelPart oar_port;
    private final ModelPart oar_starboard;
    private final ModelPart bow_floor;
    private final ModelPart seats;
    private final ModelPart bow;
    private final ModelPart port_bow;
    private final ModelPart starboard_bow;
    private final ModelPart port;
    private final ModelPart starboard;
    private final ModelPart oarlocks;
    private final ModelPart keel;
    private final ModelPart transom;

    private final ModelPart cleat;

    public RowboatEntityModel() {
        ModelPart root = createBodyLayer().bakeRoot();
        this.waterocclusion = root.getChild("waterocclusion");
        this.hull = root.getChild("hull");
        this.oar_port = root.getChild("oar_port");
        this.oar_starboard = root.getChild("oar_starboard");
        this.bow_floor = root.getChild("bow_floor");
        this.seats = root.getChild("seats");
        this.bow = root.getChild("bow");
        this.port_bow = root.getChild("port_bow");
        this.starboard_bow = root.getChild("starboard_bow");
        this.port = root.getChild("port");
        this.starboard = root.getChild("starboard");
        this.oarlocks = root.getChild("oarlocks");
        this.keel = root.getChild("keel");
        this.transom = root.getChild("transom");
        this.cleat = root.getChild("cleat");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition waterocclusion = partdefinition.addOrReplaceChild("waterocclusion", CubeListBuilder.create(), PartPose.offset(0.0123F, 13.0F, -8.7623F));

        PartDefinition cube_r1 = waterocclusion.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(84, 238).addBox(-15.513F, -1.0F, -15.539F, 16.0F, 2.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0061F, 0.0F, 2.6189F, 0.0F, -0.7854F, 0.0F));

        PartDefinition cube_r2 = waterocclusion.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(89, 231).addBox(-9.8566F, -1.0F, -11.9816F, 4.0F, 2.0F, 23.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.4939F, 0.0F, 2.6189F, 0.0F, -1.5708F, 0.0F));

        PartDefinition cube_r3 = waterocclusion.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(56, 228).addBox(-6.8566F, -1.0F, -12.9816F, 34.0F, 2.0F, 26.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0061F, 0.0F, 2.6189F, 0.0F, -1.5708F, 0.0F));

        PartDefinition hull = partdefinition.addOrReplaceChild("hull", CubeListBuilder.create(), PartPose.offset(0.0F, 22.5F, 7.1667F));

        PartDefinition cube_r4 = hull.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(154, 0).addBox(-12.5F, -2.25F, -12.0F, 27.0F, 3.0F, 24.0F, new CubeDeformation(0.0F))
                .texOffs(129, 114).addBox(-14.5F, -0.25F, 1.0F, 27.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.25F, 0.3333F, 0.0F, -1.5708F, 0.0F));

        PartDefinition cube_r5 = hull.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(61, 114).addBox(-12.5F, -0.25F, 1.0F, 27.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.25F, 0.3333F, 0.0F, 1.5708F, 0.0F));

        PartDefinition oar_starboard = partdefinition.addOrReplaceChild("oar_starboard", CubeListBuilder.create().texOffs(129, 194).addBox(-21.875F, -0.5F, -0.5F, 30.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(129, 189).addBox(8.125F, -0.5F, -1.5F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(129, 202).addBox(-35.875F, -0.5F, -2.5F, 11.0F, 1.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(115, 197).addBox(-24.875F, -0.5F, -1.5F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-14.4971F, 10.7636F, -1.2288F, -0.3441F, 0.8192F, -0.456F));

        PartDefinition oar_port = partdefinition.addOrReplaceChild("oar_port", CubeListBuilder.create().texOffs(65, 194).addBox(-8.125F, -0.5F, -0.5F, 30.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(119, 189).addBox(-9.125F, -0.5F, -1.5F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(95, 202).addBox(24.875F, -0.5F, -2.5F, 11.0F, 1.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(129, 197).addBox(21.875F, -0.5F, -1.5F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(14.4971F, 10.7636F, -1.2288F, -0.3441F, -0.8192F, 0.456F));

        PartDefinition bow_floor = partdefinition.addOrReplaceChild("bow_floor", CubeListBuilder.create(), PartPose.offset(0.0F, 19.8333F, -13.1111F));

        PartDefinition cube_r6 = bow_floor.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(125, 9).addBox(-22.4826F, 2.6196F, 13.5743F, 3.0F, 1.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(135, 8).addBox(-19.4826F, 3.6196F, 12.5743F, 2.0F, 1.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(145, 7).addBox(-17.4826F, 4.6196F, 11.5743F, 2.0F, 1.0F, 10.0F, new CubeDeformation(0.0F))
                .texOffs(155, 6).addBox(-15.4826F, 5.6196F, 10.5743F, 2.0F, 1.0F, 12.0F, new CubeDeformation(0.0F))
                .texOffs(155, 5).addBox(-13.4826F, 5.6196F, 9.5743F, 2.0F, 1.0F, 14.0F, new CubeDeformation(0.0F))
                .texOffs(155, 3).addBox(-9.4826F, 5.6196F, 7.5743F, 2.0F, 1.0F, 18.0F, new CubeDeformation(0.0F))
                .texOffs(155, 2).addBox(-7.4826F, 5.6196F, 6.5743F, 2.0F, 1.0F, 20.0F, new CubeDeformation(0.0F))
                .texOffs(155, 1).addBox(-5.4826F, 5.6196F, 5.5743F, 1.0F, 1.0F, 22.0F, new CubeDeformation(0.0F))
                .texOffs(155, 4).addBox(-11.4826F, 5.6196F, 8.5743F, 2.0F, 1.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(16.5743F, -5.4529F, 12.5937F, 0.0F, -1.5708F, 0.0F));

        PartDefinition seats = partdefinition.addOrReplaceChild("seats", CubeListBuilder.create(), PartPose.offset(0.0F, 15.5F, -13.9286F));

        PartDefinition mid_seat_r1 = seats.addOrReplaceChild("mid_seat_r1", CubeListBuilder.create().texOffs(0, 233).addBox(-3.0F, -0.5F, -11.0F, 6.0F, 1.0F, 22.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 18.9286F, 0.0F, -1.5708F, 0.0F));

        PartDefinition cube_r7 = seats.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(5, 209).addBox(-1.0F, -0.5F, -11.0F, 2.0F, 1.0F, 22.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 2.6786F, 0.0F, -1.5708F, 0.0F));

        PartDefinition cube_r8 = seats.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(8, 187).addBox(-1.0F, -0.5F, -10.0F, 2.0F, 1.0F, 20.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.6786F, 0.0F, -1.5708F, 0.0F));

        PartDefinition cube_r9 = seats.addOrReplaceChild("cube_r9", CubeListBuilder.create().texOffs(9, 169).addBox(-1.5F, -0.5F, -8.0F, 3.0F, 1.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -1.8214F, 0.0F, -1.5708F, 0.0F));

        PartDefinition cube_r10 = seats.addOrReplaceChild("cube_r10", CubeListBuilder.create().texOffs(17, 139).addBox(-0.5F, -0.5F, -6.0F, 1.0F, 1.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -6.8214F, 0.0F, -1.5708F, 0.0F));

        PartDefinition cube_r11 = seats.addOrReplaceChild("cube_r11", CubeListBuilder.create().texOffs(14, 153).addBox(-1.5F, -0.5F, -6.0F, 3.0F, 1.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -4.8214F, 0.0F, -1.5708F, 0.0F));

        PartDefinition cube_r12 = seats.addOrReplaceChild("cube_r12", CubeListBuilder.create().texOffs(17, 127).addBox(-1.5F, -0.5F, -5.0F, 3.0F, 1.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -8.8214F, 0.0F, -1.5708F, 0.0F));

        PartDefinition bow = partdefinition.addOrReplaceChild("bow", CubeListBuilder.create().texOffs(20, 32).addBox(-1.5F, 4.8927F, 1.4294F, 3.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 15.0973F, -27.5894F));

        PartDefinition cube_r13 = bow.addOrReplaceChild("cube_r13", CubeListBuilder.create().texOffs(99, 9).addBox(-6.7793F, -0.1609F, -3.0F, 7.0F, 5.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(9, 7).addBox(-4.3433F, -6.1609F, -5.0F, 7.0F, 6.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.0556F, -0.2538F, 0.0F, 1.5708F, 0.0F));

        PartDefinition cube_r14 = bow.addOrReplaceChild("cube_r14", CubeListBuilder.create().texOffs(218, 235).addBox(-1.0F, -0.3045F, -5.7929F, 2.0F, 4.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0636F, -0.2538F, -0.9599F, 0.0F, 0.0F));

        PartDefinition cube_r15 = bow.addOrReplaceChild("cube_r15", CubeListBuilder.create().texOffs(4, 33).addBox(-2.0F, -4.7847F, -7.6899F, 4.0F, 4.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0636F, -0.2538F, -0.2182F, 0.0F, 0.0F));

        PartDefinition port_bow = partdefinition.addOrReplaceChild("port_bow", CubeListBuilder.create(), PartPose.offset(-4.7771F, 18.625F, -15.5328F));

        PartDefinition cube_r16 = port_bow.addOrReplaceChild("cube_r16", CubeListBuilder.create().texOffs(105, 150).addBox(-10.7253F, 4.75F, -7.7295F, 6.0F, 2.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(113, 128).addBox(-4.7253F, 4.75F, -6.7295F, 3.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(115, 141).addBox(1.2747F, 4.75F, -4.7295F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(115, 135).addBox(-1.7253F, 4.75F, -5.7295F, 3.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(10.0099F, -1.389F, 0.7505F, 0.0F, 1.885F, 0.0F));

        PartDefinition cube_r17 = port_bow.addOrReplaceChild("cube_r17", CubeListBuilder.create().texOffs(87, 123).addBox(-7.728F, -1.0F, -0.792F, 18.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(8.2365F, 4.361F, 1.569F, 0.0F, 1.885F, 0.0F));

        PartDefinition cube_r18 = port_bow.addOrReplaceChild("cube_r18", CubeListBuilder.create().texOffs(111, 145).mirror().addBox(6.2437F, 3.75F, -0.5766F, 6.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(81, 105).mirror().addBox(-11.7563F, 3.75F, -3.5766F, 18.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(129, 96).addBox(-13.1503F, -1.25F, 0.0774F, 26.0F, 5.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(129, 91).mirror().addBox(-14.1503F, -2.986F, 1.5614F, 27.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(65, 48).mirror().addBox(-13.1823F, -4.25F, 2.3214F, 27.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(69, 44).mirror().addBox(-13.1823F, -5.218F, 4.3374F, 27.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(97, 40).mirror().addBox(0.8177F, -6.218F, 4.3374F, 13.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(10.0739F, -1.389F, 0.7505F, 0.0F, 2.0508F, 0.0F));

        PartDefinition starboard_bow = partdefinition.addOrReplaceChild("starboard_bow", CubeListBuilder.create(), PartPose.offset(-4.7771F, 18.625F, -15.5328F));

        PartDefinition cube_r19 = starboard_bow.addOrReplaceChild("cube_r19", CubeListBuilder.create().texOffs(129, 150).mirror().addBox(4.7253F, 4.75F, -7.7295F, 6.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(129, 128).mirror().addBox(1.7253F, 4.75F, -6.7295F, 3.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(129, 141).mirror().addBox(-5.2747F, 4.75F, -4.7295F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(129, 135).mirror().addBox(-1.2747F, 4.75F, -5.7295F, 3.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-0.4557F, -1.389F, 0.7505F, 0.0F, -1.885F, 0.0F));

        PartDefinition cube_r20 = starboard_bow.addOrReplaceChild("cube_r20", CubeListBuilder.create().texOffs(129, 123).mirror().addBox(-10.272F, -1.0F, -0.792F, 18.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(1.3177F, 4.361F, 1.569F, 0.0F, -1.885F, 0.0F));

        PartDefinition cube_r21 = starboard_bow.addOrReplaceChild("cube_r21", CubeListBuilder.create().texOffs(129, 145).addBox(-12.2437F, 3.75F, -0.5766F, 6.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(129, 105).addBox(-6.2437F, 3.75F, -3.5766F, 18.0F, 2.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(69, 91).addBox(-12.8497F, -2.986F, 1.5614F, 27.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(67, 96).addBox(-12.8497F, -1.25F, 0.0774F, 26.0F, 5.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(129, 48).addBox(-13.8177F, -4.25F, 2.3214F, 27.0F, 3.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(129, 44).addBox(-13.8177F, -5.218F, 4.3374F, 27.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(129, 40).addBox(-13.8177F, -6.218F, 4.3374F, 13.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5197F, -1.389F, 0.7505F, 0.0F, -2.0508F, 0.0F));

        PartDefinition port = partdefinition.addOrReplaceChild("port", CubeListBuilder.create(), PartPose.offset(13.375F, 15.375F, 7.0F));

        PartDefinition cube_r22 = port.addOrReplaceChild("cube_r22", CubeListBuilder.create().texOffs(110, 137).addBox(-15.25F, -0.75F, -11.25F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-13.625F, 1.375F, 2.25F, 0.0F, -1.5708F, 0.0F));

        PartDefinition cube_r23 = port.addOrReplaceChild("cube_r23", CubeListBuilder.create().texOffs(67, 76).addBox(-13.25F, -1.0F, -2.5F, 28.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(75, 80).addBox(-8.25F, 0.0F, -3.5F, 21.0F, 5.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(91, 67).addBox(-0.25F, -4.0F, -0.5F, 14.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(93, 57).addBox(-13.25F, -5.0F, -1.5F, 13.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.125F, 0.625F, 0.75F, 0.0F, 1.5708F, 0.0F));

        PartDefinition starboard = partdefinition.addOrReplaceChild("starboard", CubeListBuilder.create(), PartPose.offset(-13.375F, 15.375F, 7.0F));

        PartDefinition cube_r24 = starboard.addOrReplaceChild("cube_r24", CubeListBuilder.create().texOffs(142, 137).addBox(-15.25F, -0.75F, 9.75F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(129, 80).addBox(-14.25F, -0.75F, 9.75F, 21.0F, 5.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(108, 132).addBox(-17.25F, -1.75F, -12.25F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(144, 132).addBox(-17.25F, -1.75F, 10.75F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(129, 76).addBox(-16.25F, -1.75F, 10.75F, 28.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(129, 67).addBox(-15.25F, -4.75F, 12.75F, 14.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(129, 57).addBox(-1.25F, -5.75F, 11.75F, 13.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(13.125F, 1.375F, 2.25F, 0.0F, -1.5708F, 0.0F));

        PartDefinition cleat = partdefinition.addOrReplaceChild("cleat", CubeListBuilder.create().texOffs(116, 172).addBox(13.25F, 13.774F, -11.25F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(116, 173).addBox(13.25F, 13.774F, -9.25F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(118, 178).addBox(13.25F, 13.774F, -13.25F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(10.25F, -5.75F, -13.25F, 0.0F, 1.5708F, 0.0F));

        PartDefinition cleat2 = cleat.addOrReplaceChild("cleat2", CubeListBuilder.create().texOffs(119, 179).addBox(13.25F, 13.774F, -9.25F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(119, 179).addBox(13.25F, 13.774F, -12.25F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.0F, 0.0F));

        PartDefinition oarlocks = partdefinition.addOrReplaceChild("oarlocks", CubeListBuilder.create().texOffs(129, 169).addBox(-16.0F, -2.1667F, -2.1667F, 3.0F, 3.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(113, 169).addBox(13.0F, -2.1667F, -2.1667F, 3.0F, 3.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(129, 177).addBox(-15.0F, -0.1667F, 1.8333F, 2.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(117, 177).addBox(13.0F, -0.1667F, 1.8333F, 2.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(129, 165).addBox(-15.0F, -0.1667F, -4.1667F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(119, 165).addBox(13.0F, -0.1667F, -4.1667F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 11.1667F, -0.8333F));

        PartDefinition keel = partdefinition.addOrReplaceChild("keel", CubeListBuilder.create().texOffs(164, 211).addBox(-1.0F, 0.25F, -34.91F, 2.0F, 2.0F, 43.0F, new CubeDeformation(0.0F))
                .texOffs(182, 236).addBox(-1.0F, -4.75F, 8.09F, 2.0F, 7.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 21.75F, 8.75F));

        PartDefinition transom = partdefinition.addOrReplaceChild("transom", CubeListBuilder.create().texOffs(190, 146).addBox(-13.0F, 0.25F, -5.25F, 26.0F, 5.0F, 7.0F, new CubeDeformation(0.0F))
                .texOffs(193, 159).addBox(-13.0F, -5.75F, -0.25F, 26.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 15.75F, 21.25F));

        PartDefinition bb_main = partdefinition.addOrReplaceChild("bb_main", CubeListBuilder.create().texOffs(0, 0).addBox(-13.0F, 0.0F, 23.0F, 0.0F, 0.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-17.0F, 0.0F, -7.0F, 0.0F, 0.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(17.0F, 0.0F, -7.0F, 0.0F, 0.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(13.0F, 0.0F, 23.0F, 0.0F, 0.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(5.0F, 0.0F, -31.0F, 0.0F, 0.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-5.0F, 0.0F, -31.0F, 0.0F, 0.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }

    private static void animatePaddle(RowboatEntity pBoat, int pSide, ModelPart pPaddle, float pLimbSwing) {
        float f = pBoat.getRowingTime(pSide, pLimbSwing);
        if (pSide == 0) {
            pPaddle.xRot = -Mth.clampedLerp(-2.0471975512f, -1.2617994F,
                    -(Mth.sin(-f) + 1.0F) / 2.0F);
            pPaddle.yRot = Mth.clampedLerp(0.7853981634f, -0.7853981634f,
                    (Mth.sin(-f + 1.0F) + 1.0F) / 2.0F);
        }
        if (pSide == 1) {
            pPaddle.xRot = -Mth.clampedLerp(-2.0471975512f, -1.2617994F,
                    (Mth.sin(-f) + 1.0F) / 2.0F);
            pPaddle.yRot = Mth.clampedLerp(0.7853981634f, -0.7853981634f,
                    (Mth.sin(-f + 1.0F) + 1.0F) / 2.0F);
            pPaddle.yRot = -pPaddle.yRot;
        }

    }

    public static void animateDestruction(RowboatEntity pEntity, ModelPart[] bones){
        /*
        float damage = pEntity.getDamage();
        float threshold = pEntity.getDamageThreshold();
        if(pEntity.tickCount > 1 && damage > threshold){
            float randomRotation = pEntityn.getRandomRotation();
            for(ModelPart part : bones){
                if(randomRotation < 0.333){
                    part.yRot = (1+randomRotation)*randomRotation;
                } else if(randomRotation < 0.666){
                    part.xRot = (1+randomRotation)*randomRotation;
                } else {
                    part.zRot = (1+randomRotation)*randomRotation;
                }
            }
            float bottomOfBoat = 24;
            for(ModelPart part : bones){
                part.y = bottomOfBoat;
            }
        }*/
    }

    public static ModelPart[] getAllParts(RowboatEntityModel rowboatEntityModel) {

        ModelPart[] parts = new ModelPart[]{
                rowboatEntityModel.waterocclusion,
                rowboatEntityModel.hull,
                rowboatEntityModel.oar_port,
                rowboatEntityModel.oar_starboard,
                rowboatEntityModel.bow_floor,
                rowboatEntityModel.seats,
                rowboatEntityModel.bow,
                rowboatEntityModel.port_bow,
                rowboatEntityModel.starboard_bow,
                rowboatEntityModel.port,
                rowboatEntityModel.starboard,
                rowboatEntityModel.oarlocks,
                rowboatEntityModel.keel,
                rowboatEntityModel.transom,
                rowboatEntityModel.cleat,
        };

        return parts;
    }

    private ModelPart[] allParts = new ModelPart[15];

    @Override
    public void setupAnim(RowboatEntity pEntity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
                          float headPitch) {
        //animateDestruction(pEntity, allParts);
        animatePaddle(pEntity, 0, this.getOarPort(), limbSwing);
        animatePaddle(pEntity, 1, this.getOarStarboard(), limbSwing);
    }

    public ModelPart getWaterocclusion() {
        return this.waterocclusion;
    }

    public ModelPart getOarPort() {
        return this.oar_port;
    }

    public ModelPart getOarStarboard() {
        return this.oar_starboard;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay,
                               float red, float green, float blue, float alpha) {
        hull.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        bow_floor.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        seats.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        bow.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        port_bow.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        starboard_bow.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        port.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        starboard.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        oarlocks.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        keel.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        transom.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        cleat.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}