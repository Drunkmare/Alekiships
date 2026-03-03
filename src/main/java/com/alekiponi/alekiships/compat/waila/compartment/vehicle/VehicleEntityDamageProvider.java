package com.alekiponi.alekiships.compat.waila.compartment.vehicle;

import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.common.entity.vehicle.AbstractVehicle;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public enum VehicleEntityDamageProvider implements IEntityComponentProvider {
    INSTANCE;

    public static final String DAMAGE_KEY = AlekiShips.MOD_ID + ".jade.vehicle_entity_damage.damage";
    public static final String DAMAGE_ABSOLUTE_KEY = AlekiShips.MOD_ID + ".jade.vehicle_entity_damage.damage_absolute";
    public static final String HEALTH_KEY = AlekiShips.MOD_ID + ".jade.vehicle_entity_damage.health";
    public static final String HEALTH_ABSOLUTE_KEY = AlekiShips.MOD_ID + ".jade.vehicle_entity_damage.health_absolute";
    public static final String WRECKED_KEY = AlekiShips.MOD_ID + ".jade.vehicle_entity_damage.wrecked";
    public static final ResourceLocation DECIMAL_PLACES = AlekiShips.location("vehicle_entity_damage.decimal_places");
    public static final ResourceLocation DISPLAY_TYPE = AlekiShips.location("vehicle_entity_damage.display_type");
    private static final ResourceLocation NAME = AlekiShips.location("vehicle_entity_damage");

    @Override
    public void appendTooltip(final ITooltip tooltip, final EntityAccessor entityAccessor, final IPluginConfig config) {
        final var vehicle = (AbstractVehicle) entityAccessor.getEntity();

        if (!vehicle.isFunctional()) {
            tooltip.add(Component.translatable(WRECKED_KEY));
            return;
        }

        final NumberFormat numberFormat;
        {
            final var decimalPlaces = config.getInt(DECIMAL_PLACES);
            if (decimalPlaces == 0) {
                numberFormat = new DecimalFormat("0");
            } else {
                numberFormat = new DecimalFormat("0." + "#".repeat(decimalPlaces));
            }
        }

        switch (config.<DisplayType>getEnum(DISPLAY_TYPE)) {
            case DAMAGE -> {
                final var damage = vehicle.getDamage() / vehicle.getDamageThreshold() * 100;
                tooltip.add(Component.translatable(DAMAGE_KEY, numberFormat.format(damage)));
            }
            case DAMAGE_ABSOLUTE -> {
                final var damage = numberFormat.format(vehicle.getDamage());
                final var maxDamage = numberFormat.format(vehicle.getDamageThreshold());
                tooltip.add(Component.translatable(DAMAGE_ABSOLUTE_KEY, damage, maxDamage));
            }
            case HEALTH -> {
                final var health = vehicle.getDamageThreshold() - vehicle.getDamage();
                final var healthPercent = health / vehicle.getDamageThreshold() * 100;
                tooltip.add(Component.translatable(HEALTH_KEY, numberFormat.format(healthPercent)));
            }
            case HEALTH_ABSOLUTE -> {
                final var health = vehicle.getDamageThreshold() - vehicle.getDamage();
                tooltip.add(Component.translatable(HEALTH_ABSOLUTE_KEY, numberFormat.format(health),
                        numberFormat.format(vehicle.getDamageThreshold())));
            }
        }
    }

    @Override
    public ResourceLocation getUid() {
        return NAME;
    }

    public enum DisplayType {
        DAMAGE,
        DAMAGE_ABSOLUTE,
        HEALTH,
        HEALTH_ABSOLUTE;
    }
}