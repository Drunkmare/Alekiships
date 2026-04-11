package com.drunkmare.deremet.common.block;

import com.alekiponi.alekiships.common.block.ProcessedBoatFrame;
import com.alekiponi.alekiships.util.BoatMaterial;
import com.alekiponi.alekiships.util.CommonHelper;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.items.TFCItems;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;

public class MillstoneProcessedFrameBlock extends MillstoneFrameBlock implements ProcessedBoatFrame {

    public static final IntegerProperty FRAME_PROCESSED = IntegerProperty.create("frame_processed", 0, 7);
    public static final BooleanProperty COGWHEEL_OFFSET = BooleanProperty.create("cogwheel_offset");
    public static final int HANDSTONE_STEP_1 = 0;
    public static final int COGWHEEL_STEP = 1;
    public static final int HANDSTONE_STEP_2 = 5;
    public static final int SLAB_STEP = 6;
    public static final int FULLY_HAMMERED = 5;
    public static final int FULLY_PROCESSED = 7;
    public final BoatMaterial boatMaterial;

    public MillstoneProcessedFrameBlock(final BoatMaterial boatMaterial, final Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState()
                .setValue(FRAME_PROCESSED, 0)
                .setValue(COGWHEEL_OFFSET, false));
        this.boatMaterial = boatMaterial;
    }

    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(FRAME_PROCESSED, COGWHEEL_OFFSET));
    }

    @Override
    public BlockState getStateForPlacement(final BlockPlaceContext context) {
        final BlockPos pos = context.getClickedPos();
        final boolean offset = (Math.floorMod(pos.getX() + pos.getZ(), 2)) == 0;
        return Objects.requireNonNull(super.getStateForPlacement(context)).setValue(COGWHEEL_OFFSET, offset);
    }

    @Override
    public InteractionResult use(final BlockState blockState, final Level level, final BlockPos blockPos,
                                 final Player player, final InteractionHand hand, final BlockHitResult hitResult) {

        if (hand != InteractionHand.MAIN_HAND) return InteractionResult.PASS;

        final ItemStack heldStack = player.getItemInHand(hand);

        int processState = blockState.getValue(FRAME_PROCESSED);

        // Try extract
        if (heldStack.isEmpty() && !level.isClientSide) {
            if (processState == 1) {
                CommonHelper.giveItemToPlayer(player, new ItemStack(TFCItems.HANDSTONE.get()));
            }

            if (processState == 2) {
                CommonHelper.giveItemToPlayer(player, new ItemStack(Objects.requireNonNull(ForgeRegistries.ITEMS
                        .getValue(new ResourceLocation("create", "cogwheel")))));
            }

            if (processState == 6) {
                CommonHelper.giveItemToPlayer(player, new ItemStack(TFCItems.HANDSTONE.get()));
            }

            if (processState == 0) {
                level.setBlockAndUpdate(blockPos, DeReMetallicaBlocks.MILLSTONE_FRAME.get().defaultBlockState());
                CommonHelper.giveItemToPlayer(player, new ItemStack(TFCBlocks.QUERN.get().asItem()));
                return InteractionResult.SUCCESS;
            }

            level.setBlockAndUpdate(blockPos, blockState.setValue(FRAME_PROCESSED, processState - 1));

            return InteractionResult.SUCCESS;
        }

        if (heldStack.is(TFCItems.HANDSTONE.get())) {
            if (processState == HANDSTONE_STEP_1) {
                if (!player.getAbilities().instabuild) {
                    heldStack.shrink(1);
                }
                level.setBlockAndUpdate(blockPos, blockState.cycle(FRAME_PROCESSED));
                level.playSound(null, blockPos, SoundEvents.WOOD_PLACE, SoundSource.BLOCKS, 1.5F,
                        level.getRandom().nextFloat() * 0.1F + 0.9F);
                return InteractionResult.SUCCESS;
            }
            if (processState == HANDSTONE_STEP_2) {
                if (!player.getAbilities().instabuild) {
                    heldStack.shrink(1);
                }
                level.setBlockAndUpdate(blockPos, blockState.cycle(FRAME_PROCESSED));
                level.playSound(null, blockPos, SoundEvents.WOOD_PLACE, SoundSource.BLOCKS, 1.5F,
                        level.getRandom().nextFloat() * 0.1F + 0.9F);
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.CONSUME;
        }

        if (heldStack.getItem() == ForgeRegistries.ITEMS.getValue(new ResourceLocation("create", "cogwheel"))) {
            if (processState == COGWHEEL_STEP) {
                if (!player.getAbilities().instabuild) {
                    heldStack.shrink(1);
                }
                level.setBlockAndUpdate(blockPos, blockState.cycle(FRAME_PROCESSED));
                level.playSound(null, blockPos, SoundEvents.WOOD_PLACE, SoundSource.BLOCKS, 1.5F,
                        level.getRandom().nextFloat() * 0.1F + 0.9F);
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.CONSUME;
        }

        if (heldStack.is(TFCTags.Items.HAMMERS)) {
            if (COGWHEEL_STEP <= processState && processState < FULLY_HAMMERED) {
                level.setBlockAndUpdate(blockPos, blockState.cycle(FRAME_PROCESSED));
                level.playSound(null, blockPos, SoundEvents.METAL_PLACE, SoundSource.BLOCKS, 1.5F,
                        level.getRandom().nextFloat() * 0.1F + 0.9F);
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.CONSUME;
        }

        if (heldStack.is(Items.OAK_SLAB)) {
            if (processState == SLAB_STEP) {
                if (!player.getAbilities().instabuild) {
                    heldStack.shrink(1);
                }
                level.setBlockAndUpdate(blockPos, Objects.requireNonNull(ForgeRegistries.BLOCKS
                                .getValue(new ResourceLocation("create", "millstone")))
                        .defaultBlockState());
                level.playSound(null, blockPos, SoundEvents.METAL_PLACE, SoundSource.BLOCKS, 1.5F,
                        level.getRandom().nextFloat() * 0.1F + 0.9F);
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.CONSUME;
        }

        return InteractionResult.PASS;
    }

    @Override
    @SuppressWarnings("deprecation")
    public ItemStack getCloneItemStack(final BlockGetter blockGetter, final BlockPos blockPos,
                                       final BlockState blockState) {
        return DeReMetallicaBlocks.MILLSTONE_FRAME.get().getCloneItemStack(blockGetter, blockPos, blockState);
    }

    @Override
    public IntegerProperty getProcessingProperty() {
        return FRAME_PROCESSED;
    }

    @Override
    public int getProcessingLimit() {
        return FULLY_PROCESSED;
    }

    @Override
    public BoatMaterial getBoatMaterial() {
        return this.boatMaterial;
    }
}
