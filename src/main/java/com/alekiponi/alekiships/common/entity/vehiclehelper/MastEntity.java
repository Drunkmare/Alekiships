package com.alekiponi.alekiships.common.entity.vehiclehelper;

import com.alekiponi.alekiships.common.entity.vehicle.AbstractVehicle;
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
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
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
                    .getEntities(this, this.getBoundingBox().inflate(0, 0, 0).move(0, 0, 0), EntitySelector.pushableBy(this)));

            for (Entity entity : playersToMoveWithMast) {
                if ((entity instanceof LocalPlayer player)) {
                    player.move(MoverType.SELF, this.getRootVehicle().getDeltaMovement().multiply(1, 0, 1).add(0,0,0));
                    if (player.input.jumping) {
                        player.setDeltaMovement(player.getDeltaMovement().multiply(1,0,1).add(0,0.1,0));
                    } else if(player.input.shiftKeyDown){
                        player.setDeltaMovement(player.getDeltaMovement().multiply(1,0,1).add(0,0,0));
                    } else {
                        player.setDeltaMovement(player.getDeltaMovement().multiply(1,0,1).add(0,-0.1,0));
                    }

                }
            }

        }

    }

    @Override
    public InteractionResult interact(final Player player, final InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.getItem() instanceof BannerItem) {
            this.spawnAtLocation(this.getBanner());
            this.setBanner(stack.split(1));
            return InteractionResult.SUCCESS;
        }
        if (stack.is(Tags.Items.SHEARS)) {
            this.spawnAtLocation(this.getBanner());
            this.setBanner(ItemStack.EMPTY);
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
    protected void addAdditionalSaveData(CompoundTag pCompound) {
        pCompound.put("banner", this.getBanner().save(new CompoundTag()));
    }
}
