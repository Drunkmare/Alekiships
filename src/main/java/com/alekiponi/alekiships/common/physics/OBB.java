package com.alekiponi.alekiships.common.physics;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;

import static com.alekiponi.alekiships.common.physics.Vec2Helper.positionLocallyYrot;
import static com.alekiponi.alekiships.common.physics.Vec3Helper.positionLocallyYrot;

public class OBB {

    AABB EXTENT;
    Vec2[] VERTICES;
    Vec3 ORIGIN;
    Vec3 ORIENTATION;
    float YAW;
    double HEIGHT;

    Vec3[] VERTICES_IN_WORLD;

    public OBB(Vec2[] planarXZVertices, Vec3 origin, double height, float yaw) {

        VERTICES = planarXZVertices;

        ORIGIN = origin;

        HEIGHT = height;

        ORIENTATION = Vec3.ZERO;

        double halfWidth = getMaxHorizontalExtent(planarXZVertices) / 2d;

        EXTENT = new AABB(origin.x - halfWidth, origin.y, origin.z - halfWidth, origin.x + halfWidth, origin.y + HEIGHT, origin.z + halfWidth);

        YAW = yaw;

        VERTICES_IN_WORLD = collectAllVerticesInWorld();

        // TODO catch non-convex polygons ... either here or in EntityOBBDimensions
        /*
        if(// the OBB is convex){
            // construct
        else {
            throw new IllegalArgumentException("An OBB must always be convex");
        }*/


    }

    /*
    public OBB(ArrayList<Vec2> planarXZVertices, Vec3 origin, double height) {
        this(planarXZVertices.toArray(planarXZVertices.toArray(new Vec2[planarXZVertices.size()])), origin, height, 0f);
    }*/

    /*
    private OBB(AABB aabb) {
        this(new Vec2[]{
                        new Vec2((float) aabb.maxX, (float) aabb.maxZ),
                        new Vec2((float) aabb.maxX, (float) aabb.minZ),
                        new Vec2((float) aabb.minX, (float) aabb.minZ),
                        new Vec2((float) aabb.minX, (float) aabb.maxZ),
                },
                new Vec3(Mth.lerp(0.5D, aabb.minX, aabb.maxX), aabb.minY, Mth.lerp(0.5D, aabb.minZ, aabb.maxZ)), aabb.getYsize());
    }*/

