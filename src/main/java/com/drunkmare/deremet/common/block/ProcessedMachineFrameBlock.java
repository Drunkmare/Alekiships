package com.drunkmare.deremet.common.block;

import com.alekiponi.alekiships.common.block.ProcessedBoatFrame;
import com.alekiponi.alekiships.util.BoatMaterial;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Abstract base for all processed machine frame blocks.
 *
 * A "processed" frame is the multi-stage block that appears after the player places
 * the conversion item on an unprocessed {@link MachineFrameBlock}. It tracks build
 * progress via an integer blockstate property and implements {@link ProcessedBoatFrame}
 * so AlekiShips' data-gen helpers work correctly.
 *
 * Waterlogging is inherited from {@link MachineFrameBlock}.
 *
 * Subclasses must implement:
 *   - {@link #getProcessingProperty()} — declare a {@code public static final IntegerProperty}
 *                                        on the subclass and return it here
 *   - {@link #getProcessingLimit()}    — inclusive upper bound of that property
 *   - {@link #handleInteraction()}     — the full item-by-item crafting step machine
 *   - {@link #getBaseFrame()}          — the unprocessed counterpart (for pick-block)
 *   - {@link #getBoatMaterial()}       — the wood type (used by AlekiShips datagen)
 *
 * Subclasses must also call {@code super.createBlockStateDefinition(builder.add(...))}
 * with their own properties so that WATERLOGGED is always included.
 */
public abstract class ProcessedMachineFrameBlock extends MachineFrameBlock implements ProcessedBoatFrame {

    // The wood variant this block represents (drives plank texture in datagen and loot tables).
    public final BoatMaterial boatMaterial;

    public ProcessedMachineFrameBlock(final BoatMaterial boatMaterial, final Properties properties) {
        super(properties);
        this.boatMaterial = boatMaterial;
    }

    // -------------------------------------------------------------------------
    // Abstract API — implement these in each concrete processed-frame subclass
    // -------------------------------------------------------------------------

    /**
     * The {@code frame_processed} IntegerProperty for this block.
     * Declare it as a {@code public static final} field on the subclass so that
     * datagen and blockstate code can reference it without needing an instance.
     */
    @Override
    public abstract IntegerProperty getProcessingProperty();

    /** Inclusive upper bound of {@link #getProcessingProperty()} (e.g. 7 for the millstone). */
    @Override
    public abstract int getProcessingLimit();

    /**
     * All item-interaction logic for this processed frame.
     * Only called for {@link InteractionHand#MAIN_HAND} — hand filtering is done in {@link #use}.
     *
     * @param blockState   current block state
     * @param level        world
     * @param blockPos     block position
     * @param player       the interacting player
     * @param heldStack    item in main hand (may be empty)
     * @param processState current value of the processing property
     */
    protected abstract InteractionResult handleInteraction(BlockState blockState, Level level, BlockPos blockPos,
                                                           Player player, ItemStack heldStack, int processState);

    /** Returns the unprocessed frame block — used by {@link #getCloneItemStack} for pick-block. */
    protected abstract MachineFrameBlock getBaseFrame();

    // -------------------------------------------------------------------------
    // Block behaviour
    // -------------------------------------------------------------------------

    // Dispatch right-click to the subclass interaction handler.
    @Override
    public InteractionResult use(final BlockState blockState, final Level level, final BlockPos blockPos,
                                 final Player player, final InteractionHand hand, final BlockHitResult hitResult) {

        if (hand != InteractionHand.MAIN_HAND) return InteractionResult.PASS;

        final ItemStack heldStack = player.getItemInHand(hand);
        final int processState = blockState.getValue(getProcessingProperty());

        return handleInteraction(blockState, level, blockPos, player, heldStack, processState);
    }

    // Pick-block returns the unprocessed frame item, not the processed block (which has no item).
    @Override
    @SuppressWarnings("deprecation")
    public ItemStack getCloneItemStack(final BlockGetter blockGetter, final BlockPos blockPos,
                                       final BlockState blockState) {
        return getBaseFrame().getCloneItemStack(blockGetter, blockPos, blockState);
    }

    // These two are unused on processed frames but are required by MachineFrameBlock's abstract contract.
    // They delegate to the base frame so no conversion can accidentally be triggered again.
    @Override
    protected Item getConversionItem() {
        return getBaseFrame().getConversionItem();
    }

    @Override
    protected BlockState createProcessedState(final BlockPos blockPos, final BlockState blockState) {
        return blockState; // no-op
    }
}
