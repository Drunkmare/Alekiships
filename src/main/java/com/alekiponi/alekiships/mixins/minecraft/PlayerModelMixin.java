package com.alekiponi.alekiships.mixins.minecraft;

import com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.AbstractCompartmentEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerModel.class)
public abstract class PlayerModelMixin<T extends LivingEntity> extends HumanoidModel<T> {

    @Shadow
    @Final
    public ModelPart rightPants;

    @Shadow
    @Final
    public ModelPart leftPants;

    public PlayerModelMixin(ModelPart pRoot) {
        super(pRoot);
    }

    @Inject(method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V", at = @At(value = "TAIL"))
    void injectRidingPoseChange(T pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks,
                                float pNetHeadYaw, float pHeadPitch, CallbackInfo ci) {

        if (this.riding) {
            if (pEntity.getVehicle() instanceof AbstractCompartmentEntity compartment) {
                if (compartment.getRidingPose() == AbstractCompartmentEntity.RidingPose.STANDING) {
                    this.rightLeg.setPos(-1.9F, 12, 0);
                    this.leftLeg.setPos(01.9F, 12, 0);
                    this.rightPants.setPos(-1.9F, 12, 0);
                    this.leftPants.setPos(01.9F, 12, 0);

                    this.rightLeg.xRot = 0;
                    this.rightLeg.yRot = 0;
                    this.rightLeg.zRot = 0;

                    this.leftLeg.xRot = 0;
                    this.leftLeg.yRot = 0;
                    this.leftLeg.zRot = 0;

                    this.rightPants.xRot = 0;
                    this.rightPants.yRot = 0;
                    this.rightPants.zRot = 0;

                    this.leftPants.xRot = 0;
                    this.leftPants.yRot = 0;
                    this.leftPants.zRot = 0;

                } else if (compartment.getRidingPose() == AbstractCompartmentEntity.RidingPose.COMPACT) {
                    this.rightLeg.xRot = -1.570796F;
                    this.rightLeg.yRot = 0F;
                    this.rightLeg.zRot = 0F;

                    this.leftLeg.xRot = -1.570796F;
                    this.leftLeg.yRot = -0.002F;
                    this.leftLeg.zRot = -0.002F;

                    this.rightPants.xRot = -1.570796F;
                    this.rightPants.yRot = 0F;
                    this.rightPants.zRot = 0F;

                    this.leftPants.xRot = -1.570796F;
                    this.leftPants.yRot = -0.002F;
                    this.leftPants.zRot = -0.002F;

                    this.rightLeg.setPos(-1.9F, 11.5F, 0.0F);
                    this.leftLeg.setPos(01.9F, 11.5F, 0.0F);

                    this.rightPants.setPos(-1.9F, 11.6F, 0F);
                    this.leftPants.setPos(01.9F, 11.6F, 0F);
                } else if (compartment.getRidingPose() == AbstractCompartmentEntity.RidingPose.ULTRA_COMPACT) {
                    this.rightLeg.xRot = -1.570796F;
                    this.rightLeg.yRot = -0.1570796F;
                    this.rightLeg.zRot = 0F;

                    this.leftLeg.xRot = -1.570796F;
                    this.leftLeg.yRot = 0.1570796F;
                    this.leftLeg.zRot = -0F;

                    this.rightPants.xRot = -1.570796F;
                    this.rightPants.yRot = -0.1570796F;
                    this.rightPants.zRot = 0F;

                    this.leftPants.xRot = -1.570796F;
                    this.leftPants.yRot = 0.1570796F;
                    this.leftPants.zRot = -0F;

                    this.rightLeg.setPos(-1.9F, 13.6F, 1F);
                    this.leftLeg.setPos(01.9F, 13.6F, 1F);

                    this.rightPants.setPos(-1.9F, 13.6F, 1F);
                    this.leftPants.setPos(01.9F, 13.6F, 1F);
                }
            }
        } /*else {
            this.rightLeg.setPos(-1.9F, 12.0F, 0.0F);
            this.leftLeg.setPos(01.9F, 12.0F, 0.0F);
            this.rightPants.setPos(-1.9F, 12.0F, 0.0F);
            this.leftPants.setPos(01.9F, 12.0F, 0.0F);
            if (this.crouching) {
                this.rightLeg.z = 4.0F;
                this.leftLeg.z = 4.0F;
                this.rightLeg.y = 12.2F;
                this.leftLeg.y = 12.2F;

                this.rightPants.z = 4.0F;
                this.leftPants.z = 4.0F;
                this.rightPants.y = 12.2F;
                this.leftPants.y = 12.2F;
            }
        }*/
    }
}
