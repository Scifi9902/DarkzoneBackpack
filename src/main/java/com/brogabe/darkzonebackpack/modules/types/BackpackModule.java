package com.brogabe.darkzonebackpack.modules.types;

import com.brogabe.darkzonebackpack.DarkzoneBackpack;
import com.brogabe.darkzonebackpack.configuration.ConfigManager;
import com.brogabe.darkzonebackpack.utils.ColorUtil;
import com.brogabe.darkzonebackpack.utils.ItemCreator;
import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class BackpackModule {

    private final DarkzoneBackpack plugin;

    private final ConfigManager configManager;

    public BackpackModule(DarkzoneBackpack plugin) {
        this.plugin = plugin;

        configManager = plugin.getConfigManager();
    }

    public void itemPickup(Player player, PlayerPickupItemEvent event) {
        if(event.getItem().getItemStack().getType() != Material.valueOf(configManager.getDzItemString())) return;

        if(event.getItem().getItemStack().hasItemMeta()) return;

        int backpackSlot = getBackpackSlot(player);

        if(backpackSlot == -1) return;

        ItemStack backpack = player.getInventory().getItem(backpackSlot);

        if(backpack == null) return;

        NBTItem nbtItem = new NBTItem(backpack);

        if(nbtItem.getCompound("DarkzoneBackpack") == null) return;

        NBTCompound compound = nbtItem.getCompound("DarkzoneBackpack");

        int tier = compound.getInteger("tier");

        int maxCapacity = getMaxCapacity(tier);
        int capacity = compound.getInteger("capacity");

        int pickupAmount = event.getItem().getItemStack().getAmount();

        if(capacity == maxCapacity) return;

        int newAmount = capacity + pickupAmount;

        if(newAmount > maxCapacity) {
            int removal = newAmount - maxCapacity;
            ItemStack newItem = new ItemStack(event.getItem().getItemStack());
            newItem.setAmount(removal);
            player.getInventory().addItem(newItem);
            compound.setInteger("capacity", maxCapacity);
            updateItem(player, backpackSlot, nbtItem);

            event.setCancelled(true);
            event.getItem().remove();
            return;
        }

        compound.setInteger("capacity", newAmount);
        updateItem(player, backpackSlot, nbtItem);
        event.setCancelled(true);
        event.getItem().remove();
    }

    private void updateItem(Player player, int slot, NBTItem nbtItem) {
        ItemStack itemAtSlot = player.getInventory().getItem(slot);

        if(itemAtSlot == null) return;

        if(itemAtSlot == nbtItem.getItem()) return;

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

    private int getBackpackSlot(Player player) {
        return player.getInventory().first(getBackpack(player));
    }

    private ItemStack getBackpack(Player player) {
        for(ItemStack itemStack : player.getInventory().getContents()) {
            if(itemStack == null) continue;

            NBTItem nbtItem = new NBTItem(itemStack);

            if(nbtItem.getCompound("DarkzoneBackpack") == null) continue;

            return itemStack;
        }

        return null;
    }

    public ItemStack getBackpackItem(int tier) {
        Material material = Material.valueOf(configManager.getBackpackMaterial());
        String name = configManager.getBackpackName().replace("%tier%", String.valueOf(tier));
        List<String> lore = new ArrayList<>(configManager.getBackpackLore());


        lore.replaceAll(s -> s.replace("%amount%", String.valueOf(0)));
        lore.replaceAll(s -> s.replace("%max%", String.valueOf(getMaxCapacity(tier))));

        ItemStack itemStack = new ItemCreator(material, name, 1, 0, "", lore).getItem();
        NBTItem nbtItem = new NBTItem(itemStack);

        NBTCompound compound = nbtItem.getOrCreateCompound("DarkzoneBackpack");

        compound.setInteger("tier", tier);
        compound.setInteger("capacity", 0);

        return nbtItem.getItem();
    }

    private int getMaxCapacity(int tier) {
        FileConfiguration config = plugin.getConfig();

        int startingCapacity = config.getInt("settings.starting-capacity");

        if(config.getConfigurationSection("backpack-tiers." + tier) == null) return startingCapacity;

        return config.getInt("backpack-tiers." + tier + ".capacity");
    }
}
