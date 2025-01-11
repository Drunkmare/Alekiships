package com.alekiponi.alekiships.data.providers.models;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.common.item.AlekiShipsItems;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.CheckReturnValue;
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
import java.util.function.Supplier;

public class AlekiShipsItemModelProvider extends ItemModelProvider {

    public AlekiShipsItemModelProvider(final PackOutput output, final ExistingFileHelper existingFileHelper) {
        super(output, AlekiShips.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        this.iconWithHeldModel(AlekiShipsItems.CANNON);
        this.basicItem(AlekiShipsItems.CANNONBALL.get());
        this.iconWithHeldModel(AlekiShipsItems.ANCHOR);
        this.basicItem(AlekiShipsItems.SLOOP_ICON_ONLY.get());
        this.basicItem(AlekiShipsItems.ROWBOAT_ICON_ONLY.get());
        this.basicItem(AlekiShipsItems.MUSIC_DISC_PIRATE_CRAFTING.get());
        this.iconWithHeldModel(AlekiShipsItems.OAR);
    }

    @CanIgnoreReturnValue
    private SeparateTransformsModelBuilder<ItemModelBuilder> iconWithHeldModel(final Supplier<? extends Item> item) {
        return this.iconWithHeldModel(item.get());
    }

    @CanIgnoreReturnValue
    private SeparateTransformsModelBuilder<ItemModelBuilder> iconWithHeldModel(final Item item) {
        return this.iconWithHeldModel(Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(item)));
    }

    @CanIgnoreReturnValue
    private SeparateTransformsModelBuilder<ItemModelBuilder> iconWithHeldModel(final ResourceLocation item) {
        return this.iconWithHeldModel(item, getExistingFile(item.withPrefix(ITEM_FOLDER + "/held/")));
    }

    @CanIgnoreReturnValue
    @SuppressWarnings("unused")
    private SeparateTransformsModelBuilder<ItemModelBuilder> iconWithHeldModel(final Supplier<? extends Item> item,
            final ModelFile heldModel) {
        return this.iconWithHeldModel(item.get(), heldModel);
    }

    @CanIgnoreReturnValue
    private SeparateTransformsModelBuilder<ItemModelBuilder> iconWithHeldModel(final Item item,
            final ModelFile heldModel) {
        return this.iconWithHeldModel(Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(item)), heldModel);
    }

    @CanIgnoreReturnValue
    private SeparateTransformsModelBuilder<ItemModelBuilder> iconWithHeldModel(final ResourceLocation item,
            final ModelFile heldModel) {
        return this.iconWithHeldModel(item, heldModel, this.icon(item));
    }

    @CanIgnoreReturnValue
    private SeparateTransformsModelBuilder<ItemModelBuilder> iconWithHeldModel(final ResourceLocation item,
            final ModelFile heldModel, final ModelFile iconModel) {
        return this.getTransformedItemModelBuilder(item).base(nested().parent(heldModel))
                .perspective(ItemDisplayContext.GUI, nested().parent(iconModel))
                .perspective(ItemDisplayContext.GROUND, nested().parent(iconModel))
                .perspective(ItemDisplayContext.FIXED, nested().parent(iconModel));
    }

    @CanIgnoreReturnValue
    @SuppressWarnings("unused")
    private ItemModelBuilder icon(final Item item) {
        return this.icon(Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(item)));
    }

    @CanIgnoreReturnValue
    private ItemModelBuilder icon(final ResourceLocation item) {
        return this.getBuilder(item.withPrefix("item/icon/").toString())
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", item.withPrefix("item/icon/"));
    }

    @CheckReturnValue
    @SuppressWarnings("unused")
    private SeparateTransformsModelBuilder<ItemModelBuilder> getTransformedItemModelBuilder(
            final Supplier<? extends Item> item) {
        return this.getTransformedItemModelBuilder(item.get());
    }

    @CheckReturnValue
    private SeparateTransformsModelBuilder<ItemModelBuilder> getTransformedItemModelBuilder(final Item item) {
        return this.getTransformedItemModelBuilder(Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(item)));
    }

    @CheckReturnValue
    private SeparateTransformsModelBuilder<ItemModelBuilder> getTransformedItemModelBuilder(
            final ResourceLocation key) {
        return this.getBuilder(key.toString())
                .parent(this.getExistingFile(ResourceLocation.fromNamespaceAndPath("neoforge", "item/default")))
                .customLoader(SeparateTransformsModelBuilder::begin);
    }
}