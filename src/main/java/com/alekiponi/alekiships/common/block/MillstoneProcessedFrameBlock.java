package com.alekiponi.alekiships.common.block;

import com.alekiponi.alekiships.common.item.AlekiShipsItems;
import com.alekiponi.alekiships.util.BoatMaterial;
import com.alekiponi.alekiships.util.CommonHelper;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.items.TFCItems;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
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

    public static final IntegerProperty FRAME_PROCESSED = AlekiShipsBlockStateProperties.FRAME_PROCESSED;
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

    public static final BooleanProperty COGWHEEL_OFFSET = BooleanProperty.create("cogwheel_offset");

    @Override
    public BlockState getStateForPlacement(final BlockPlaceContext context) {
        final BlockPos pos = context.getClickedPos();
        final boolean offset = (Math.floorMod(pos.getX() + pos.getZ(), 2)) == 0;
        return super.getStateForPlacement(context).setValue(COGWHEEL_OFFSET, offset);
    }

    @Override
    public InteractionResult use(final BlockState blockState, final Level level, final BlockPos blockPos,
                                 final Player player, final InteractionHand hand, final BlockHitResult hitResult) {

        if (hand != InteractionHand.MAIN_HAND) return InteractionResult.PASS;



        final ItemStack heldStack = player.getItemInHand(hand);

        int processState = blockState.getValue(FRAME_PROCESSED);

        // Try extract
        if (heldStack.isEmpty() && !level.isClientSide) {
            // Extract an item
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

            // Set ourselves back to our base
            if (processState == 0) {
                level.setBlockAndUpdate(blockPos, AlekiShipsBlocks.MILLSTONE_FRAME.get().defaultBlockState());
                CommonHelper.giveItemToPlayer(player, new ItemStack(TFCBlocks.QUERN.get().asItem()));
                return InteractionResult.SUCCESS;
            }

            level.setBlockAndUpdate(blockPos, blockState.setValue(FRAME_PROCESSED, processState - 1));

            return InteractionResult.SUCCESS;
        }

        // Should we place the handstone
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

        // Should we place the Cogwheel
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

        // Should we do hammer stuff
        if (heldStack.is(TFCTags.Items.HAMMERS)) {
            if (COGWHEEL_STEP <= processState && processState < FULLY_HAMMERED) {
                level.setBlockAndUpdate(blockPos, blockState.cycle(FRAME_PROCESSED));
                level.playSound(null, blockPos, SoundEvents.METAL_PLACE, SoundSource.BLOCKS, 1.5F,
                        level.getRandom().nextFloat() * 0.1F + 0.9F);
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.CONSUME;
        }

        // Should we add the top
        if (heldStack.is(Items.OAK_SLAB)) {
            if (processState == SLAB_STEP) {
                if (!player.getAbilities().instabuild) {
                    heldStack.shrink(1);
                }
                level.setBlockAndUpdate(blockPos, ForgeRegistries.BLOCKS
                        .getValue(new ResourceLocation("create", "millstone"))
                        .defaultBlockState());
                level.playSound(null, blockPos, SoundEvents.METAL_PLACE, SoundSource.BLOCKS, 1.5F,
                        level.getRandom().nextFloat() * 0.1F + 0.9F);
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.CONSUME;
        }

        /**
        // Should we do bolt stuff
        if (heldStack.is(AlekiShipsItems.COPPER_BOLT.get()) && player.getOffhandItem().is(TFCTags.Items.HAMMERS)) {
            // Must be [3,7)
            if (FULLY_HAMMERED <= processState && processState < FULLY_PROCESSED) {
                if (!player.getAbilities().instabuild) {
                    heldStack.shrink(1);
                }
                level.setBlockAndUpdate(blockPos, blockState.cycle(FRAME_PROCESSED));
                level.playSound(null, blockPos, SoundEvents.METAL_PLACE, SoundSource.BLOCKS, 1.5F,
                        level.getRandom().nextFloat() * 0.1F + 0.9F);
                if (processState + 1 == FULLY_PROCESSED) {
                    AngledWoodenBoatFrameBlock.triggerDetection(level, blockPos);
                }
                return InteractionResult.sidedSuccess(level.isClientSide());
            }
            return InteractionResult.FAIL;
        }
        */

        return InteractionResult.PASS;
    }

    @Override
    @SuppressWarnings("deprecation")
    public ItemStack getCloneItemStack(final BlockGetter blockGetter, final BlockPos blockPos,
                                       final BlockState blockState) {
        // We don't exist as an item so pass it the base version instead
        return AlekiShipsBlocks.MILLSTONE_FRAME.get().getCloneItemStack(blockGetter, blockPos, blockState);
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