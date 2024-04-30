package com.alekiponi.alekiships.client.render.flywheel;

import com.alekiponi.alekiships.common.entity.AlekiShipsEntities;
import com.alekiponi.alekiships.util.VanillaWood;

import static com.jozufozu.flywheel.lib.visual.SimpleEntityVisualizer.builder;

public class Flywheel {


    public static void init() {
        for (final VanillaWood vanillaWood : VanillaWood.values()) {
            builder(AlekiShipsEntities.ROWBOATS.get(vanillaWood).get()).factory(RowboatVisual::new).apply();
        }
    }

}
