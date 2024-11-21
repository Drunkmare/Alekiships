package com.alekiponi.alekiships.compat.waila.compartment;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.vanilla.JukeboxCompartmentEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.JukeboxSong;
import net.minecraft.world.level.block.JukeboxBlock;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.IDisplayHelper;

public enum JukeboxCompartmentProvider implements IEntityComponentProvider, IServerDataProvider<EntityAccessor> {
    INSTANCE;

    private static final MapCodec<Holder<JukeboxSong>> SONG_CODEC = JukeboxSong.CODEC.fieldOf("JukeboxSong");

    private static final ResourceLocation NAME = ResourceLocation.fromNamespaceAndPath(AlekiShips.MOD_ID, "jukebox");

    @Override
    public void appendTooltip(final ITooltip tooltip, final EntityAccessor entityAccessor,
            final IPluginConfig iPluginConfig) {
        if (!((JukeboxCompartmentEntity) entityAccessor.getEntity()).getDisplayBlockState()
                .getValue(JukeboxBlock.HAS_RECORD)) {
            tooltip.add(Component.translatable("tooltip.jade.empty"));
            return;
        }

        entityAccessor.readData(SONG_CODEC).map(songHolder -> songHolder.value().description()).ifPresent(
                component -> tooltip.add(
                        Component.translatable("record.nowPlaying", IDisplayHelper.get().stripColor(component))));
    }

    @Override
    public void appendServerData(final CompoundTag compoundTag, final EntityAccessor entityAccessor) {
        final ItemStack itemStack = ((JukeboxCompartmentEntity) entityAccessor.getEntity()).getTheItem();
        JukeboxSong.fromStack(entityAccessor.getLevel().registryAccess(), itemStack)
                .ifPresent(songHolder -> entityAccessor.writeData(SONG_CODEC, songHolder));
    }

    @Override
    public ResourceLocation getUid() {
        return NAME;
    }
}