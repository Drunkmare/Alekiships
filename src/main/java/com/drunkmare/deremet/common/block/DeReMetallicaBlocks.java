package com.drunkmare.deremet.common.block;

import com.drunkmare.deremet.DeReMetallica;
import com.drunkmare.deremet.common.item.DeReMetallicaItems;
import com.drunkmare.deremet.util.VanillaWood;
import com.alekiponi.alekiships.util.CommonHelper;
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

// Central block registry. All blocks and their BlockItems are registered here.
public final class DeReMetallicaBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS,
            DeReMetallica.MOD_ID);

    // A custom SoundType that uses scaffolding sounds except for the place sound,
    // which uses the softer wood place sound.
    static final SoundType FRAME_SOUND =
            new SoundType(SoundType.SCAFFOLDING.volume, SoundType.SCAFFOLDING.pitch,
                    SoundType.SCAFFOLDING.getBreakSound(), SoundType.SCAFFOLDING.getStepSound(),
                    SoundType.WOOD.getPlaceSound(), SoundType.SCAFFOLDING.getHitSound(),
                    SoundType.SCAFFOLDING.getFallSound());

    // The unprocessed frame — has a BlockItem so it can be placed by the player.
    public static final RegistryObject<MillstoneFrameBlock> MILLSTONE_FRAME = registerBlockWithItem(
            "millstone_frame_full",
            () -> new MillstoneFrameBlock(
                    BlockBehaviour.Properties.copy(Blocks.BAMBOO_PLANKS).instabreak().noOcclusion()
                            .sound(SoundType.SCAFFOLDING)));

    // One processed frame block per wood type, keyed by VanillaWood enum.
    // These have no BlockItem — the player gets back the unprocessed frame on pick-block.
    public static final EnumMap<VanillaWood, RegistryObject<MillstoneProcessedFrameBlock>> PROCESSED_MILLSTONE_FRAME =
            CommonHelper.mapOfKeys(VanillaWood.class,
                    vanillaWood -> registerBlock("wood/millstone_frame/full/" + vanillaWood.getSerializedName(),
                            () -> new MillstoneProcessedFrameBlock(vanillaWood,
                                    BlockBehaviour.Properties.copy(MILLSTONE_FRAME.get()).sound(FRAME_SOUND))));

    // Registers a block without a corresponding item.
    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        return BLOCKS.register(name, block);
    }

    // Registers a block and automatically creates a plain BlockItem for it.
    private static <T extends Block> RegistryObject<T> registerBlockWithItem(String name, Supplier<T> block) {
        RegistryObject<T> blockRegistryObject = BLOCKS.register(name, block);
        registerBlockItem(name, blockRegistryObject);
        return blockRegistryObject;
    }

    private static <T extends Block> void registerBlockItem(String name, RegistryObject<T> block) {
        DeReMetallicaItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }
}
