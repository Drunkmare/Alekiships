package com.drunkmare.deremet.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

/**
 * Abstract base for all unprocessed machine frame blocks.
 *
 * Handles the shared boilerplate: waterlogging, full-cube shape, and the
 * right-click conversion trigger. Subclasses only need to supply:
 *   - {@link #getConversionItem()}     — which item starts the build process
 *   - {@link #createProcessedState()}  — what block state to place on conversion
 *
 * To add a new machine frame, extend this class and register a pair of blocks
 * (unprocessed + processed) in {@link DeReMetallicaBlocks}.
 */
public abstract class MachineFrameBlock extends Block implements SimpleWaterloggedBlock {

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    protected static final VoxelShape FULL_SHAPE = Block.box(0, 0, 0, 16, 16, 16);

    public MachineFrameBlock(final Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(WATERLOGGED, false));
    }

    // -------------------------------------------------------------------------
    // Abstract API — implement these in each concrete frame subclass
    // -------------------------------------------------------------------------

    /** The item that, when held and right-clicked, converts this frame to its processed form. */
    protected abstract Item getConversionItem();

    /**
     * Builds the initial {@link BlockState} for the processed form of this frame.
     *
     * @param blockPos   position being converted (used to compute things like cogwheel offset)
     * @param blockState current unprocessed state (copy WATERLOGGED etc. from here)
     */
    protected abstract BlockState createProcessedState(BlockPos blockPos, BlockState blockState);

    // -------------------------------------------------------------------------
    // Block behaviour
    // -------------------------------------------------------------------------

    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED);
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(final BlockState blockState, final Level level, final BlockPos blockPos,
                                 final Player player, final InteractionHand hand, final BlockHitResult hitResult) {

        if (hand != InteractionHand.MAIN_HAND) return InteractionResult.PASS;

        final ItemStack heldStack = player.getItemInHand(hand);

        // Only the designated conversion item starts the build process.
        if (!heldStack.is(getConversionItem())) return InteractionResult.PASS;

        level.setBlockAndUpdate(blockPos, createProcessedState(blockPos, blockState));

        if (!player.getAbilities().instabuild) heldStack.shrink(1);

        level.playSound(player, blockPos, SoundEvents.STONE_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
        return InteractionResult.SUCCESS;
    }

    // Keeps the waterlogged fluid ticking when a neighbouring block updates.
    @Override
    @SuppressWarnings("deprecation")
    public BlockState updateShape(final BlockState blockState, final Direction direction,
                                  final BlockState neighborState, final LevelAccessor levelAccessor,
                                  final BlockPos blockPos, final BlockPos neighborPos) {

        if (blockState.getValue(WATERLOGGED)) {
            levelAccessor.scheduleTick(blockPos, Fluids.WATER, Fluids.WATER.getTickDelay(levelAccessor));
        }
        return super.updateShape(blockState, direction, neighborState, levelAccessor, blockPos, neighborPos);
    }

    // Frame is not a full opaque cube, so don't use it for light occlusion.
    @Override
    @SuppressWarnings("deprecation")
    public boolean useShapeForLightOcclusion(final BlockState pState) {
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(final BlockState blockState, final BlockGetter blockGetter, final BlockPos blockPos,
                               final CollisionContext collisionContext) {
        return FULL_SHAPE;
    }

    @Override
    @SuppressWarnings("deprecation")
    public FluidState getFluidState(final BlockState blockState) {
        return blockState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(blockState);
    }

    // Set WATERLOGGED on placement if placed inside water.
    @Nullable
    @Override
    public BlockState getStateForPlacement(final BlockPlaceContext placeContext) {
        final FluidState fluidState = placeContext.getLevel().getFluidState(placeContext.getClickedPos());
        return this.defaultBlockState().setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isPathfindable(final BlockState blockState, final BlockGetter blockGetter, final BlockPos blockPos,
                                  final PathComputationType computationType) {
        return computationType == PathComputationType.WATER && blockGetter.getFluidState(blockPos).is(FluidTags.WATER);
    }
}
