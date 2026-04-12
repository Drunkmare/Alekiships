package com.drunkmare.deremet.data.providers;

import com.alekiponi.alekiships.data.DataGenHelper;
import com.alekiponi.alekiships.data.SmartLanguageProvider;
import com.drunkmare.deremet.DeReMetallica;
import com.drunkmare.deremet.common.block.DeReMetallicaBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;

import java.util.Collections;
import java.util.Locale;

// Generates the en_us lang file. Add entries here whenever a new block, item, or
// translatable component is added to the mod.
public class DeReMetallicaLanguageProvider extends SmartLanguageProvider {

    public DeReMetallicaLanguageProvider(final PackOutput output) {
        super(output, DeReMetallica.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations() {
        // One entry per wood variant, e.g. "Oak Flat Millstone Scaffolding".
        DeReMetallicaBlocks.PROCESSED_MILLSTONE_FRAME.forEach((wood, registryObject) -> this.addBlock(registryObject,
                String.format(Locale.ROOT, "%s Flat Millstone Scaffolding",
                        DataGenHelper.langify(wood.getSerializedName()))));

        this.addBlock(DeReMetallicaBlocks.MILLSTONE_FRAME, "Millstone Scaffolding");
        this.add("creativetab.de_re_metallica_tab", "Millstone Scaffolding");
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return DeReMetallicaBlocks.BLOCKS.getEntries().stream().map(RegistryObject::get)::iterator;
    }

    @Override
    protected Iterable<Item> getKnownItems() {
        return Collections.emptyList();
    }

    @Override
    protected Iterable<EntityType<?>> getKnownEntityTypes() {
        return Collections.emptyList();
    }
}
