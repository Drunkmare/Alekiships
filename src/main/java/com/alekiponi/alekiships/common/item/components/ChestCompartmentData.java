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

import org.jetbrains.annotations.NotNull;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.Accessors;

@Value
@Accessors(fluent = true)
@Builder(toBuilder = true)
public class ChestCompartmentData {

    public static final ChestCompartmentData VANILLA_CHEST_NORMAL = ChestCompartmentData.builder()
            .texture(ResourceLocation.withDefaultNamespace("entity/chest/normal"))
            .build();

    private static final Codec<SoundEvent> SOUND_EVENT_CODEC = BuiltInRegistries.SOUND_EVENT.byNameCodec();

    public static final Codec<ChestCompartmentData> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(RowCount.CODEC.fieldOf("row_count").forGetter(ChestCompartmentData::rowCount),
                            SOUND_EVENT_CODEC.optionalFieldOf("hurt_sound", SoundEvents.WOOD_HIT)
                                    .forGetter(ChestCompartmentData::hurtSound),
                            SOUND_EVENT_CODEC.optionalFieldOf("place_sound", SoundEvents.WOOD_PLACE)
                                    .forGetter(ChestCompartmentData::placeSound),
                            SOUND_EVENT_CODEC.optionalFieldOf("break_sound", SoundEvents.WOOD_BREAK)
                                    .forGetter(ChestCompartmentData::breakSound),
                            Codec.FLOAT.optionalFieldOf("sound_volume", 1F).forGetter(ChestCompartmentData::soundVolume),
                            Codec.FLOAT.optionalFieldOf("sound_pitch", 1F).forGetter(ChestCompartmentData::soundPitch),
                            ResourceLocation.CODEC.fieldOf("texture").forGetter(ChestCompartmentData::texture))
                    .apply(instance, ChestCompartmentData::new));

    private static final StreamCodec<RegistryFriendlyByteBuf, SoundEvent> SOUND_EVENT_STREAM_CODEC = ByteBufCodecs.registry(
            Registries.SOUND_EVENT);

    public static final StreamCodec<RegistryFriendlyByteBuf, ChestCompartmentData> STREAM_CODEC = NeoForgeStreamCodecs.composite(
            NeoForgeStreamCodecs.enumCodec(RowCount.class), ChestCompartmentData::rowCount, SOUND_EVENT_STREAM_CODEC,
            ChestCompartmentData::hurtSound, SOUND_EVENT_STREAM_CODEC, ChestCompartmentData::placeSound,
            SOUND_EVENT_STREAM_CODEC, ChestCompartmentData::breakSound, ByteBufCodecs.FLOAT,
            ChestCompartmentData::soundVolume, ByteBufCodecs.FLOAT, ChestCompartmentData::soundPitch,
            ResourceLocation.STREAM_CODEC, ChestCompartmentData::texture, ChestCompartmentData::new);

    private static final int ROW_LENGTH = 9;
    @Builder.Default
    RowCount rowCount = RowCount.THREE;
    @Builder.Default
    SoundEvent hurtSound = SoundEvents.WOOD_HIT;
    @Builder.Default
    SoundEvent placeSound = SoundEvents.WOOD_PLACE;
    @Builder.Default
    SoundEvent breakSound = SoundEvents.WOOD_BREAK;
    @Builder.Default
    float soundVolume = 1;
    @Builder.Default
    float soundPitch = 1;
    @NotNull ResourceLocation texture;

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