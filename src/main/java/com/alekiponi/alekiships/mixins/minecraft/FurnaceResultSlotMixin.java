package com.alekiponi.alekiships.mixins.minecraft;

import com.alekiponi.alekiships.common.entity.compartment.vanilla.AbstractFurnaceCompartmentEntity;
import com.alekiponi.alekiships.common.entity.compartment.vanilla.FurnaceCompartmentEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.FurnaceResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FurnaceResultSlot.class)
public class FurnaceResultSlotMixin extends Slot {

    @Shadow
    @Final
    private Player player;

    public FurnaceResultSlotMixin(final Container pContainer, final int pSlot, final int pX, final int pY) {
        super(pContainer, pSlot, pX, pY);
    }

    /**
     * @reason Our {@link FurnaceCompartmentEntity} and children need to reward XP like vanilla furnaces.
     * This mixin is to do exactly that.
     * @author Traister101
     */
    @Inject(method = "checkTakeAchievements", at = @At(value = "FIELD", target = "Lnet/minecraft/world/inventory/FurnaceResultSlot;container:Lnet/minecraft/world/Container;"))
    private void alekiships$rewardAbstractFurnaceCompartmentXP(final ItemStack pStack, final CallbackInfo ci) {
        if (this.container instanceof AbstractFurnaceCompartmentEntity furnaceCompartment) {
            furnaceCompartment.awardUsedRecipesAndPopExperience(((ServerPlayer) this.player));
        }
    }
}