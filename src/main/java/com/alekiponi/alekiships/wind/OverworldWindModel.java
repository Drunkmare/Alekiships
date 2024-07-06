package com.alekiponi.alekiships.wind;

import net.minecraft.core.BlockPos;

public class OverworldWindModel implements WindModel {

    @Override
    public Wind getWind(final BlockPos blockPos) {
        // TODO make vanilla wind better
        return Wind.fromComponents(0.25F, 0.25F);
    }
}