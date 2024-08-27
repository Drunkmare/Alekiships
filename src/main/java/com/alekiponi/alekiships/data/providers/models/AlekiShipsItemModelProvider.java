package com.alekiponi.alekiships.data.providers.models;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.common.item.AlekiShipsItems;
import net.minecraft.data.PackOutput;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class AlekiShipsItemModelProvider extends ItemModelProvider {

    public AlekiShipsItemModelProvider(final PackOutput output, final ExistingFileHelper existingFileHelper) {
        super(output, AlekiShips.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        this.basicItem(AlekiShipsItems.CANNON.get());
        this.basicItem(AlekiShipsItems.CANNONBALL.get());
        this.basicItem(AlekiShipsItems.ANCHOR.get());
        this.basicItem(AlekiShipsItems.SLOOP_ICON_ONLY.get());
        this.basicItem(AlekiShipsItems.ROWBOAT_ICON_ONLY.get());
        this.basicItem(AlekiShipsItems.OAR.get());
    }
}