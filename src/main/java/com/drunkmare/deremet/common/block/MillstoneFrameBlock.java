package com.drunkmare.deremet.common.block;

import com.alekiponi.alekiships.common.block.BoatFrame;
import com.drunkmare.deremet.util.VanillaWood;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
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
import java.util.IdentityHashMap;

public class MillstoneFrameBlock extends Block implements SimpleWaterloggedBlock {

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    protected static final VoxelShape FULL_SHAPE = Block.box(0, 0, 0, 16, 16, 16);
    private final IdentityHashMap<Item, BoatFrame> boatFrames = new IdentityHashMap<>();

    public MillstoneFrameBlock(final Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(WATERLOGGED, false));
    }

    public final void registerFrame(final Item item, final BoatFrame boatFrame) {
        assert boatFrame instanceof Block : "Registered Frames must be implemented on a Block";
        boatFrames.put(item, boatFrame);
    }

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

        if (!heldStack.is(TFCBlocks.QUERN.get().asItem())) return InteractionResult.PASS;

        final boolean cogwheelOffset = (Math.floorMod(blockPos.getX() + blockPos.getZ(), 2)) == 0;
        final BlockState newState = DeReMetallicaBlocks.PROCESSED_MILLSTONE_FRAME.get(VanillaWood.OAK).get()
                .defaultBlockState()
                .setValue(WATERLOGGED, blockState.getValue(WATERLOGGED))
                .setValue(MillstoneProcessedFrameBlock.COGWHEEL_OFFSET, cogwheelOffset);

        level.setBlockAndUpdate(blockPos, newState);

        if (!player.getAbilities().instabuild) heldStack.shrink(1);

        level.playSound(player, blockPos, SoundEvents.STONE_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
        return InteractionResult.SUCCESS;
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState updateShape(final BlockState blockState, final Direction direction,
                                  final BlockState neighborState, final LevelAccessor levelAccessor, final BlockPos blockPos,
                                  final BlockPos neighborPos) {

        if (blockState.getValue(WATERLOGGED)) {
            levelAccessor.scheduleTick(blockPos, Fluids.WATER, Fluids.WATER.getTickDelay(levelAccessor));
        }

        return super.updateShape(blockState, direction, neighborState, levelAccessor, blockPos, neighborPos);
    }

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
