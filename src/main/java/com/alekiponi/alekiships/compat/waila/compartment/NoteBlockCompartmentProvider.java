package com.alekiponi.alekiships.compat.waila.compartment;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.vanilla.NoteBlockCompartmentEntity;
import com.google.common.base.Joiner;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.NoteBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import org.apache.commons.lang3.StringUtils;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

import java.util.stream.Stream;

public enum NoteBlockCompartmentProvider implements IEntityComponentProvider {
    INSTANCE;

    private static final String[] PITCH = {"F♯/G♭", "G", "G♯/A♭", "A", "A♯/B♭", "B", "C", "C♯/D♭", "D", "D♯/E♭", "E", "F"};
    private static final ChatFormatting[] OCTAVE = new ChatFormatting[]{ChatFormatting.WHITE, ChatFormatting.YELLOW, ChatFormatting.GOLD};
    private static final ResourceLocation NAME = new ResourceLocation(AlekiShips.MOD_ID, "note_block");

    @Override
    public void appendTooltip(final ITooltip tooltip, final EntityAccessor entityAccessor,
            final IPluginConfig iPluginConfig) {
        final BlockState blockState = ((NoteBlockCompartmentEntity) entityAccessor.getEntity()).getDisplayBlockState();
        final String name;
        {
            final NoteBlockInstrument instrument = blockState.getValue(NoteBlock.INSTRUMENT);
            final String key = "jade.instrument." + instrument.getSerializedName();
            if (I18n.exists(key)) {
                name = I18n.get(key);
            } else {
                name = Joiner.on(' ').join(Stream.of(instrument.getSerializedName().replace('_', ' ').split(" "))
                        .map(StringUtils::capitalize).toList());
            }
        }

        final int note = blockState.getValue(NoteBlock.NOTE);
        tooltip.add(Component.literal(name + " " + OCTAVE[note / PITCH.length] + PITCH[note % PITCH.length]));
    }

    @Override
    public ResourceLocation getUid() {
        return NAME;
    }
}