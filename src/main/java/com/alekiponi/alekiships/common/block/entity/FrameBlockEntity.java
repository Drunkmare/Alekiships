package com.alekiponi.alekiships.common.block.entity;

import com.alekiponi.alekiships.client.model.DynamicTextureModelData;
import com.alekiponi.alekiships.common.AlekiShipsRegistries;
import com.alekiponi.alekiships.util.AlekiShipsExtraCodecs;
import com.alekiponi.alekiships.util.FrameMaterial;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import net.neoforged.neoforge.client.model.data.ModelData;

import java.util.function.Consumer;
import org.jetbrains.annotations.UnknownNullability;
import lombok.Getter;

public final class FrameBlockEntity extends BlockEntity {

    private final NonNullList<ItemStack> stacks = NonNullList.create();
    @Getter
    @UnknownNullability // This should in actuality never be null but static analysis can't tell
    private Holder<FrameMaterial> frameMaterial;

    public FrameBlockEntity(final BlockEntityType<? extends FrameBlockEntity> type, final BlockPos pos,
            final BlockState blockState) {
        super(type, pos, blockState);
    }

    public FrameBlockEntity(final BlockPos pos, final BlockState state) {
        this(AlekishipsBlockEntities.FRAME_BLOCK.get(), pos, state);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(final HolderLookup.Provider registries) {
        return this.saveCustomOnly(registries);
    }

    /**
     * @param stack The stack
     */
    public void addStack(final ItemStack stack) {
        this.stacks.add(stack);
        this.setChanged();
    }

    /**
     * @return The removed stack
     */
    public ItemStack removeStack() {
        if (!this.stacks.isEmpty()) {
            final var itemStack = this.stacks.removeLast();
            this.setChanged();
            return itemStack;
        }
        return ItemStack.EMPTY;
    }

    /**
     * Remove all stacks passing them into the consumer
     */
    public void removeAllStacks(final Consumer<ItemStack> consumer) {
        this.stacks.forEach(consumer);
        this.stacks.clear();
        this.setChanged();
    }

    /**
     * @return The frame material
     */
    public FrameMaterial getMaterial() {
        return this.frameMaterial.value();
    }

    public ItemStack getPickedItemStack() {
        if (this.stacks.isEmpty()) return ItemStack.EMPTY;
        return this.stacks.getFirst();
    }

    public void setFrameMaterial(final Holder<FrameMaterial> frameMaterial) {
        this.frameMaterial = frameMaterial;
        this.requestModelDataUpdate();
    }

    @Override
    public void setLevel(final Level level) {
        super.setLevel(level);
        final var frameMaterials = level.registryAccess().registryOrThrow(AlekiShipsRegistries.FRAME_MATERIAL);
        this.frameMaterial = frameMaterials.getHolder(FrameMaterial.DEFAULT).or(frameMaterials::getAny).orElseThrow();
    }

    @Override
    public ModelData getModelData() {
        return ModelData.of(DynamicTextureModelData.PROPERTY,
                new DynamicTextureModelData(this.getMaterial().texture()));
    }

    @Override
    protected void saveAdditional(final CompoundTag tag, final HolderLookup.Provider registries) {
        final ListTag stacks = new ListTag();
        for (final var material : this.stacks) {
            stacks.add(material.save(registries));
        }
        tag.put("stacks", stacks);

        AlekiShipsExtraCodecs.save(FrameMaterial.CODEC, registries.createSerializationContext(NbtOps.INSTANCE),
                this.frameMaterial, t -> tag.put("material", t));

        super.saveAdditional(tag, registries);
    }

    @Override
    protected void loadAdditional(final CompoundTag tag, final HolderLookup.Provider registries) {
        this.stacks.clear();
        final ListTag stacks = tag.getList("stacks", Tag.TAG_COMPOUND);
        for (int i = 0; i < stacks.size(); i++) {
            this.stacks.add(ItemStack.parse(registries, stacks.getCompound(i)).orElse(ItemStack.EMPTY));
        }
        final var frameMaterial = tag.get("material");
        if (frameMaterial != null) {
            AlekiShipsExtraCodecs.load(FrameMaterial.CODEC, registries.createSerializationContext(NbtOps.INSTANCE),
                    frameMaterial, this::setFrameMaterial);
        }
        super.loadAdditional(tag, registries);
    }
}