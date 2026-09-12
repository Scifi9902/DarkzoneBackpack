package com.brogabe.darkzonebackpack.utils;

import com.brogabe.darkzonebackpack.configuration.ConfigManager;
import com.brogabe.darkzonebackpack.constant.DarkzoneBackpackConstant;
import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class BackpackHelper {

    private final ConfigManager configManager;

    private final TierInfo tierInfo;
    private final Logger logger;

    public boolean isValidBackpack(ItemStack itemStack) {
        if (itemStack == null || !itemStack.hasItemMeta() || itemStack.getAmount() < 1)  {
            return false;
        }

        return NBT.modify(itemStack, nbt -> nbt.getCompound(DarkzoneBackpackConstant.BACKPACK_COMPOUND_KEY) != null);
    }

    public int getBackpackSlot(Player player) {
        ItemStack backpack = getBackpack(player);

        if (backpack == null) {
            return -1;
        }

        return player.getInventory().first(backpack);
    }

    private ItemStack getBackpack(Player player) {
        for (ItemStack itemStack : player.getInventory().getContents()) {
            if (!this.isValidBackpack(itemStack)) {
                continue;
            }

            return itemStack;
        }

        return null;
    }

    public void updateBackpackSlot(Player player, int slot, NBTItem nbtItem) {
        NBTCompound compound = nbtItem.getCompound(DarkzoneBackpackConstant.BACKPACK_COMPOUND_KEY);

        if (compound == null) {
            logger.severe("NBT Compound not found " + DarkzoneBackpackConstant.BACKPACK_COMPOUND_KEY);
            return;
        }

        ItemStack itemStack = nbtItem.getItem();
        ItemMeta itemMeta = itemStack.getItemMeta();

        int tier = compound.hasTag("tier") ? compound.getInteger("tier") : -1;
        int capacity = compound.getInteger("capacity");

        if (tier == -1L) {
            this.logger.severe("Backpack does not have a tier.");
            return;
        }

        itemMeta.setDisplayName(ColorUtil.color(configManager.getBackpackName()
                .replace("%tier%", String.valueOf(tier))));

        List<String> lore = new ArrayList<>(configManager.getBackpackLore()).stream()
                .map(line -> ColorUtil.color(line)
                        .replace("%amount%", String.valueOf(capacity))
                        .replace("%max%", String.valueOf(tierInfo.getMaxCapacity(tier))))
                .collect(Collectors.toList());

        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);

        player.getInventory().setItem(slot, itemStack);
    }
}
