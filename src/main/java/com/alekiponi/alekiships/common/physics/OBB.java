package com.alekiponi.alekiships.common.physics;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;

import static com.alekiponi.alekiships.client.render.util.AlekiShipsRenderHelper.vectorTo;

public class OBB {

    AABB EXTENT;
    Vec2[] LOWER_VERTICES;
    Vec3 ORIGIN;
    Vec3 ORIENTATION;
    double YAW;
    double HEIGHT;

    public OBB(Vec2[] planarXZVertices) {

    }

    public OBB(ArrayList<Vec2> planarXZVertices, Vec3 origin, double height) {
        this(planarXZVertices.toArray(planarXZVertices.toArray(new Vec2[planarXZVertices.size()])), origin, height);
    }

    public OBB(Vec2[] planarXZVertices, Vec3 origin, double height, Vec3 orientation) {

        this(planarXZVertices, origin, height);

        ORIENTATION = orientation;

    }

    public OBB(Vec2[] planarXZVertices, Vec3 origin, double height) {

        LOWER_VERTICES = planarXZVertices;

        ORIGIN = origin;

        HEIGHT = height;

        ORIENTATION = Vec3.ZERO;

        double halfWidth = getMaxHorizontalExtent(planarXZVertices)/2d;
        double halfHeight = height/2d;

        EXTENT = new AABB(origin.x-halfWidth, origin.y-halfHeight, origin.z-halfWidth, origin.x + halfWidth, origin.y+halfHeight, origin.z+halfWidth);

    }

    public double[][] planarVertices() {
        double[][] vertices = new double[LOWER_VERTICES.length][2];
        for (int i = 0; i < LOWER_VERTICES.length; i++){
            vertices[i][0] = LOWER_VERTICES[i].x;
            vertices[i][1] = LOWER_VERTICES[i].y;
        }
        return vertices;
    }

    public static float getMaxHorizontalExtent(ArrayList<Vec2> vertices) {
        Vec2[] verticesArr = new Vec2[vertices.size()];
        verticesArr = vertices.toArray(verticesArr);

        return getMaxHorizontalExtent(verticesArr);
    }

    public static float getMaxHorizontalExtent(Vec2[] vertices) {
        float maxExtent = Float.MIN_VALUE;

        for (Vec2 vertex1 : vertices) {
            for (Vec2 vertex2 : vertices) {
                maxExtent = Math.max(vectorTo(vertex1, vertex2).length(), maxExtent);
            }
        }
        return maxExtent;
    }

    Vec2[] getPlanarVertices() {
        return LOWER_VERTICES;
    }

    public Vec3[] getAllVerticesInWorld() {
        //TODO orientation
        Vec3[] allVertices = new Vec3[LOWER_VERTICES.length * 2];
        Vec3[] lowerVertices = getLowerVerticesInWorld();
        Vec3[] upperVertices = getUpperVerticesInWorld();

        for (int i = 0; i < LOWER_VERTICES.length * 2; i++) {
            if (i < LOWER_VERTICES.length) {
                allVertices[i] = lowerVertices[i];
            } else {
                allVertices[i] = upperVertices[i-LOWER_VERTICES.length];
            }
        }

        return allVertices;
    }

    public Vec3[] getLowerVerticesInWorld() {
        //TODO orientation
        Vec3[] allVertices = new Vec3[LOWER_VERTICES.length * 2];
        double halfHeight = HEIGHT/2d;

        for (int i = 0; i < LOWER_VERTICES.length; i++) {
            allVertices[i] = new Vec3(LOWER_VERTICES[i].x + ORIGIN.x, ORIGIN.y - halfHeight, LOWER_VERTICES[i].y + ORIGIN.z);
            allVertices[i] = positionLocally(allVertices[i]);
        }

        return allVertices;
    }

    public Vec3[] getUpperVerticesInWorld() {
        //TODO orientation
        Vec3[] allVertices = new Vec3[LOWER_VERTICES.length * 2];
        double halfHeight = HEIGHT/2d;

        for (int i = 0; i < LOWER_VERTICES.length; i++) {
            allVertices[i] = new Vec3(LOWER_VERTICES[i].x + ORIGIN.x, ORIGIN.y + halfHeight, LOWER_VERTICES[i].y + ORIGIN.z);
            allVertices[i] = positionLocally(allVertices[i]);
        }

        return allVertices;
    }

    protected Vec3 positionLocally(float localX, float localY, float localZ) {
        return (new Vec3(localX, localY, localZ)).yRot(
                (float) (-YAW * ((float) Math.PI / 180F) - ((float) Math.PI / 2F)));
    }

    protected Vec3 positionLocally(Vec3 vec) {
        return (positionLocally((float) vec.x, (float) vec.y, (float) vec.z));
    }


    public OBB move(double pX, double pY, double pZ) {
        return new OBB(LOWER_VERTICES, new Vec3(ORIGIN.x+pX, ORIGIN.y + pY, ORIGIN.z + pZ), HEIGHT);
    }

    public OBB move(Vec3 pVec) {
        return this.move(pVec.x, pVec.y, pVec.z);
    }

}
