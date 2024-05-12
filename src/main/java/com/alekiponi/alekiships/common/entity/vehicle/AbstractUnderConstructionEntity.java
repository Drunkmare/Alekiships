package com.alekiponi.alekiships.common.entity.vehicle;

import com.alekiponi.alekiships.common.entity.vehiclecapability.IHaveConstructionEntities;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityDimensions;
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

    public InteractionResult interact(final Player player, final InteractionHand hand) {
        return InteractionResult.PASS;
    }

    public abstract void interactFromConstructionEntity(final Player player, final InteractionHand hand);

}
