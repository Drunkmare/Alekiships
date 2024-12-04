package com.alekiponi.alekiships.common.entity.vehiclehelper;

import java.util.ArrayList;
import java.util.List;
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
import net.neoforged.neoforge.common.Tags;

public class MastEntity extends AbstractPassthroughHelper {

    static String BANNER_KEY = "banner";

    public MastEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }


    protected static final EntityDataAccessor<ItemStack> DATA_ID_BANNER = SynchedEntityData.defineId(
            MastEntity.class, EntityDataSerializers.ITEM_STACK);

    @Override
    public void tick(){
        super.tick();

        if(this.level().isClientSide()){

            List<Entity> playersToMoveWithMast = new ArrayList<Entity>(this.level()
                .getEntities(this, this.getBoundingBox().inflate(0, 0, 0).move(0, 0, 0), EntitySelector.NO_SPECTATORS));

            for (Entity entity : playersToMoveWithMast) {
                if ((entity instanceof LocalPlayer player)) {
                    Vec3 vehicleMovement = this.getRootVehicle().getDeltaMovement();
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
        if (stack.is(Tags.Items.TOOLS_SHEAR))
        {
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
    protected void defineSynchedData(SynchedEntityData.Builder builder)
    {
        builder.define(DATA_ID_BANNER, ItemStack.EMPTY);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound)
    {
        pCompound.put(BANNER_KEY, CommonHelper.serializeItemStack(this.getBanner(), this.registryAccess()));
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
        this.setBanner(CommonHelper.deserializeItemStack(pCompound.getCompound(BANNER_KEY), this.registryAccess()));
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


}
