package com.alekiponi.alekiships.common.entity.vehiclecapability;

import com.alekiponi.alekiships.events.config.AlekishipsConfig;

public interface IBoatNoAnchor {
    default boolean windShouldAffect() {
        return AlekishipsConfig.SERVER.windAffectsBoatsWithNoAnchor.get();
    }

}
