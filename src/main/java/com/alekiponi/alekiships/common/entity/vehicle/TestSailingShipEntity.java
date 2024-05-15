package com.alekiponi.alekiships.common.entity.vehicle;

import com.alekiponi.alekiships.common.entity.vehiclecapability.IHaveAnchorWindlass;
import com.alekiponi.alekiships.common.entity.vehiclecapability.IHaveBlockOnlyCompartments;
import com.alekiponi.alekiships.common.entity.vehiclecapability.IHaveCleats;
import com.alekiponi.alekiships.util.BoatMaterial;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class TestSailingShipEntity extends AbstractAlekiBoatEntity implements IHaveBlockOnlyCompartments, IHaveAnchorWindlass,IHaveCleats {

    public TestSailingShipEntity(EntityType<? extends AbstractAlekiBoatEntity> entityType, Level level, BoatMaterial material) {
        super(entityType, level, material);
    }

    @Override
    protected double windDriftMultiplier() {
        return 1;
    }

    public final int PASSENGER_NUMBER = 24;

    public final int[] CLEATS = {20,21,22,23};

    public final int[][] COMPARTMENT_ROTATIONS = {};

    public final int[] CAN_ADD_ONLY_BLOCKS = {};

    public final int[] COMPARTMENTS = {0, 1, 2, 3, 4, 5, 6, 7, 8,9,10,11,12,13,14,15,16,17,18,19};

    @Override
    protected float getPaddleMultiplier() {
        return 0;
    }

    @Override
    protected float getMomentumSubtractor() {
        return 0;
    }

    @Override
    public int getMaxPassengers() {
        return PASSENGER_NUMBER;
    }

    @Override
    public float getPassengerSizeLimit() {
        return 20;
    }

    @Override
    public float getDamageThreshold() {
        return 100;
    }

    @Override
    public float getDamageRecovery() {
        return 0;
    }

    @Override
    public int[] getCleatIndices() {
        return CLEATS;
    }

    @Override
    public int[] getColliderIndices() {
        return new int[0];
    }

    @Override
    public int[] getCompartmentIndices() {
        return COMPARTMENTS;
    }

    @Override
    public int[][] getCompartmentRotationsArray() {
        return new int[0][];
    }

    @Override
    public int getCompartmentRotation(int i) {
        return 0;
    }

    @Override
    public int[] getCanAddOnlyBlocksIndices() {
        return new int[0];
    }

    @Override
    public int[] getWindlassIndices() {
        return new int[0];
    }

    @Override
    public float getCleatMovementMultiplier(){
        return 10;
    }
}
