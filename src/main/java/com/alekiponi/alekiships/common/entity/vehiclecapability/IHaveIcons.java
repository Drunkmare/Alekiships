package com.alekiponi.alekiships.common.entity.vehiclecapability;

import com.alekiponi.alekiships.client.IngameOverlays;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public interface IHaveIcons {
    public ArrayList<IngameOverlays.IconState> getIconStates(Player player);

}
