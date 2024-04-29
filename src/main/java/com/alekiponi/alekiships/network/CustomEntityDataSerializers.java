package com.alekiponi.alekiships.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.world.item.DyeColor;

import java.util.Optional;

public final class CustomEntityDataSerializers {

    public static final EntityDataSerializer<DyeColor> DYE_COLOR = EntityDataSerializer.simpleEnum(DyeColor.class);
    public static final EntityDataSerializer<Optional<DyeColor>> OPTIONAL_DYE_COLOR = EntityDataSerializer.optional(
            FriendlyByteBuf::writeEnum, (friendlyByteBuf) -> friendlyByteBuf.readEnum(DyeColor.class));
}