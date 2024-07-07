package com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.vanilla.crafting;

import com.alekiponi.alekiships.common.entity.vehiclehelper.CompartmentType;
import com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.SimpleBlockMenuCompartmentEntity;
import com.alekiponi.alekiships.util.CommonHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.GrindstoneBlock;
import net.minecraft.world.level.block.state.properties.AttachFace;
import org.jetbrains.annotations.Nullable;

public class GrindstoneCompartmentEntity extends SimpleBlockMenuCompartmentEntity {

    private static final Component CONTAINER_TITLE = Component.translatable("container.grindstone_title");

    public GrindstoneCompartmentEntity(final CompartmentType<? extends GrindstoneCompartmentEntity> compartmentType,
            final Level level) {
        super(compartmentType, level);
    }

    public GrindstoneCompartmentEntity(final CompartmentType<? extends GrindstoneCompartmentEntity> compartmentType,
            final Level level, final ItemStack itemStack) {
        super(compartmentType, level, itemStack);
        this.setDisplayBlockState(this.getDisplayBlockState().setValue(GrindstoneBlock.FACE, AttachFace.FLOOR));
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(final int id, final Inventory playerInventory, final Player player) {
        return new GrindstoneMenu(id, playerInventory, CommonHelper.createEntityContainerLevelAccess(this)) {
            @Override
            public boolean stillValid(final Player player) {
                return CommonHelper.stillValidEntity(GrindstoneCompartmentEntity.this, player);
            }
        };
    }

    @Override
    protected Stat<ResourceLocation> getInteractionStat() {
        return Stats.CUSTOM.get(Stats.INTERACT_WITH_GRINDSTONE);
    }

    @Override
    protected Component getContainerTitle() {
        return CONTAINER_TITLE;
    }
}