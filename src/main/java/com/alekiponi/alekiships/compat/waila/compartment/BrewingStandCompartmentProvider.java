package com.alekiponi.alekiships.compat.waila.compartment;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.vanilla.BrewingStandCompartmentEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.IElementHelper;

public enum BrewingStandCompartmentProvider implements IEntityComponentProvider, IServerDataProvider<EntityAccessor> {
    INSTANCE;

    private static final ResourceLocation NAME = new ResourceLocation(AlekiShips.MOD_ID, "brewing_stand");

    @Override
    public void appendTooltip(final ITooltip tooltip, final EntityAccessor entityAccessor,
            final IPluginConfig iPluginConfig) {
        final CompoundTag serverData = entityAccessor.getServerData();
        if (!serverData.contains("BrewingStand", Tag.TAG_COMPOUND)) return;

        final CompoundTag brewingStand = serverData.getCompound("BrewingStand");
        final IElementHelper helper = IElementHelper.get();
        tooltip.add(helper.smallItem(new ItemStack(Items.BLAZE_POWDER)));
        tooltip.append(Component.literal(Integer.toString(brewingStand.getInt("Fuel"))));

        final int time = brewingStand.getInt("Time");
        if (time > 0) {
            tooltip.append(helper.spacer(5, 0));
            tooltip.append(helper.smallItem(new ItemStack(Items.CLOCK)));
            tooltip.append(Component.translatable("jade.seconds", time / 20));
        }
    }

    @Override
    public void appendServerData(final CompoundTag compoundTag, final EntityAccessor entityAccessor) {
        final BrewingStandCompartmentEntity brewingStandCompartment = (BrewingStandCompartmentEntity) entityAccessor.getEntity();
        final CompoundTag brewingStand = new CompoundTag();
        brewingStand.putInt("Time", brewingStandCompartment.getBrewTime());
        brewingStand.putInt("Fuel", brewingStandCompartment.getFuel());
        compoundTag.put("BrewingStand", brewingStand);
    }

    @Override
    public ResourceLocation getUid() {
        return NAME;
    }
}