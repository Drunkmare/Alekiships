package com.drunkmare.deremet.common.block;

import com.alekiponi.alekiships.util.BoatMaterial;
import com.alekiponi.alekiships.util.CommonHelper;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.items.TFCItems;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;

/**
 * The in-progress millstone frame — a multi-stage block the player builds up
 * by right-clicking with specific items in order.
 *
 * Build sequence (frame_processed value → action required):
 *
 *   0  — right-click with TFC Handstone     → consumes handstone, advances to 1
 *   1  — right-click with Create Cogwheel   → consumes cogwheel, advances to 2
 *   2  — right-click with TFC Hammer        → advances to 3
 *   3  — right-click with TFC Hammer        → advances to 4
 *   4  — right-click with TFC Hammer        → advances to 5  (fully hammered)
 *   5  — right-click with TFC Handstone     → consumes handstone, advances to 6
 *   6  — right-click with Oak Slab          → consumes slab, places Create Millstone
 *
 * Empty-hand right-click reverses the last step and refunds the consumed item.
 * Empty-hand on stage 0 reverts to the unprocessed frame and refunds the Quern.
 *
 * {@code COGWHEEL_OFFSET} is a checkerboard boolean set at conversion time so
 * adjacent frames' cogwheel models interlock visually.
 */
public class MillstoneProcessedFrameBlock extends ProcessedMachineFrameBlock {

    // Owned here as a static so datagen and loot table code can reference it without an instance.
    // Range is 0-7 (8 visible stages). NOT the same as AlekiShipsBlockStateProperties.FRAME_PROCESSED
    // which only goes 0-3 — we define our own to cover the full millstone build sequence.
    public static final IntegerProperty FRAME_PROCESSED = IntegerProperty.create("frame_processed", 0, 7);

    // Alternates the cogwheel model between two positions so adjacent fully-built
    // millstones mesh correctly. Set once at frame conversion time, never changed again.
    public static final BooleanProperty COGWHEEL_OFFSET = BooleanProperty.create("cogwheel_offset");

    // Step constants — see class-level javadoc for the full sequence.
    private static final int HANDSTONE_STEP_1 = 0;
    private static final int COGWHEEL_STEP    = 1;
    private static final int HANDSTONE_STEP_2 = 5;
    private static final int SLAB_STEP        = 6;
    private static final int FULLY_HAMMERED   = 5; // exclusive upper bound for hammer steps (2, 3, 4)
    public  static final int FULLY_PROCESSED  = 7; // public so loot table code can reference it

    public MillstoneProcessedFrameBlock(final BoatMaterial boatMaterial, final Properties properties) {
        super(boatMaterial, properties);
        this.registerDefaultState(this.defaultBlockState()
                .setValue(FRAME_PROCESSED, 0)
                .setValue(COGWHEEL_OFFSET, false));
    }

    // -------------------------------------------------------------------------
    // ProcessedMachineFrameBlock API
    // -------------------------------------------------------------------------

    @Override
    public IntegerProperty getProcessingProperty() {
        return FRAME_PROCESSED;
    }

    @Override
    public int getProcessingLimit() {
        return FULLY_PROCESSED;
    }

    @Override
    protected MachineFrameBlock getBaseFrame() {
        return DeReMetallicaBlocks.MILLSTONE_FRAME.get();
    }

    @Override
    public BoatMaterial getBoatMaterial() {
        return this.boatMaterial;
    }

    // -------------------------------------------------------------------------
    // Build sequence interaction handler
    // -------------------------------------------------------------------------

