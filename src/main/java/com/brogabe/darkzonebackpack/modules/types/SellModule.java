package com.brogabe.darkzonebackpack.modules.types;

import com.brogabe.darkzonebackpack.configuration.ConfigManager;
import com.brogabe.darkzonebackpack.events.BackpackSellEvent;
import com.brogabe.darkzonebackpack.utils.BackpackHelper;
import com.brogabe.darkzonebackpack.utils.ColorUtil;
import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
public class SellModule {

    private final ConfigManager configManager;

    private final BackpackHelper backpackHelper;

    public void onSell(Player player, ItemStack itemInHand) {
        if(player.getLocation().getWorld().getName().equalsIgnoreCase(configManager.getDarkzoneWorld())) {
            player.sendMessage(ColorUtil.color("&4&lBACKPACKS &fYou cannot sell at darkzone."));
            return;
        }

        if(!backpackHelper.isValidBackpack(itemInHand)) return;

        int slot = player.getInventory().first(itemInHand);

        if(slot == -1) return;

        NBTItem nbtItem = new NBTItem(itemInHand);
        NBTCompound compound = nbtItem.getCompound("DarkzoneBackpack");

        int amount = compound.getInteger("capacity");

        if(amount <= 0) {
            player.sendMessage(ColorUtil.color("&4&lDARKZONE &fThis backpack is empty!"));
            player.playSound(player.getLocation(), Sound.VILLAGER_NO, 7, 6);
            return;
        }

        int sellPrice = configManager.getSellPrice();
        int earnedMoney = Math.max(0, amount * sellPrice);

        BackpackSellEvent event = new BackpackSellEvent(player, earnedMoney);

        Bukkit.getPluginManager().callEvent(event);

        if(event.isCancelled()) return;

        compound.setInteger("capacity", 0);

        backpackHelper.updateBackpackSlot(player, slot, nbtItem);

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "eco give " + player.getName() + " " + event.getAmount());

        player.sendMessage(ColorUtil.color("&4&lDARKZONE &fYou have sold your backpack"));
        player.playSound(player.getLocation(), Sound.LEVEL_UP, 7, 7);
    }
}
