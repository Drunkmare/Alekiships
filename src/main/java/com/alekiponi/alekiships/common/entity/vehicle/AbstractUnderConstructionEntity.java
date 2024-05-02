package com.alekiponi.alekiships.common.entity.vehicle;

import com.alekiponi.alekiships.common.entity.AlekiShipsEntities;
import com.alekiponi.alekiships.common.entity.vehiclecapability.IHaveConstructionEntities;
import com.alekiponi.alekiships.common.entity.vehiclehelper.VehiclePart;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public abstract class AbstractUnderConstructionEntity extends AbstractVehicle implements IHaveConstructionEntities {

    public AbstractUnderConstructionEntity(EntityType entityType, Level level) {
        super(entityType, level);
    }

    public int getMaxPassengers() {
        return 1;
    }

    public void tick() {
        if (this.getPassengers().size() < this.getMaxPassengers()) {
            final VehiclePart newPart = AlekiShipsEntities.CONSTRUCTION_VEHICLE_PART.get().create(this.level());
            newPart.setPos(this.getX(), this.getY(), this.getZ());
            this.level().addFreshEntity(newPart);
            newPart.startRiding(this);
        }

        super.tick();
    }

    public InteractionResult interact(final Player player, final InteractionHand hand) {
        return InteractionResult.PASS;
    }

    public abstract void interactFromConstructionEntity(final Player player, final InteractionHand hand);

}
