package com.alekiponi.alekiships.client;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import static com.alekiponi.alekiships.AlekiShips.*;

public class AlekiShipsSounds
{
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Registries.SOUND_EVENT, MOD_ID);

    // Items
    public static final RegistryObject<SoundEvent> MUSIC_DISC_PIRATE_CRAFTING = create("music_disc_pirate_crafting");


    private static RegistryObject<SoundEvent> create(String name)
    {
        return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(MOD_ID, name)));
    }
}
