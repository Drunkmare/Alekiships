package com.alekiponi.alekiships.compat.waila.compartment;

import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.theme.IThemeHelper;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.common.entity.compartment.BlockCompartment;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

public enum BlockCompartmentProvider implements IEntityComponentProvider {
    INSTANCE;

    public static final String COMPARTMENT_BLOCK_KEY = "alekiships.jade.compartment_block";
    private static final ResourceLocation NAME = ResourceLocation.fromNamespaceAndPath(AlekiShips.MOD_ID, "block");

    @Override
    public void appendTooltip(final ITooltip tooltip, final EntityAccessor entityAccessor,
            final IPluginConfig iPluginConfig) {
        tooltip.remove(JadeIds.CORE_OBJECT_NAME);

        final String blockName = I18n.get(
                ((BlockCompartment) entityAccessor.getEntity()).getDisplayBlockState().getBlock().getDescriptionId());

        // "<BlockName> Compartment"
        final MutableComponent name = Component.translatable(COMPARTMENT_BLOCK_KEY, blockName);
        tooltip.add(0, IThemeHelper.get().title(name), JadeIds.CORE_OBJECT_NAME);
    }

    @Override
    public ResourceLocation getUid() {
        return NAME;
    }

    @Override
    public int getDefaultPriority() {
        // We want to replace the name only
        return TooltipPosition.HEAD;
    }
}