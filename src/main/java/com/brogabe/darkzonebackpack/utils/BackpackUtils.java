package com.brogabe.darkzonebackpack.utils;

import com.brogabe.darkzonebackpack.DarkzoneBackpack;
import com.brogabe.darkzonebackpack.configuration.ConfigManager;
import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class BackpackUtils {

    private final ConfigManager configManager;

    private final TierInfo tierInfo;

    public BackpackUtils(DarkzoneBackpack plugin) {
        configManager = plugin.getConfigManager();
        tierInfo = plugin.getTierInfo();
    }

    public boolean isValidBackpack(ItemStack itemStack) {
        if(itemStack == null || !itemStack.hasItemMeta()) return false;

        NBTItem nbtItem = new NBTItem(itemStack);

        return (nbtItem.getCompound("DarkzoneBackpack") != null);
    }

    public int getBackpackSlot(Player player) {
        ItemStack backpack = getBackpack(player);

        if(backpack == null) return -1;

        return player.getInventory().first(backpack);
    }

    private ItemStack getBackpack(Player player) {
        for(ItemStack itemStack : player.getInventory().getContents()) {
            if(!isValidBackpack(itemStack)) continue;

            return itemStack;
        }

        return null;
    }

    public void updateBackpackSlot(Player player, int slot, NBTItem nbtItem) {
        NBTCompound compound = nbtItem.getCompound("DarkzoneBackpack");

        ItemStack itemStack = nbtItem.getItem();
        ItemMeta itemMeta = itemStack.getItemMeta();

        int tier = compound.getInteger("tier");
        int capacity = compound.getInteger("capacity");

        itemMeta.setDisplayName(ColorUtil.color(configManager.getBackpackName()
                .replace("%tier%", String.valueOf(tier))));

        List<String> lore = new ArrayList<>(configManager.getBackpackLore());
        lore.replaceAll(s -> s.replace("%amount%", String.valueOf(capacity)));
        lore.replaceAll(s -> s.replace("%max%", String.valueOf(tierInfo.getMaxCapacity(tier))));
        lore.replaceAll(ColorUtil::color);

        itemMeta.setLore(lore);

        itemStack.setItemMeta(itemMeta);

        player.getInventory().setItem(slot, itemStack);
    }
}
