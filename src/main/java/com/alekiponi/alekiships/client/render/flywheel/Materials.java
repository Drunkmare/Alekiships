package com.alekiponi.alekiships.client.render.flywheel;

import com.alekiponi.alekiships.client.render.ShipSheets;
import com.jozufozu.flywheel.api.material.Material;
import com.jozufozu.flywheel.lib.material.CutoutShaders;
import com.jozufozu.flywheel.lib.material.SimpleMaterial;

public final class Materials {

    public static final Material ROWBOAT = SimpleMaterial.builder().cutout(CutoutShaders.ONE_TENTH)
            .texture(ShipSheets.ROWBOAT_SHEET).mipmap(false).backfaceCulling(false).build();
}