package com.alekiponi.alekiships.network;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.wind.Wind;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.world.item.DyeColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Optional;
import java.util.function.Supplier;

public final class AlekiShipsEntityDataSerializers {

    public static final DeferredRegister<EntityDataSerializer<?>> ENTITY_DATA_SERIALIZERS = DeferredRegister.create(
            ForgeRegistries.Keys.ENTITY_DATA_SERIALIZERS, AlekiShips.MOD_ID);

    public static final RegistryObject<EntityDataSerializer<DyeColor>> DYE_COLOR = register("dye_color",
            () -> EntityDataSerializer.simpleEnum(DyeColor.class));
    public static final RegistryObject<EntityDataSerializer<Optional<DyeColor>>> OPTIONAL_DYE_COLOR = register(
            "optional_dye_color", () -> EntityDataSerializer.optional(FriendlyByteBuf::writeEnum,
                    (friendlyByteBuf) -> friendlyByteBuf.readEnum(DyeColor.class)));
    public static final RegistryObject<EntityDataSerializer<Wind>> WIND = register("",
            () -> EntityDataSerializer.simple((friendlyByteBuf, windVector) -> {
                friendlyByteBuf.writeFloat(windVector.speed);
                friendlyByteBuf.writeFloat(windVector.angle);
            }, friendlyByteBuf -> new Wind(friendlyByteBuf.readFloat(), friendlyByteBuf.readFloat())));

    private static <T extends EntityDataSerializer<?>> RegistryObject<T> register(String name,
            Supplier<T> dataSerializer) {
        return ENTITY_DATA_SERIALIZERS.register(name, dataSerializer);
    }
}