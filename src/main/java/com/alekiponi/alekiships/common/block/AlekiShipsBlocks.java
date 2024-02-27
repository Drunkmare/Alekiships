package com.alekiponi.alekiships.common.block;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.common.item.AlekiShipsItems;
import com.alekiponi.alekiships.util.AlekiShipsHelper;
import com.alekiponi.alekiships.util.VanillaWood;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.util.Metal;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.EnumMap;
import java.util.function.Supplier;

public final class AlekiShipsBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS,
            AlekiShips.MOD_ID);
    public static final RegistryObject<Block> BOAT_FRAME_ANGLED = registerBlockWithItem("watercraft_frame_angled",
            () -> new AngledBoatFrameBlock(BlockBehaviour.Properties.copy(Blocks.BAMBOO_PLANKS).instabreak().noOcclusion().sound(SoundType.SCAFFOLDING)));

    public static final EnumMap<VanillaWood, RegistryObject<AngledWoodenBoatFrameBlock>> WOODEN_BOAT_FRAME_ANGLED = AlekiShipsHelper.mapOfKeys(
            VanillaWood.class,
            vanillaWood -> registerBlock("wood/watercraft_frame_angled/" + vanillaWood.getSerializedName(),
                    () -> new AngledWoodenBoatFrameBlock(vanillaWood,
                            BlockBehaviour.Properties.copy(BOAT_FRAME_ANGLED.get()))));

    public static final RegistryObject<FlatBoatFrameBlock> BOAT_FRAME_FLAT = registerBlockWithItem(
            "watercraft_frame_flat",
            () -> new FlatBoatFrameBlock(BlockBehaviour.Properties.copy(BOAT_FRAME_ANGLED.get())));

    public static final EnumMap<VanillaWood, RegistryObject<FlatWoodenBoatFrameBlock>> WOODEN_BOAT_FRAME_FLAT = AlekiShipsHelper.mapOfKeys( VanillaWood.class,
            vanillaWood -> registerBlock("wood/watercraft_frame_flat/" + vanillaWood.getSerializedName(),
                    () -> new FlatWoodenBoatFrameBlock(vanillaWood, BlockBehaviour.Properties.copy(BOAT_FRAME_FLAT.get()))));

    public static final RegistryObject<Block> OARLOCK = registerBlockWithItem("oarlock", () -> new OarlockBlock(
            BlockBehaviour.Properties.copy(
                    TFCBlocks.METALS.get(Metal.Default.WROUGHT_IRON).get(Metal.BlockType.BLOCK).get()).noOcclusion()));

    public static final RegistryObject<Block> CLEAT = registerBlockWithItem("cleat", () -> new CleatBlock(
            BlockBehaviour.Properties.copy(
                    TFCBlocks.METALS.get(Metal.Default.WROUGHT_IRON).get(Metal.BlockType.BLOCK).get()).noOcclusion()));

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

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
