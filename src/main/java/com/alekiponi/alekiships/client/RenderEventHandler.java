package com.alekiponi.alekiships.client;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.client.event.IconRenderersEvent;
import com.alekiponi.alekiships.client.model.entity.AnchorEntityModel;
import com.alekiponi.alekiships.client.model.entity.RowboatEntityModel;
import com.alekiponi.alekiships.client.model.entity.SloopEntityModel;
import com.alekiponi.alekiships.client.render.entity.CannonRenderer;
import com.alekiponi.alekiships.client.render.entity.CannonballRenderer;
import com.alekiponi.alekiships.client.render.entity.vehicle.RowboatRenderer;
import com.alekiponi.alekiships.client.render.entity.vehicle.SloopConstructionRenderer;
import com.alekiponi.alekiships.client.render.entity.vehicle.SloopRenderer;
import com.alekiponi.alekiships.client.render.entity.vehicle.vehiclehelper.*;
import com.alekiponi.alekiships.client.render.icon.compartment.EmptyCompartmentIconRenderer;
import com.alekiponi.alekiships.client.render.icon.vehicle.RowboatIconRenderer;
import com.alekiponi.alekiships.client.render.icon.vehicle.SloopIconRenderer;
import com.alekiponi.alekiships.client.render.icon.vehiclehelper.*;
import com.alekiponi.alekiships.common.entity.AlekiShipsEntities;
import com.alekiponi.alekiships.util.VanillaWood;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterLayerDefinitions;

public final class RenderEventHandler {

    @SubscribeEvent
    public static void registerLayers(final RegisterLayerDefinitions event) {
        event.registerLayerDefinition(RowboatEntityModel.LAYER_LOCATION, RowboatEntityModel::createBodyLayer);
        event.registerLayerDefinition(SloopEntityModel.LAYER_LOCATION, SloopEntityModel::createBodyLayer);
        event.registerLayerDefinition(AnchorEntityModel.LAYER_LOCATION, AnchorEntityModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        for (final VanillaWood vanillaWood : VanillaWood.values()) {
            event.registerEntityRenderer(AlekiShipsEntities.ROWBOATS.get(vanillaWood).get(),
                    RowboatRenderer.provider(AlekiShips::location, vanillaWood));
            event.registerEntityRenderer(AlekiShipsEntities.SLOOPS.get(vanillaWood).get(),
                    SloopRenderer.provider(AlekiShips::location, vanillaWood));
            event.registerEntityRenderer(AlekiShipsEntities.SLOOPS_UNDER_CONSTRUCTION.get(vanillaWood).get(),
                    SloopConstructionRenderer.provider(AlekiShips::location, vanillaWood));
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
        event.registerEntityRenderer(AlekiShipsEntities.NOTE_BLOCK_COMPARTMENT_ENTITY.get(),
                BlockCompartmentRenderer::new);
        event.registerEntityRenderer(AlekiShipsEntities.JUKEBOX_COMPARTMENT_ENTITY.get(),
                BlockCompartmentRenderer::new);

        event.registerEntityRenderer(AlekiShipsEntities.VEHICLE_PART.get(), NoopRenderer::new);
        event.registerEntityRenderer(AlekiShipsEntities.SAIL_SWITCH_ENTITY.get(), NoopRenderer::new);
        event.registerEntityRenderer(AlekiShipsEntities.VEHICLE_COLLIDER_ENTITY.get(), NoopRenderer::new);
        event.registerEntityRenderer(AlekiShipsEntities.WINDLASS_SWITCH_ENTITY.get(), NoopRenderer::new);

        event.registerEntityRenderer(AlekiShipsEntities.MAST_ENTITY.get(), MastRenderer::new);

        event.registerEntityRenderer(AlekiShipsEntities.CANNONBALL_ENTITY.get(), CannonballRenderer::new);
        event.registerEntityRenderer(AlekiShipsEntities.CANNON_ENTITY.get(), CannonRenderer::new);
        event.registerEntityRenderer(AlekiShipsEntities.VEHICLE_CLEAT_ENTITY.get(), VehicleCleatRenderer::new);
        event.registerEntityRenderer(AlekiShipsEntities.ANCHOR_ENTITY.get(), AnchorRenderer::new);
        event.registerEntityRenderer(AlekiShipsEntities.CONSTRUCTION_ENTITY.get(), ConstructionRenderer::new);

        //event.registerEntityRenderer(AlekiShipsEntities.TEST_SAILING_SHIP.get(), TestSailingShipRenderer::new);
    }

    @SubscribeEvent
    public static void registerIconRenderers(final IconRenderersEvent.RegisterIconRenderersEvent event) {
        for (final VanillaWood vanillaWood : VanillaWood.values()) {
            event.registerIconRenderer(AlekiShipsEntities.ROWBOATS.get(vanillaWood).get(), RowboatIconRenderer::new);
            event.registerIconRenderer(AlekiShipsEntities.SLOOPS.get(vanillaWood).get(), SloopIconRenderer::new);
        }

        event.registerIconRenderer(AlekiShipsEntities.EMPTY_COMPARTMENT_ENTITY.get(),
                EmptyCompartmentIconRenderer::new);

        event.registerIconRenderer(AlekiShipsEntities.SAIL_SWITCH_ENTITY.get(), SailSwitchIconRenderer::new);
        event.registerIconRenderer(AlekiShipsEntities.VEHICLE_COLLIDER_ENTITY.get(), IconPassthroughRenderer::new);
        event.registerIconRenderer(AlekiShipsEntities.WINDLASS_SWITCH_ENTITY.get(), WindlassIconRenderer::new);

        event.registerIconRenderer(AlekiShipsEntities.MAST_ENTITY.get(), MastIconRenderer::new);

        event.registerIconRenderer(AlekiShipsEntities.VEHICLE_CLEAT_ENTITY.get(), CleatIconRenderer::new);
        event.registerIconRenderer(AlekiShipsEntities.CONSTRUCTION_ENTITY.get(), IconPassthroughRenderer::new);
    }
}