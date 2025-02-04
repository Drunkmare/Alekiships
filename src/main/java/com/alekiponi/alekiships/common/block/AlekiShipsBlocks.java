package com.alekiponi.alekiships.common.block;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.common.item.AlekiShipsItems;
import com.alekiponi.alekiships.util.CommonHelper;
import com.alekiponi.alekiships.util.VanillaWood;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.EnumMap;
import java.util.function.Supplier;

public final class AlekiShipsBlocks {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(AlekiShips.MOD_ID);

    public static final DeferredBlock<AngledBoatFrameBlock> BOAT_FRAME_ANGLED = registerBlockWithItem(
            "watercraft_frame_angled", () -> new AngledBoatFrameBlock(
                    BlockBehaviour.Properties.ofFullCopy(Blocks.BAMBOO_PLANKS).instabreak().noOcclusion()
                            .sound(SoundType.SCAFFOLDING)));

    public static final DeferredBlock<FlatBoatFrameBlock> BOAT_FRAME_FLAT = registerBlockWithItem(
            "watercraft_frame_flat",
            () -> new FlatBoatFrameBlock(BlockBehaviour.Properties.ofFullCopy(BOAT_FRAME_ANGLED.get())));

    public static final DeferredBlock<OarlockBlock> OARLOCK = registerBlockWithItem("oarlock",
            () -> new OarlockBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).noOcclusion()));

    public static final DeferredBlock<CleatBlock> CLEAT = registerBlockWithItem("cleat",
            () -> new CleatBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).noOcclusion()));

    private static final SoundType BOAT_FRAME_SOUND = new SoundType(SoundType.SCAFFOLDING.volume,
            SoundType.SCAFFOLDING.pitch, SoundType.SCAFFOLDING.getBreakSound(), SoundType.SCAFFOLDING.getStepSound(),
            SoundType.WOOD.getPlaceSound(), SoundType.SCAFFOLDING.getHitSound(), SoundType.SCAFFOLDING.getFallSound());

    public static final EnumMap<VanillaWood, DeferredBlock<AngledWoodenBoatFrameBlock>> WOODEN_BOAT_FRAME_ANGLED = CommonHelper.mapOfKeys(
            VanillaWood.class,
            vanillaWood -> registerBlock("wood/watercraft_frame/angled/" + vanillaWood.getSerializedName(),
                    () -> new AngledWoodenBoatFrameBlock(vanillaWood,
                            vanillaWood::getDeckItem, BlockBehaviour.Properties.ofFullCopy(BOAT_FRAME_ANGLED.get()).sound(BOAT_FRAME_SOUND)
                    )));

    public static final EnumMap<VanillaWood, DeferredBlock<FlatWoodenBoatFrameBlock>> WOODEN_BOAT_FRAME_FLAT = CommonHelper.mapOfKeys(
            VanillaWood.class,
            vanillaWood -> registerBlock("wood/watercraft_frame/flat/" + vanillaWood.getSerializedName(),
                    () -> new FlatWoodenBoatFrameBlock(vanillaWood, vanillaWood::getDeckItem,
                            BlockBehaviour.Properties.ofFullCopy(BOAT_FRAME_FLAT.get()).sound(BOAT_FRAME_SOUND))));


    private static <B extends Block> DeferredBlock<B> registerBlock(final String name, final Supplier<B> block) {
        return BLOCKS.register(name, block);
    }

    private static <B extends Block> DeferredBlock<B> registerBlockWithItem(final String name,
            final Supplier<B> block) {
        final var blockRegistryObject = BLOCKS.register(name, block);
        registerBlockItem(name, blockRegistryObject);
        return blockRegistryObject;
    }

    private static <B extends Block> void registerBlockItem(final String name, final Supplier<B> block) {
        AlekiShipsItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }
}