package com.alekiponi.alekiships.network;

import com.alekiponi.alekiships.AlekiShips;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.VarInt;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.world.item.DyeColor;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.Optional;
import java.util.function.Supplier;

public final class AlekiShipsEntityDataSerializers {

    public static final DeferredRegister<EntityDataSerializer<?>> ENTITY_DATA_SERIALIZERS = DeferredRegister.create(
            NeoForgeRegistries.ENTITY_DATA_SERIALIZERS, AlekiShips.MOD_ID);

    public static final Supplier<EntityDataSerializer<DyeColor>> DYE_COLOR = register("dye_color",
            () -> EntityDataSerializer.forValueType(DyeColor.STREAM_CODEC));

    private static final StreamCodec<ByteBuf, Optional<DyeColor>> OPTIONAL_DYE_COLOR_CODEC = new StreamCodec<>() {
        public void encode(final ByteBuf byteBuf, Optional<DyeColor> dyeColor) {
            if (dyeColor.isPresent()) {
                VarInt.write(byteBuf, dyeColor.get().getId());
            } else {
                VarInt.write(byteBuf, 0);
            }
        }

        public Optional<DyeColor> decode(final ByteBuf byteBuf) {
            int i = VarInt.read(byteBuf);
            return i == 0 ? Optional.empty() : Optional.of(DyeColor.byId(i));
        }
    };

    public static final Supplier<EntityDataSerializer<Optional<DyeColor>>> OPTIONAL_DYE_COLOR = register(
            "optional_dye_color", () -> EntityDataSerializer.forValueType(OPTIONAL_DYE_COLOR_CODEC));

//  TODO Our wind type needs a StreamCodec ugh
//
//    public static final Supplier<EntityDataSerializer<Wind>> WIND = register("",
//            () -> EntityDataSerializer.simple((friendlyByteBuf, windVector) -> {
//                friendlyByteBuf.writeFloat(windVector.speed);
//                friendlyByteBuf.writeFloat(windVector.angle);
//            }, friendlyByteBuf -> new Wind(friendlyByteBuf.readFloat(), friendlyByteBuf.readFloat())));

    private static <T extends EntityDataSerializer<?>> DeferredHolder<EntityDataSerializer<?>, T> register(
            final String name, final Supplier<T> dataSerializer) {
        return ENTITY_DATA_SERIALIZERS.register(name, dataSerializer);
    }
}