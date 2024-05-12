package com.alekiponi.alekiships.common.entity.vehiclehelper;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public abstract class AbstractHelper extends Entity {
    public AbstractHelper(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public void tick() {
        if (!this.isPassenger()) {
            this.kill();
        }
        super.tick();
    }

}
