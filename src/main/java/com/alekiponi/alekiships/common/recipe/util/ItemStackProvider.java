package com.alekiponi.alekiships.common.recipe.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.function.Supplier;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

/**
 * An {@link ItemStack} {@link Supplier} which always returns a new stack from {@link #get()}
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ItemStackProvider implements Supplier<ItemStack> {

    public static final Codec<ItemStackProvider> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    ItemStack.ITEM_NON_AIR_CODEC.fieldOf("id").forGetter(stackProvider -> stackProvider.get().getItemHolder()),
                    ExtraCodecs.intRange(1, 99)
                            .optionalFieldOf("count", 1)
                            .forGetter(stackProvider -> stackProvider.get().getCount()),
                    DataComponentPatch.CODEC.optionalFieldOf("components", DataComponentPatch.EMPTY)
                            .forGetter(stackProvider -> stackProvider.get().getComponentsPatch()))
            .apply(instance, ItemStackProvider::of));

    public static final StreamCodec<RegistryFriendlyByteBuf, ItemStackProvider> STREAM_CODEC = ItemStack.STREAM_CODEC.map(
            ItemStackProvider::ofTrusted, ItemStackProvider::get);

    private static final int PRIME = 31;

    private final Supplier<ItemStack> stackSupplier;

    /**
     * @param itemStack An {@link ItemStack} known to be safe from mutation
     */
    private static ItemStackProvider ofTrusted(final ItemStack itemStack) {
        // We must copy the stack when we hand it off
        return new ItemStackProvider(itemStack::copy);
    }

    /**
     * @param itemStack The {@link ItemStack} to return.
     *
     * @implNote A copy of the stack is made to prevent unexpected mutations
     * @see #of(ItemLike)
     * @see #of(ItemLike, int)
     * @see #of(Holder, int, DataComponentPatch)
     */
    public static ItemStackProvider of(final ItemStack itemStack) {
        // Copy to ensure the stack isn't externally mutated by callie
        return ofTrusted(itemStack.copy());
    }

    /**
     * @param item The item
     *
     * @implNote A new stack is created each time {@link #get()} is called
     * @see #of(ItemLike, int)
     * @see #of(Holder, int, DataComponentPatch)
     */
    public static ItemStackProvider of(final ItemLike item) {
        return new ItemStackProvider(() -> new ItemStack(item));
    }

    /**
     * @param item  The item
     * @param count The count
     *
     * @implNote A new stack is created each time {@link #get()} is called
     * @see #of(ItemLike)
     * @see #of(Holder, int, DataComponentPatch)
     */
    public static ItemStackProvider of(final ItemLike item, final int count) {
        return new ItemStackProvider(() -> new ItemStack(item, count));
    }

    /**
     * @param item       The item
     * @param count      The count
     * @param components The components
     *
     * @implNote A new stack is created each time {@link #get()} is called
     * @see #of(ItemLike)
     * @see #of(ItemLike, int)
     */
    public static ItemStackProvider of(final Holder<Item> item, final int count, final DataComponentPatch components) {
        return new ItemStackProvider(() -> new ItemStack(item, count, components));
    }

    /**
     * Get a new {@link ItemStack}
     *
     * @return A new {@link ItemStack}
     */
    @Override
    public ItemStack get() {
        return this.stackSupplier.get();
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof final ItemStackProvider other)) return false;
        return ItemStack.matches(this.get(), other.get());
    }


    @Override
    public int hashCode() {
        final var stack = this.get();
        final int i = PRIME + stack.getItem().hashCode() + stack.getCount();
        return PRIME * i + stack.getComponents().hashCode();
    }

    @Override
    public String toString() {
        return this.get().toString();
    }
}