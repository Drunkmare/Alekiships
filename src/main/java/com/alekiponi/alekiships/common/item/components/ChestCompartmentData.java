package com.alekiponi.alekiships.common.item.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

public record ChestCompartmentData(RowCount rowCount, SoundEvent hurtSound, SoundEvent placeSound,
                                   SoundEvent breakSound, float soundVolume, float soundPitch,
                                   ResourceLocation chestTexture) {

    public static final Codec<ChestCompartmentData> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(RowCount.CODEC.fieldOf("row_count").forGetter(ChestCompartmentData::rowCount),
                            BuiltInRegistries.SOUND_EVENT.byNameCodec().optionalFieldOf("hurt_sound", SoundEvents.WOOD_HIT)
                                    .forGetter(ChestCompartmentData::hurtSound),
                            BuiltInRegistries.SOUND_EVENT.byNameCodec().optionalFieldOf("place_sound", SoundEvents.WOOD_PLACE)
                                    .forGetter(ChestCompartmentData::placeSound),
                            BuiltInRegistries.SOUND_EVENT.byNameCodec().optionalFieldOf("break_sound", SoundEvents.WOOD_BREAK)
                                    .forGetter(ChestCompartmentData::breakSound),
                            Codec.FLOAT.optionalFieldOf("sound_volume", 1F).forGetter(ChestCompartmentData::soundVolume),
                            Codec.FLOAT.optionalFieldOf("sound_pitch", 1F).forGetter(ChestCompartmentData::soundPitch),
                            ResourceLocation.CODEC.fieldOf("chest_texture").forGetter(ChestCompartmentData::chestTexture))
                    .apply(instance, ChestCompartmentData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ChestCompartmentData> STREAM_CODEC = NeoForgeStreamCodecs.composite(
            NeoForgeStreamCodecs.enumCodec(RowCount.class), ChestCompartmentData::rowCount,
            ByteBufCodecs.registry(Registries.SOUND_EVENT), ChestCompartmentData::hurtSound,
            ByteBufCodecs.registry(Registries.SOUND_EVENT), ChestCompartmentData::placeSound,
            ByteBufCodecs.registry(Registries.SOUND_EVENT), ChestCompartmentData::breakSound, ByteBufCodecs.FLOAT,
            ChestCompartmentData::soundVolume, ByteBufCodecs.FLOAT, ChestCompartmentData::soundPitch,
            ResourceLocation.STREAM_CODEC, ChestCompartmentData::chestTexture, ChestCompartmentData::new);

    public static final ChestCompartmentData VANILLA_CHEST_NORMAL = new ChestCompartmentData(RowCount.THREE,
            SoundEvents.WOOD_HIT, SoundEvents.WOOD_PLACE, SoundEvents.WOOD_BREAK, 1, 1,
            ResourceLocation.withDefaultNamespace("entity/chest/normal"));

    public static final ChestCompartmentData VANILLA_CHEST_TRAPPED = new ChestCompartmentData(RowCount.THREE,
            SoundEvents.WOOD_HIT, SoundEvents.WOOD_PLACE, SoundEvents.WOOD_BREAK, 1, 1,
            ResourceLocation.withDefaultNamespace("entity/chest/trapped"));

    private static final int ROW_LENGTH = 9;

    public int slotCount() {
        return switch (this.rowCount) {
            case ONE -> ROW_LENGTH;
            case TWO -> 2 * ROW_LENGTH;
            case THREE -> 3 * ROW_LENGTH;
            case FOUR -> 4 * ROW_LENGTH;
            case FIVE -> 5 * ROW_LENGTH;
            case SIX -> 6 * ROW_LENGTH;
        };
    }

    public AbstractContainerMenu createMenu(final int containerId, final Inventory playerInventory,
            final Container container) {
        return switch (this.rowCount) {
            case ONE -> new ChestMenu(MenuType.GENERIC_9x1, containerId, playerInventory, container, 1);
            case TWO -> new ChestMenu(MenuType.GENERIC_9x2, containerId, playerInventory, container, 2);
            case THREE -> new ChestMenu(MenuType.GENERIC_9x3, containerId, playerInventory, container, 3);
            case FOUR -> new ChestMenu(MenuType.GENERIC_9x4, containerId, playerInventory, container, 4);
            case FIVE -> new ChestMenu(MenuType.GENERIC_9x5, containerId, playerInventory, container, 5);
            case SIX -> new ChestMenu(MenuType.GENERIC_9x6, containerId, playerInventory, container, 6);
        };
    }

    public enum RowCount implements StringRepresentable {
        ONE("one"),
        TWO("two"),
        THREE("three"),
        FOUR("four"),
        FIVE("five"),
        SIX("six");

        public static final Codec<RowCount> CODEC = StringRepresentable.fromEnum(RowCount::values);

        private final String name;

        RowCount(final String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }
}