package com.alekiponi.alekiships.common.physics;

import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;

import static com.alekiponi.alekiships.common.physics.Vec2Helper.positionLocallyYrot;
import static com.alekiponi.alekiships.common.physics.Vec2Helper.vectorTo;
import static com.alekiponi.alekiships.common.physics.Vec3Helper.positionLocallyYrot;

public class OBB {

    AABB EXTENT;
    Vec2[] LOWER_VERTICES;
    Vec3 ORIGIN;
    Vec3 ORIENTATION;
    float YAW;
    double HEIGHT;

    public OBB(Vec2[] planarXZVertices) {

    }

    public OBB(ArrayList<Vec2> planarXZVertices, Vec3 origin, double height) {
        this(planarXZVertices.toArray(planarXZVertices.toArray(new Vec2[planarXZVertices.size()])), origin, height);
    }

    public OBB(Vec2[] planarXZVertices, Vec3 origin, double height, float yaw) {

        this(planarXZVertices, origin, height);

        YAW = yaw;

    }

    public OBB(Vec2[] planarXZVertices, double x, double y, double z, double height, float yaw) {

        this(planarXZVertices, new Vec3(x, y, z), height, yaw);

    }

    public OBB(OBB obb, Vec3 origin) {

        this(obb, origin, obb.YAW);

    }

    public OBB(OBB obb, float yaw) {

        this(obb, obb.ORIGIN, yaw);

    }

    public OBB(OBB obb, Vec3 origin, float yaw) {

        this(obb.getPlanarVertices(), origin, obb.getHeight(), yaw);

    }

    public OBB(Vec2[] planarXZVertices, Vec3 origin, double height) {

        LOWER_VERTICES = planarXZVertices;

        ORIGIN = origin;

        HEIGHT = height;

        ORIENTATION = Vec3.ZERO;

        double halfWidth = getMaxHorizontalExtent(planarXZVertices) / 2d;

        EXTENT = new AABB(origin.x - halfWidth, origin.y, origin.z - halfWidth, origin.x + halfWidth, origin.y + HEIGHT, origin.z + halfWidth);

    }

    public AABB getExtent() {
        return EXTENT;
    }

    public double getHeight() {
        return HEIGHT;
    }

    public double getWidth(){
        return getMaxHorizontalExtent(LOWER_VERTICES);
    }

    public double[][] planarVertices() {
        double[][] vertices = new double[LOWER_VERTICES.length][2];
        for (int i = 0; i < LOWER_VERTICES.length; i++) {
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

        for (Vec2 vertex : vertices) {
            maxExtent = Math.max(vertex.length() * 2, maxExtent);
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
                allVertices[i] = upperVertices[i - LOWER_VERTICES.length];
            }
        }

        return allVertices;
    }

    public Vec3[] getLowerVerticesInWorld() {
        //TODO orientation
        Vec3[] allVertices = new Vec3[LOWER_VERTICES.length * 2];

        for (int i = 0; i < LOWER_VERTICES.length; i++) {
            allVertices[i] = new Vec3(LOWER_VERTICES[i].x + ORIGIN.x, ORIGIN.y, LOWER_VERTICES[i].y + ORIGIN.z);
            allVertices[i] = positionLocallyYrot(allVertices[i], YAW);
        }

        return allVertices;
    }

    public Vec3[] getUpperVerticesInWorld() {
        //TODO orientation
        Vec3[] allVertices = new Vec3[LOWER_VERTICES.length * 2];

        for (int i = 0; i < LOWER_VERTICES.length; i++) {
            allVertices[i] = new Vec3(LOWER_VERTICES[i].x + ORIGIN.x, ORIGIN.y + HEIGHT, LOWER_VERTICES[i].y + ORIGIN.z);
            allVertices[i] = positionLocallyYrot(allVertices[i], YAW);
        }

        return allVertices;
    }

    public double[][] getLowerVerticesForRender() {
        double[][] vertices = new double[LOWER_VERTICES.length][2];
        for (int i = 0; i < LOWER_VERTICES.length; i++) {
            vertices[i][0] = positionLocallyYrot(LOWER_VERTICES[i], YAW).x;
            vertices[i][1] = positionLocallyYrot(LOWER_VERTICES[i], YAW).y;
        }
        return vertices;
    }

    public OBB move(double pX, double pY, double pZ) {
        return new OBB(this, new Vec3(ORIGIN.x + pX, ORIGIN.y + pY, ORIGIN.z + pZ));
    }

    public OBB move(Vec3 pVec) {
        return this.move(pVec.x, pVec.y, pVec.z);
    }

    public OBB rotateToY(float yaw) {
        return new OBB(this, yaw);
    }

    public OBB rotateByY(float yaw) {
        return new OBB(this, YAW + yaw);
    }

}
