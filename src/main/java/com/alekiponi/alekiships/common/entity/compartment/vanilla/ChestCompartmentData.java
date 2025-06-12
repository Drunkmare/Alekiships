package com.alekiponi.alekiships.common.entity.compartment.vanilla;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import com.alekiponi.alekiships.common.AlekiShipsBuiltInRegistries;
import com.alekiponi.alekiships.common.AlekiShipsRegistries;
import com.alekiponi.alekiships.common.compartment.AlekiShipsChestCompartmentTypes;
import com.alekiponi.alekiships.network.AlekiShipsStreamCodecs;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

import org.jetbrains.annotations.NotNull;
import lombok.AllArgsConstructor;
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

    public static final Codec<ChestCompartmentData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    AlekiShipsBuiltInRegistries.CHEST_COMPARTMENT_TYPES.holderByNameCodec()
                            .optionalFieldOf("chest_type", AlekiShipsChestCompartmentTypes.VANILLA_CHEST)
                            .forGetter(ChestCompartmentData::chestType),
                    RowCount.CODEC.optionalFieldOf("row_count", RowCount.THREE).forGetter(ChestCompartmentData::rowCount),
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

    public static final StreamCodec<RegistryFriendlyByteBuf, ChestCompartmentData> STREAM_CODEC = AlekiShipsStreamCodecs.composite(
            ByteBufCodecs.holderRegistry(AlekiShipsRegistries.CHEST_COMPARTMENT_TYPES), ChestCompartmentData::chestType,
            NeoForgeStreamCodecs.enumCodec(RowCount.class), ChestCompartmentData::rowCount, SOUND_EVENT_STREAM_CODEC,
            ChestCompartmentData::hurtSound, SOUND_EVENT_STREAM_CODEC, ChestCompartmentData::placeSound,
            SOUND_EVENT_STREAM_CODEC, ChestCompartmentData::breakSound, ByteBufCodecs.FLOAT,
            ChestCompartmentData::soundVolume, ByteBufCodecs.FLOAT, ChestCompartmentData::soundPitch,
            ResourceLocation.STREAM_CODEC, ChestCompartmentData::texture, ChestCompartmentData::new);

    private static final int ROW_LENGTH = 9;
    @Builder.Default
    Holder<ChestType> chestType = AlekiShipsChestCompartmentTypes.VANILLA_CHEST;
    /**
     * The amount of rows
     */
    @Builder.Default
    RowCount rowCount = RowCount.THREE;
    /**
     * The sound to use when the compartment is being broken
     */
    @Builder.Default
    SoundEvent hurtSound = SoundEvents.WOOD_HIT;
    /**
     * The sound to use when the compartment is placed
     */
    @Builder.Default
    SoundEvent placeSound = SoundEvents.WOOD_PLACE;
    /**
     * The sound to use when the compartment is broken
     */
    @Builder.Default
    SoundEvent breakSound = SoundEvents.WOOD_BREAK;
    /**
     * The sound volume
     */
    @Builder.Default
    float soundVolume = 1;
    /**
     * The sound pitch
     */
    @Builder.Default
    float soundPitch = 1;
    /**
     * The texture to use. Must be in the chest atlas {@link net.minecraft.client.renderer.Sheets#CHEST_SHEET Sheets#CHEST_SHEET}
     */
    @NotNull ResourceLocation texture;

    /**
     * The slot count calculated from the row count using rows of 9 slots
     */
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

    public boolean isItemValid(final ItemStack stack) {
        return this.chestType.value().isItemValid(stack);
    }

    public AbstractContainerMenu createMenu(final int id, final Inventory playerInventory,
            final ChestCompartmentEntity chestCompartmentEntity) {
        return this.chestType.value().createMenu(id, playerInventory, this.rowCount, chestCompartmentEntity);
    }

    @AllArgsConstructor
    public enum RowCount implements StringRepresentable {
        ONE("one"),
        TWO("two"),
        THREE("three"),
        FOUR("four"),
        FIVE("five"),
        SIX("six");

        public static final Codec<RowCount> CODEC = StringRepresentable.fromEnum(RowCount::values);

        private final String name;

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }

    /**
     * This interface is for wrapping up the unique code only parts of a compartment.
     * <p>
     * This is primarily useful for very minor changes like seen with the chests in TFC.
     */
    public interface ChestType {

        /**
         * Create the menu for this chest type
         *
         * @param id               The menu id
         * @param inventory        The inventory
         * @param rowCount         The requested row count
         * @param chestCompartment The chest compartment
         */
        default AbstractContainerMenu createMenu(int id, Inventory inventory, RowCount rowCount,
                ChestCompartmentEntity chestCompartment) {
            return switch (rowCount) {
                case ONE -> new ChestMenu(MenuType.GENERIC_9x1, id, inventory, chestCompartment, 1);
                case TWO -> new ChestMenu(MenuType.GENERIC_9x2, id, inventory, chestCompartment, 2);
                case THREE -> new ChestMenu(MenuType.GENERIC_9x3, id, inventory, chestCompartment, 3);
                case FOUR -> new ChestMenu(MenuType.GENERIC_9x4, id, inventory, chestCompartment, 4);
                case FIVE -> new ChestMenu(MenuType.GENERIC_9x5, id, inventory, chestCompartment, 5);
                case SIX -> new ChestMenu(MenuType.GENERIC_9x6, id, inventory, chestCompartment, 6);
            };
        }

        /**
         * Callback for if the provided item is valid for insertion
         *
         * @param itemStack The item stack
         *
         * @return If the provided stack is valid for insertion
         */
        default boolean isItemValid(@SuppressWarnings("unused") ItemStack itemStack) {return true;}
    }
}
