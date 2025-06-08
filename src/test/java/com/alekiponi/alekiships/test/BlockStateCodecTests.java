package com.alekiponi.alekiships.test;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.JavaOps;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.alekiponi.alekiships.util.AlekiShipsExtraCodecs;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;

public final class BlockStateCodecTests {

    private static DataResult<BlockState> parse(final String string) {
        return AlekiShipsExtraCodecs.BLOCK_STATE_CODEC.parse(JavaOps.INSTANCE, string);
    }

    @ParameterizedTest
    @DisplayName("From name no properties")
    @ValueSource(strings = {"stone", "minecraft:stone", "stone_slab", "minecraft:stone_slab"})
    public void fromName(final String string) {
        Assertions.assertEquals(parse(string).getOrThrow(),
                BuiltInRegistries.BLOCK.get(ResourceLocation.parse(string)).defaultBlockState());
    }

    @ParameterizedTest
    @DisplayName("From name with properties")
    @ValueSource(strings = {"stone_slab[type=top]", "minecraft:stone_slab[type=top]"})
    public void fromNameWithProperties(final String string) {
        Assertions.assertEquals(parse(string).getOrThrow(),
                Blocks.STONE_SLAB.defaultBlockState().setValue(SlabBlock.TYPE, SlabType.TOP));
    }

    @ParameterizedTest
    @DisplayName("Bad formatting")
    @ValueSource(strings = {" stone ", "minecraft :stone", "stone_slab [type=top]", "minecraft:stone_slab[type= top]", "minecraft:[stone]", "stone[", "stone]", "stone[]", "stone_slab[type=top"})
    public void fromNameWithWhitespace(final String string) {
        Assertions.assertTrue(parse(string).isError());
    }

    @ParameterizedTest
    @DisplayName("Equivalent conversion")
    @ValueSource(strings = {"stone_slab[type=bottom]", "stone_slab[type=double,waterlogged=false]", "minecraft:stone_slab[type=top]"})
    public void equivalentConversion(final String string) {
        final var parsedBlockState = parse(string).getOrThrow();
        final String encodedString = ((String) AlekiShipsExtraCodecs.BLOCK_STATE_CODEC.encodeStart(JavaOps.INSTANCE,
                parsedBlockState).getOrThrow());
        Assertions.assertEquals(parsedBlockState, parse(encodedString).getOrThrow());
    }
}