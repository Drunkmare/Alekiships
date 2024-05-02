package com.alekiponi.alekiships.client;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.client.model.entity.RowboatEntityModel;
import com.alekiponi.alekiships.client.render.entity.CannonRenderer;
import com.alekiponi.alekiships.client.render.entity.CannonballRenderer;
import com.alekiponi.alekiships.client.render.entity.vehicle.RowboatRenderer;
import com.alekiponi.alekiships.client.render.entity.vehicle.SloopConstructionRenderer;
import com.alekiponi.alekiships.client.render.entity.vehicle.SloopRenderer;
import com.alekiponi.alekiships.client.render.entity.vehicle.vehiclehelper.*;
import com.alekiponi.alekiships.common.entity.AlekiShipsEntities;
import com.alekiponi.alekiships.util.VanillaWood;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AlekiShips.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class RenderEventHandler {

    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(RowboatEntityModel.LAYER_LOCATION, RowboatEntityModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        for (final VanillaWood vanillaWood : VanillaWood.values()) {
            event.registerEntityRenderer(AlekiShipsEntities.ROWBOATS.get(vanillaWood).get(),
                    context -> new RowboatRenderer(context, vanillaWood));
            event.registerEntityRenderer(AlekiShipsEntities.SLOOPS.get(vanillaWood).get(),
                    context -> new SloopRenderer(context, vanillaWood));
            event.registerEntityRenderer(AlekiShipsEntities.SLOOPS_UNDER_CONSTRUCTION.get(vanillaWood).get(),
                    context -> new SloopConstructionRenderer(context, vanillaWood));
        }

        event.registerEntityRenderer(AlekiShipsEntities.EMPTY_COMPARTMENT_ENTITY.get(), NoopRenderer::new);

        event.registerEntityRenderer(AlekiShipsEntities.BLOCK_COMPARTMENT_ENTITY.get(), BlockCompartmentRenderer::new);

        event.registerEntityRenderer(AlekiShipsEntities.BARREL_COMPARTMENT_ENTITY.get(), BlockCompartmentRenderer::new);
        event.registerEntityRenderer(AlekiShipsEntities.CHEST_COMPARTMENT_ENTITY.get(), ChestCompartmentRenderer::new);

        event.registerEntityRenderer(AlekiShipsEntities.ENDER_CHEST_COMPARTMENT_ENTITY.get(),
                EnderChestCompartmentRenderer::new);

        event.registerEntityRenderer(AlekiShipsEntities.SHULKER_BOX_COMPARTMENT_ENTITY.get(),
                ShulkerBoxCompartmentRenderer::new);

        event.registerEntityRenderer(AlekiShipsEntities.FURNACE_COMPARTMENT_ENTITY.get(),
                BlockCompartmentRenderer::new);
        event.registerEntityRenderer(AlekiShipsEntities.BLAST_FURNACE_COMPARTMENT_ENTITY.get(),
                BlockCompartmentRenderer::new);
        event.registerEntityRenderer(AlekiShipsEntities.SMOKER_COMPARTMENT_ENTITY.get(), BlockCompartmentRenderer::new);
        event.registerEntityRenderer(AlekiShipsEntities.BREWING_STAND_COMPARTMENT_ENTITY.get(),
                BlockCompartmentRenderer::new);

        event.registerEntityRenderer(AlekiShipsEntities.WORKBENCH_COMPARTMENT_ENTITY.get(),
                BlockCompartmentRenderer::new);

        event.registerEntityRenderer(AlekiShipsEntities.STONECUTTER_COMPARTMENT_ENTITY.get(),
                BlockCompartmentRenderer::new);
        event.registerEntityRenderer(AlekiShipsEntities.CARTOGRAPHY_TABLE_COMPARTMENT_ENTITY.get(),
                BlockCompartmentRenderer::new);
        event.registerEntityRenderer(AlekiShipsEntities.SMITHING_TABLE_COMPARTMENT_ENTITY.get(),
                BlockCompartmentRenderer::new);
        event.registerEntityRenderer(AlekiShipsEntities.GRINDSTONE_COMPARTMENT_ENTITY.get(),
                BlockCompartmentRenderer::new);
        event.registerEntityRenderer(AlekiShipsEntities.LOOM_COMPARTMENT_ENTITY.get(), BlockCompartmentRenderer::new);

        event.registerEntityRenderer(AlekiShipsEntities.VEHICLE_PART.get(), NoopRenderer::new);
        event.registerEntityRenderer(AlekiShipsEntities.SAIL_SWITCH_ENTITY.get(), NoopRenderer::new);
        event.registerEntityRenderer(AlekiShipsEntities.VEHICLE_COLLIDER_ENTITY.get(), NoopRenderer::new);
        event.registerEntityRenderer(AlekiShipsEntities.WINDLASS_SWITCH_ENTITY.get(), NoopRenderer::new);
        event.registerEntityRenderer(AlekiShipsEntities.MAST_ENTITY.get(), NoopRenderer::new);

        event.registerEntityRenderer(AlekiShipsEntities.CANNONBALL_ENTITY.get(), CannonballRenderer::new);
        event.registerEntityRenderer(AlekiShipsEntities.CANNON_ENTITY.get(), CannonRenderer::new);
        event.registerEntityRenderer(AlekiShipsEntities.VEHICLE_CLEAT_ENTITY.get(), VehicleCleatRenderer::new);
        event.registerEntityRenderer(AlekiShipsEntities.ANCHOR_ENTITY.get(), AnchorRenderer::new);
        event.registerEntityRenderer(AlekiShipsEntities.CONSTRUCTION_ENTITY.get(), ConstructionRenderer::new);
    }
}