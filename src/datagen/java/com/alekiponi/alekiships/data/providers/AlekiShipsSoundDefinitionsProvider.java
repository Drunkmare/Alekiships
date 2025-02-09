package com.alekiponi.alekiships.data.providers;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.common.sounds.AlekiShipsSounds;

import net.minecraft.data.PackOutput;

import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.SoundDefinitionsProvider;

public class AlekiShipsSoundDefinitionsProvider extends SoundDefinitionsProvider {

    public AlekiShipsSoundDefinitionsProvider(final PackOutput output, final ExistingFileHelper helper) {
        super(output, AlekiShips.MOD_ID, helper);
    }

    @Override
    public void registerSounds() {
        this.add(AlekiShipsSounds.MUSIC_DISC_PIRATE_CRAFTING, definition().with(
                sound(AlekiShipsSounds.MUSIC_DISC_PIRATE_CRAFTING.getId().withPrefix("record/")).stream()));
    }
}