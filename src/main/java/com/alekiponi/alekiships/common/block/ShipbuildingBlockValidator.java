package com.alekiponi.alekiships.common.block;

import com.alekiponi.alekiships.util.BoatMaterial;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class ShipbuildingBlockValidator {

    private AngledWoodenBoatFrameBlock.ConstantDirection constantDirection = null;

    private AngledWoodenBoatFrameBlock.ConstantShape constantShape = null;

    private boolean flat = false;

    private boolean validatingThisBlock = false;
    private boolean shouldDestroyAbove = false;

    @Nullable
    private Direction direction = null;

    /**
     * Use this constructor for inner/outer angled frames
     */
    ShipbuildingBlockValidator(AngledWoodenBoatFrameBlock.ConstantShape constantShape,
            AngledWoodenBoatFrameBlock.ConstantDirection constantDirection) {
        this.constantShape = constantShape;
        this.constantDirection = constantDirection;
        this.validatingThisBlock = true;
    }

    ShipbuildingBlockValidator(AngledWoodenBoatFrameBlock.ConstantShape constantShape,
            AngledWoodenBoatFrameBlock.ConstantDirection constantDirection, boolean shouldDestroyAbove) {
        this.constantShape = constantShape;
        this.constantDirection = constantDirection;
        this.validatingThisBlock = true;
        this.shouldDestroyAbove = shouldDestroyAbove;
    }

    /**
     * Use this constructor for flat frames or for blocks we don't need to care about (false)
     */
    ShipbuildingBlockValidator(boolean flat) {
        this.flat = flat;
        this.validatingThisBlock = flat;
    }

    /**
     * Use this constructor for straight angled frames
     */
    ShipbuildingBlockValidator(Direction direction) {
        this.constantShape = AngledBoatFrameBlock.ConstantShape.STRAIGHT;
        this.direction = direction;
        this.validatingThisBlock = true;
    }

    ShipbuildingBlockValidator(Direction direction, boolean shouldDestroyAbove) {
        this.constantShape = AngledBoatFrameBlock.ConstantShape.STRAIGHT;
        this.direction = direction;
        this.validatingThisBlock = true;
        this.shouldDestroyAbove = shouldDestroyAbove;
    }

    public boolean shouldDestroy() {
        return validatingThisBlock;
    }

    public boolean shouldDestroyAbove() {
        return shouldDestroyAbove;
    }

    public boolean validate(final BlockState blockState, final Direction structureDirection,
            final BoatMaterial boatMaterial) {
        if (!validatingThisBlock) return true;

        if (!(blockState.getBlock() instanceof BoatFrame boatFrame)) return false;

        if (boatFrame.getBoatMaterial() != boatMaterial) return false;

        if (boatMaterial.withstandsLava()) return false;

        if (boatFrame instanceof FlatBoatFrameBlock) return ProcessedBoatFrame.isFullyProcessed(blockState);

        if (!(blockState.getBlock() instanceof AngledBoatFrameBlock)) return false;

        final AngledBoatFrameBlock.ConstantDirection localConstantDirection = AngledBoatFrameBlock.ConstantDirection.getConstantDirection(
                blockState);
        if (localConstantDirection == null) return false;

        // straight validation
        if (this.constantShape == AngledBoatFrameBlock.ConstantShape.STRAIGHT) {
            if (direction == null) return false;

            if (AngledBoatFrameBlock.ConstantShape.getConstantShape(
                    blockState) != AngledBoatFrameBlock.ConstantShape.STRAIGHT) {
                return false;
            }
            final Direction rotatedDirection = switch (structureDirection) {
                case SOUTH -> this.direction.getOpposite();
                case EAST -> this.direction.getClockWise();
                case WEST -> this.direction.getCounterClockWise();
                default -> this.direction;
            };
            return blockState.getValue(
                    AngledBoatFrameBlock.FACING) == rotatedDirection && ProcessedBoatFrame.isFullyProcessed(blockState);
        }

        // angled validation
        if (!ProcessedBoatFrame.isFullyProcessed(blockState)) return false;

        final AngledWoodenBoatFrameBlock.ConstantDirection rotatedValidatorDirection = AngledBoatFrameBlock.ConstantDirection.rotateConstantDirection(
                this.constantDirection, structureDirection);
        if (localConstantDirection != rotatedValidatorDirection) return false;

        return this.constantShape == AngledBoatFrameBlock.ConstantShape.getConstantShape(blockState);
    }

}