    @Override
    protected InteractionResult handleInteraction(final BlockState blockState, final Level level,
                                                  final BlockPos blockPos, final Player player,
                                                  final ItemStack heldStack, final int processState) {

        // Empty hand — undo the last step and refund the item.
        if (heldStack.isEmpty() && !level.isClientSide) {
            // Refund the item consumed at the previous step before decrementing.
            if (processState == 1) CommonHelper.giveItemToPlayer(player, new ItemStack(TFCItems.HANDSTONE.get()));
            if (processState == 2) CommonHelper.giveItemToPlayer(player, new ItemStack(Objects.requireNonNull(
                    ForgeRegistries.ITEMS.getValue(new ResourceLocation("create", "cogwheel")))));
            if (processState == 6) CommonHelper.giveItemToPlayer(player, new ItemStack(TFCItems.HANDSTONE.get()));

            if (processState == 0) {
                // Fully revert — replace with the unprocessed frame and refund the quern.
                level.setBlockAndUpdate(blockPos, DeReMetallicaBlocks.MILLSTONE_FRAME.get().defaultBlockState());
                CommonHelper.giveItemToPlayer(player, new ItemStack(TFCBlocks.QUERN.get().asItem()));
                return InteractionResult.SUCCESS;
            }

            level.setBlockAndUpdate(blockPos, blockState.setValue(FRAME_PROCESSED, processState - 1));
            return InteractionResult.SUCCESS;
        }

        // Step 0 → 1: place the first handstone.
        if (heldStack.is(TFCItems.HANDSTONE.get())) {
            if (processState == HANDSTONE_STEP_1 || processState == HANDSTONE_STEP_2) {
                if (!player.getAbilities().instabuild) heldStack.shrink(1);
                level.setBlockAndUpdate(blockPos, blockState.cycle(FRAME_PROCESSED));
                level.playSound(null, blockPos, SoundEvents.WOOD_PLACE, SoundSource.BLOCKS, 1.5F,
                        level.getRandom().nextFloat() * 0.1F + 0.9F);
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.CONSUME; // wrong stage — swallow the click
        }

        // Step 1 → 2: attach the cogwheel.
        if (heldStack.getItem() == ForgeRegistries.ITEMS.getValue(new ResourceLocation("create", "cogwheel"))) {
            if (processState == COGWHEEL_STEP) {
                if (!player.getAbilities().instabuild) heldStack.shrink(1);
                level.setBlockAndUpdate(blockPos, blockState.cycle(FRAME_PROCESSED));
                level.playSound(null, blockPos, SoundEvents.WOOD_PLACE, SoundSource.BLOCKS, 1.5F,
                        level.getRandom().nextFloat() * 0.1F + 0.9F);
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.CONSUME;
        }

        // Steps 2 → 3 → 4: hammer the cogwheel housing into shape (no item consumed).
        if (heldStack.is(TFCTags.Items.HAMMERS)) {
            if (COGWHEEL_STEP <= processState && processState < FULLY_HAMMERED) {
                level.setBlockAndUpdate(blockPos, blockState.cycle(FRAME_PROCESSED));
                level.playSound(null, blockPos, SoundEvents.METAL_PLACE, SoundSource.BLOCKS, 1.5F,
                        level.getRandom().nextFloat() * 0.1F + 0.9F);
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.CONSUME;
        }

        // Step 6 → done: nail on the oak slab and place the final Create Millstone.
        if (heldStack.is(Items.OAK_SLAB)) {
            if (processState == SLAB_STEP) {
                if (!player.getAbilities().instabuild) heldStack.shrink(1);
                level.setBlockAndUpdate(blockPos, Objects.requireNonNull(
                        ForgeRegistries.BLOCKS.getValue(new ResourceLocation("create", "millstone")))
                        .defaultBlockState());
                level.playSound(null, blockPos, SoundEvents.METAL_PLACE, SoundSource.BLOCKS, 1.5F,
                        level.getRandom().nextFloat() * 0.1F + 0.9F);
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.CONSUME;
        }

        return InteractionResult.PASS;
    }

    // -------------------------------------------------------------------------
    // Block state
    // -------------------------------------------------------------------------

    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
        // super adds WATERLOGGED; we add our two additional properties here.
        super.createBlockStateDefinition(builder.add(FRAME_PROCESSED, COGWHEEL_OFFSET));
    }

    // Compute and bake the cogwheel offset into the block state at placement time.
    @Override
    public BlockState getStateForPlacement(final BlockPlaceContext context) {
        final BlockPos pos = context.getClickedPos();
        final boolean offset = (Math.floorMod(pos.getX() + pos.getZ(), 2)) == 0;
        return Objects.requireNonNull(super.getStateForPlacement(context)).setValue(COGWHEEL_OFFSET, offset);
    }
}
