package com.alekiponi.alekiships.util.advancements;

import com.alekiponi.alekiships.AlekiShips;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.resources.ResourceLocation;

public class AlekiShipsAdvancements {

    public static void registerTriggers() { }

    public static final GenericTrigger FULL_BROADSIDE = registerGeneric("full_broadside");

    public static final GenericTrigger SLOOP_COMPLETED = registerGeneric("sloop_completed");

    public static final GenericTrigger ROWBOAT_COMPLETED = registerGeneric("rowboat_completed");

    public static final GenericTrigger RIDE_BARREL = registerGeneric("ride_barrel");

    public static final GenericTrigger ARMOR_STAND_ON_BOAT = registerGeneric("armor_stand_on_boat");

    public static GenericTrigger registerGeneric(String name)
    {
        return CriteriaTriggers.register(new GenericTrigger(new ResourceLocation(AlekiShips.MOD_ID, name)));
    }

}
