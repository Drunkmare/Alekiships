package com.alekiponi.alekiships.common.item;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.common.block.AlekiShipsBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class AlekiShipsTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(
            Registries.CREATIVE_MODE_TAB, AlekiShips.MOD_ID);


    public static final RegistryObject<CreativeModeTab> ALEKISHIPS_TAB = CREATIVE_MODE_TABS.register("alekiships_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(AlekiShipsItems.ANCHOR.get()))
                    .title(Component.translatable("creativetab.alekiships_tab")).displayItems((pParameters, pOutput) -> {

                        pOutput.accept(AlekiShipsItems.OAR.get());
                        pOutput.accept(AlekiShipsBlocks.OARLOCK.get());
                        pOutput.accept(AlekiShipsBlocks.CLEAT.get());
                        pOutput.accept(AlekiShipsItems.ANCHOR.get());

                        pOutput.accept(AlekiShipsItems.CANNON.get());
                        pOutput.accept(AlekiShipsItems.CANNONBALL.get());

                        pOutput.accept(AlekiShipsBlocks.BOAT_FRAME_ANGLED.get());
                        pOutput.accept(AlekiShipsBlocks.BOAT_FRAME_FLAT.get());

                        pOutput.accept(AlekiShipsItems.SMALL_TRIANGULAR_SAIL.get());
                        pOutput.accept(AlekiShipsItems.MEDIUM_TRIANGULAR_SAIL.get());
                        pOutput.accept(AlekiShipsItems.LARGE_TRIANGULAR_SAIL.get());

                    }).build());
}