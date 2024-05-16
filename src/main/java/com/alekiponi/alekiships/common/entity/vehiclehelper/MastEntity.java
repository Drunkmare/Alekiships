package com.alekiponi.alekiships.common.entity.vehiclehelper;

import com.alekiponi.alekiships.client.IngameOverlays;
import com.alekiponi.alekiships.util.CommonHelper;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags;

import java.util.ArrayList;
import java.util.List;

public class MastEntity extends AbstractPassthroughHelper {
    public MastEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    protected static final EntityDataAccessor<ItemStack> DATA_ID_BANNER = SynchedEntityData.defineId(
            MastEntity.class, EntityDataSerializers.ITEM_STACK);

    @Override
    public void tick(){
        super.tick();

        if(this.level().isClientSide()){
            List<net.minecraft.world.entity.Entity> playersToMoveWithMast = new ArrayList<Entity>();

            playersToMoveWithMast.addAll(this.level()
                    .getEntities(this, this.getBoundingBox().inflate(0, 0, 0).move(0, 0, 0), EntitySelector.NO_SPECTATORS));

            for (Entity entity : playersToMoveWithMast) {
                if ((entity instanceof LocalPlayer player)) {
                    Vec3 vehicleMovement = this.getRootVehicle().getDeltaMovement();
                    /*
                    if (player.input.jumping || player.input.left || player.input.right || player.input.up || player.input.down) {
                        player.setDeltaMovement(player.getDeltaMovement().multiply(1.0, 1, 1.0).add(vehicleMovement.multiply(0.45, 0, 0.45)));
                    } else {
                        player.setPos(player.getPosition(0).add(vehicleMovement));
                        if (player.getDeltaMovement().length() > vehicleMovement.length() + 0.01) {
                            player.setDeltaMovement(Vec3.ZERO);
                        }
                    }*/
                    player.setPos(new Vec3(this.position().x + 0.3f, player.position().y, this.position().z + 0.3f));
                    if (player.input.jumping) {
                        player.setDeltaMovement(player.getDeltaMovement().multiply(1,0,1).add(0,0.1,0));
                    } else if(player.input.shiftKeyDown){
                        player.setDeltaMovement(player.getDeltaMovement().multiply(1,0,1).add(0,0,0));
                    } else {
                        player.setDeltaMovement(player.getDeltaMovement().multiply(1,0,1).add(0,-0.1,0));
                    }

                }
                if(entity instanceof Player player){
                    player.resetFallDistance();
                }

            }

        }

    }

    @Override
    public InteractionResult interact(final Player player, final InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.getItem() instanceof BannerItem bannerItem && !stack.is(getBanner().getItem())) {
            CommonHelper.giveItemToPlayer(player, this.getBanner());
            this.setBanner(stack.split(1));
            //bannerItem.patterns
            this.level().playSound(null, this, SoundEvents.WOOL_PLACE, SoundSource.BLOCKS, 1.5F,
                    this.level().getRandom().nextFloat() * 0.1F + 0.9F);
            return InteractionResult.SUCCESS;
        }
        if (stack.is(Tags.Items.SHEARS)) {
            CommonHelper.giveItemToPlayer(player, this.getBanner());
            this.setBanner(ItemStack.EMPTY);
            this.level().playSound(null, this, SoundEvents.SHEEP_SHEAR, SoundSource.BLOCKS, 1.5F,
                    this.level().getRandom().nextFloat() * 0.1F + 0.9F);
            return InteractionResult.SUCCESS;
        }
        return super.interact(player,hand);
    }

    public void setBanner(ItemStack banner){
        entityData.set(DATA_ID_BANNER, banner);
    }

    public ItemStack getBanner(){
        return entityData.get(DATA_ID_BANNER);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_ID_BANNER, ItemStack.EMPTY);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
        this.setBanner(ItemStack.of(pCompound.getCompound("banner")));
    }

    @Override
    public ArrayList<IngameOverlays.IconState> getIconStates(Player player) {
        ArrayList<IngameOverlays.IconState> states = new ArrayList<>();

        for (final ItemStack itemStack : player.getHandSlots()) {
            if (itemStack.getItem() instanceof BannerItem && !itemStack.is(getBanner().getItem())) {
                states.add(IngameOverlays.IconState.BRUSH);
                return states;
            }
        }

        return super.getIconStates(player);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {
        pCompound.put("banner", this.getBanner().save(new CompoundTag()));
    }
}
