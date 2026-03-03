package com.alekiponi.alekiships.mixins.client.accessors;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.renderer.block.model.BlockElement;

import net.neoforged.neoforge.client.model.ElementsModel;

import java.util.List;

@Mixin(ElementsModel.class)
public interface ElementsModelAccessor {
    @Accessor
    List<BlockElement> getElements();
}