package com.alekiponi.alekiships.compat.waila.compartment;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.common.entity.compartment.vanilla.AbstractFurnaceCompartmentEntity;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.IElementHelper;

public enum FurnaceCompartmentProvider implements IEntityComponentProvider, IServerDataProvider<EntityAccessor> {
    INSTANCE;

    private static final ResourceLocation NAME = ResourceLocation.fromNamespaceAndPath(AlekiShips.MOD_ID, "furnace");

    @Override
    public void appendTooltip(final ITooltip tooltip, final EntityAccessor entityAccessor,
            final IPluginConfig iPluginConfig) {
        tooltip.remove(JadeIds.UNIVERSAL_ITEM_STORAGE);

        final CompoundTag serverData = entityAccessor.getServerData();
        final int progress = serverData.getInt("progress");
        if (progress == 0) return;

        final ListTag furnaceItems = serverData.getList("furnace", Tag.TAG_COMPOUND);
        final NonNullList<ItemStack> inventory = NonNullList.withSize(AbstractFurnaceCompartmentEntity.SLOT_COUNT,
                ItemStack.EMPTY);

        for (int slotIndex = 0; slotIndex < furnaceItems.size(); ++slotIndex) {
            inventory.set(slotIndex, ItemStack.parseOptional(entityAccessor.getEntity().registryAccess(),
                    furnaceItems.getCompound(slotIndex)));
        }

        final IElementHelper helper = IElementHelper.get();
        //noinspection SequencedCollectionMethodCanBeUsed
        tooltip.add(helper.item(inventory.get(AbstractFurnaceCompartmentEntity.SLOT_INPUT)));
        tooltip.append(helper.item(inventory.get(AbstractFurnaceCompartmentEntity.SLOT_FUEL)));
        tooltip.append(helper.progress((float) progress / (float) serverData.getInt("total")));
        tooltip.append(helper.item(inventory.get(AbstractFurnaceCompartmentEntity.SLOT_RESULT)));
    }

    @Override
    public void appendServerData(final CompoundTag compoundTag, final EntityAccessor entityAccessor) {
        final AbstractFurnaceCompartmentEntity furnaceCompartment = ((AbstractFurnaceCompartmentEntity) entityAccessor.getEntity());

        {
            final ListTag items = new ListTag();
            for (int slotIndex = 0; slotIndex < AbstractFurnaceCompartmentEntity.SLOT_COUNT; ++slotIndex) {
                items.add(furnaceCompartment.getItem(slotIndex).saveOptional(furnaceCompartment.registryAccess()));
            }
            compoundTag.put("furnace", items);
        }

        compoundTag.putInt("progress", furnaceCompartment.getCookTime());
        compoundTag.putInt("total", furnaceCompartment.getTotalCookTime());
    }

    @Override
    public ResourceLocation getUid() {
        return NAME;
    }

    @Override
    public int getDefaultPriority() {
        // Must run after Jades universal item storage
        return 1000 + 1;
    }
}