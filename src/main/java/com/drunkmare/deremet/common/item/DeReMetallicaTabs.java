package com.drunkmare.deremet.common.item;

import com.drunkmare.deremet.DeReMetallica;
import com.drunkmare.deremet.common.block.DeReMetallicaBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class DeReMetallicaTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(
            Registries.CREATIVE_MODE_TAB, DeReMetallica.MOD_ID);

    public static final RegistryObject<CreativeModeTab> DE_RE_METALLICA_TAB = CREATIVE_MODE_TABS.register(
            "de_re_metallica_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("creativetab.de_re_metallica_tab"))
                    .icon(() -> DeReMetallicaBlocks.MILLSTONE_FRAME.get().asItem().getDefaultInstance())
                    .displayItems((parameters, output) -> output.accept(DeReMetallicaBlocks.MILLSTONE_FRAME.get()))
                    .build());
}
