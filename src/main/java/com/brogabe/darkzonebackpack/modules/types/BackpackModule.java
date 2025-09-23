package com.brogabe.darkzonebackpack.modules.types;

import com.brogabe.darkzonebackpack.DarkzoneBackpack;
import com.brogabe.darkzonebackpack.configuration.ConfigManager;
import com.brogabe.darkzonebackpack.utils.BackpackUtils;
import com.brogabe.darkzonebackpack.utils.ItemCreator;
import com.brogabe.darkzonebackpack.utils.TierInfo;
import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.stream.Collectors;

public class BackpackModule {

    private final ConfigManager configManager;

    private final TierInfo tierInfo;

    private final BackpackUtils backpackUtils;

    public BackpackModule(DarkzoneBackpack plugin) {
        backpackUtils = plugin.getBackpackUtils();
        configManager = plugin.getConfigManager();
        tierInfo = plugin.getTierInfo();
    }

    public void itemPickup(Player player, PlayerPickupItemEvent event) {
        ItemStack pickedItem = event.getItem().getItemStack();

        if(pickedItem.getType() != Material.valueOf(configManager.getDzItemString())) return;

        if(pickedItem.hasItemMeta()) return;

        int backpackSlot = backpackUtils.getBackpackSlot(player);

        if(backpackSlot == -1) return;

        ItemStack backpack = player.getInventory().getItem(backpackSlot);
        NBTItem nbtItem = new NBTItem(backpack);
        NBTCompound compound = nbtItem.getCompound("DarkzoneBackpack");

        int tier = compound.getInteger("tier");
        int maxCapacity = tierInfo.getMaxCapacity(tier);
        int capacity = compound.getInteger("capacity");
        int pickupAmount = event.getItem().getItemStack().getAmount();

        if(capacity >= maxCapacity && maxCapacity != -1) return;

        int newCapacity = Math.min(capacity + pickupAmount, maxCapacity);
        int overflow = (capacity + pickupAmount) - maxCapacity;

        compound.setInteger("capacity", newCapacity);
        backpackUtils.updateBackpackSlot(player, backpackSlot, nbtItem);

        if(overflow > 0) {
            ItemStack leftover = new ItemStack((pickedItem));
            leftover.setAmount(overflow);
            player.getInventory().addItem(leftover);
        }

        event.setCancelled(true);
        event.getItem().remove();
    }

    public ItemStack getBackpackItem(int tier) {
        Material material = Material.valueOf(configManager.getBackpackMaterial());
        String name = configManager.getBackpackName().replace("%tier%", String.valueOf(tier));

        List<String> lore = formatBackpackLore(configManager.getBackpackLore(), 0, tierInfo.getMaxCapacity(tier));

        ItemStack itemStack = new ItemCreator(material, name, 1, 0, "", lore).getItem();
        NBTItem nbtItem = new NBTItem(itemStack);

        NBTCompound compound = nbtItem.getOrCreateCompound("DarkzoneBackpack");
        compound.setInteger("tier", tier);
        compound.setInteger("capacity", 0);

        return nbtItem.getItem();
    }

    private List<String> formatBackpackLore(List<String> baseLore, int amount, int max) {
        return baseLore.stream()
                .map(s -> s.replace("%amount%", String.valueOf(amount))
                        .replace("%max%", String.valueOf(max)))
                .collect(Collectors.toList());
    }
}
