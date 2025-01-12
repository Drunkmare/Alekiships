package com.alekiponi.alekiships.compat.waila.compartment.vehicle;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.common.entity.vehicle.ConstructionInput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public enum ConstructionEntityProvider implements IEntityComponentProvider {
    INSTANCE;

    public static final String INPUTS_REMAINING_KEY = "alekiships.jade.construction_entity.remaining_inputs";
    public static final String CURRENT_STAGE_KEY = "alekiships.jade.construction_entity.current_stage";
    public static final String NEXT_STAGE_KEY = "alekiships.jade.construction_entity.next_stage";
    private static final ResourceLocation NAME = AlekiShips.location("construction_entity");

    @Override
    public void appendTooltip(final ITooltip iTooltip, final EntityAccessor entityAccessor,
            final IPluginConfig iPluginConfig) {
        if (!(entityAccessor.getEntity()
                .getRootVehicle() instanceof final ConstructionInput.ConstructedEntity<?, ?> entity)) {
            return;
        }

        final var constructionState = entity.getConstructionState();
        iTooltip.add(Component.translatable(INPUTS_REMAINING_KEY, constructionState.remainingInputs()));
        iTooltip.add(Component.translatable(CURRENT_STAGE_KEY, constructionState.stage().getSerializedName()));
        iTooltip.add(Component.translatable(NEXT_STAGE_KEY, constructionState.stage().next().getSerializedName()));
    }

    @Override
    public ResourceLocation getUid() {
        return NAME;
    }
}