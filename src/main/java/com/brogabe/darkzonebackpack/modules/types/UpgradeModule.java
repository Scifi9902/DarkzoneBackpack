package com.brogabe.darkzonebackpack.modules.types;

import com.brogabe.darkzonebackpack.DarkzoneBackpack;
import com.brogabe.darkzonebackpack.utils.ColorUtil;
import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class UpgradeModule {

    private final DarkzoneBackpack plugin;

    public UpgradeModule(DarkzoneBackpack plugin) {
        this.plugin = plugin;
    }

    public void upgradeItem(Player player) {
        ItemStack itemInHand = player.getItemInHand();

        if(!isValidBackpack(itemInHand)) return;

        NBTItem nbtItem = new NBTItem(itemInHand);

        NBTCompound compound = nbtItem.getCompound("DarkzoneBackpack");

        int tier = compound.getInteger("tier");
        int nextTier = tier + 1;

        if(!nextTierExists(tier)) {
            player.sendMessage(ColorUtil.color("&4&lBACKPACKS &fYou already have a maxed backpack!"));
            player.playSound(player.getLocation(), Sound.VILLAGER_NO, 6, 6);
            return;
        }

        int price = nextTierPrice(tier);

        Economy economy = plugin.getEconomy();

        if(!economy.has(player, Math.max(0, price))) {
            player.sendMessage(ColorUtil.color("&4&lBACKPACKS &fYou cannot afford this!"));
            player.playSound(player.getLocation(), Sound.VILLAGER_NO, 6, 6);
            return;
        }

        economy.withdrawPlayer(player, Math.max(0, price));

        BackpackModule backpackModule = plugin.getModuleManager().getBackpackModule();

        player.setItemInHand(backpackModule.getBackpackItem(nextTier));
        player.closeInventory();
    }

    public boolean isValidBackpack(ItemStack itemStack) {
        if(itemStack == null || !itemStack.hasItemMeta()) return false;

        NBTItem nbtItem = new NBTItem(itemStack);

        return (nbtItem.getCompound("DarkzoneBackpack") != null);
    }

    public boolean nextTierExists(int currentTier) {
        FileConfiguration config = plugin.getConfig();

        int nextTier = currentTier + 1;

        return (config.getConfigurationSection("backpack-tiers." + nextTier) != null);
    }

    public int nextTierPrice(int currentTier) {
        if(!nextTierExists(currentTier)) return -1;

        FileConfiguration config = plugin.getConfig();

        int nextTier = currentTier + 1;

        return config.getInt("backpack-tiers." + nextTier + ".cost");
    }
}
