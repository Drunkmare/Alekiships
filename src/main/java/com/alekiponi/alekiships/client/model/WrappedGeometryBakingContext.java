package com.alekiponi.alekiships.client.model;

import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;

import lombok.AllArgsConstructor;
import lombok.experimental.Delegate;

@AllArgsConstructor
public class WrappedGeometryBakingContext implements IGeometryBakingContext {
    @Delegate
    private final IGeometryBakingContext delegate;
}