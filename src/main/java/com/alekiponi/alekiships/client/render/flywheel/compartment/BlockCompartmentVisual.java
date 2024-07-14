package com.alekiponi.alekiships.client.render.flywheel.compartment;

import com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.AbstractCompartmentEntity;
import com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.BlockCompartment;
import com.jozufozu.flywheel.api.instance.Instancer;
import com.jozufozu.flywheel.api.visualization.VisualizationContext;
import com.jozufozu.flywheel.lib.instance.TransformedInstance;
import com.jozufozu.flywheel.lib.model.Models;
import com.jozufozu.flywheel.lib.visual.SimpleTickableVisual;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BlockCompartmentVisual<Compartment extends AbstractCompartmentEntity & BlockCompartment> extends CompartmentVisual<Compartment> implements SimpleTickableVisual {

    private BlockState lastBlockState;
    private TransformedInstance blockModel;

    public BlockCompartmentVisual(final VisualizationContext ctx, final Compartment entity) {
        super(ctx, entity);
    }

    @Override
    public void init(final float partialTick) {
        this.lastBlockState = this.entity.getDisplayBlockState();
        this.blockModel = this.getBlockInstance(this.lastBlockState).createInstance();

        super.init(partialTick);
    }

    @Override
    protected final void updateContents(final PoseStack poseStack, final float partialTick) {
        this.updateLight();
        this.updateBlockModel(this.blockModel, this.lastBlockState, poseStack, partialTick);
    }

    protected void updateBlockModel(final TransformedInstance model, final BlockState blockState,
            final PoseStack poseStack, final float partialTick) {
        model.setTransform(poseStack).setChanged();
    }

    @Override
    public void tick() {
        final BlockState blockState = this.entity.getDisplayBlockState();

        if (blockState != this.lastBlockState) {
            this.lastBlockState = blockState;
            this.getBlockInstance(this.lastBlockState).stealInstance(this.blockModel);
        }

        this.updateLight();
    }

    protected void updateLight() {
        this.relight(this.entity.blockPosition(), this.blockModel);
    }

    private Instancer<TransformedInstance> getBlockInstance(final BlockState blockState) {
        return this.getTransformedInstance(Models.block(blockState));
    }

    @Override
    protected void _delete() {
        super._delete();
        this.blockModel.delete();
    }
}