    public static OBB createFromAABB(AABB aabb){
        Vec3 origin = new Vec3(Mth.lerp(0.5D, aabb.minX, aabb.maxX), aabb.minY, Mth.lerp(0.5D, aabb.minZ, aabb.maxZ));
        double halfXSize = aabb.getXsize()/2f;
        double halfZSize = aabb.getZsize()/2f;
        double height = aabb.getYsize();
        Vec2[] vertices = new Vec2[]{
                new Vec2((float) halfXSize, (float) halfZSize),
                new Vec2((float) halfXSize, (float) -halfZSize),
                new Vec2((float) -halfXSize, (float) -halfZSize),
                new Vec2((float) -halfXSize, (float) halfZSize),
        };

        return new OBB(vertices, origin, height, 0);
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

    public AABB getExtent() {
        return EXTENT;
    }

    public double getHeight() {
        return HEIGHT;
    }

    public double getWidth() {
        return getMaxHorizontalExtent(VERTICES);
    }

    public double[][] planarVertices() {
        double[][] vertices = new double[VERTICES.length][2];
        for (int i = 0; i < VERTICES.length; i++) {
            vertices[i][0] = VERTICES[i].x;
            vertices[i][1] = VERTICES[i].y;
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
        return VERTICES;
    }

    public Vec3[] collectAllVerticesInWorld() {
        //TODO orientation
        Vec3[] allVertices = new Vec3[VERTICES.length * 2];
        Vec3[] lowerVertices = collectLowerVerticesInWorld();
        Vec3[] upperVertices = collectUpperVerticesInWorld();

        for (int i = 0; i < VERTICES.length * 2; i++) {
            if (i < VERTICES.length) {
                allVertices[i] = lowerVertices[i];
            } else {
                allVertices[i] = upperVertices[i - VERTICES.length];
            }
        }

        return allVertices;
    }

    public Vec3[] getAllVerticesInWorld() {
        return VERTICES_IN_WORLD;
    }

    public Vec3[] collectLowerVerticesInWorld() {
        Vec3[] allVertices = new Vec3[VERTICES.length];

        for (int i = 0; i < VERTICES.length; i++) {
            Vec3 vertex = new Vec3(VERTICES[i].x, ORIGIN.y, VERTICES[i].y).yRot((float) Math.toRadians(-YAW));
            allVertices[i] = new Vec3(vertex.x + ORIGIN.x, ORIGIN.y, vertex.z + ORIGIN.z);
        }

        return allVertices;
    }

    public Vec3[] getLowerVerticesInWorld() {
        Vec3[] vertices = new Vec3[this.vertexCount()];
        if (this.vertexCount() >= 0) System.arraycopy(this.VERTICES_IN_WORLD, 0, vertices, 0, this.vertexCount());
        return vertices;
    }

    public Vec3[] collectUpperVerticesInWorld() {
        Vec3[] allVertices = new Vec3[VERTICES.length];

        for (int i = 0; i < VERTICES.length; i++) {
            Vec3 vertex = new Vec3(VERTICES[i].x, ORIGIN.y, VERTICES[i].y).yRot((float) Math.toRadians(-YAW));
            allVertices[i] = new Vec3(vertex.x + ORIGIN.x, ORIGIN.y+HEIGHT, vertex.z + ORIGIN.z);
        }

        return allVertices;
    }

    public Vec3[] getUpperVerticesInWorld() {
        Vec3[] vertices = new Vec3[this.vertexCount()];
        if (this.vertexCount() * 2 - this.vertexCount() >= 0)
            System.arraycopy(this.VERTICES_IN_WORLD, this.vertexCount(), vertices, this.vertexCount(), this.vertexCount() * 2 - this.vertexCount());
        return vertices;
    }

    public double[][] collectLowerVerticesForRender() {
        double[][] vertices = new double[VERTICES.length][2];
        for (int i = 0; i < VERTICES.length; i++) {
            Vec3 vertex = new Vec3(VERTICES[i].x, 0, VERTICES[i].y).yRot((float) Math.toRadians(-YAW));
            vertices[i][0] = vertex.x;
            vertices[i][1] = vertex.z;
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

    public int vertexCount() {
        return VERTICES.length;
    }

    public float getYaw(){
        return YAW;
    }

    public boolean intersects(AABB pAABB) {
        return this.intersects(createFromAABB(pAABB));
    }

    public boolean intersects(double pX1, double pY1, double pZ1, double pX2, double pY2, double pZ2) {
        return this.intersects(new AABB(pX1, pY1, pZ1, pX2, pY2, pZ2));
    }

    public boolean intersects(OBB other) {
        if(other.getOrigin().y > this.getOrigin().y + this.getHeight() || other.getOrigin().y + other.getHeight() < this.getOrigin().y){
            return false;
        }

        OBB obb1 = this;
        OBB obb2 = other;

        // https://github.com/OneLoneCoder/Javidx9/blob/master/PixelGameEngine/SmallerProjects/OneLoneCoder_PGE_PolygonCollisions1.cpp

        // https://www.youtube.com/watch?v=7Ik2vowGcU0

        for (int selector = 0; selector < 2; selector++) {
            if (selector == 1) {
                obb1 = other;
                obb2 = this;
            }

            for (int i = 0; i < obb1.vertexCount(); i++) {

                int j = (i + 1) % obb1.vertexCount();

                Vec2 axisProj = new Vec2(-(obb1.vertexL2D(j).y - obb1.vertexL2D(i).y), (obb1.vertexL2D(j).x - obb1.vertexL2D(i).x));
                float d = Mth.sqrt(axisProj.x * axisProj.x + axisProj.y * axisProj.y);
                axisProj = new Vec2(axisProj.x / d, axisProj.y / d);

                // Work out min and max 1D points for r1
                float min_r1 = Float.MAX_VALUE, max_r1 = Float.MIN_VALUE;
                for (int p = 0; p < obb1.vertexCount(); p++) {
                    float q = (obb1.vertexL2D(p).x * axisProj.x + obb1.vertexL2D(p).y * axisProj.y);
                    min_r1 = Math.min(min_r1, q);
                    max_r1 = Math.max(max_r1, q);
                }

                // Work out min and max 1D points for r2
                float min_r2 = Float.MAX_VALUE, max_r2 = Float.MIN_VALUE;
                for (int p = 0; p < obb2.vertexCount(); p++) {
                    float q = (obb2.vertexL2D(p).x * axisProj.x + obb2.vertexL2D(p).y * axisProj.y);
                    min_r2 = Math.min(min_r2, q);
                    max_r2 = Math.max(max_r2, q);
                }

                if (!(max_r2 >= min_r1 && max_r1 >= min_r2))
                    return false;
            }

        }

        return true;

    }

    public Vec3 vertexL(int i) {
        return getLowerVerticesInWorld()[i];
    }

    public Vec2 vertexL2D(int i) {
        return new Vec2((float) getLowerVerticesInWorld()[i].x, (float) getLowerVerticesInWorld()[i].z);
    }

    public Vec3 vertexU(int i) {
        return getUpperVerticesInWorld()[i];
    }

    public Vec3 getOrigin(){
        return ORIGIN;
    }

    public boolean intersects(Vec3 pMin, Vec3 pMax) {
        return this.intersects(Math.min(pMin.x, pMax.x), Math.min(pMin.y, pMax.y), Math.min(pMin.z, pMax.z), Math.max(pMin.x, pMax.x), Math.max(pMin.y, pMax.y), Math.max(pMin.z, pMax.z));
    }

    public boolean contains(Vec3 point) {
        if (point.y < ORIGIN.y || point.y > ORIGIN.y + HEIGHT) {
            return false;
        }

        Vec3[] verticesInWorld = getLowerVerticesInWorld();

        boolean inside = false;
        for (int i = 0, j = verticesInWorld.length - 1; i < verticesInWorld.length; j = i++) {
            if ((verticesInWorld[i].z > point.z) != (verticesInWorld[j].z > point.z) &&
                    point.x < (verticesInWorld[j].x - verticesInWorld[i].x) * (point.z - verticesInWorld[i].z) / (verticesInWorld[j].z - verticesInWorld[i].z) + verticesInWorld[i].x) {

                inside = !inside;
            }
        }

        return inside;

    }

    public boolean contains(double pX, double pY, double pZ) {
        return this.contains(new Vec3(pX, pY, pZ));
    }

}
