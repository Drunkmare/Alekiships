package com.alekiponi.alekiships.compat.waila.compartment;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.BlockCompartment;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.config.IWailaConfig;

public enum BlockCompartmentProvider implements IEntityComponentProvider {
    INSTANCE;

    private static final ResourceLocation NAME = new ResourceLocation(AlekiShips.MOD_ID, "block");

    @Override
    public void appendTooltip(final ITooltip tooltip, final EntityAccessor entityAccessor,
            final IPluginConfig iPluginConfig) {
        tooltip.remove(Identifiers.CORE_OBJECT_NAME);

        final String blockName = I18n.get(
                ((BlockCompartment) entityAccessor.getEntity()).getDisplayBlockState().getBlock().getDescriptionId());

        // "<BlockName> Compartment"
        final MutableComponent name = Component.translatable("alekiships.jade.compartment_block", blockName);
        final IWailaConfig.IConfigFormatting formatting = IWailaConfig.get().getFormatting();
        tooltip.add(0, formatting.title(name), Identifiers.CORE_OBJECT_NAME);
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