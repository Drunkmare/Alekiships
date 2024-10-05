package com.alekiponi.alekiships.common.sounds;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.alekiponi.alekiships.AlekiShips.MOD_ID;

public final class AlekiShipsSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Registries.SOUND_EVENT, MOD_ID);

    // Items
    public static final DeferredHolder<SoundEvent, SoundEvent> MUSIC_DISC_PIRATE_CRAFTING = create(
            "music_disc_pirate_crafting");

    private static DeferredHolder<SoundEvent, SoundEvent> create(String name) {
        return SOUNDS.register(name,
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(MOD_ID, name)));
    }
}
