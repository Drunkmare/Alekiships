package com.alekiponi.alekiships.compat.waila;

import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.JadeIds;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.theme.IThemeHelper;
import snownee.jade.api.ui.IElement;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.common.block.AngledBoatFrameBlock;
import com.alekiponi.alekiships.common.block.entity.FrameBlockEntity;
import com.alekiponi.alekiships.compat.waila.ui.BlockStateElement;

import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.StairsShape;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum FrameBlockProvider implements IBlockComponentProvider {
    ANGLED(AlekiShips.MOD_ID + ".jade.angled_frame_block"),
    FLAT(AlekiShips.MOD_ID + ".jade.flat_frame_block");

    private static final ResourceLocation NAME = AlekiShips.location("frame_block");

    public final String key;

    @Override
    public IElement getIcon(final BlockAccessor accessor, final IPluginConfig config, final IElement currentIcon) {
        final var state = accessor.getBlockState()
                .trySetValue(AngledBoatFrameBlock.SHAPE, StairsShape.STRAIGHT)
                .trySetValue(AngledBoatFrameBlock.FACING, Direction.NORTH);
        final var modelData = accessor.getBlockEntity().getModelData();
        return new BlockStateElement(state, modelData);
    }

    @Override
    public void appendTooltip(final ITooltip tooltip, final BlockAccessor blockAccessor, final IPluginConfig config) {
        final var frameBlock = (FrameBlockEntity) blockAccessor.getBlockEntity();
        // <Material Name> Frame Block
        final var title = IThemeHelper.get().title(Component.translatable(this.key, frameBlock.getMaterial().name()));
        tooltip.replace(JadeIds.CORE_OBJECT_NAME, title);
    }

    @Override
    public ResourceLocation getUid() {
        return NAME;
    }
}