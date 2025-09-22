package com.brogabe.darkzonebackpack.modules.types;

import com.brogabe.darkzonebackpack.DarkzoneBackpack;
import com.brogabe.darkzonebackpack.configuration.ConfigManager;
import com.brogabe.darkzonebackpack.utils.ColorUtil;
import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class SellModule {

    private final DarkzoneBackpack plugin;

    private final ConfigManager configManager;

    public SellModule(DarkzoneBackpack plugin) {
        this.plugin = plugin;

        configManager = plugin.getConfigManager();
    }

    public void onSell(Player player, ItemStack itemInHand) {
        if(player.getLocation().getWorld().getName().equalsIgnoreCase(configManager.getDarkzoneWorld())) {
            player.sendMessage(ColorUtil.color("&4&lBACKPACKS &fYou cannot sell at darkzone."));
            return;
        }

        // Reduce code by making one check to see if item is Backpack Item
        if(itemInHand == null || !itemInHand.hasItemMeta()) return;

        NBTItem nbtItem = new NBTItem(itemInHand);

        if(nbtItem.getCompound("DarkzoneBackpack") == null) return;

        int slot = player.getInventory().first(itemInHand);

        if(slot == -1) return;

        NBTCompound compound = nbtItem.getCompound("DarkzoneBackpack");

        int amount = compound.getInteger("capacity");

        if(amount <= 0) {
            player.sendMessage(ColorUtil.color("&4&lDARKZONE &fThis backpack is empty!"));
            player.playSound(player.getLocation(), Sound.VILLAGER_NO, 7, 6);
            return;
        }

        int sellPrice = configManager.getSellPrice();

        int earnedMoney = Math.max(0, amount * sellPrice);

        compound.setInteger("capacity", 0);

        updateItem(player, slot, nbtItem);

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "eco give " + player.getName() + " " + earnedMoney);

        player.sendMessage(ColorUtil.color("&4&lDARKZONE &fYou have sold your backpack"));
        player.playSound(player.getLocation(), Sound.LEVEL_UP, 7, 7);
    }

    private void updateItem(Player player, int slot, NBTItem nbtItem) {
        if(nbtItem.getCompound("DarkzoneBackpack") == null) return;

        NBTCompound compound = nbtItem.getCompound("DarkzoneBackpack");

        ItemStack itemStack = nbtItem.getItem();
        ItemMeta itemMeta = itemStack.getItemMeta();

        int tier = compound.getInteger("tier");
        int capacity = compound.getInteger("capacity");

        itemMeta.setDisplayName(ColorUtil.color(configManager.getBackpackName()
                .replace("%tier%", String.valueOf(tier))));

        List<String> lore = new ArrayList<>(configManager.getBackpackLore());
        lore.replaceAll(s -> s.replace("%amount%", String.valueOf(capacity)));
        lore.replaceAll(s -> s.replace("%max%", String.valueOf(getMaxCapacity(tier))));
        lore.replaceAll(ColorUtil::color);

        itemMeta.setLore(lore);

        itemStack.setItemMeta(itemMeta);

        player.getInventory().setItem(slot, itemStack);
    }

    private int getMaxCapacity(int tier) {
        FileConfiguration config = plugin.getConfig();

        int startingCapacity = config.getInt("settings.starting-capacity");

        if(config.getConfigurationSection("backpack-tiers." + tier) == null) return startingCapacity;

        return config.getInt("backpack-tiers." + tier + ".capacity");
    }

}
