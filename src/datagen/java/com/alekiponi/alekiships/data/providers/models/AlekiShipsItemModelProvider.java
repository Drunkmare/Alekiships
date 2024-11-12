package com.alekiponi.alekiships.data.providers.models;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.common.item.AlekiShipsItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.loaders.SeparateTransformsModelBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.Objects;

public class AlekiShipsItemModelProvider extends ItemModelProvider {

    public AlekiShipsItemModelProvider(final PackOutput output, final ExistingFileHelper existingFileHelper) {
        super(output, AlekiShips.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        //this.heldItem(AlekiShipsItems.CANNON.get());
        this.basicItem(AlekiShipsItems.CANNONBALL.get());
        //this.heldItem(AlekiShipsItems.ANCHOR.get());
        this.basicItem(AlekiShipsItems.SLOOP_ICON_ONLY.get());
        this.basicItem(AlekiShipsItems.ROWBOAT_ICON_ONLY.get());
        this.basicItem(AlekiShipsItems.MUSIC_DISC_PIRATE_CRAFTING.get());
        //this.heldItem(AlekiShipsItems.OAR.get());
    }

    public ItemModelBuilder heldItem(Item item) {
        return heldItem(Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(item)));
    }

    public ItemModelBuilder itemNoTexture(ResourceLocation item) {
        return getBuilder(item.toString()).parent(new ModelFile.UncheckedModelFile("item/generated"));
    }

    public ItemModelBuilder heldItem(ResourceLocation item) {
        return SeparateTransformsModelBuilder.begin(this.itemNoTexture(
                                ResourceLocation.fromNamespaceAndPath(item.getNamespace(), "held/" + item.getPath())),
                        existingFileHelper).base(getBuilder(item.toString()).parent(new ModelFile.UncheckedModelFile("held/")))
                .perspective(ItemDisplayContext.FIXED, iconItem(item))
                .perspective(ItemDisplayContext.GROUND, iconItem(item))
                .perspective(ItemDisplayContext.GUI, iconItem(item)).end();
    }

    public ItemModelBuilder iconItem(ResourceLocation item) {
        return getBuilder(item.toString()).parent(this.itemNoTexture(
                ResourceLocation.fromNamespaceAndPath(item.getNamespace(), "icon/" + item.getPath())));
    }
}