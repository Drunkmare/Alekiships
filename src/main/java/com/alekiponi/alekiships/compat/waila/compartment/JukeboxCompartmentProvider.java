package com.alekiponi.alekiships.compat.waila.compartment;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.vanilla.JukeboxCompartmentEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.level.block.JukeboxBlock;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.IDisplayHelper;

public enum JukeboxCompartmentProvider implements IEntityComponentProvider, IServerDataProvider<EntityAccessor> {
    INSTANCE;

    private static final String TRACK_NAME_KEY = "TrackName";
    private static final ResourceLocation NAME = new ResourceLocation(AlekiShips.MOD_ID, "jukebox");

    @Override
    public void appendTooltip(final ITooltip tooltip, final EntityAccessor entityAccessor,
            final IPluginConfig iPluginConfig) {
        if (!((JukeboxCompartmentEntity) entityAccessor.getEntity()).getDisplayBlockState()
                .getValue(JukeboxBlock.HAS_RECORD)) {
            tooltip.add(Component.translatable("tooltip.jade.empty"));
            return;
        }

        final CompoundTag serverData = entityAccessor.getServerData();
        if (serverData.contains(TRACK_NAME_KEY, CompoundTag.TAG_STRING)) {
            final Component trackName = Component.Serializer.fromJson(serverData.getString(TRACK_NAME_KEY));
            tooltip.add(Component.translatable("record.nowPlaying", IDisplayHelper.get().stripColor(trackName)));
        }
    }

    @Override
    public void appendServerData(final CompoundTag compoundTag, final EntityAccessor entityAccessor) {
        final ItemStack itemStack = ((JukeboxCompartmentEntity) entityAccessor.getEntity()).getFirstItem();
        if (itemStack.getItem() instanceof final RecordItem recordItem) {
            compoundTag.putString(TRACK_NAME_KEY, Component.Serializer.toJson(recordItem.getDisplayName()));
            return;
        }

        compoundTag.putString(TRACK_NAME_KEY, Component.Serializer.toJson(itemStack.getHoverName()));
    }

    @Override
    public ResourceLocation getUid() {
        return NAME;
    }
}