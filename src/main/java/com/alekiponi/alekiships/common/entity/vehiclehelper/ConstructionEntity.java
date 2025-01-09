package com.alekiponi.alekiships.common.entity.vehiclehelper;

import com.alekiponi.alekiships.common.entity.vehicle.AbstractUnderConstructionEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ConstructionEntity extends AbstractPassthroughHelper {

    public ConstructionEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    protected void defineSynchedData(final SynchedEntityData.Builder builder) {

    }

    public float getSpin(float pPartialTicks) {
        return (this.tickCount + pPartialTicks) / 20.0F;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {

    }

    /**
     * @return A potentially empty array of accepted ItemStack inputs
     */
    public ItemStack[] getRequiredItems() {
        if (this.getRootVehicle() instanceof AbstractUnderConstructionEntity<?, ?> constructionEntity) {
            return constructionEntity.getRequiredItems();
        }
        return new ItemStack[0];
    }

    @Override
    public InteractionResult interact(final Player player, final InteractionHand hand) {
        if (this.getRootVehicle() instanceof AbstractUnderConstructionEntity<?, ?> constructionEntity) {
            return constructionEntity.interactFromConstructionEntity(player, hand);
        }
        return InteractionResult.FAIL;
    }
}