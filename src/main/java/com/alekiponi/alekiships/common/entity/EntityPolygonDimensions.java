package com.alekiponi.alekiships.common.entity;

import com.alekiponi.alekiships.common.physics.OBB;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class EntityPolygonDimensions extends EntityDimensions {

    Vec2[] PLANAR_XZ_VERTICES;
    public EntityPolygonDimensions(ArrayList<Vec2> planarXZVertices, float pHeight, boolean pFixed) {
        super(OBB.getMaxHorizontalExtent(planarXZVertices), pHeight, pFixed);

        PLANAR_XZ_VERTICES = new Vec2[planarXZVertices.size()];
        PLANAR_XZ_VERTICES = planarXZVertices.toArray(PLANAR_XZ_VERTICES);

    }

    public EntityPolygonDimensions(Vec2[] planarXZVertices, float pHeight, boolean pFixed) {
        super(OBB.getMaxHorizontalExtent(planarXZVertices), pHeight, pFixed);

        PLANAR_XZ_VERTICES = planarXZVertices;

    }

    public Vec2[] getPlanarVertices(){
        return PLANAR_XZ_VERTICES;
    }
    public EntityPolygonDimensions(float pWidth, float pHeight, boolean pFixed) {
        super(pWidth, pHeight, pFixed);
    }

    @Override
    @NotNull
    public  String toString() {
        String string = super.toString() + " Polygon: ";
        int i = 0;
        for(Vec2 vertex : PLANAR_XZ_VERTICES){
            string += i + ": " + vertex.x + "," + vertex.y + ";";
        }
        return string;
    }

}
