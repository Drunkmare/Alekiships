package com.alekiponi.alekiships.common.entity.vehiclehelper;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.common.entity.AlekiShipsEntities;
import com.alekiponi.alekiships.common.entity.vehicle.AbstractUnderConstructionEntity;
import com.alekiponi.alekiships.common.entity.vehicle.AbstractVehicle;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class ConstructionVehiclePart extends VehiclePart {

    public ConstructionVehiclePart(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void tickAddAppropriateHelper(AbstractVehicle vehicle) {
        tickAddConstruction(vehicle);
    }

    protected boolean tickAddConstruction(AbstractVehicle entity) {
        if (entity instanceof AbstractUnderConstructionEntity vehicle) {
            for (int i : vehicle.getConstructionIndices()) {
                if (vehicle.getPassengers().get(i).is(this) && !vehicle.getPassengers().get(i).isVehicle()) {
                    final ConstructionEntity constructionEntity = AlekiShipsEntities.CONSTRUCTION_ENTITY.get()
                            .create(this.level());
                    assert constructionEntity != null;
                    constructionEntity.setPos(this.getX(), this.getY(), this.getZ());
                    constructionEntity.setYRot(this.getVehicle().getYRot());
                    if (!constructionEntity.startRiding(this)) {
                        AlekiShips.LOGGER.error("New Construction Entity: {} unable to ride Vehicle Part: {}", constructionEntity, this);
                    }
                    this.level().addFreshEntity(constructionEntity);
                    return true;
                }
            }
        }
        return false;
    }
}
