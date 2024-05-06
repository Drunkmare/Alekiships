package com.alekiponi.alekiships.common.physics;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class Vec2Helper {
    public static Vec2 yRot(Vec2 vec2, float pYaw) {
        float f = Mth.cos(pYaw);
        float f1 = Mth.sin(pYaw);
        float d0 = vec2.x * f + vec2.y * f1;
        float d2 = vec2.y * f - vec2.x * f1;
        return new Vec2(d0, d2);
    }

    public static Vec2 vectorTo(Vec2 pVec1, Vec2 pVec2) {
        return new Vec2(pVec2.x - pVec1.x, pVec2.y - pVec1.y);
    }

    public static Vec2 rightHandPerpendicular(Vec2 pVec1) {
        return new Vec2(-pVec1.y, pVec1.x);
    }

    public static Vec2 leftHandPerpendicular(Vec2 pVec1) {
        return new Vec2(pVec1.y, -pVec1.x);
    }

    public static Vec2 positionLocallyYrot(float localX, float localZ, float yRot) {
        return yRot(new Vec2(localX, localZ), (-yRot * ((float) Math.PI / 180F) - ((float) Math.PI / 2F)));
    }

    public static Vec2 positionLocallyYrot(Vec2 vec, float yRot) {
        return (positionLocallyYrot((float) vec.x, (float) vec.y, yRot));
    }
}
