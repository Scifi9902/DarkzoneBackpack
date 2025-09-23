package com.brogabe.darkzonebackpack.modules.types;

import com.brogabe.darkzonebackpack.DarkzoneBackpack;
import com.brogabe.darkzonebackpack.utils.BackpackUtils;
import com.brogabe.darkzonebackpack.utils.ColorUtil;
import com.brogabe.darkzonebackpack.utils.TierInfo;
import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class UpgradeModule {

    private final DarkzoneBackpack plugin;

    private final TierInfo tierInfo;

    private final BackpackUtils backpackUtils;

    public UpgradeModule(DarkzoneBackpack plugin) {
        this.plugin = plugin;

        backpackUtils = plugin.getBackpackUtils();
        tierInfo = plugin.getTierInfo();
    }

    public void upgradeItem(Player player) {
        ItemStack itemInHand = player.getItemInHand();

        if(!backpackUtils.isValidBackpack(itemInHand)) return;

        NBTItem nbtItem = new NBTItem(itemInHand);
        NBTCompound compound = nbtItem.getCompound("DarkzoneBackpack");

        int tier = compound.getInteger("tier");
        int price = tierInfo.nextTierPrice(tier);

        if(price == -1) {
            player.sendMessage(ColorUtil.color("&4&lBACKPACKS &fYou already have a maxed backpack!"));
            player.playSound(player.getLocation(), Sound.VILLAGER_NO, 6, 6);
            return;
        }

        Economy economy = plugin.getEconomy();

        int updatedPrice = Math.max(0, price);

        if(!economy.has(player, updatedPrice)) {
            player.sendMessage(ColorUtil.color("&4&lBACKPACKS &fYou cannot afford this!"));
            player.playSound(player.getLocation(), Sound.VILLAGER_NO, 6, 6);
            return;
        }

        economy.withdrawPlayer(player, updatedPrice);

        BackpackModule backpackModule = plugin.getModuleManager().getBackpackModule();

        player.setItemInHand(backpackModule.getBackpackItem(tier + 1));
        player.closeInventory();
    }
}
