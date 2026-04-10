package com.alekiponi.alekiships.common.block;
import com.alekiponi.alekiships.util.VanillaWood;
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


    /**
     * super(properties) — passes the block properties (hardness, sound, material etc.) up to Minecraft's base Block class to handle.
     * this.registerDefaultState(...setValue(WATERLOGGED, false)) —
     * sets the default block state so that whenever this block is placed,
     * it starts as not waterlogged unless the placement logic overrides it.
     * Without this, the waterlogged state could default to an unpredictable value.
     */
    public MillstoneFrameBlock(final Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(WATERLOGGED, false));
    }

    /**
     * Registers a mapping of the passed in {@link Item} instance and the passed in {@link BoatFrame}
     * A given {@link Item} instance may only map to one {@link BoatFrame} instance but multiple
     * {@link Item}s can map to the same {@link BoatFrame}.
     */
    public final void registerFrame(final Item item, final BoatFrame boatFrame) {
        assert boatFrame instanceof Block : "Registered Frames must be implemented on a Block";
        boatFrames.put(item, boatFrame);
    }

    /**
     * Tells Minecraft which block state properties this block uses.
     * By calling builder.add(WATERLOGGED), it registers the waterlogged boolean as the one property this block tracks.
     * Minecraft uses this to generate every valid combination of states for the block —
     * in this case just waterlogged=true and waterlogged=false. If you forget to add a property here,
     * Minecraft won't know it exists and it'll crash or silently break.
     */
    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED);
    }

    /**
     * This is the right-click handler. Step by step:
     * javafinal ItemStack heldStack = player.getItemInHand(hand);
     * Gets whatever item the player is currently holding.
     *
     * javafinal BoatFrame frameBlock = getFrame(heldStack.getItem());
     * if (frameBlock == null) return InteractionResult.PASS;
     * Looks up whether the held item has a registered frame. If not, returns PASS —
     * meaning "I didn't handle this, let the game do whatever it would normally do."
     *
     * javafinal BlockState frameBlockstate = frameBlock.withPropertiesOf(blockState);
     * level.setBlockAndUpdate(blockPos, frameBlockstate);
     * Gets the new frame's block state, copying properties (like WATERLOGGED)
     * from the current block so nothing is lost in the swap. Then replaces the block in the world with the new frame.
     *
     * javaif (!player.getAbilities().instabuild) heldStack.shrink(1);
     * Consumes one item from the player's hand, unless they're in creative mode (instabuild).
     *
     * javafinal SoundType soundType = frameBlockstate.getSoundType(level, blockPos, player);
     * level.playSound(player, blockPos, soundType.getPlaceSound(), SoundSource.BLOCKS,
     *         (soundType.getVolume() + 1) / 2, soundType.getPitch() * 0.8F);
     * Plays the new block's placement sound. The volume formula (volume + 1) / 2 softens it slightly,
     * and pitch * 0.8F makes it slightly deeper than a normal placement sound.
     *
     * javareturn InteractionResult.SUCCESS;
     * Tells the game the interaction was handled successfully, preventing anything else from triggering.
     */


    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(final BlockState blockState, final Level level, final BlockPos blockPos,
                                 final Player player, final InteractionHand hand, final BlockHitResult hitResult) {

        if (hand != InteractionHand.MAIN_HAND) return InteractionResult.PASS;

        final ItemStack heldStack = player.getItemInHand(hand);

        if (!heldStack.is(TFCBlocks.QUERN.get().asItem())) return InteractionResult.PASS;

        final BlockState newState = AlekiShipsBlocks.PROCESSED_MILLSTONE_FRAME.get(VanillaWood.OAK).get()
                .defaultBlockState()
                .setValue(WATERLOGGED, blockState.getValue(WATERLOGGED));

        level.setBlockAndUpdate(blockPos, newState);

        if (!player.getAbilities().instabuild) heldStack.shrink(1);

        level.playSound(player, blockPos, SoundEvents.STONE_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
        return InteractionResult.SUCCESS;
    }

    /**
     * This method is called whenever a neighboring block changes (placed, broken, updated etc.).
     * javaif (blockState.getValue(WATERLOGGED)) {
     *     levelAccessor.scheduleTick(blockPos, Fluids.WATER, Fluids.WATER.getTickDelay(levelAccessor));
     * }
     * If the block is currently waterlogged, it schedules a water tick. This is what keeps water behaving correctly when neighbors change
     * — for example, if a waterlogged block is placed next to air, this ensures the water flows out properly. Without this,
     * waterlogging would visually work but the water fluid simulation would break.
     * javareturn super.updateShape(blockState, direction, neighborState, levelAccessor, blockPos, neighborPos);
     * Passes the call up to the base Block class to handle everything else normally —
     * this method only adds the waterlogging logic on top, it doesn't replace the default neighbor update behavior.
     */
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

    /**
     * useShapeForLightOcclusion()
     * Returns true to tell the engine to use the block's actual shape (HALF_SHAPE) when calculating light blocking,
     * rather than assuming it's a full cube. Without this, the half-height block would incorrectly block light as if it were a solid full block.
     */
    @Override
    @SuppressWarnings("deprecation")
    public boolean useShapeForLightOcclusion(final BlockState pState) {
        return false;
    }

    /**
     * getShape()
     * Returns FULL_SHAPE — the Block.box(0, 0, 0, 16, 16, 16) defined at the top of the class.
     * This controls both the visible outline when you hover over the block and its collision box.
     * The Full-height shape makes it act like a normal block physically.
     */
    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(final BlockState blockState, final BlockGetter blockGetter, final BlockPos blockPos,
                               final CollisionContext collisionContext) {
        return FULL_SHAPE;
    }

    /**
     * getFluidState()
     * Controls what fluid exists inside the block. If waterlogged, returns a still water source (false meaning non-flowing).
     * If not waterlogged, falls back to the default which returns empty.
     * This is what makes water actually render inside the block and behave as a water source block.
     */
    @Override
    @SuppressWarnings("deprecation")
    public FluidState getFluidState(final BlockState blockState) {
        return blockState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(blockState);
    }

    /**
     * getStateForPlacement()
     * Called the moment the block is placed.
     * It checks whether the position being placed into already contains water
     * — if it does, the block is placed in its waterlogged state automatically.
     * This is what makes it seamlessly slot into existing water without needing to manually waterlog it.
     */
    @Nullable
    @Override
    public BlockState getStateForPlacement(final BlockPlaceContext placeContext) {
        final FluidState fluidState = placeContext.getLevel().getFluidState(placeContext.getClickedPos());
        return this.defaultBlockState().setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
    }

    /**
     * isPathfindable()
     * Controls whether mobs can pathfind through this block.
     * Returns true only when the pathfinding type is WATER and the block actually contains water
     * — so aquatic mobs like fish can swim through it when waterlogged. For land or air pathfinding it always returns false,
     * treating it as a solid obstacle.
     */
    @Override
    @SuppressWarnings("deprecation")
    public boolean isPathfindable(final BlockState blockState, final BlockGetter blockGetter, final BlockPos blockPos,
                                  final PathComputationType computationType) {
        return computationType == PathComputationType.WATER && blockGetter.getFluidState(blockPos).is(FluidTags.WATER);
    }
}