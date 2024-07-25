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
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.EnumMap;
import java.util.function.Supplier;

public final class AlekiShipsBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS,
            AlekiShips.MOD_ID);

    static final SoundType BOAT_FRAME_SOUND =
            new SoundType(SoundType.SCAFFOLDING.volume, SoundType.SCAFFOLDING.pitch, SoundType.SCAFFOLDING.getBreakSound(), SoundType.SCAFFOLDING.getStepSound(), SoundType.WOOD.getPlaceSound(), SoundType.SCAFFOLDING.getHitSound(), SoundType.SCAFFOLDING.getFallSound());

    public static final RegistryObject<AngledBoatFrameBlock> BOAT_FRAME_ANGLED = registerBlockWithItem(
            "watercraft_frame_angled", () -> new AngledBoatFrameBlock(
                    BlockBehaviour.Properties.copy(Blocks.BAMBOO_PLANKS).instabreak().noOcclusion()
                            .sound(SoundType.SCAFFOLDING)));

    public static final EnumMap<VanillaWood, RegistryObject<AngledWoodenBoatFrameBlock>> WOODEN_BOAT_FRAME_ANGLED = CommonHelper.mapOfKeys(
            VanillaWood.class,
            vanillaWood -> registerBlock("wood/watercraft_frame/angled/" + vanillaWood.getSerializedName(),
                    () -> new AngledWoodenBoatFrameBlock(vanillaWood,
                            BlockBehaviour.Properties.copy(BOAT_FRAME_ANGLED.get()).sound(BOAT_FRAME_SOUND))));

    public static final RegistryObject<FlatBoatFrameBlock> BOAT_FRAME_FLAT = registerBlockWithItem(
            "watercraft_frame_flat",
            () -> new FlatBoatFrameBlock(BlockBehaviour.Properties.copy(BOAT_FRAME_ANGLED.get())));

    public static final EnumMap<VanillaWood, RegistryObject<FlatWoodenBoatFrameBlock>> WOODEN_BOAT_FRAME_FLAT = CommonHelper.mapOfKeys(
            VanillaWood.class,
            vanillaWood -> registerBlock("wood/watercraft_frame/flat/" + vanillaWood.getSerializedName(),
                    () -> new FlatWoodenBoatFrameBlock(vanillaWood,
                            BlockBehaviour.Properties.copy(BOAT_FRAME_FLAT.get()).sound(BOAT_FRAME_SOUND))));

    public static final RegistryObject<OarlockBlock> OARLOCK = registerBlockWithItem("oarlock",
            () -> new OarlockBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).noOcclusion()));

    public static final RegistryObject<CleatBlock> CLEAT = registerBlockWithItem("cleat",
            () -> new CleatBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).noOcclusion()));

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        return BLOCKS.register(name, block);
    }

    private static <T extends Block> RegistryObject<T> registerBlockWithItem(String name, Supplier<T> block) {
        RegistryObject<T> blockRegistryObject = BLOCKS.register(name, block);
        registerBlockItem(name, blockRegistryObject);
        return blockRegistryObject;
    }

    private static <T extends Block> void registerBlockItem(String name, RegistryObject<T> block) {
        AlekiShipsItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }
}