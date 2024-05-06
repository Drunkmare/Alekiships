package com.alekiponi.alekiships.common.physics;

import net.minecraft.world.phys.Vec3;

public class Vec3Helper {
    public static Vec3 positionLocallyYrot(float localX, float localY, float localZ, float yRot) {
        return (new Vec3(localX, localY, localZ)).yRot(
                (-yRot * ((float) Math.PI / 180F) - ((float) Math.PI / 2F)));
    }

    public static Vec3 positionLocallyYrot(Vec3 vec, float yRot) {
        return (positionLocallyYrot((float) vec.x, (float) vec.y, (float) vec.z, yRot));
    }

}
